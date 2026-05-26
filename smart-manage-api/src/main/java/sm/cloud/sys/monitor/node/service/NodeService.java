package sm.cloud.sys.monitor.node.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import sm.cloud.sys.monitor.node.domain.vo.NodeInfoVO;
import sm.cloud.sys.monitor.node.domain.vo.NodeInfoVO.*;

import java.lang.management.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 节点监控服务：聚合 OSHI（硬件）+ Actuator（健康检查）+ ManagementFactory（JVM）
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NodeService {

    private final HealthEndpoint healthEndpoint;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public NodeInfoVO getNodeInfo() {
        NodeInfoVO vo = new NodeInfoVO();
        vo.setJvm(buildJvmInfo());
        vo.setMemory(buildMemoryInfo());
        vo.setCpu(buildCpuInfo());
        vo.setOs(buildOsInfo());
        vo.setDisk(buildDiskInfo());
        vo.setThreads(buildThreadsInfo());
        vo.setGc(buildGcInfo());
        vo.setClassLoading(buildClassLoadingInfo());
        vo.setHealth(buildHealthInfo());
        return vo;
    }

    // ── JVM 运行时 ──
    private JvmInfo buildJvmInfo() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        JvmInfo info = new JvmInfo();
        info.setVersion(System.getProperty("java.version"));
        info.setVendor(System.getProperty("java.vendor"));
        info.setHome(System.getProperty("java.home"));
        info.setRuntimeName(runtime.getVmName());
        info.setUptime(runtime.getUptime());
        info.setStartTime(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(runtime.getStartTime()), ZoneId.systemDefault()).format(ISO_FORMATTER));
        info.setInputArgs(runtime.getInputArguments());
        return info;
    }

    // ── 内存 ──
    private MemoryInfo buildMemoryInfo() {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        MemoryInfo info = new MemoryInfo();

        MemorySegment heap = new MemorySegment();
        heap.setUsed(mem.getHeapMemoryUsage().getUsed());
        heap.setMax(mem.getHeapMemoryUsage().getMax());
        heap.setCommitted(mem.getHeapMemoryUsage().getCommitted());
        heap.setInit(mem.getHeapMemoryUsage().getInit());
        info.setHeap(heap);

        MemorySegment nonHeap = new MemorySegment();
        nonHeap.setUsed(mem.getNonHeapMemoryUsage().getUsed());
        nonHeap.setCommitted(mem.getNonHeapMemoryUsage().getCommitted());
        info.setNonHeap(nonHeap);

        List<MemoryPoolInfo> pools = new ArrayList<>();
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            MemoryPoolInfo poolInfo = new MemoryPoolInfo();
            poolInfo.setName(pool.getName());
            poolInfo.setType(pool.getType().name());
            poolInfo.setUsed(pool.getUsage().getUsed());
            poolInfo.setMax(pool.getUsage().getMax());
            poolInfo.setCommitted(pool.getUsage().getCommitted());
            pools.add(poolInfo);
        }
        info.setPools(pools);
        return info;
    }

    // ── CPU ──
    private CpuInfo buildCpuInfo() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor processor = hal.getProcessor();

        CpuInfo info = new CpuInfo();
        info.setModel(processor.getProcessorIdentifier().getName());
        info.setPhysicalCores(processor.getPhysicalProcessorCount());
        info.setLogicalCores(processor.getLogicalProcessorCount());
        // OSHI 测量 CPU 负载：阻塞 500ms 采样
        info.setSystemLoad(processor.getSystemCpuLoad(500));

        // 进程 CPU 负载：通过 com.sun.management 扩展接口
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunBean) {
            info.setProcessCpuLoad(sunBean.getProcessCpuLoad());
        }
        return info;
    }

    // ── OS ──
    private OsInfo buildOsInfo() {
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        OsInfo info = new OsInfo();
        info.setName(os.getFamily());
        info.setVersion(os.getVersionInfo().getVersion());
        info.setArch(System.getProperty("os.arch"));
        return info;
    }

    // ── 磁盘（仅应用所在磁盘）──
    private DiskInfo buildDiskInfo() {
        String appDir = System.getProperty("user.dir");
        SystemInfo si = new SystemInfo();
        FileSystem fs = si.getOperatingSystem().getFileSystem();
        List<OSFileStore> stores = fs.getFileStores();

        // 按挂载点路径长度降序排列，找到最匹配当前目录的文件存储
        OSFileStore matched = stores.stream()
                .filter(s -> appDir.toLowerCase().startsWith(s.getMount().toLowerCase()))
                .max(Comparator.comparingInt(s -> s.getMount().length()))
                .orElse(null);

        DiskInfo info = new DiskInfo();
        if (matched != null) {
            info.setName(matched.getMount());
            info.setTotal(matched.getTotalSpace());
            info.setFree(matched.getUsableSpace());
            info.setUsed(matched.getTotalSpace() - matched.getUsableSpace());
            long total = matched.getTotalSpace();
            if (total > 0) {
                info.setUsagePercent((double) (total - matched.getUsableSpace()) / total);
            }
        }
        return info;
    }

    // ── 线程列表 ──
    private ThreadsInfo buildThreadsInfo() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        ThreadsInfo info = new ThreadsInfo();
        info.setTotal(threadBean.getThreadCount());
        info.setDaemon(threadBean.getDaemonThreadCount());
        info.setPeak(threadBean.getPeakThreadCount());

        // 轻量模式：不获取锁信息
        List<ThreadItem> items = new ArrayList<>();
        for (ThreadInfo ti : threadBean.dumpAllThreads(false, false)) {
            ThreadItem item = new ThreadItem();
            item.setId(ti.getThreadId());
            item.setName(ti.getThreadName());
            item.setState(ti.getThreadState().name());
            item.setCpuTimeMs(threadBean.getThreadCpuTime(ti.getThreadId()) / 1_000_000L);
            item.setBlockedTimeMs(threadBean.getThreadInfo(ti.getThreadId()).getBlockedTime());
            items.add(item);
        }
        info.setList(items);
        return info;
    }

    // ── GC ──
    private List<GcInfo> buildGcInfo() {
        List<GcInfo> list = new ArrayList<>();
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            GcInfo info = new GcInfo();
            info.setName(gc.getName());
            info.setCollectionCount(gc.getCollectionCount());
            info.setCollectionTimeMs(gc.getCollectionTime());
            list.add(info);
        }
        return list;
    }

    // ── 类加载 ──
    private ClassLoadingInfo buildClassLoadingInfo() {
        ClassLoadingMXBean cl = ManagementFactory.getClassLoadingMXBean();
        ClassLoadingInfo info = new ClassLoadingInfo();
        info.setLoaded(cl.getLoadedClassCount());
        info.setTotalLoaded(cl.getTotalLoadedClassCount());
        info.setUnloaded(cl.getUnloadedClassCount());
        return info;
    }

    // ── Actuator 健康检查 ──
    private HealthInfo buildHealthInfo() {
        var actHealth = healthEndpoint.health();
        HealthInfo info = new HealthInfo();
        info.setStatus(actHealth.getStatus().getCode());

        java.util.List<NodeInfoVO.HealthComponent> comps = new ArrayList<>();
        if (actHealth instanceof org.springframework.boot.actuate.health.CompositeHealth composite) {
            composite.getComponents().forEach((name, c) -> {
                NodeInfoVO.HealthComponent hc = new NodeInfoVO.HealthComponent();
                hc.setName(name);
                hc.setStatus(c.getStatus().getCode());
                if (c instanceof org.springframework.boot.actuate.health.Health healthDetail) {
                    hc.setDetails(healthDetail.getDetails());
                }
                comps.add(hc);
            });
        }
        info.setComponents(comps);
        return info;
    }
}

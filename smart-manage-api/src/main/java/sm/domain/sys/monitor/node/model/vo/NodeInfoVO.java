package sm.domain.sys.monitor.node.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 节点监控信息聚合 VO
 */
@Data
@Schema(description = "节点监控信息")
public class NodeInfoVO {

    @Schema(description = "JVM 运行时信息")
    private JvmInfo jvm;

    @Schema(description = "内存信息")
    private MemoryInfo memory;

    @Schema(description = "CPU 信息")
    private CpuInfo cpu;

    @Schema(description = "操作系统信息")
    private OsInfo os;

    @Schema(description = "磁盘信息")
    private DiskInfo disk;

    @Schema(description = "线程信息")
    private ThreadsInfo threads;

    @Schema(description = "GC 信息")
    private List<GcInfo> gc;

    @Schema(description = "类加载信息")
    private ClassLoadingInfo classLoading;

    @Schema(description = "健康检查信息")
    private HealthInfo health;

    @Data
    @Schema(description = "JVM 运行时")
    public static class JvmInfo {
        @Schema(description = "JDK 版本")
        private String version;
        @Schema(description = "JDK 厂商")
        private String vendor;
        @Schema(description = "JDK 安装目录")
        private String home;
        @Schema(description = "运行时名称")
        private String runtimeName;
        @Schema(description = "已运行时间（毫秒）")
        private long uptime;
        @Schema(description = "启动时间（ISO 格式）")
        private String startTime;
        @Schema(description = "启动参数")
        private List<String> inputArgs;
    }

    @Data
    @Schema(description = "内存信息")
    public static class MemoryInfo {
        @Schema(description = "堆内存")
        private MemorySegment heap;
        @Schema(description = "非堆内存")
        private MemorySegment nonHeap;
        @Schema(description = "内存池列表")
        private List<MemoryPoolInfo> pools;
    }

    @Data
    @Schema(description = "内存段")
    public static class MemorySegment {
        @Schema(description = "已使用（字节）")
        private long used;
        @Schema(description = "最大值（字节）")
        private long max;
        @Schema(description = "已提交（字节）")
        private long committed;
        @Schema(description = "初始值（字节）")
        private long init;
    }

    @Data
    @Schema(description = "内存池")
    public static class MemoryPoolInfo {
        @Schema(description = "名称")
        private String name;
        @Schema(description = "类型（HEAP/NON_HEAP）")
        private String type;
        @Schema(description = "已使用（字节）")
        private long used;
        @Schema(description = "最大值（字节）")
        private long max;
        @Schema(description = "已提交（字节）")
        private long committed;
    }

    @Data
    @Schema(description = "CPU 信息")
    public static class CpuInfo {
        @Schema(description = "CPU 型号")
        private String model;
        @Schema(description = "物理核心数")
        private int physicalCores;
        @Schema(description = "逻辑核心数")
        private int logicalCores;
        @Schema(description = "系统 CPU 使用率（0-1）")
        private double systemLoad;
        @Schema(description = "进程 CPU 使用率（0-1）")
        private double processCpuLoad;
    }

    @Data
    @Schema(description = "操作系统信息")
    public static class OsInfo {
        @Schema(description = "操作系统名称")
        private String name;
        @Schema(description = "版本")
        private String version;
        @Schema(description = "架构")
        private String arch;
    }

    @Data
    @Schema(description = "磁盘信息")
    public static class DiskInfo {
        @Schema(description = "盘符/挂载点")
        private String name;
        @Schema(description = "总容量（字节）")
        private long total;
        @Schema(description = "已使用（字节）")
        private long used;
        @Schema(description = "可用（字节）")
        private long free;
        @Schema(description = "使用率（0-1）")
        private double usagePercent;
    }

    @Data
    @Schema(description = "线程信息")
    public static class ThreadsInfo {
        @Schema(description = "当前线程总数")
        private int total;
        @Schema(description = "守护线程数")
        private int daemon;
        @Schema(description = "历史峰值线程数")
        private int peak;
        @Schema(description = "线程列表")
        private List<ThreadItem> list;
    }

    @Data
    @Schema(description = "线程项")
    public static class ThreadItem {
        @Schema(description = "线程 ID")
        private long id;
        @Schema(description = "线程名称")
        private String name;
        @Schema(description = "线程状态")
        private String state;
        @Schema(description = "CPU 时间（毫秒）")
        private long cpuTimeMs;
        @Schema(description = "阻塞时间（毫秒）")
        private long blockedTimeMs;
    }

    @Data
    @Schema(description = "GC 信息")
    public static class GcInfo {
        @Schema(description = "GC 收集器名称")
        private String name;
        @Schema(description = "累计收集次数")
        private long collectionCount;
        @Schema(description = "累计收集耗时（毫秒）")
        private long collectionTimeMs;
    }

    @Data
    @Schema(description = "类加载信息")
    public static class ClassLoadingInfo {
        @Schema(description = "当前已加载类数")
        private int loaded;
        @Schema(description = "累计已加载类数")
        private long totalLoaded;
        @Schema(description = "累计已卸载类数")
        private long unloaded;
    }

    @Data
    @Schema(description = "健康检查信息")
    public static class HealthInfo {
        @Schema(description = "整体状态：UP / DOWN")
        private String status;
        @Schema(description = "组件状态列表")
        private List<HealthComponent> components;
    }

    @Data
    @Schema(description = "健康组件")
    public static class HealthComponent {
        @Schema(description = "组件名称（如 db、diskSpace、redis）")
        private String name;
        @Schema(description = "组件状态：UP / DOWN / UNKNOWN")
        private String status;
        @Schema(description = "详细信息")
        private Object details;
    }
}

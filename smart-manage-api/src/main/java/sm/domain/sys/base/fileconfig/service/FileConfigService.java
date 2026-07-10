package sm.domain.sys.base.fileconfig.service;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;
import sm.domain.sys.base.fileconfig.model.entity.FileConfigEntity;
import sm.domain.sys.base.fileconfig.model.form.FileConfigListForm;
import sm.domain.sys.base.fileconfig.model.form.FileConfigSaveForm;
import sm.domain.sys.base.fileconfig.model.form.FtpTestForm;
import sm.domain.sys.base.fileconfig.model.vo.FileConfigDetailVO;
import sm.domain.sys.base.fileconfig.mapper.FileConfigMapper;
import sm.system.exception.BizException;
import sm.system.aop.log.BizLog;
import sm.system.response.PageData;
import sm.system.response.ResultEnum;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文件配置服务
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileConfigService {
    private final FileConfigMapper mapper;
    private final FileConfigTxService txService;

    public PageData<FileConfigDetailVO> listPage(FileConfigListForm form) {
        LambdaQueryWrapper<FileConfigEntity> qw = new LambdaQueryWrapper<FileConfigEntity>();
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String kw = form.getKeyword().trim();
            qw.like(FileConfigEntity::getStorageType, kw);
        }
        qw.orderByAsc(FileConfigEntity::getId);
        Page<FileConfigEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
        Page<FileConfigEntity> result = mapper.selectPage(page, qw);
        List<FileConfigDetailVO> vos = result.getRecords().stream().map(this::toDetailVo).collect(Collectors.toList());
        return PageData.of(result.getTotal(), form.getPageNum(), form.getPageSize(), vos);
    }

    public FileConfigEntity getById(Long id) {
        return mapper.selectById(id);
    }

    public FileConfigDetailVO getDetail(Long id) {
        if (id == null) {
            throw new BizException(ResultEnum.PARAM_ERROR, "文件配置ID不能为空");
        }
        FileConfigEntity entity = mapper.selectById(id);
        if (entity == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "文件配置不存在");
        }
        return toDetailVo(entity);
    }

    /** 获取活跃配置（Caffeine 本地缓存） */
    @Cached(cacheType = CacheType.LOCAL, name = "common", key = "'file:config'", expire = 30, timeUnit = TimeUnit.MINUTES)
    public FileConfigDetailVO getActiveConfig() {
        List<FileConfigEntity> entityList = mapper.selectList(null);
        return entityList.isEmpty() ? defaultConfig() : toDetailVo(entityList.get(0));
    }

    private FileConfigDetailVO defaultConfig() {
        FileConfigDetailVO vo = new FileConfigDetailVO();
        vo.setStorageType("LOCAL");
        vo.setLocalDir("E:/upload/");
        return vo;
    }

    private FileConfigDetailVO toDetailVo(FileConfigEntity entity) {
        FileConfigDetailVO vo = new FileConfigDetailVO();
        vo.setId(entity.getId());
        vo.setStorageType(entity.getStorageType());
        vo.setLocalDir(entity.getLocalDir());
        vo.setFtpHost(entity.getFtpHost());
        vo.setFtpPort(entity.getFtpPort());
        vo.setFtpUsername(entity.getFtpUsername());
        vo.setFtpPassword(entity.getFtpPassword());
        vo.setFtpDir(entity.getFtpDir());
        vo.setFtpPassiveMode(entity.getFtpPassiveMode());
        return vo;
    }

    @BizLog("保存文件存储配置")
    public Long save(FileConfigSaveForm form) {
        return txService.save(form);
    }

    @BizLog("删除文件存储配置")
    public void deleteById(Long id) {
        txService.deleteById(id);
    }

    /**
     * 使用前端当前填写的参数测试 FTP 连通性，不读取也不保存文件配置。
     */
    @BizLog(value = "测试FTP连接", saveRequest = false)
    public String testFtp(FtpTestForm form) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(form.getFtpHost(), form.getFtpPort());
            if (!ftpClient.login(form.getFtpUsername(), form.getFtpPassword())) {
                throw new BizException(ResultEnum.CONFIG_ERROR, "FTP 登录失败: " + ftpClient.getReplyString());
            }
            if (Boolean.TRUE.equals(form.getFtpPassiveMode())) {
                ftpClient.enterLocalPassiveMode();
            }
            if (form.getFtpDir() != null && !form.getFtpDir().isBlank()
                    && !ftpClient.changeWorkingDirectory(form.getFtpDir())) {
                throw new BizException(ResultEnum.CONFIG_ERROR, "FTP 目录切换失败: " + ftpClient.getReplyString());
            }
            ftpClient.logout();
            return "FTP 连接成功";
        } catch (BizException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BizException(ResultEnum.CONFIG_ERROR, "FTP 连接失败: " + exception.getMessage());
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (Exception exception) {
                    log.warn("关闭 FTP 测试连接失败", exception);
                }
            }
        }
    }
}

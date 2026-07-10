package sm.system.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sm.domain.sys.base.fileconfig.model.vo.FileConfigDetailVO;
import sm.domain.sys.base.fileconfig.service.FileConfigService;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/**
 * 文件存储服务工厂——根据配置返回对应实现
 *
 * @author Chekfu
 */
@Component
@RequiredArgsConstructor
public class FileStorageServiceFactory {

    private final LocalFileStorageService localFileStorageService;
    private final FtpFileStorageService ftpFileStorageService;
    private final FileConfigService fileConfigService;

    public FileStorageService getService() {
        FileConfigDetailVO config = fileConfigService.getActiveConfig();
        if ("FTP".equalsIgnoreCase(config.getStorageType())) {
            return ftpFileStorageService;
        }
        if ("LOCAL".equalsIgnoreCase(config.getStorageType())) {
            return localFileStorageService;
        }
        // 未知存储类型属于配置错误，禁止静默回退到本地存储。
        throw new BizException(ResultEnum.CONFIG_ERROR, "不支持的文件存储类型: " + config.getStorageType());
    }
}

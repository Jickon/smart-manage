package sm.system.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sm.cloud.sys.base.fileconfig.domain.vo.FileConfigDetailVO;
import sm.cloud.sys.base.fileconfig.service.FileConfigService;

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
        return localFileStorageService;
    }
}

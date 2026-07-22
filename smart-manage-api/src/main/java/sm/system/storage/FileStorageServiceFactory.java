package sm.system.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
    private final FileStorageConfigProvider configProvider;

    public FileStorageService getService() {
        FileStorageConfig config = configProvider.getFileStorageConfig();
        if ("FTP".equalsIgnoreCase(config.storageType())) {
            return ftpFileStorageService;
        }
        if ("LOCAL".equalsIgnoreCase(config.storageType())) {
            return localFileStorageService;
        }
        // 未知存储类型属于配置错误，禁止静默回退到本地存储。
        throw new BizException(ResultEnum.CONFIG_ERROR, "不支持的文件存储类型: " + config.storageType());
    }
}

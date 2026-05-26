package sm.system.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件存储服务接口
 *
 * @author Chekfu
 */
public interface FileStorageService {

    /** 存储文件到指定子目录（如 sys、biz/expense_report、other） */
    FileStoreResult store(String subDir, MultipartFile file) throws IOException;

    /** 存储临时文件（写入 temp 子目录） */
    FileStoreResult storeTemp(MultipartFile file) throws IOException;

    /** 将临时文件提升到目标子目录 */
    String promote(String tempPath, String targetSubDir) throws IOException;

    /** 删除文件 */
    void delete(String storedPath) throws IOException;

    /** 获取文件字节（下载用） */
    byte[] getBytes(String storedPath) throws IOException;

    /** 获取文件公开访问 URL */
    String getAccessUrl(String storedPath);

    /** 获取存储类型标识 */
    String getType();
}

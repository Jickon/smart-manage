export type FileStorageType = 'LOCAL' | 'FTP';

export interface FileConfigDetailVO {
  id?: string;
  storageType: FileStorageType;
  localDir?: string;
  ftpHost?: string;
  ftpPort?: number;
  ftpUsername?: string;
  ftpPassword?: string;
  ftpDir?: string;
  ftpPassiveMode?: boolean;
}

export type FileConfigSaveForm = FileConfigDetailVO;

export interface FtpTestForm {
  ftpHost?: string;
  ftpPort?: number;
  ftpUsername?: string;
  ftpPassword?: string;
  ftpDir?: string;
  ftpPassiveMode?: boolean;
}

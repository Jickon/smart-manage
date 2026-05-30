import request from '@/api/request';
import type { Result } from '@/types/api';
import type { FileConfigDetailVO, FileConfigSaveForm, FtpTestForm } from './types';

export const fileConfigApi = {
  active: () =>
    request.post<Result<FileConfigDetailVO>>('/sys/base/file-config/active').then((response) => response.data.data),

  save: (form: FileConfigSaveForm) =>
    request.post<Result<string>>('/sys/base/file-config/save', form).then((response) => response.data.data),

  testFtp: (form: FtpTestForm) =>
    request.post<Result<string>>('/sys/base/file-config/test-ftp', form).then((response) => response.data.data),
};

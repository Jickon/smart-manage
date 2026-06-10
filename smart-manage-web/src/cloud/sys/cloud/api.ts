import request from '@/api/request';
import type { PageResult, Result } from '@/types/api';
import type { CloudListForm, CloudListVO, CloudDetailVO, CloudSaveForm } from './types';

export const cloudApi = {
  listPage: (form: CloudListForm) =>
    request
      .post<Result<PageResult<CloudListVO>>>('/sys/base/cloud/listPage', form)
      .then((res) => res.data.data),

  detail: (id: string) =>
    request
      .post<Result<CloudDetailVO>>('/sys/base/cloud/detail', { id: Number(id) })
      .then((res) => res.data.data),

  save: (form: CloudSaveForm) =>
    request
      .post<Result<number>>('/sys/base/cloud/save', form)
      .then((res) => res.data.data),

  delete: (id: string) =>
    request
      .post<Result<string>>('/sys/base/cloud/delete', { id: Number(id) })
      .then((res) => res.data.data),
};

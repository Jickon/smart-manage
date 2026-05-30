import request from './request';
import type {
  CloudCreateNewDataVO,
  CloudDetailVO,
  CloudListForm,
  CloudListVO,
  CloudSaveForm,
  CloudSelectForm,
  CloudSelectVO,
  PageResult,
  Result,
} from '@/types/api';

export const cloudApi = {
  listPage: (form: CloudListForm) =>
    request
      .post<Result<PageResult<CloudListVO>>>('/sys/base/cloud/listPage', form)
      .then((response) => response.data.data),

  select: (form: CloudSelectForm) =>
    request
      .post<Result<PageResult<CloudSelectVO>>>('/sys/base/cloud/select', form)
      .then((response) => response.data.data),

  detail: (id: string) =>
    request.post<Result<CloudDetailVO>>('/sys/base/cloud/detail', { id }).then((response) => response.data.data),

  save: (form: CloudSaveForm) =>
    request.post<Result<string>>('/sys/base/cloud/save', form).then((response) => response.data.data),

  delete: (id: string) =>
    request.post<Result<string>>('/sys/base/cloud/delete', { id }).then((response) => response.data.data),

  createNewData: () =>
    request.get<Result<CloudCreateNewDataVO>>('/sys/base/cloud/createNewData').then((response) => response.data.data),
};

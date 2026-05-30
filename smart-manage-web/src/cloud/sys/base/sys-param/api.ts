import request from '@/api/request';
import type { PageResult, Result } from '@/types/api';
import type {
  SysParamCreateNewDataVO,
  SysParamDetailVO,
  SysParamListForm,
  SysParamListVO,
  SysParamSaveForm,
} from './types';

export const sysParamApi = {
  listPage: (form: SysParamListForm) =>
    request
      .post<Result<PageResult<SysParamListVO>>>('/sys/base/param/listPage', form)
      .then((response) => response.data.data),

  detail: (id: string) =>
    request.post<Result<SysParamDetailVO>>('/sys/base/param/detail', { id }).then((response) => response.data.data),

  save: (form: SysParamSaveForm) =>
    request.post<Result<string>>('/sys/base/param/save', form).then((response) => response.data.data),

  delete: (id: string) =>
    request.post<Result<string>>('/sys/base/param/delete', { id }).then((response) => response.data.data),

  createNewData: () =>
    request
      .get<Result<SysParamCreateNewDataVO>>('/sys/base/param/createNewData')
      .then((response) => response.data.data),
};

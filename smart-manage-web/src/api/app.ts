import request from './request';
import type {
  AppCreateNewDataVO,
  AppDetailVO,
  AppListForm,
  AppListVO,
  AppSaveForm,
  AppVO,
  CloudAppsVO,
  PageResult,
  Result,
} from '@/types/api';

export const appApi = {
  listPage: (form: AppListForm) =>
    request.post<Result<PageResult<AppListVO>>>('/sys/base/app/listPage', form).then((response) => response.data.data),

  detail: (id: string) =>
    request.post<Result<AppDetailVO>>('/sys/base/app/detail', { id }).then((response) => response.data.data),

  save: (form: AppSaveForm) =>
    request.post<Result<string>>('/sys/base/app/save', form).then((response) => response.data.data),

  delete: (id: string) =>
    request.post<Result<string>>('/sys/base/app/delete', { id }).then((response) => response.data.data),

  createNewData: () =>
    request.get<Result<AppCreateNewDataVO>>('/sys/base/app/createNewData').then((response) => response.data.data),

  apps: () => request.get<Result<CloudAppsVO[]>>('/sys/base/app/apps').then((response) => response.data.data),

  openByNumber: (number: string) =>
    request.post<Result<AppVO>>('/sys/base/app/openByNumber', { number }).then((response) => response.data.data),
};

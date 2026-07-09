import request from '@/api/request';
import type { PageData, Result } from '@/types/api';
import type {
  UserListForm,
  UserListVO,
  UserDetailVO,
  UserSaveForm,
  UserSaveWithRolesForm,
  UserRoleVO,
} from './types';

export const userApi = {
  listPage: (form: UserListForm) =>
    request
      .post<Result<PageData<UserListVO>>>('/sys/base/user/listPage', form)
      .then((res) => res.data.data),

  detail: (id: string) =>
    request
      .post<Result<UserDetailVO>>('/sys/base/user/detail', { id })
      .then((res) => res.data.data),

  save: (form: UserSaveForm) =>
    request.post<Result<string>>('/sys/base/user/save', form).then((res) => res.data.data),

  /** 鑱氬悎淇濆瓨锛氱敤鎴峰熀鏈俊鎭?+ 瑙掕壊鍒嗛厤锛屼簨鍔″唴瀹屾垚 */
  saveWithRoles: (form: UserSaveWithRolesForm) =>
    request.post<Result<string>>('/sys/base/user/saveWithRoles', form).then((res) => res.data.data),

  delete: (id: string) =>
    request.post<Result<string>>('/sys/base/user/delete', { id }).then((res) => res.data.data),
};

export const userRoleApi = {
  /** 鑾峰彇鐢ㄦ埛鍦ㄥ綋鍓嶇粍缁囦笅鐨勮鑹插垪琛紝缁勭粐鐢辨湇鍔＄涓婁笅鏂囧喅瀹?*/
  listByCurrentOrgUser: (userId: string) =>
    request
      .post<Result<UserRoleVO[]>>('/sys/base/userrole/listByCurrentOrgUser', { userId })
      .then((res) => res.data.data),
};

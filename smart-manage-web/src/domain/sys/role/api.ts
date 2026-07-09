import request from '@/api/request';
import type { PageData, Result } from '@/types/api';
import type {
  RoleListForm,
  RoleListVO,
  RoleListAllVO,
  RoleDetailVO,
  RoleSelectVO,
  RoleSaveWithPermsForm,
  RolePermsVO,
} from './types';

export const roleApi = {
  listPage: (form: RoleListForm) =>
    request
      .post<Result<PageData<RoleListVO>>>('/sys/base/role/listPage', form)
      .then((res) => res.data.data),

  /** 鍏ㄩ噺瑙掕壊锛堜笉鍒嗛〉锛夛紝鐢ㄤ簬鐢ㄦ埛瑙掕壊鍒嗛厤闈㈡澘 */
  listAll: () =>
    request
      .post<Result<RoleListAllVO[]>>('/sys/base/role/listAll', {})
      .then((res) => res.data.data),

  select: (form: { pageNum: number; pageSize: number; keyword?: string }) =>
    request
      .post<Result<PageData<RoleSelectVO>>>('/sys/base/role/select', form)
      .then((res) => res.data.data),

  detail: (id: string) =>
    request
      .post<Result<RoleDetailVO>>('/sys/base/role/detail', { id })
      .then((res) => res.data.data),

  /** 鑱氬悎淇濆瓨锛氳鑹插熀鏈俊鎭?+ 鏉冮檺鍒嗛厤锛屼簨鍔″唴瀹屾垚 */
  saveWithPerms: (form: RoleSaveWithPermsForm) =>
    request.post<Result<string>>('/sys/base/role/saveWithPerms', form).then((res) => res.data.data),

  delete: (id: string) =>
    request.post<Result<string>>('/sys/base/role/delete', { id }).then((res) => res.data.data),
};

export const rolePermsApi = {
  /** 鑾峰彇鎸囧畾瑙掕壊鐨勬潈闄愬垪琛?鈥?roleId 浠ュ瓧绗︿覆浼犻€?*/
  listByRole: (roleId: string) =>
    request
      .post<Result<RolePermsVO[]>>('/sys/base/roleperms/listByRole', { roleId })
      .then((res) => res.data.data),
};

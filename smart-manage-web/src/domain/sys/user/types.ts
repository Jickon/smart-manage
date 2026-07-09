import type { PageForm } from '@/types/api';

/** 用户列表查询 */
export interface UserListForm extends PageForm {
  keyword?: string;
}

/** 用户列表项 */
export interface UserListVO {
  id: string;
  username: string;
  nickname: string;
  avatar: string;
}

/** 用户详情 — 所有 ID 均为字符串 */
export interface UserDetailVO {
  id: string;
  username: string;
  nickname: string;
  avatar: string;
  themeColor: string;
  email?: string;
  phone?: string;
  enableFlag?: boolean;
  createTime?: string;
  updateTime?: string;
}

/** 用户保存 — ID 均以字符串传递 */
export interface UserSaveForm {
  id?: string;
  username: string;
  password?: string;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  themeColor?: string;
  enableFlag?: boolean;
}

/** 用户+角色聚合保存 */
export interface UserSaveWithRolesForm {
  id?: string;
  username: string;
  password?: string;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  themeColor?: string;
  enableFlag?: boolean;
  roleIds: string[];
}

/** 用户角色项 */
export interface UserRoleVO {
  id: string;
  userId: string;
  orgId: string;
  roleId: string;
  roleName: string;
  roleNumber: string;
}

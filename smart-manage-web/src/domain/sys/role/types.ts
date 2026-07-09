import type { PageForm } from '@/types/api';

/** 角色列表查询 */
export interface RoleListForm extends PageForm {
  keyword?: string;
}

/** 角色列表项 */
export interface RoleListVO {
  id: string;
  name: string;
  number: string;
  remark: string;
}

/** 角色详情 */
export interface RoleDetailVO {
  id: string;
  name: string;
  number: string;
  createTime?: string;
  updateTime?: string;
}

/** 角色保存 */
export interface RoleSaveForm {
  id?: string;
  name: string;
  number: string;
  remark?: string;
}

/** 角色+权限聚合保存 — ID 均以字符串传递 */
export interface RoleSaveWithPermsForm {
  id?: string;
  name: string;
  number: string;
  remark?: string;
  permissionIds: string[];
}

/** 角色全量列表（不分页） */
export interface RoleListAllVO {
  id: string;
  number: string;
  name: string;
}

/** 角色选择器列表项 */
export interface RoleSelectVO {
  id: string;
  number: string;
  name: string;
}

/** 角色权限视图 */
export interface RolePermsVO {
  id: string;
  roleId: string;
  permissionId: string;
  permName: string;
  permNumber: string;
}

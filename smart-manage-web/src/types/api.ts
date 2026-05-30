/** 后端统一响应体 */
export interface Result<T = unknown> {
  code: number;
  msg: string;
  data: T;
  traceId: string;
}

/** 分页结果 */
export interface PageResult<T> {
  total: number;
  records: T[];
}

export interface PageForm {
  pageNum: number;
  pageSize: number;
}

export interface IdForm {
  id: string;
}

/** 用户信息 */
export interface UserInfoVO {
  id: string;
  username: string;
  nickname: string;
  avatar: string;
  themeColor: string;
}

/** 应用信息 */
export interface AppVO {
  id: string;
  cloudNumber: string;
  number: string;
  name: string;
  icon: string;
  iconColor: string;
  seq: number;
  description: string;
}

/** 云及其下应用 */
export interface CloudAppsVO {
  id: string;
  name: string;
  number: string;
  seq: number;
  appList: AppVO[];
}

/** 菜单节点 */
export interface MenuVO {
  name: string;
  path: string;
  component: string;
  icon: string;
  level: number;
  routes: MenuVO[];
}

export interface CloudListForm extends PageForm {
  keyword?: string;
  enableFlag?: boolean;
}

export interface CloudSelectForm extends PageForm {
  keyword?: string;
  enableFlag?: boolean;
}

export interface CloudListVO {
  id: string;
  name: string;
  number: string;
  seq: number;
  enableFlag: boolean;
  createTime?: string;
  updateTime?: string;
}

export interface CloudDetailVO extends CloudListVO {
  createUser?: string;
  updateUser?: string;
}

export interface CloudCreateNewDataVO {
  seq: number;
  enableFlag: boolean;
}

export interface CloudSaveForm {
  id?: string;
  name: string;
  number: string;
  seq?: number;
  enableFlag?: boolean;
}

export interface CloudSelectVO {
  id: string;
  name: string;
  number: string;
  enableFlag: boolean;
}

export interface AppListForm extends PageForm {
  cloudId?: string;
  keyword?: string;
}

export interface AppListVO {
  id: string;
  name: string;
  number: string;
  icon?: string;
  iconColor?: string;
  seq: number;
  description?: string;
  cloudId: string;
  cloudName?: string;
  enableFlag: boolean;
  createTime?: string;
  updateTime?: string;
}

export interface AppCloudRefVO {
  id: string;
  number: string;
  name: string;
}

export interface AppDetailVO {
  id: string;
  name: string;
  number: string;
  icon?: string;
  iconColor?: string;
  seq: number;
  description?: string;
  cloud?: AppCloudRefVO | null;
  enableFlag: boolean;
  createTime?: string;
  updateTime?: string;
}

export interface AppCreateNewDataVO {
  seq: number;
  enableFlag: boolean;
}

export interface AppSaveForm {
  id?: string;
  name: string;
  number: string;
  icon?: string;
  iconColor?: string;
  seq?: number;
  description?: string;
  cloudId: string;
  enableFlag?: boolean;
}

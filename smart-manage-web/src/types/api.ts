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

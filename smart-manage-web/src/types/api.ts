/** 后端统一响应体 */
export interface Result<T = unknown> {
  code: number;
  msg: string;
  data: T;
  traceId: string;
}

/** 分页响应 */
export interface PageResult<T> {
  total: number;
  records: T[];
}

/** 分页入参 */
export interface PageForm {
  pageNum: number;
  pageSize: number;
}

/** ID 入参 */
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

/** 菜单节点 */
export interface MenuVO {
  name: string;
  path: string;
  component: string;
  icon: string;
  level: number;
  routes: MenuVO[];
}

export interface Result<T = unknown> {
  code: number;
  msg: string;
  data: T;
  traceId: string;
}

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

export interface UserInfoVO {
  id: string;
  username: string;
  nickname: string;
  avatar: string;
  themeColor: string;
}

export interface MenuVO {
  name: string;
  path: string;
  component: string;
  icon: string;
  level: number;
  routes: MenuVO[];
}

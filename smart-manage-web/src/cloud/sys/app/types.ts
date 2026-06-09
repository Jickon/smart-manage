/** 应用 VO */
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

/** 按领域分组的应用列表 */
export interface CloudAppsVO {
  id: string;
  name: string;
  number: string;
  seq: number;
  appList: AppVO[];
}

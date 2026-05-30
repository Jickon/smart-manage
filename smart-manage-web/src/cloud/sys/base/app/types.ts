import type { PageForm } from '@/types/api';

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

export interface CloudAppsVO {
  id: string;
  name: string;
  number: string;
  seq: number;
  appList: AppVO[];
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

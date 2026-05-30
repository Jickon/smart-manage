import type { PageForm } from '@/types/api';

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

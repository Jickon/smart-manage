import type { PageForm } from '@/types/api';

export interface CloudListForm extends PageForm {
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

/** 云详情 */
export interface CloudDetailVO {
  id: string;
  name: string;
  number: string;
  seq: number;
  enableFlag: boolean;
  createTime?: string;
  updateTime?: string;
  createUser?: string;
  updateUser?: string;
}

/** 云保存 */
export interface CloudSaveForm {
  id?: number;
  name: string;
  number: string;
  seq: number;
  enableFlag: boolean;
}

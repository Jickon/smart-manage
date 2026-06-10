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

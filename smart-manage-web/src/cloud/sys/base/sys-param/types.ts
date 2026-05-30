import type { PageForm } from '@/types/api';

export interface SysParamListForm extends PageForm {
  keyword?: string;
}

export interface SysParamVO {
  id: string;
  number: string;
  name: string;
  value?: string;
  remark?: string;
  isSystem?: boolean;
}

export type SysParamListVO = SysParamVO;
export type SysParamDetailVO = SysParamVO;

export interface SysParamCreateNewDataVO {
  number?: string;
  name?: string;
  value?: string;
  remark?: string;
}

export interface SysParamSaveForm {
  id?: string;
  number: string;
  name: string;
  value?: string;
  remark?: string;
}

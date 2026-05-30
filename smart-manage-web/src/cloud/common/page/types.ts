import type { ComponentType, LazyExoticComponent, ReactNode } from 'react';
import type { SplitProps } from '@arco-design/web-react/es/ResizeBox/interface';

export type PageType = 'LIST' | 'EDIT' | 'CUSTOM';

export enum OperationType {
  ADDNEW = 'ADDNEW',
  EDIT = 'EDIT',
  VIEW = 'VIEW',
}

export enum BillStatus {
  SAVED = 'A',
  SUBMITTED = 'B',
  AUDITED = 'C',
  CLOSED = 'D',
}

export interface PageComponentProps {
  appNumber: string;
  componentKey: string;
  tabKey: string;
  title: string;
  operationType?: OperationType;
  billId?: string;
  temporary?: boolean;
}

export interface PageRegistration {
  componentKey: string;
  pageType: PageType;
  component: ComponentType<PageComponentProps> | LazyExoticComponent<ComponentType<PageComponentProps>>;
}

export interface CommonPageProps extends PageComponentProps {
  actions?: ReactNode;
  children?: ReactNode;
}

export interface ListPageProps extends CommonPageProps {
  filterContent?: ReactNode;
  filterSummary?: ReactNode;
  toolbarActions?: ReactNode;
  table?: ReactNode;
  treePanel?: ReactNode;
  treePanelSize?: number | string;
  treePanelMin?: number | string;
  treePanelMax?: number | string;
  treeSplitDirection?: SplitProps['direction'];
  total?: number;
  selectedCount?: number;
  allSelected?: boolean;
  quickSearchPlaceholder?: string;
  pageNum?: number;
  pageSize?: number;
  onAddNew?: () => void;
  onDelete?: () => void;
  onRefresh?: () => void;
  onQuickSearch?: (value: string) => void;
  onToggleSelectAll?: (checked: boolean) => void;
  onPageChange?: (pageNum: number, pageSize: number) => void;
}

export interface EditPageSection {
  key: string;
  title: string;
  content: ReactNode;
  extra?: ReactNode;
  defaultCollapsed?: boolean;
}

export interface EditPageProps extends CommonPageProps {
  sections?: EditPageSection[];
  attachment?: ReactNode;
  toolbarActions?: ReactNode;
  onSave?: () => void;
  onSubmit?: () => void;
  onCancel?: () => void;
}

import { Button, Space } from 'antd';
import { PlusOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ReactNode } from 'react';
import ListFilterBar from './ListFilterBar';
import ListTableShell from './ListTableShell';
import './ListPage.css';

interface ListPageProps {
  title: string;
  filterContent?: ReactNode;
  filterSummary?: ReactNode;
  toolbarActions?: ReactNode;
  table?: ReactNode;
  treePanel?: ReactNode;
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
  children?: ReactNode;
}

/** 通用列表页框架 */
const ListPage = ({
  title,
  filterContent,
  filterSummary,
  toolbarActions,
  table,
  treePanel,
  total,
  selectedCount,
  allSelected,
  quickSearchPlaceholder,
  pageNum,
  pageSize,
  onAddNew,
  onDelete,
  onRefresh,
  onQuickSearch,
  onToggleSelectAll,
  onPageChange,
  children,
}: ListPageProps) => {
  return (
    <section className="sm-common-page sm-list-page">
      {/* 顶部：过滤区 + 工具栏 */}
      <div className="sm-list-top">
        <ListFilterBar
          title={title}
          filterContent={filterContent}
          filterSummary={filterSummary}
          quickSearchPlaceholder={quickSearchPlaceholder}
          onQuickSearch={onQuickSearch}
        />
        <div className="sm-list-toolbar">
          <Space>
            {onAddNew && (
              <Button type="primary" icon={<PlusOutlined />} onClick={onAddNew}>
                新增
              </Button>
            )}
            {onDelete && (
              <Button danger icon={<DeleteOutlined />} onClick={onDelete}>
                删除
              </Button>
            )}
            {onRefresh && (
              <Button icon={<ReloadOutlined />} onClick={onRefresh}>
                刷新
              </Button>
            )}
            {toolbarActions}
          </Space>
        </div>
      </div>

      {/* 主体：表格区 */}
      <div className="sm-list-main">
        <ListTableShell
          table={table ?? children}
          total={total}
          selectedCount={selectedCount}
          allSelected={allSelected}
          pageNum={pageNum}
          pageSize={pageSize}
          onToggleSelectAll={onToggleSelectAll}
          onPageChange={onPageChange}
          treePanel={treePanel}
        />
      </div>
    </section>
  );
};

export default ListPage;

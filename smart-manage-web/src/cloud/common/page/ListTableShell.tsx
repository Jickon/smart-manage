import type { ReactNode } from 'react';
import { Checkbox, Empty, Pagination } from 'antd';

interface ListTableShellProps {
  table?: ReactNode;
  total?: number;
  selectedCount?: number;
  allSelected?: boolean;
  pageNum?: number;
  pageSize?: number;
  onToggleSelectAll?: (checked: boolean) => void;
  onPageChange?: (pageNum: number, pageSize: number) => void;
  /** 左侧树面板（后续扩展左树右表布局） */
  treePanel?: ReactNode;
}

const ListTableShell = ({
  table,
  total = 0,
  selectedCount = 0,
  allSelected,
  pageNum = 1,
  pageSize = 20,
  onToggleSelectAll,
  onPageChange,
  treePanel,
}: ListTableShellProps) => {
  const rightContent = (
    <div className="sm-list-table-content">
      {/* 元信息栏：总数 + 全选 + 分页 */}
      <div className="sm-list-table-meta">
        <div className="sm-list-table-count">
          <span>共 {total} 条</span>
          <Checkbox
            checked={allSelected}
            onChange={(event) => onToggleSelectAll?.(event.target.checked)}
          >
            {allSelected ? '取消选择' : '选择全部'}
          </Checkbox>
          {selectedCount > 0 && <span>已选 {selectedCount} 条</span>}
        </div>
        <Pagination
          size="small"
          showSizeChanger
          current={pageNum}
          pageSize={pageSize}
          total={total}
          showTotal={(t) => `共 ${t} 条`}
          onChange={(nextPage, nextSize) => onPageChange?.(nextPage, nextSize)}
        />
      </div>
      {/* 表格主体 */}
      <div className="sm-list-table-body">{table ?? <Empty description="暂无列表配置" />}</div>
    </div>
  );

  return (
    <div className="sm-list-table-shell">
      {treePanel ? (
        <div className="sm-list-table-split">
          <aside className="sm-list-tree-panel">{treePanel}</aside>
          {rightContent}
        </div>
      ) : (
        rightContent
      )}
    </div>
  );
};

export default ListTableShell;

import { Checkbox, Empty, Pagination } from '@arco-design/web-react';
import type { ReactNode } from 'react';

interface ListTableShellProps {
  table?: ReactNode;
  total?: number;
  selectedCount?: number;
  allSelected?: boolean;
  pageNum?: number;
  pageSize?: number;
  onToggleSelectAll?: (checked: boolean) => void;
  onPageChange?: (pageNum: number, pageSize: number) => void;
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
}: ListTableShellProps) => {
  return (
    <div className="sm-list-table-shell">
      <div className="sm-list-table-meta">
        <div className="sm-list-table-count">
          <span>共 {total} 条</span>
          <Checkbox checked={allSelected} onChange={(checked) => onToggleSelectAll?.(Boolean(checked))}>
            {allSelected ? '取消选择' : '选择全部'}
          </Checkbox>
          {selectedCount > 0 && <span>已选 {selectedCount} 条</span>}
        </div>
        <Pagination
          sizeCanChange
          current={pageNum}
          pageSize={pageSize}
          total={total}
          size="small"
          onChange={(nextPageNum, nextPageSize) => onPageChange?.(nextPageNum, nextPageSize)}
        />
      </div>
      <div className="sm-list-table-body">{table ?? <Empty description="暂无列表配置" />}</div>
    </div>
  );
};

export default ListTableShell;

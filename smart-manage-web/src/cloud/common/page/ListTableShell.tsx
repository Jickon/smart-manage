import { Checkbox, Empty, Pagination, ResizeBox } from '@arco-design/web-react';
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
  /** 左侧树面板（有值时内部左右分割：左树，右 meta+body） */
  treePanel?: ReactNode;
  treePanelSize?: number | string;
  treePanelMin?: number | string;
  treePanelMax?: number | string;
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
  treePanelSize = '270px',
  treePanelMin = '220px',
  treePanelMax = '420px',
}: ListTableShellProps) => {
  const rightContent = (
    <div className="sm-list-table-content">
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

  return (
    <div className="sm-list-table-shell">
      {treePanel ? (
        <ResizeBox.Split
          className="sm-list-table-split"
          direction="horizontal"
          size={treePanelSize}
          min={treePanelMin}
          max={treePanelMax}
          panes={[<aside className="sm-list-tree-panel">{treePanel}</aside>, rightContent]}
        />
      ) : (
        rightContent
      )}
    </div>
  );
};

export default ListTableShell;

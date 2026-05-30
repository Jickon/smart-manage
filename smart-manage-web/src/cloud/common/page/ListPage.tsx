import { Button, ResizeBox } from '@arco-design/web-react';
import ListFilterBar from './ListFilterBar';
import ListTableShell from './ListTableShell';
import type { ListPageProps } from '@/cloud/common/page/types';

const ListPage = ({
  title,
  filterContent,
  filterSummary,
  toolbarActions,
  table,
  treePanel,
  treePanelSize = '270px',
  treePanelMin = '220px',
  treePanelMax = '420px',
  treeSplitDirection = 'horizontal',
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
  const mainContent = (
    <div className="sm-list-main">
      <ListFilterBar
        title={title}
        filterContent={filterContent}
        filterSummary={filterSummary}
        quickSearchPlaceholder={quickSearchPlaceholder}
        onQuickSearch={onQuickSearch}
      />
      <div className="sm-list-toolbar">
        <Button type="primary" onClick={onAddNew}>
          新增
        </Button>
        <Button type="primary" onClick={onDelete}>
          删除
        </Button>
        <Button type="primary" onClick={onRefresh}>
          刷新
        </Button>
        {toolbarActions}
      </div>
      <ListTableShell
        table={table ?? children}
        total={total}
        selectedCount={selectedCount}
        allSelected={allSelected}
        pageNum={pageNum}
        pageSize={pageSize}
        onToggleSelectAll={onToggleSelectAll}
        onPageChange={onPageChange}
      />
    </div>
  );

  return (
    <section className="sm-common-page sm-list-page">
      {treePanel ? (
        <ResizeBox.Split
          className="sm-list-split"
          direction={treeSplitDirection}
          size={treePanelSize}
          min={treePanelMin}
          max={treePanelMax}
          panes={[<aside className="sm-list-tree-panel">{treePanel}</aside>, mainContent]}
        />
      ) : (
        mainContent
      )}
    </section>
  );
};

export default ListPage;

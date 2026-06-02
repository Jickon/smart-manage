import {Button} from '@arco-design/web-react';
import ListFilterBar from './ListFilterBar';
import ListTableShell from './ListTableShell';
import type {ListPageProps} from '@/cloud/common/page/types';

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
      <div className="sm-list-top">
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
      </div>
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
          treePanelSize={treePanelSize}
          treePanelMin={treePanelMin}
          treePanelMax={treePanelMax}
        />
      </div>
    </section>
  );
};

export default ListPage;

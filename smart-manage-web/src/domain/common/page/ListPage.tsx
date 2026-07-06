import type { ReactNode } from 'react';
import { useMemo } from 'react';
import { Button, Result, Space, Spin, Table } from 'antd';
import type { ColumnsType, TableRowSelection } from 'antd/es/table/interface';
import ListFilterBar from './ListFilterBar';
import ListTableShell from './ListTableShell';
import './ListPage.css';

interface ListPageProps<T> {
  title: string;
  /** 过滤区内容 */
  filterContent?: ReactNode;
  /** 过滤摘要文案 */
  filterSummary?: ReactNode;
  /** 工具栏额外操作 */
  toolbarActions?: ReactNode;
  /** 左侧树面板（左树右表布局） */
  treePanel?: ReactNode;
  /** 是否加载中 */
  loading?: boolean;
  /** 是否请求出错 */
  error?: Error | null;
  /** 手动重试 */
  onRetry?: () => void;
  total?: number;
  quickSearchPlaceholder?: string;
  pageNum?: number;
  pageSize?: number;
  onAddNew?: () => void;
  onDelete?: () => void;
  onRefresh?: () => void;
  onQuickSearch?: (value: string) => void;
  onPageChange?: (pageNum: number, pageSize: number) => void;

  /** Table — 行 key */
  rowKey: string | ((record: T) => string);
  /** Table — 列定义（不含勾选列和序号列，由 ListPage 自动添加） */
  columns: ColumnsType<T>;
  /** Table — 数据源 */
  dataSource: T[];
  /** 勾选模式：不传则不显示勾选列 */
  selectMode?: 'checkbox' | 'radio';
  /** 受控选中 key */
  selectedRowKeys?: React.Key[];
  /** 选中变更 */
  onSelectChange?: (keys: React.Key[]) => void;
}

/**
 * 通用列表页框架。
 *
 * 自动注入：
 * - 序号列（`#` 表头，跨页递增）
 * - 勾选列（checkbox / radio，由 selectMode 控制）
 * - Table size="small" 紧凑模式
 * - 加载中 / 错误 / 空数据四态
 */
function ListPage<T>({
  title,
  filterContent,
  filterSummary,
  toolbarActions,
  treePanel,
  loading = false,
  error = null,
  onRetry,
  total,
  quickSearchPlaceholder,
  pageNum = 1,
  pageSize = 20,
  onAddNew,
  onDelete,
  onRefresh,
  onQuickSearch,
  onPageChange,
  rowKey,
  columns,
  dataSource,
  selectMode,
  selectedRowKeys,
  onSelectChange,
}: ListPageProps<T>) {
  const rowSelection: TableRowSelection<T> | undefined = useMemo(
    () =>
      selectMode
        ? {
            type: selectMode,
            selectedRowKeys,
            onChange: (keys) => onSelectChange?.(keys),
            columnWidth: 36,
          }
        : undefined,
    [selectMode, selectedRowKeys, onSelectChange],
  );

  // 注入序号列 + 业务列（pageNum/pageSize 变化时更新序号公式）
  const fullColumns: ColumnsType<T> = useMemo(
    () => [
      {
        title: '#',
        width: 44,
        align: 'center',
        fixed: 'left' as const,
        render: (_text, _record, index) => (pageNum - 1) * pageSize + index + 1,
      },
      ...columns,
    ],
    [columns, pageNum, pageSize],
  );

  // 错误态
  if (error) {
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
        </div>
        <div className="sm-list-main">
          <Result
            status="error"
            title="加载失败"
            subTitle={error.message || '请检查网络连接后重试'}
            extra={
              onRetry && (
                <Button type="primary" onClick={onRetry}>
                  重试
                </Button>
              )
            }
          />
        </div>
      </section>
    );
  }

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
          <Space size="medium">
            {onAddNew && (
              <Button type="primary" onClick={onAddNew}>
                新增
              </Button>
            )}
            {onDelete && (
              <Button danger onClick={onDelete}>
                删除
              </Button>
            )}
            {onRefresh && (
              <Button type="primary" onClick={onRefresh}>
                刷新
              </Button>
            )}
            {toolbarActions}
          </Space>
        </div>
      </div>

      <div className="sm-list-main">
        <Spin spinning={loading}>
          <ListTableShell
            table={
              <Table<T>
                className="sm-list-table"
                rowKey={rowKey}
                rowSelection={rowSelection}
                columns={fullColumns}
                dataSource={dataSource}
                size="small"
                pagination={false}
                sticky
                scroll={{ x: 'max-content', y: 1 }}
              />
            }
            total={total}
            selectedCount={selectedRowKeys?.length ?? 0}
            pageNum={pageNum}
            pageSize={pageSize}
            onPageChange={onPageChange}
            treePanel={treePanel}
          />
        </Spin>
      </div>
    </section>
  );
}

export default ListPage;

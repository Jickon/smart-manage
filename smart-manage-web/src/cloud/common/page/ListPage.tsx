import { Button, Space, Result, Spin } from 'antd';
import { PlusOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ReactNode } from 'react';
import ListFilterBar from './ListFilterBar';
import ListTableShell from './ListTableShell';
import './ListPage.css';

interface ListPageProps {
  title: string;
  /** 过滤区内容 */
  filterContent?: ReactNode;
  /** 过滤摘要文案，如 "关键字：xxx" */
  filterSummary?: ReactNode;
  /** 工具栏额外操作 */
  toolbarActions?: ReactNode;
  /** 表格组件（通常直接传入 antd Table） */
  table?: ReactNode;
  /** 左侧树面板（左树右表布局） */
  treePanel?: ReactNode;
  /** 是否加载中 */
  loading?: boolean;
  /** 是否请求出错 */
  error?: Error | null;
  /** 手动重试（TanStack Query 的 refetch） */
  onRetry?: () => void;
  total?: number;
  selectedCount?: number;
  quickSearchPlaceholder?: string;
  pageNum?: number;
  pageSize?: number;
  onAddNew?: () => void;
  onDelete?: () => void;
  onRefresh?: () => void;
  onQuickSearch?: (value: string) => void;
  onPageChange?: (pageNum: number, pageSize: number) => void;
  children?: ReactNode;
}

/**
 * 通用列表页框架。
 *
 * 统一处理四态：
 * - 首次加载中：Spin
 * - 请求失败：Result error + 重试按钮
 * - 成功空数据：正常渲染表格区（表格自行展示 Empty）
 * - 成功有数据：正常渲染
 */
const ListPage = ({
  title,
  filterContent,
  filterSummary,
  toolbarActions,
  table,
  treePanel,
  loading = false,
  error = null,
  onRetry,
  total,
  selectedCount,
  quickSearchPlaceholder,
  pageNum,
  pageSize,
  onAddNew,
  onDelete,
  onRefresh,
  onQuickSearch,
  onPageChange,
  children,
}: ListPageProps) => {
  // 错误态 — 显示错误信息并提供重试按钮
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
                <Button type="primary" icon={<ReloadOutlined />} onClick={onRetry}>
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

      {/* 主体：加载遮罩 + 表格区 */}
      <div className="sm-list-main">
        <Spin spinning={loading}>
          <ListTableShell
            table={table ?? children}
            total={total}
            selectedCount={selectedCount}
            pageNum={pageNum}
            pageSize={pageSize}
            onPageChange={onPageChange}
            treePanel={treePanel}
          />
        </Spin>
      </div>
    </section>
  );
};

export default ListPage;

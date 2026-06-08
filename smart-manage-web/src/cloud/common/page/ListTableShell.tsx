import { useCallback, useLayoutEffect, useRef, useState } from 'react';
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
  const bodyRef = useRef<HTMLDivElement>(null);
  const [bodyHeight, setBodyHeight] = useState(0);
  // 动态测量 Arco 表头实际高度，避免硬编码导致底部留白
  const [headerHeight, setHeaderHeight] = useState<number | null>(null);

  const measureBody = useCallback(() => {
    const el = bodyRef.current;
    if (!el) return;
    setBodyHeight(el.clientHeight);

    // 测量 Arco 表头的真实渲染高度（仅当固定表头 DOM 存在时）
    const headerEl = el.querySelector<HTMLElement>('.arco-table-header');
    if (headerEl) {
      const measured = headerEl.clientHeight;
      if (measured > 0) {
        setHeaderHeight(measured);
      }
    }
  }, []);

  useLayoutEffect(() => {
    measureBody();
    const observer = new ResizeObserver(measureBody);
    const el = bodyRef.current;
    if (el) observer.observe(el);
    return () => observer.disconnect();
  }, [measureBody]);

  // 表头高度：优先使用实测值，未测量到时回退 47px（Arco 默认单行表头约 41px + 余量）
  const effectiveHeaderHeight = headerHeight ?? 47;

  // 将动态计算的高度注入 table 的 scroll.y
  const tableWithScrollY =
    bodyHeight > 0 && table && typeof table === 'object' && 'props' in table
      ? (() => {
          const element = table as React.ReactElement<{ scroll?: Record<string, unknown> }>;
          const existingScroll = (element.props as Record<string, unknown>).scroll as Record<string, unknown> | undefined;
          return {
            ...element,
            props: {
              ...(element.props as Record<string, unknown>),
              scroll: { ...existingScroll, y: bodyHeight - effectiveHeaderHeight },
            },
          } as React.ReactElement;
        })()
      : table;

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
      <div
        className="sm-list-table-body"
        ref={bodyRef}
        style={
          bodyHeight > 0
            ? ({ '--sm-table-body-height': `${bodyHeight - effectiveHeaderHeight}px` } as React.CSSProperties)
            : undefined
        }
      >
        {tableWithScrollY ?? <Empty description="暂无列表配置" />}
      </div>
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

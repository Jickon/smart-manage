import { useState, useCallback, useMemo } from 'react';
import type { ReactNode } from 'react';
import { Modal, Button, Input, Table, Tree, Pagination, Spin, Empty, Splitter } from 'antd';
import { SearchOutlined, CloseOutlined } from '@ant-design/icons';
import type { ColumnsType, TableRowSelection } from 'antd/es/table/interface';
import { useRefSelectorQuery } from './useRefSelectorQuery';
import type { RefSelectorFetchFn } from './useRefSelectorQuery';
import './RefSelector.css';

// ============================================================
// 类型定义
// ============================================================

/** RefSelector 选择模式 */
type RefSelectorMode = 'default' | 'multiple' | 'tree-table';

/** 表格列定义 */
interface RefSelectorColumn<T> {
  title: string;
  dataIndex: string;
  width?: number | string;
  render?: (text: unknown, record: T, index: number) => ReactNode;
}

/** 字段名映射：key 用于 rowKey，label 用于备用文本 */
interface RefSelectorFieldNames {
  key: string;
  label: string;
}

/** RefSelector Props */
interface RefSelectorProps<T extends Record<string, unknown>> {
  /** 受控值（Form.Item 注入），单选为 T，多选为 T[] */
  value?: T | T[] | null;
  onChange?: (value: T | T[] | null) => void;

  /** 显示渲染函数，用于触发器展示 */
  displayRender: (record: T) => ReactNode;
  /** 字段名映射 */
  fieldNames: RefSelectorFieldNames;
  placeholder?: string;
  disabled?: boolean;

  /** 表格数据获取函数 */
  fetchFn: RefSelectorFetchFn<T>;
  /** 表格列定义（不含序号列和选择列，组件自动注入） */
  columns: RefSelectorColumn<T>[];

  /** 选择模式，默认单选 */
  mode?: RefSelectorMode;
  /** Modal 标题 */
  modalTitle: string;
  /** 每页条数，默认 20 */
  pageSize?: number;

  /** 树表模式：树形数据 */
  treeData?: Record<string, unknown>[];
  /** 树表模式：树字段映射 */
  treeFieldNames?: { key: string; title: string; children: string };
}

// ============================================================
// 组件
// ============================================================

/**
 * RefSelector — 引用选择器（F7 选择器）。
 *
 * 用于替代 antd Select 在数据量大或需要展示多列信息时的场景。
 * 触发器仿 Input variant="underlined" 下划线样式，点击放大镜弹出选择 Modal。
 *
 * 支持三种模式：
 * - default：单选，radio 列，双击行自动确认关闭
 * - multiple：多选，checkbox 列，右侧已选面板
 * - tree-table：单选，左树右表 Splitter 布局
 */
function RefSelector<T extends Record<string, unknown>>({
  value,
  onChange,
  displayRender,
  fieldNames,
  placeholder,
  disabled = false,
  fetchFn,
  columns,
  mode,
  modalTitle,
  pageSize = 20,
  treeData,
  treeFieldNames,
}: RefSelectorProps<T>) {
  const [modalOpen, setModalOpen] = useState(false);
  /** 弹窗内暂存的选中记录，确认后提交给 onChange */
  const [selectedRecords, setSelectedRecords] = useState<T[]>([]);

  const query = useRefSelectorQuery({ fetchFn, initialPageSize: pageSize, enabled: modalOpen });

  // ---- 派生 ----

  const isMultiple = mode === 'multiple';

  /** 根据选中记录计算 rowKey 数组，控制 Table 勾选态 */
  const selectedRowKeys = useMemo(
    () => selectedRecords.map((r) => String(r[fieldNames.key])),
    [selectedRecords, fieldNames.key],
  );

  /** Table rowKey 函数 */
  const rowKey = useCallback((record: T) => String(record[fieldNames.key]), [fieldNames.key]);

  // ---- 事件处理 ----

  /** 打开 Modal：重置查询状态 + 同步外部 value → 内部 selectedRecords */
  const handleOpen = useCallback(() => {
    query.reset();
    if (value == null) {
      setSelectedRecords([]);
    } else if (Array.isArray(value)) {
      setSelectedRecords(value);
    } else {
      setSelectedRecords([value as T]);
    }
    setModalOpen(true);
  }, [query, value]);

  /** 取消：丢弃选择，关闭 Modal */
  const handleCancel = useCallback(() => {
    setModalOpen(false);
  }, []);

  /** 确认：提交选择给 onChange */
  const handleConfirm = useCallback(() => {
    if (isMultiple) {
      onChange?.(selectedRecords.length > 0 ? selectedRecords : null);
    } else {
      onChange?.(selectedRecords.length > 0 ? selectedRecords[0]! : null);
    }
    setModalOpen(false);
  }, [isMultiple, onChange, selectedRecords]);

  /** 双击行（单选模式）：选中 + 确认关闭 */
  const handleRowDoubleClick = useCallback(
    (record: T) => {
      if (isMultiple) return;
      onChange?.(record);
      setModalOpen(false);
    },
    [isMultiple, onChange],
  );

  /** 清空已选值 */
  const handleClear = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      if (disabled) return;
      onChange?.(null);
    },
    [disabled, onChange],
  );

  /** 是否有已选值 */
  const hasValue = value != null && (!Array.isArray(value) || value.length > 0);

  // ---- 渲染：触发器 ----

  function renderTriggerContent(): ReactNode {
    if (value == null) {
      return <span className="sm-ref-selector-placeholder">{placeholder || '请选择'}</span>;
    }
    if (Array.isArray(value)) {
      if (value.length === 0) {
        return <span className="sm-ref-selector-placeholder">{placeholder || '请选择'}</span>;
      }
      return <span>已选 {value.length} 项</span>;
    }
    return displayRender(value);
  }

  // ---- 渲染：Modal 标题栏 ----

  const modalTitleNode = (
    <div className="sm-ref-selector-header">
      <span className="sm-ref-selector-header-title">{modalTitle}</span>
      <Input.Search
        variant="underlined"
        className="sm-ref-selector-header-search"
        placeholder="快速搜索"
        onSearch={query.onSearch}
      />
      <Button type="link" icon={<CloseOutlined />} onClick={handleCancel} />
    </div>
  );

  // ---- 渲染：Modal 底部按钮 ----

  const modalFooter = (
    <div className="sm-ref-selector-footer">
      <Button onClick={handleCancel}>取消</Button>
      <Button type="primary" onClick={handleConfirm}>
        确定
      </Button>
    </div>
  );

  // ---- 渲染：表格 ----

  /** rowSelection 配置（受控选中态） */
  const rowSelection: TableRowSelection<T> = useMemo(
    () => ({
      type: isMultiple ? 'checkbox' : 'radio',
      selectedRowKeys,
      onChange: (_keys, rows) => {
        if (isMultiple) {
          setSelectedRecords(rows as T[]);
        } else {
          setSelectedRecords(rows.length > 0 ? [rows[0] as T] : []);
        }
      },
      columnWidth: 36,
    }),
    [isMultiple, selectedRowKeys],
  );

  /** 完整列定义：序号 + 用户列 */
  const fullColumns: ColumnsType<T> = useMemo(
    () => [
      {
        title: '#',
        width: 44,
        align: 'center' as const,
        fixed: 'left' as const,
        render: (_text: unknown, _record: T, index: number) =>
          (query.pageNum - 1) * query.pageSize + index + 1,
      },
      ...columns.map((col): ColumnsType<T>[number] => ({
        title: col.title,
        dataIndex: col.dataIndex,
        width: col.width,
        render: col.render as unknown as ColumnsType<T>[number]['render'],
      })),
    ],
    [columns, query.pageNum, query.pageSize],
  );

  /** 行事件：点击行即选中（单选/多选），单选模式双击确认关闭 */
  const onRow = useCallback(
    (record: T) => ({
      onClick: () => {
        if (isMultiple) {
          // 多选：切换勾选
          setSelectedRecords((prev) => {
            const key = record[fieldNames.key];
            const exists = prev.some((r) => r[fieldNames.key] === key);
            return exists ? prev.filter((r) => r[fieldNames.key] !== key) : [...prev, record];
          });
        } else {
          // 单选：直接选中
          setSelectedRecords([record]);
        }
      },
      onDoubleClick: () => handleRowDoubleClick(record),
    }),
    [isMultiple, fieldNames.key, handleRowDoubleClick],
  );

  /** 表格内容（meta 栏 + Table） */
  function renderTableContent(): ReactNode {
    return (
      <div className="sm-ref-selector-table-wrap">
        {/* 元信息栏：总条数 + 分页 */}
        <div className="sm-ref-selector-meta">
          <span>共 {query.total} 条</span>
          <Pagination
            size="small"
            showSizeChanger
            pageSizeOptions={['10', '20', '50', '100']}
            current={query.pageNum}
            pageSize={query.pageSize}
            total={query.total}
            showTotal={(t) => `共 ${t} 条`}
            onChange={(nextPage, nextSize) => query.onPageChange(nextPage, nextSize)}
          />
        </div>
        {/* 表格体 */}
        <div className="sm-ref-selector-table-body">
          <Table<T>
            rowKey={rowKey}
            rowSelection={rowSelection}
            columns={fullColumns}
            dataSource={query.records}
            size="small"
            pagination={false}
            loading={query.fetching}
            onRow={onRow}
            scroll={{ y: '100%' }}
          />
        </div>
      </div>
    );
  }

  // ---- 渲染：多选右侧已选面板 ----

  function renderSelectedPanel(): ReactNode {
    return (
      <aside className="sm-ref-selector-selected-panel">
        <div className="sm-ref-selector-selected-header">
          <span>已选 {selectedRecords.length} 项</span>
          {selectedRecords.length > 0 && (
            <Button type="link" onClick={() => setSelectedRecords([])}>
              清空
            </Button>
          )}
        </div>
        <div className="sm-ref-selector-selected-list">
          {selectedRecords.map((record) => (
            <div key={String(record[fieldNames.key])} className="sm-ref-selector-selected-item">
              <span className="sm-ref-selector-selected-item-label">{displayRender(record)}</span>
              <span
                className="sm-ref-selector-selected-item-remove"
                onClick={() => {
                  setSelectedRecords((prev) =>
                    prev.filter((r) => r[fieldNames.key] !== record[fieldNames.key]),
                  );
                }}
              >
                <CloseOutlined />
              </span>
            </div>
          ))}
          {selectedRecords.length === 0 && (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description="暂未选择"
              style={{ marginTop: 40 }}
            />
          )}
        </div>
      </aside>
    );
  }

  // ---- 渲染：树表模式的树面板 ----

  function renderTree(): ReactNode {
    if (!treeData || treeData.length === 0) return null;
    return (
      <Tree
        treeData={treeData as Parameters<typeof Tree>[0]['treeData']}
        fieldNames={treeFieldNames}
        onSelect={(keys) => {
          query.onTreeSelect(keys.length > 0 ? String(keys[0]) : undefined);
        }}
        defaultExpandAll
        blockNode
      />
    );
  }

  // ---- 渲染：Modal Body ----

  function renderModalBody(): ReactNode {
    if (query.error) {
      return (
        <div
          className="sm-ref-selector-body"
          style={{ justifyContent: 'center', alignItems: 'center' }}
        >
          <Empty description={query.error.message || '加载失败'} />
        </div>
      );
    }

    if (mode === 'tree-table') {
      return (
        <Splitter className="sm-ref-selector-split">
          <Splitter.Panel defaultSize={200} min={160} max="40%">
            <div className="sm-ref-selector-tree-panel">{renderTree()}</div>
          </Splitter.Panel>
          <Splitter.Panel>
            <div className="sm-ref-selector-body">{renderTableContent()}</div>
          </Splitter.Panel>
        </Splitter>
      );
    }

    if (mode === 'multiple') {
      return (
        <div className="sm-ref-selector-body-multi">
          {renderTableContent()}
          {renderSelectedPanel()}
        </div>
      );
    }

    return <div className="sm-ref-selector-body">{renderTableContent()}</div>;
  }

  // ============================================================
  // 主渲染
  // ============================================================

  return (
    <>
      {/* 触发器 */}
      <div
        className="sm-ref-selector-trigger"
        onClick={disabled ? undefined : handleOpen}
        role="button"
        tabIndex={disabled ? -1 : 0}
        onKeyDown={(e) => {
          if (e.key === 'Enter' && !disabled) handleOpen();
        }}
      >
        <div className="sm-ref-selector-trigger-display">{renderTriggerContent()}</div>
        {/* 清空按钮 — 有值时显示 */}
        {hasValue && !disabled && (
          <Button
            type="link"
            icon={<CloseOutlined />}
            className="sm-ref-selector-trigger-clear"
            onClick={handleClear}
          />
        )}
        <Button
          type="text"
          icon={<SearchOutlined />}
          className="sm-ref-selector-trigger-btn"
          onClick={(e) => {
            e.stopPropagation();
            if (!disabled) handleOpen();
          }}
          disabled={disabled}
        />
      </div>

      {/* 选择 Modal */}
      <Modal
        title={modalTitleNode}
        closeIcon={null}
        open={modalOpen}
        onCancel={handleCancel}
        centered
        maskClosable={false}
        className="sm-modal sm-ref-selector-modal"
        destroyOnClose
        width={mode === 'tree-table' ? 960 : 800}
        footer={modalFooter}
      >
        <Spin spinning={query.loading && query.records.length === 0}>{renderModalBody()}</Spin>
      </Modal>
    </>
  );
}

export default RefSelector;
export type { RefSelectorProps, RefSelectorColumn, RefSelectorFieldNames, RefSelectorMode };

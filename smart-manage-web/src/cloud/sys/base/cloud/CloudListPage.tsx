import { useState } from 'react';
import { Form, Select, Table, Tag } from '@arco-design/web-react';
import { useQuery } from '@tanstack/react-query';
import { ListPage, OperationType } from '@/cloud/common/page';
import { createAddNewTabKey, createBillTabKey } from '@/cloud/common/page/tabKeys';
import { usePagination, useRowSelection } from '@/hooks';
import { cloudApi } from './api';
import CloudEditPage from './CloudEditPage';
import type { PageComponentProps } from '@/cloud/common/page';
import type { CloudListVO } from './types';
import type { ColumnProps } from '@arco-design/web-react/es/Table/interface';

interface CloudEditModalState {
  tabKey: string;
  title: string;
  operationType: OperationType;
  billId?: string;
  temporary?: boolean;
}

const CloudListPage = (props: PageComponentProps) => {
  const { pageNum, pageSize, setPageNum, setPageSize, resetPage } = usePagination();
  const [keyword, setKeyword] = useState('');
  const [enableFlag, setEnableFlag] = useState<boolean | undefined>();
  const [editModalState, setEditModalState] = useState<CloudEditModalState>();

  const listQuery = useQuery({
    queryKey: ['cloud-list-page', pageNum, pageSize, keyword, enableFlag],
    queryFn: () => cloudApi.listPage({ pageNum, pageSize, keyword: keyword || undefined, enableFlag }),
  });

  const records = listQuery.data?.records ?? [];
  const total = listQuery.data?.total ?? 0;
  const { selectedRowKeys, setSelectedRowKeys, allSelected, toggleSelectAll } = useRowSelection(records);

  const deleteSelected = async () => {
    try {
      await Promise.all(selectedRowKeys.map((id) => cloudApi.delete(id)));
      setSelectedRowKeys([]);
      await listQuery.refetch();
    } catch {
      // 错误由 API 拦截器和调用方统一处理
    }
  };

  const openDetail = (record: CloudListVO) => {
    setEditModalState({
      tabKey: createBillTabKey(props.componentKey, record.id),
      title: record.name,
      operationType: OperationType.EDIT,
      billId: record.id,
    });
  };

  const columns: ColumnProps<CloudListVO>[] = [
    {
      title: '#',
      width: 56,
      render: (_value, _record, index) => (pageNum - 1) * pageSize + index + 1,
    },
    {
      title: '编码',
      dataIndex: 'number',
      width: 180,
      render: (_value, record) => (
        <button className="sm-table-link" type="button" onClick={() => openDetail(record)}>
          {record.number}
        </button>
      ),
    },
    { title: '名称', dataIndex: 'name', width: 220 },
    { title: '排序', dataIndex: 'seq', width: 100 },
    {
      title: '启用',
      dataIndex: 'enableFlag',
      width: 100,
      render: (value) => <Tag color={value ? 'green' : 'gray'}>{value ? '启用' : '停用'}</Tag>,
    },
    { title: '创建时间', dataIndex: 'createTime', width: 180 },
    { title: '更新时间', dataIndex: 'updateTime', width: 180 },
  ];

  return (
    <>
      <ListPage
        {...props}
        title="云管理"
        total={total}
        selectedCount={selectedRowKeys.length}
        allSelected={allSelected}
        pageNum={pageNum}
        pageSize={pageSize}
        quickSearchPlaceholder="搜索编码/名称"
        filterSummary={
          [
            keyword ? `关键字：${keyword}` : '',
            enableFlag === undefined ? '' : `启用状态：${enableFlag ? '启用' : '停用'}`,
          ]
            .filter(Boolean)
            .join('；') || '未设置筛选条件'
        }
        filterContent={
          <Form className="sm-list-filter-form" layout="inline">
            <Form.Item label="启用状态">
              <Select
                allowClear
                className="sm-list-filter-control"
                value={enableFlag === undefined ? undefined : String(enableFlag)}
                placeholder="全部"
                onChange={(value) => {
                  setEnableFlag(value === 'true' ? true : value === 'false' ? false : undefined);
                  resetPage();
                }}
              >
                <Select.Option value="true">启用</Select.Option>
                <Select.Option value="false">停用</Select.Option>
              </Select>
            </Form.Item>
          </Form>
        }
        onAddNew={() =>
          setEditModalState({
            tabKey: createAddNewTabKey(props.componentKey),
            title: '新增云',
            operationType: OperationType.ADDNEW,
            temporary: true,
          })
        }
        onDelete={() => void deleteSelected()}
        onRefresh={() => void listQuery.refetch()}
        onQuickSearch={(value) => {
          setKeyword(value.trim());
          resetPage();
        }}
        onToggleSelectAll={toggleSelectAll}
        onPageChange={(nextPageNum, nextPageSize) => {
          setPageNum(nextPageNum);
          setPageSize(nextPageSize);
        }}
        table={
          <Table
            rowKey="id"
            columns={columns}
            data={records}
            loading={listQuery.isFetching}
            pagination={false}
            scroll={{ x: 1040 }}
            rowSelection={{
              type: 'checkbox',
              selectedRowKeys,
              onChange: (keys) => setSelectedRowKeys(keys.map(String)),
            }}
          />
        }
      />
      {editModalState && (
        <CloudEditPage
          {...props}
          modal
          visible
          tabKey={editModalState.tabKey}
          title={editModalState.title}
          operationType={editModalState.operationType}
          billId={editModalState.billId}
          temporary={editModalState.temporary}
          onClose={() => setEditModalState(undefined)}
          onSaved={(id) => {
            setEditModalState((current) =>
              current
                ? {
                    ...current,
                    tabKey: createBillTabKey(props.componentKey, id),
                    operationType: OperationType.EDIT,
                    billId: id,
                    temporary: false,
                  }
                : current,
            );
            void listQuery.refetch();
          }}
        />
      )}
    </>
  );
};

export default CloudListPage;

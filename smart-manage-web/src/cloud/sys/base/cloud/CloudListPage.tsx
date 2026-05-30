import { useState } from 'react';
import { Form, Message, Modal, Select, Table, Tag } from '@arco-design/web-react';
import { useQuery } from '@tanstack/react-query';
import { ListPage, OperationType } from '@/cloud/common/page';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { cloudApi } from './api';
import type { PageComponentProps } from '@/cloud/common/page';
import type { CloudListVO } from './types';
import type { ColumnProps } from '@arco-design/web-react/es/Table/interface';

const DEFAULT_PAGE_SIZE = 20;

const CloudListPage = (props: PageComponentProps) => {
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  const [keyword, setKeyword] = useState('');
  const [enableFlag, setEnableFlag] = useState<boolean | undefined>();
  const [selectedRowKeys, setSelectedRowKeys] = useState<string[]>([]);
  const openAddNewTab = useAppWorkspaceStore((store) => store.openAddNewTab);
  const openBillTab = useAppWorkspaceStore((store) => store.openBillTab);

  const listQuery = useQuery({
    queryKey: ['cloud-list-page', pageNum, pageSize, keyword, enableFlag],
    queryFn: () => cloudApi.listPage({ pageNum, pageSize, keyword: keyword || undefined, enableFlag }),
  });

  const records = listQuery.data?.records ?? [];
  const total = listQuery.data?.total ?? 0;
  const allSelected = records.length > 0 && selectedRowKeys.length === records.length;

  const deleteSelected = () => {
    if (selectedRowKeys.length === 0) {
      Message.warning('请先选择要删除的数据');
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: `确定删除已选的 ${selectedRowKeys.length} 条数据吗？`,
      onOk: async () => {
        await Promise.all(selectedRowKeys.map((id) => cloudApi.delete(id)));
        setSelectedRowKeys([]);
        Message.success('删除成功');
        await listQuery.refetch();
      },
    });
  };

  const openDetail = (record: CloudListVO) => {
    openBillTab(props.appNumber, props.componentKey, record.name, record.id, OperationType.EDIT);
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
                setPageNum(1);
              }}
            >
              <Select.Option value="true">启用</Select.Option>
              <Select.Option value="false">停用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      }
      onAddNew={() => openAddNewTab(props.appNumber, props.componentKey, '新增云')}
      onDelete={deleteSelected}
      onRefresh={() => void listQuery.refetch()}
      onQuickSearch={(value) => {
        setKeyword(value.trim());
        setPageNum(1);
      }}
      onToggleSelectAll={(checked) => setSelectedRowKeys(checked ? records.map((record) => record.id) : [])}
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
  );
};

export default CloudListPage;

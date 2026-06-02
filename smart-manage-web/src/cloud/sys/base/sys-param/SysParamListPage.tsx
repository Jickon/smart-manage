import { useState } from 'react';
import { Message, Modal, Table, Tag } from '@arco-design/web-react';
import { useQuery } from '@tanstack/react-query';
import { ListPage, OperationType } from '@/cloud/common/page';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { sysParamApi } from './api';
import type { PageComponentProps } from '@/cloud/common/page';
import type { SysParamListVO } from './types';
import type { ColumnProps } from '@arco-design/web-react/es/Table/interface';

const DEFAULT_PAGE_SIZE = 20;

const SysParamListPage = (props: PageComponentProps) => {
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  const [keyword, setKeyword] = useState('');
  const [selectedRowKeys, setSelectedRowKeys] = useState<string[]>([]);
  const openAddNewTab = useAppWorkspaceStore((store) => store.openAddNewTab);
  const openBillTab = useAppWorkspaceStore((store) => store.openBillTab);

  const listQuery = useQuery({
    queryKey: ['sys-param-list-page', pageNum, pageSize, keyword],
    queryFn: () => sysParamApi.listPage({ pageNum, pageSize, keyword: keyword || undefined }),
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
        try {
          await Promise.all(selectedRowKeys.map((id) => sysParamApi.delete(id)));
          setSelectedRowKeys([]);
          Message.success('删除成功');
          await listQuery.refetch();
        } catch (error) {
          Message.error(error instanceof Error ? error.message : '删除失败');
        }
      },
    });
  };

  const openDetail = (record: SysParamListVO) => {
    openBillTab(props.appNumber, props.componentKey, record.name, record.id, OperationType.EDIT);
  };

  const columns: ColumnProps<SysParamListVO>[] = [
    {
      title: '#',
      width: 56,
      fixed: 'left' as const,
      render: (_value, _record, index) => (pageNum - 1) * pageSize + index + 1,
    },
    {
      title: '编码',
      dataIndex: 'number',
      width: 220,
      render: (_value, record) => (
        <button className="sm-table-link" type="button" onClick={() => openDetail(record)}>
          {record.number}
        </button>
      ),
    },
    { title: '名称', dataIndex: 'name', width: 220 },
    { title: '参数值', dataIndex: 'value', width: 260 },
    {
      title: '类型',
      dataIndex: 'isSystem',
      width: 120,
      render: (value) => <Tag color={value ? 'arcoblue' : 'green'}>{value ? '系统' : '自定义'}</Tag>,
    },
    { title: '备注', dataIndex: 'remark', width: 320 },
  ];

  return (
    <ListPage
      {...props}
      title="系统参数"
      total={total}
      selectedCount={selectedRowKeys.length}
      allSelected={allSelected}
      pageNum={pageNum}
      pageSize={pageSize}
      quickSearchPlaceholder="搜索编码/名称"
      filterSummary={keyword ? `关键字：${keyword}` : '未设置筛选条件'}
      onAddNew={() => openAddNewTab(props.appNumber, props.componentKey, '新增系统参数')}
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
          scroll={{ x: 1200 }}
          rowSelection={{
            type: 'checkbox',
            fixed: true,
            selectedRowKeys,
            onChange: (keys) => setSelectedRowKeys(keys.map(String)),
          }}
        />
      }
    />
  );
};

export default SysParamListPage;

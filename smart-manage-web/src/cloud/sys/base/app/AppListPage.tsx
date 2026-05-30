import { useState } from 'react';
import { Message, Modal, Table, Tag } from '@arco-design/web-react';
import { useQuery } from '@tanstack/react-query';
import { ListPage, OperationType } from '@/cloud/common/page';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { appApi } from './api';
import { cloudApi } from '../cloud/api';
import AppCloudTree from './AppCloudTree';
import type { PageComponentProps } from '@/cloud/common/page';
import type { AppListVO } from './types';
import type { ColumnProps } from '@arco-design/web-react/es/Table/interface';

const DEFAULT_PAGE_SIZE = 20;

const AppListPage = (props: PageComponentProps) => {
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  const [keyword, setKeyword] = useState('');
  const [treeKeyword, setTreeKeyword] = useState('');
  const [selectedCloudId, setSelectedCloudId] = useState<string | undefined>();
  const [includeChildren, setIncludeChildren] = useState(false);
  const [showDisabled, setShowDisabled] = useState(false);
  const [selectedRowKeys, setSelectedRowKeys] = useState<string[]>([]);
  const openAddNewTab = useAppWorkspaceStore((store) => store.openAddNewTab);
  const openBillTab = useAppWorkspaceStore((store) => store.openBillTab);

  const cloudQuery = useQuery({
    queryKey: ['cloud-select-tree', treeKeyword, showDisabled],
    queryFn: () =>
      cloudApi.select({
        pageNum: 1,
        pageSize: 200,
        keyword: treeKeyword || undefined,
        enableFlag: showDisabled ? undefined : true,
      }),
  });

  const listQuery = useQuery({
    queryKey: ['app-list-page', pageNum, pageSize, keyword, selectedCloudId, includeChildren],
    queryFn: () => appApi.listPage({ pageNum, pageSize, keyword: keyword || undefined, cloudId: selectedCloudId }),
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
        await Promise.all(selectedRowKeys.map((id) => appApi.delete(id)));
        setSelectedRowKeys([]);
        Message.success('删除成功');
        await listQuery.refetch();
      },
    });
  };

  const openDetail = (record: AppListVO) => {
    openBillTab(props.appNumber, props.componentKey, record.name, record.id, OperationType.EDIT);
  };

  const columns: ColumnProps<AppListVO>[] = [
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
    { title: '所属云', dataIndex: 'cloudName', width: 220 },
    { title: '图标', dataIndex: 'icon', width: 120 },
    { title: '排序', dataIndex: 'seq', width: 90 },
    {
      title: '启用',
      dataIndex: 'enableFlag',
      width: 100,
      render: (value) => <Tag color={value ? 'green' : 'gray'}>{value ? '启用' : '停用'}</Tag>,
    },
    { title: '描述', dataIndex: 'description', width: 260 },
  ];

  return (
    <ListPage
      {...props}
      title="应用管理"
      total={total}
      selectedCount={selectedRowKeys.length}
      allSelected={allSelected}
      pageNum={pageNum}
      pageSize={pageSize}
      quickSearchPlaceholder="搜索编码/名称"
      filterSummary={keyword ? `关键字：${keyword}` : '未设置筛选条件'}
      treePanel={
        <AppCloudTree
          clouds={cloudQuery.data?.records ?? []}
          loading={cloudQuery.isFetching}
          selectedCloudId={selectedCloudId}
          includeChildren={includeChildren}
          showDisabled={showDisabled}
          keyword={treeKeyword}
          onKeywordChange={setTreeKeyword}
          onCloudChange={(cloudId) => {
            setSelectedCloudId(cloudId);
            setPageNum(1);
          }}
          onIncludeChildrenChange={setIncludeChildren}
          onShowDisabledChange={setShowDisabled}
        />
      }
      treePanelSize="280px"
      treeSplitDirection="horizontal"
      onAddNew={() => openAddNewTab(props.appNumber, props.componentKey, '新增应用')}
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
          scroll={{ x: 1240 }}
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

export default AppListPage;

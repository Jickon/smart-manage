import { useState } from 'react';
import { Table, Tag } from '@arco-design/web-react';
import { useQuery } from '@tanstack/react-query';
import { ListPage, OperationType } from '@/cloud/common/page';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { usePagination, useRowSelection } from '@/hooks';
import { appApi } from './api';
import { cloudApi } from '../cloud/api';
import AppCloudTree from './AppCloudTree';
import type { PageComponentProps } from '@/cloud/common/page';
import type { AppListVO } from './types';
import type { ColumnProps } from '@arco-design/web-react/es/Table/interface';

const AppListPage = (props: PageComponentProps) => {
  const { pageNum, pageSize, setPageNum, setPageSize, resetPage } = usePagination();
  const [keyword, setKeyword] = useState('');
  const [treeKeyword, setTreeKeyword] = useState('');
  const [selectedCloudId, setSelectedCloudId] = useState<string | undefined>();
  const [includeChildren, setIncludeChildren] = useState(false);
  const [showDisabled, setShowDisabled] = useState(false);
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
  const { selectedRowKeys, setSelectedRowKeys, allSelected, toggleSelectAll } = useRowSelection(records);

  const handleDeleteSelected = () => {
    // 使用简单的内联确认弹窗，因为 AppListPage 的删除模式与 CloudListPage 稍有不同
    // 未来可统一迁移到 useDeleteMutation
    void (async () => {
      try {
        await Promise.all(selectedRowKeys.map((id) => appApi.delete(id)));
        setSelectedRowKeys([]);
        await listQuery.refetch();
      } catch {
        // 错误由 API 拦截器统一处理
      }
    })();
  };

  const openDetail = (record: AppListVO) => {
    openBillTab(props.appNumber, props.componentKey, record.name, record.id, OperationType.EDIT);
  };

  const columns: ColumnProps<AppListVO>[] = [
    {
      title: '#',
      width: 56,
      fixed: 'left' as const,
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
            resetPage();
          }}
          onIncludeChildrenChange={setIncludeChildren}
          onShowDisabledChange={setShowDisabled}
        />
      }
      treePanelSize="280px"
      treeSplitDirection="horizontal"
      onAddNew={() => openAddNewTab(props.appNumber, props.componentKey, '新增应用')}
      onDelete={handleDeleteSelected}
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
          scroll={{ x: 1240 }}
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

export default AppListPage;

import { useState, useCallback } from 'react';
import { Button, Modal, Tag, Avatar, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import ListPage from '@/domain/common/page/ListPage';
import { useListPageQuery } from '@/domain/common/page/useListPageQuery';
import { useWorkbenchStore } from '@/stores/workbench';
import { OperationType } from '@/domain/common/page/types';
import { userApi } from './api';
import type { UserListVO } from './types';
import type { PageComponentProps } from '@/domain/common/page/types';

/** 用户编辑页 componentKey */
const USER_EDIT_KEY = 'sys/base/user/edit';

/** 用户管理列表页 */
const UserListPage = (props: PageComponentProps) => {
  const { records, total, pageNum, pageSize, keyword, query, onSearch, onPageChange, onRefresh } =
    useListPageQuery({
      queryKey: ['user-list'],
      queryFn: (params) => userApi.listPage(params),
    });

  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const openBillTab = useWorkbenchStore((s) => s.openBillTab);
  const openAddNewTab = useWorkbenchStore((s) => s.openAddNewTab);

  const handleOpenEdit = useCallback(
    (id: string) => {
      openBillTab(props.appNumber, USER_EDIT_KEY, '编辑用户', id, OperationType.EDIT);
    },
    [props.appNumber, openBillTab],
  );

  const handleOpenAdd = useCallback(() => {
    openAddNewTab(props.appNumber, USER_EDIT_KEY, '新增用户');
  }, [props.appNumber, openAddNewTab]);

  const handleDelete = useCallback(() => {
    if (selectedRowKeys.length === 0) return;
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedRowKeys.length} 条记录吗？`,
      okText: '删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        await Promise.all(selectedRowKeys.map((id) => userApi.delete(String(id))));
        message.success('删除成功');
        setSelectedRowKeys([]);
        query.refetch();
      },
    });
  }, [selectedRowKeys, query]);

  const columns: ColumnsType<UserListVO> = [
    {
      title: '用户名',
      dataIndex: 'username',
      width: 160,
      render: (text, record) => (
        <Button type="link" size="small" onClick={() => handleOpenEdit(record.id)}>
          {text}
        </Button>
      ),
    },
    { title: '昵称', dataIndex: 'nickname', width: 160 },
    {
      title: '头像',
      dataIndex: 'avatar',
      width: 60,
      render: (url) => (url ? <Avatar src={url} size="small" /> : <Avatar size="small">-</Avatar>),
    },
    {
      title: '状态',
      dataIndex: 'enableFlag',
      width: 80,
      render: (value) => (value ? <Tag color="green">启用</Tag> : <Tag color="default">停用</Tag>),
    },
    { title: '创建时间', dataIndex: 'createTime', width: 180 },
  ];

  return (
    <ListPage<UserListVO>
      {...props}
      title="用户管理"
      loading={query.isLoading}
      error={query.error as Error | null}
      onRetry={() => query.refetch()}
      total={total}
      pageNum={pageNum}
      pageSize={pageSize}
      quickSearchPlaceholder="搜索用户名/昵称"
      filterSummary={keyword ? `关键字：${keyword}` : undefined}
      onAddNew={handleOpenAdd}
      onDelete={handleDelete}
      onRefresh={onRefresh}
      onQuickSearch={onSearch}
      onPageChange={onPageChange}
      rowKey="id"
      columns={columns}
      dataSource={records}
      selectMode="checkbox"
      selectedRowKeys={selectedRowKeys}
      onSelectChange={(keys) => setSelectedRowKeys(keys)}
    />
  );
};

export default UserListPage;

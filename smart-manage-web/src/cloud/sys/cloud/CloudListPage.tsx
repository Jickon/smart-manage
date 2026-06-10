import { useState, useCallback } from 'react';
import { Tag, Button } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import ListPage from '@/cloud/common/page/ListPage';
import { useListPageQuery } from '@/cloud/common/page/useListPageQuery';
import { cloudApi } from './api';
import type { CloudListVO } from './types';
import type { PageComponentProps } from '@/cloud/common/page/types';
import CloudEditPage from './CloudEditPage';

/** 云管理列表页 */
const CloudListPage = (props: PageComponentProps) => {
  const { records, total, pageNum, pageSize, keyword, query, onSearch, onPageChange, onRefresh } =
    useListPageQuery({
      queryKey: ['cloud-list'],
      queryFn: (params) => cloudApi.listPage(params),
    });

  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [editId, setEditId] = useState<string | null>(null); // null = 新增
  const [modalOpen, setModalOpen] = useState(false);

  const handleOpenEdit = useCallback((id: string) => {
    setEditId(id);
    setModalOpen(true);
  }, []);

  const handleOpenAdd = useCallback(() => {
    setEditId(null);
    setModalOpen(true);
  }, []);

  const handleModalClose = useCallback(() => {
    setModalOpen(false);
  }, []);

  const handleSaved = useCallback(() => {
    query.refetch();
  }, [query]);

  const columns: ColumnsType<CloudListVO> = [
    {
      title: '编码',
      dataIndex: 'number',
      width: 180,
      render: (text, record) => (
        <Button type="link" size="small" onClick={() => handleOpenEdit(record.id)}>
          {text}
        </Button>
      ),
    },
    { title: '名称', dataIndex: 'name', width: 220 },
    { title: '排序', dataIndex: 'seq', width: 80 },
    {
      title: '状态',
      dataIndex: 'enableFlag',
      width: 80,
      render: (value) => (value ? <Tag color="green">启用</Tag> : <Tag color="default">停用</Tag>),
    },
    { title: '创建时间', dataIndex: 'createTime', width: 180 },
    { title: '更新时间', dataIndex: 'updateTime', width: 180 },
  ];

  return (
    <>
      <ListPage<CloudListVO>
        {...props}
        title="云管理"
        loading={query.isLoading}
        error={query.error as Error | null}
        onRetry={() => query.refetch()}
        total={total}
        pageNum={pageNum}
        pageSize={pageSize}
        quickSearchPlaceholder="搜索编码/名称"
        filterSummary={keyword ? `关键字：${keyword}` : undefined}
        onAddNew={handleOpenAdd}
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
      <CloudEditPage
        open={modalOpen}
        cloudId={editId}
        onClose={handleModalClose}
        onSaved={handleSaved}
      />
    </>
  );
};

export default CloudListPage;

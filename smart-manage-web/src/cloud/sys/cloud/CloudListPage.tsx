import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Table, Tag } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import ListPage from '@/cloud/common/page/ListPage';
import { cloudApi } from './api';
import type { CloudListVO } from './types';
import type { PageComponentProps } from '@/cloud/common/page/types';

/** 云管理列表页 */
const CloudListPage = (props: PageComponentProps) => {
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [keyword, setKeyword] = useState('');

  const listQuery = useQuery({
    queryKey: ['cloud-list', pageNum, pageSize, keyword],
    queryFn: () => cloudApi.listPage({ pageNum, pageSize, keyword: keyword || undefined }),
  });

  const records = listQuery.data?.records ?? [];
  const total = listQuery.data?.total ?? 0;

  const columns: ColumnsType<CloudListVO> = [
    { title: '编码', dataIndex: 'number', width: 180, render: (text) => <a>{text}</a> },
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
    <ListPage
      {...props}
      title="云管理"
      total={total}
      pageNum={pageNum}
      pageSize={pageSize}
      quickSearchPlaceholder="搜索编码/名称"
      filterSummary={keyword ? `关键字：${keyword}` : '未设置筛选条件'}
      onAddNew={() => {
        // TODO: 打开新增云弹窗
      }}
      onRefresh={() => listQuery.refetch()}
      onQuickSearch={(value) => {
        setKeyword(value);
        setPageNum(1);
      }}
      onPageChange={(nextPage, nextSize) => {
        setPageNum(nextPage);
        setPageSize(nextSize);
      }}
      table={
        <Table<CloudListVO>
          rowKey="id"
          columns={columns}
          dataSource={records}
          loading={listQuery.isFetching}
          pagination={false}
          scroll={{ x: 920 }}
        />
      }
    />
  );
};

export default CloudListPage;

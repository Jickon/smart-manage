import { useMemo, useState } from 'react';
import { Form, Input, InputNumber, Message, Modal, Select, Spin, Switch, Table, Tag } from '@arco-design/web-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { cloudApi } from '@/api/cloud';
import { EditPage, ListPage, OperationType } from '@/cloud/common/page';
import { createBillTabKey } from '@/cloud/common/page/tabKeys';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import type { PageComponentProps } from '@/cloud/common/page';
import type { CloudDetailVO, CloudListVO, CloudSaveForm } from '@/types/api';
import type { ColumnProps } from '@arco-design/web-react/es/Table/interface';

const DEFAULT_PAGE_SIZE = 20;

interface CloudFormState {
  id?: string;
  number: string;
  name: string;
  seq?: number;
  enableFlag: boolean;
}

const emptyCloudForm = (): CloudFormState => ({
  number: '',
  name: '',
  seq: 99,
  enableFlag: true,
});

const toCloudForm = (detail: Partial<CloudDetailVO>): CloudFormState => ({
  id: detail.id,
  number: detail.number ?? '',
  name: detail.name ?? '',
  seq: detail.seq ?? 99,
  enableFlag: detail.enableFlag ?? true,
});

const toCloudSaveForm = (formState: CloudFormState): CloudSaveForm => ({
  id: formState.id,
  number: formState.number.trim(),
  name: formState.name.trim(),
  seq: formState.seq,
  enableFlag: formState.enableFlag,
});

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

const CloudEditPage = (props: PageComponentProps) => {
  const [formPatch, setFormPatch] = useState<Partial<CloudFormState>>({});
  const queryClient = useQueryClient();
  const replaceContentTab = useAppWorkspaceStore((store) => store.replaceContentTab);
  const removeContentTab = useAppWorkspaceStore((store) => store.removeContentTab);

  const isAddNew = props.operationType === OperationType.ADDNEW;
  const detailQuery = useQuery({
    queryKey: ['cloud-detail', props.billId],
    queryFn: () => cloudApi.detail(props.billId ?? ''),
    enabled: Boolean(props.billId) && !isAddNew,
  });
  const createNewDataQuery = useQuery({
    queryKey: ['cloud-create-new-data', props.tabKey],
    queryFn: cloudApi.createNewData,
    enabled: isAddNew,
  });

  const baseFormState = useMemo(() => {
    if (detailQuery.data) {
      return toCloudForm(detailQuery.data);
    }
    if (createNewDataQuery.data) {
      return toCloudForm(createNewDataQuery.data);
    }
    return emptyCloudForm();
  }, [createNewDataQuery.data, detailQuery.data]);

  const formState = { ...baseFormState, ...formPatch };

  const saveMutation = useMutation({
    mutationFn: () => cloudApi.save(toCloudSaveForm(formState)),
    onSuccess: async (id) => {
      Message.success('保存成功');
      await queryClient.invalidateQueries({ queryKey: ['cloud-list-page'] });
      if (props.temporary || isAddNew) {
        replaceContentTab(props.appNumber, props.tabKey, {
          key: createBillTabKey(props.componentKey, id),
          label: formState.name || '云详情',
          closable: true,
          componentKey: props.componentKey,
          pageType: 'EDIT',
          operationType: OperationType.EDIT,
          billId: id,
        });
      } else {
        await queryClient.invalidateQueries({ queryKey: ['cloud-detail', props.billId] });
      }
    },
  });

  const loading = detailQuery.isFetching || createNewDataQuery.isFetching;

  return (
    <EditPage
      {...props}
      onSave={() => saveMutation.mutate()}
      onCancel={() => removeContentTab(props.appNumber, props.tabKey)}
      sections={[
        {
          key: 'basic',
          title: '基本信息',
          content: (
            <Spin loading={loading}>
              <Form className="sm-edit-form" layout="vertical">
                <Form.Item label="编码" required>
                  <Input
                    value={formState.number}
                    placeholder="请输入编码"
                    onChange={(number) => setFormPatch((current) => ({ ...current, number }))}
                  />
                </Form.Item>
                <Form.Item label="名称" required>
                  <Input
                    value={formState.name}
                    placeholder="请输入名称"
                    onChange={(name) => setFormPatch((current) => ({ ...current, name }))}
                  />
                </Form.Item>
                <Form.Item label="排序">
                  <InputNumber
                    value={formState.seq}
                    min={0}
                    onChange={(seq) => setFormPatch((current) => ({ ...current, seq }))}
                  />
                </Form.Item>
                <Form.Item label="启用">
                  <Switch
                    checked={formState.enableFlag}
                    onChange={(enableFlag) => setFormPatch((current) => ({ ...current, enableFlag }))}
                  />
                </Form.Item>
              </Form>
            </Spin>
          ),
        },
      ]}
    />
  );
};

const CloudPage = (props: PageComponentProps) =>
  props.operationType || props.billId || props.temporary ? <CloudEditPage {...props} /> : <CloudListPage {...props} />;

export default CloudPage;

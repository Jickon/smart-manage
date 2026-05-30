import { useMemo, useState } from 'react';
import {
  Checkbox,
  Form,
  Input,
  InputNumber,
  Message,
  Modal,
  Select,
  Spin,
  Switch,
  Table,
  Tag,
  Tree,
} from '@arco-design/web-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { appApi } from '@/api/app';
import { cloudApi } from '@/api/cloud';
import { EditPage, ListPage, OperationType } from '@/cloud/common/page';
import { createBillTabKey } from '@/cloud/common/page/tabKeys';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import type { PageComponentProps } from '@/cloud/common/page';
import type { AppDetailVO, AppListVO, AppSaveForm, CloudSelectVO } from '@/types/api';
import type { ColumnProps } from '@arco-design/web-react/es/Table/interface';
import type { TreeDataType } from '@arco-design/web-react/es/Tree/interface';

const DEFAULT_PAGE_SIZE = 20;
const ALL_CLOUD_KEY = '__all_clouds__';
const TextArea = Input.TextArea;

interface AppFormState {
  id?: string;
  number: string;
  name: string;
  icon?: string;
  iconColor?: string;
  seq?: number;
  description?: string;
  cloudId?: string;
  enableFlag: boolean;
}

const emptyAppForm = (): AppFormState => ({
  number: '',
  name: '',
  icon: '',
  iconColor: '#165dff',
  seq: 99,
  description: '',
  cloudId: undefined,
  enableFlag: true,
});

const toAppForm = (detail: Partial<AppDetailVO>): AppFormState => ({
  id: detail.id,
  number: detail.number ?? '',
  name: detail.name ?? '',
  icon: detail.icon ?? '',
  iconColor: detail.iconColor ?? '#165dff',
  seq: detail.seq ?? 99,
  description: detail.description ?? '',
  cloudId: detail.cloud?.id,
  enableFlag: detail.enableFlag ?? true,
});

const toAppSaveForm = (formState: AppFormState): AppSaveForm => {
  if (!formState.cloudId) {
    throw new Error('请选择所属云');
  }
  return {
    id: formState.id,
    number: formState.number.trim(),
    name: formState.name.trim(),
    icon: formState.icon?.trim(),
    iconColor: formState.iconColor?.trim(),
    seq: formState.seq,
    description: formState.description?.trim(),
    cloudId: formState.cloudId,
    enableFlag: formState.enableFlag,
  };
};

const AppCloudTree = ({
  clouds,
  loading,
  selectedCloudId,
  includeChildren,
  showDisabled,
  keyword,
  onKeywordChange,
  onCloudChange,
  onIncludeChildrenChange,
  onShowDisabledChange,
}: {
  clouds: CloudSelectVO[];
  loading: boolean;
  selectedCloudId?: string;
  includeChildren: boolean;
  showDisabled: boolean;
  keyword: string;
  onKeywordChange: (keyword: string) => void;
  onCloudChange: (cloudId?: string) => void;
  onIncludeChildrenChange: (checked: boolean) => void;
  onShowDisabledChange: (checked: boolean) => void;
}) => {
  const treeData: TreeDataType[] = useMemo(
    () => [
      {
        key: ALL_CLOUD_KEY,
        title: '全部云',
        children: clouds.map((cloud) => ({
          key: cloud.id,
          title: `${cloud.name}（${cloud.number}）`,
        })),
      },
    ],
    [clouds],
  );

  const selectedKeys = [selectedCloudId ?? ALL_CLOUD_KEY];

  return (
    <>
      <div className="sm-list-tree-search">
        <Input.Search
          allowClear
          placeholder="搜索云编码/名称"
          value={keyword}
          onChange={onKeywordChange}
          onSearch={onKeywordChange}
        />
      </div>
      <Spin className="sm-list-tree-body" loading={loading}>
        <Tree
          blockNode
          size="small"
          treeData={treeData}
          selectedKeys={selectedKeys}
          defaultExpandedKeys={[ALL_CLOUD_KEY]}
          onSelect={(keys) => onCloudChange(keys[0] === ALL_CLOUD_KEY ? undefined : keys[0])}
        />
      </Spin>
      <div className="sm-list-tree-actions">
        <Checkbox checked={includeChildren} onChange={(checked) => onIncludeChildrenChange(Boolean(checked))}>
          包含下级
        </Checkbox>
        <Checkbox checked={showDisabled} onChange={(checked) => onShowDisabledChange(Boolean(checked))}>
          显示停用
        </Checkbox>
      </div>
    </>
  );
};

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

const AppEditPage = (props: PageComponentProps) => {
  const [formPatch, setFormPatch] = useState<Partial<AppFormState>>({});
  const queryClient = useQueryClient();
  const replaceContentTab = useAppWorkspaceStore((store) => store.replaceContentTab);
  const removeContentTab = useAppWorkspaceStore((store) => store.removeContentTab);

  const isAddNew = props.operationType === OperationType.ADDNEW;
  const detailQuery = useQuery({
    queryKey: ['app-detail', props.billId],
    queryFn: () => appApi.detail(props.billId ?? ''),
    enabled: Boolean(props.billId) && !isAddNew,
  });
  const createNewDataQuery = useQuery({
    queryKey: ['app-create-new-data', props.tabKey],
    queryFn: appApi.createNewData,
    enabled: isAddNew,
  });
  const cloudOptionsQuery = useQuery({
    queryKey: ['cloud-select-options'],
    queryFn: () => cloudApi.select({ pageNum: 1, pageSize: 200, enableFlag: true }),
  });

  const baseFormState = useMemo(() => {
    if (detailQuery.data) {
      return toAppForm(detailQuery.data);
    }
    if (createNewDataQuery.data) {
      return { ...emptyAppForm(), ...createNewDataQuery.data };
    }
    return emptyAppForm();
  }, [createNewDataQuery.data, detailQuery.data]);

  const formState = { ...baseFormState, ...formPatch };

  const saveMutation = useMutation({
    mutationFn: () => appApi.save(toAppSaveForm(formState)),
    onSuccess: async (id) => {
      Message.success('保存成功');
      await queryClient.invalidateQueries({ queryKey: ['app-list-page'] });
      if (props.temporary || isAddNew) {
        replaceContentTab(props.appNumber, props.tabKey, {
          key: createBillTabKey(props.componentKey, id),
          label: formState.name || '应用详情',
          closable: true,
          componentKey: props.componentKey,
          pageType: 'EDIT',
          operationType: OperationType.EDIT,
          billId: id,
        });
      } else {
        await queryClient.invalidateQueries({ queryKey: ['app-detail', props.billId] });
      }
    },
    onError: (error) => Message.error(error.message),
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
                <Form.Item label="所属云" required>
                  <Select
                    allowClear
                    loading={cloudOptionsQuery.isFetching}
                    value={formState.cloudId}
                    placeholder="请选择所属云"
                    onChange={(cloudId) =>
                      setFormPatch((current) => ({
                        ...current,
                        cloudId: typeof cloudId === 'string' ? cloudId : undefined,
                      }))
                    }
                  >
                    {(cloudOptionsQuery.data?.records ?? []).map((cloud) => (
                      <Select.Option key={cloud.id} value={cloud.id}>
                        {cloud.name}
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item label="图标">
                  <Input
                    value={formState.icon}
                    placeholder="请输入图标标识"
                    onChange={(icon) => setFormPatch((current) => ({ ...current, icon }))}
                  />
                </Form.Item>
                <Form.Item label="图标颜色">
                  <Input
                    value={formState.iconColor}
                    placeholder="请输入颜色值"
                    onChange={(iconColor) => setFormPatch((current) => ({ ...current, iconColor }))}
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
                <Form.Item label="描述">
                  <TextArea
                    value={formState.description}
                    placeholder="请输入描述"
                    autoSize={{ minRows: 3, maxRows: 5 }}
                    onChange={(description) => setFormPatch((current) => ({ ...current, description }))}
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

const AppPage = (props: PageComponentProps) =>
  props.operationType || props.billId || props.temporary ? <AppEditPage {...props} /> : <AppListPage {...props} />;

export default AppPage;

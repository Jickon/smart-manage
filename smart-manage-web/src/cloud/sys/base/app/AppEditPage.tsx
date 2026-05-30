import { useMemo, useState } from 'react';
import { Form, Input, InputNumber, Message, Select, Spin, Switch } from '@arco-design/web-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { EditPage, OperationType } from '@/cloud/common/page';
import { createBillTabKey } from '@/cloud/common/page/tabKeys';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { appApi } from './api';
import { cloudApi } from '../cloud/api';
import type { PageComponentProps } from '@/cloud/common/page';
import type { AppDetailVO, AppSaveForm } from './types';

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

export default AppEditPage;

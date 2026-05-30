import { useMemo, useState } from 'react';
import { Form, Input, InputNumber, Message, Spin, Switch } from '@arco-design/web-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { EditPage, OperationType } from '@/cloud/common/page';
import { createBillTabKey } from '@/cloud/common/page/tabKeys';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { cloudApi } from './api';
import type { PageComponentProps } from '@/cloud/common/page';
import type { CloudDetailVO, CloudSaveForm } from './types';

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

export default CloudEditPage;

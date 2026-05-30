import { useMemo, useState } from 'react';
import { Form, Input, Message, Spin, Tag } from '@arco-design/web-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { EditPage, OperationType } from '@/cloud/common/page';
import { getMissingRequiredField, requiredFieldMessage } from '@/cloud/common/page/formValidation';
import { createBillTabKey } from '@/cloud/common/page/tabKeys';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { sysParamApi } from './api';
import type { PageComponentProps } from '@/cloud/common/page';
import type { SysParamDetailVO, SysParamSaveForm } from './types';

const TextArea = Input.TextArea;

interface SysParamFormState {
  id?: string;
  number: string;
  name: string;
  value?: string;
  remark?: string;
  isSystem?: boolean;
}

const emptySysParamForm = (): SysParamFormState => ({
  number: '',
  name: '',
  value: '',
  remark: '',
  isSystem: false,
});

const toSysParamForm = (detail: Partial<SysParamDetailVO>): SysParamFormState => ({
  id: detail.id,
  number: detail.number ?? '',
  name: detail.name ?? '',
  value: detail.value ?? '',
  remark: detail.remark ?? '',
  isSystem: detail.isSystem ?? false,
});

const toSysParamSaveForm = (formState: SysParamFormState): SysParamSaveForm => ({
  id: formState.id,
  number: formState.number.trim(),
  name: formState.name.trim(),
  value: formState.value?.trim(),
  remark: formState.remark?.trim(),
});

const validateSysParamForm = (formState: SysParamFormState) => {
  const missingField = getMissingRequiredField([
    { label: '编码', value: formState.number },
    { label: '名称', value: formState.name },
  ]);
  if (missingField) {
    Message.warning(requiredFieldMessage(missingField));
    return false;
  }
  return true;
};

const SysParamEditPage = (props: PageComponentProps) => {
  const [formPatch, setFormPatch] = useState<Partial<SysParamFormState>>({});
  const queryClient = useQueryClient();
  const replaceContentTab = useAppWorkspaceStore((store) => store.replaceContentTab);
  const removeContentTab = useAppWorkspaceStore((store) => store.removeContentTab);

  const isAddNew = props.operationType === OperationType.ADDNEW;
  const detailQuery = useQuery({
    queryKey: ['sys-param-detail', props.billId],
    queryFn: () => sysParamApi.detail(props.billId ?? ''),
    enabled: Boolean(props.billId) && !isAddNew,
  });
  const createNewDataQuery = useQuery({
    queryKey: ['sys-param-create-new-data', props.tabKey],
    queryFn: sysParamApi.createNewData,
    enabled: isAddNew,
  });

  const baseFormState = useMemo(() => {
    if (detailQuery.data) {
      return toSysParamForm(detailQuery.data);
    }
    if (createNewDataQuery.data) {
      return toSysParamForm(createNewDataQuery.data);
    }
    return emptySysParamForm();
  }, [createNewDataQuery.data, detailQuery.data]);

  const formState = { ...baseFormState, ...formPatch };

  const saveMutation = useMutation({
    mutationFn: () => sysParamApi.save(toSysParamSaveForm(formState)),
    onSuccess: async (id) => {
      Message.success('保存成功');
      await queryClient.invalidateQueries({ queryKey: ['sys-param-list-page'] });
      if (props.temporary || isAddNew) {
        replaceContentTab(props.appNumber, props.tabKey, {
          key: createBillTabKey(props.componentKey, id),
          label: formState.name || '系统参数详情',
          closable: true,
          componentKey: props.componentKey,
          pageType: 'EDIT',
          operationType: OperationType.EDIT,
          billId: id,
        });
      } else {
        await queryClient.invalidateQueries({ queryKey: ['sys-param-detail', props.billId] });
      }
    },
    onError: (error) => Message.error(error.message),
  });

  const loading = detailQuery.isFetching || createNewDataQuery.isFetching;

  return (
    <EditPage
      {...props}
      submitVisible={false}
      onSave={() => {
        if (validateSysParamForm(formState)) {
          saveMutation.mutate();
        }
      }}
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
                <Form.Item label="类型">
                  <Tag color={formState.isSystem ? 'arcoblue' : 'green'}>
                    {formState.isSystem ? '系统参数' : '自定义参数'}
                  </Tag>
                </Form.Item>
                <Form.Item label="参数值">
                  <TextArea
                    value={formState.value}
                    placeholder="请输入参数值"
                    autoSize={{ minRows: 3, maxRows: 6 }}
                    onChange={(value) => setFormPatch((current) => ({ ...current, value }))}
                  />
                </Form.Item>
                <Form.Item label="备注">
                  <TextArea
                    value={formState.remark}
                    placeholder="请输入备注"
                    autoSize={{ minRows: 3, maxRows: 6 }}
                    onChange={(remark) => setFormPatch((current) => ({ ...current, remark }))}
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

export default SysParamEditPage;

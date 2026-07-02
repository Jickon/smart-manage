import { useMemo } from 'react';
import { message } from 'antd';
import { useQuery } from '@tanstack/react-query';
import EditPage from '@/domain/common/page/EditPage';
import { OperationType } from '@/domain/common/page/types';
import type { EditField } from '@/domain/common/page/EditPage';
import { useWorkbenchStore } from '@/stores/workbench';
import { appApi, fetchAppsAll } from './api';
import type { PageComponentProps } from '@/domain/common/page/types';

/** 应用编辑字段定义 */
const fields: EditField[] = [
  {
    label: '编码',
    dataIndex: 'number',
    type: 'text',
    rules: [{ required: true, message: '编码不能为空' }],
  },
  {
    label: '名称',
    dataIndex: 'name',
    type: 'text',
    rules: [{ required: true, message: '名称不能为空' }],
  },
  {
    label: '所属云',
    dataIndex: 'cloudId',
    type: 'select',
    rules: [{ required: true, message: '所属云不能为空' }],
  },
  { label: '图标', dataIndex: 'icon', type: 'text' },
  { label: '图标颜色', dataIndex: 'iconColor', type: 'text', placeholder: '如 #1677ff' },
  { label: '排序', dataIndex: 'seq', type: 'number' },
  { label: '描述', dataIndex: 'description', type: 'textarea', width: '100%' },
  { label: '启用', dataIndex: 'enableFlag', type: 'switch' },
  { label: '创建时间', dataIndex: 'createTime', type: 'readonly' },
  { label: '更新时间', dataIndex: 'updateTime', type: 'readonly' },
];

/** 应用编辑页 — 独立页形态，无单据状态 */
const AppEditPage = (props: PageComponentProps) => {
  const { appNumber, tabKey, operationType, billId } = props;
  const isAddNew = operationType === OperationType.ADDNEW;
  const replaceContentTab = useWorkbenchStore((s) => s.replaceContentTab);
  const activateContentTab = useWorkbenchStore((s) => s.activateContentTab);

  // 详情查询（仅编辑模式）
  const detailQuery = useQuery({
    queryKey: ['app-detail', billId],
    queryFn: () => appApi.detail(billId!),
    enabled: !!billId,
  });

  // 云选项查询
  const cloudQuery = useQuery({
    queryKey: ['cloud-apps-all'],
    queryFn: fetchAppsAll,
    staleTime: 5 * 60 * 1000,
  });

  const detail = detailQuery.data;
  const cloudOptions = useMemo(
    () => cloudQuery.data?.map((c) => ({ label: c.name, value: Number(c.id) })) ?? [],
    [cloudQuery.data],
  );

  // Form 初始值，从详情数据派生
  const initialValues = useMemo(() => {
    if (!detail) return {};
    return {
      number: detail.number ?? '',
      name: detail.name ?? '',
      cloudId: detail.cloud ? Number(detail.cloud.id) : undefined,
      icon: detail.icon ?? '',
      iconColor: detail.iconColor ?? '',
      seq: detail.seq ?? undefined,
      description: detail.description ?? '',
      enableFlag: detail.enableFlag ?? true,
      createTime: detail.createTime ?? '',
      updateTime: detail.updateTime ?? '',
    };
  }, [detail]);

  const handleSave = async (values: Record<string, unknown>) => {
    const name = (values.name as string).trim();
    const number = (values.number as string).trim();
    const savedId = await appApi.save({
      id: billId ? Number(billId) : undefined,
      name,
      number,
      icon: (values.icon as string) ?? '',
      iconColor: (values.iconColor as string) ?? '',
      seq: (values.seq as number) ?? 0,
      description: (values.description as string) ?? '',
      cloudId: values.cloudId as number,
      enableFlag: Boolean(values.enableFlag),
    });
    // 新增成功后替换临时 tab key
    if (isAddNew && tabKey !== String(savedId)) {
      replaceContentTab(appNumber, tabKey, {
        key: `bill:${props.componentKey}:${savedId}`,
        label: name,
        closable: true,
        componentKey: props.componentKey,
        pageType: 'EDIT',
        operationType: OperationType.EDIT,
        billId: String(savedId),
      });
      activateContentTab(appNumber, `bill:${props.componentKey}:${savedId}`);
    }
    message.success(isAddNew ? '新增成功' : '保存成功');
  };

  // 为 cloudId select 注入动态选项
  const resolvedFields = useMemo(
    () => fields.map((f) => (f.dataIndex === 'cloudId' ? { ...f, options: cloudOptions } : f)),
    [cloudOptions],
  );

  return (
    <EditPage
      title="应用管理"
      fields={resolvedFields}
      initialValues={initialValues}
      operationType={operationType ?? OperationType.EDIT}
      loading={detailQuery.isLoading}
      error={detailQuery.error as Error | null}
      onRetry={() => detailQuery.refetch()}
      onSave={handleSave}
      onExit={() => {
        useWorkbenchStore.getState().removeContentTab(appNumber, tabKey);
      }}
    />
  );
};

export default AppEditPage;

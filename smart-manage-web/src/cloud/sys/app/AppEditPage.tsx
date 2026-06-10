import { useState } from 'react';
import { message } from 'antd';
import { useQuery } from '@tanstack/react-query';
import EditPage from '@/cloud/common/page/EditPage';
import { OperationType } from '@/cloud/common/page/types';
import type { EditField } from '@/cloud/common/page/EditPage';
import { useWorkbenchStore } from '@/stores/workbench';
import { appApi, fetchAppsAll } from './api';
import type { PageComponentProps } from '@/cloud/common/page/types';

/** 应用编辑字段定义 */
const fields: EditField[] = [
  { label: '编码', dataIndex: 'number', type: 'text', required: true },
  { label: '名称', dataIndex: 'name', type: 'text', required: true },
  { label: '所属云', dataIndex: 'cloudId', type: 'select', required: true },
  { label: '图标', dataIndex: 'icon', type: 'text' },
  { label: '图标颜色', dataIndex: 'iconColor', type: 'text', placeholder: '如 #1677ff' },
  { label: '排序', dataIndex: 'seq', type: 'number' },
  { label: '描述', dataIndex: 'description', type: 'textarea' },
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
  const [edits, setEdits] = useState<Record<string, unknown>>({});

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
  const cloudOptions =
    cloudQuery.data?.map((c) => ({ label: c.name, value: Number(c.id) })) ?? [];

  // 派生值：用户编辑优先，其次详情数据，最后默认值
  const values: Record<string, unknown> = {
    number: edits.number ?? detail?.number ?? '',
    name: edits.name ?? detail?.name ?? '',
    cloudId: edits.cloudId ?? (detail?.cloud ? Number(detail.cloud.id) : undefined),
    icon: edits.icon ?? detail?.icon ?? '',
    iconColor: edits.iconColor ?? detail?.iconColor ?? '',
    seq: edits.seq ?? detail?.seq ?? null,
    description: edits.description ?? detail?.description ?? '',
    enableFlag: edits.enableFlag ?? detail?.enableFlag ?? true,
    createTime: detail?.createTime ?? '',
    updateTime: detail?.updateTime ?? '',
  };

  const handleChange = (dataIndex: string, value: unknown) => {
    setEdits((prev) => ({ ...prev, [dataIndex]: value }));
  };

  const handleSave = async () => {
    const name = (values.name as string) ?? '';
    const number = (values.number as string) ?? '';
    const cloudId = (values.cloudId as number) ?? 0;
    if (!name.trim() || !number.trim() || !cloudId) {
      message.warning('名称、编码和所属云不能为空');
      return Promise.reject();
    }
    const savedId = await appApi.save({
      id: billId ? Number(billId) : undefined,
      name: name.trim(),
      number: number.trim(),
      icon: (values.icon as string) ?? '',
      iconColor: (values.iconColor as string) ?? '',
      seq: (values.seq as number) ?? 0,
      description: (values.description as string) ?? '',
      cloudId,
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

  const resolvedFields = fields.map((f) =>
    f.dataIndex === 'cloudId' ? { ...f, options: cloudOptions } : f,
  );

  return (
    <EditPage
      title="应用管理"
      fields={resolvedFields}
      values={values}
      onChange={handleChange}
      operationType={operationType ?? OperationType.EDIT}
      loading={detailQuery.isLoading}
      error={detailQuery.error as Error | null}
      onRetry={() => detailQuery.refetch()}
      onSave={handleSave}
      onCancel={() => {
        useWorkbenchStore.getState().removeContentTab(appNumber, tabKey);
      }}
    />
  );
};

export default AppEditPage;

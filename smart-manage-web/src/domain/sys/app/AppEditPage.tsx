import { useMemo } from 'react';
import { message } from 'antd';
import { useQuery } from '@tanstack/react-query';
import EditPage from '@/domain/common/page/EditPage';
import { OperationType } from '@/domain/common/page/types';
import type { EditField } from '@/domain/common/page/EditPage';
import { useWorkbenchStore } from '@/stores/workbench';
import { appApi } from './api';
import { cloudApi } from '@/domain/sys/cloud/api';
import type { CloudSelectVO } from '@/domain/sys/cloud/types';
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
    dataIndex: 'cloud',
    type: 'ref-selector',
    rules: [{ required: true, message: '所属云不能为空' }],
    refSelector: {
      selectorKey: 'sys-cloud',
      modalTitle: '选择所属云',
      // 使用专用的 select 接口（区别于列表页的 listPage）
      fetchFn: (params) =>
        cloudApi
          .select({ pageNum: params.pageNum, pageSize: params.pageSize, keyword: params.keyword })
          .then((res) => res as unknown as { records: Record<string, unknown>[]; total: number }),
      displayRender: (record) => (record as unknown as CloudSelectVO).name,
      fieldNames: { key: 'id', label: 'name' },
      columns: [
        { title: '编码', dataIndex: 'number', width: 160 },
        { title: '名称', dataIndex: 'name', width: 200 },
      ],
    },
  },
  { label: '图标', dataIndex: 'icon', type: 'text' },
  { label: '图标颜色', dataIndex: 'iconColor', type: 'text', placeholder: '如 #1677ff' },
  { label: '排序', dataIndex: 'seq', type: 'number' },
  { label: '描述', dataIndex: 'description', type: 'textarea', fullWidth: true },
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

  const detail = detailQuery.data;

  // Form 初始值，从详情数据派生
  const initialValues = useMemo(() => {
    if (!detail) return {};
    return {
      number: detail.number ?? '',
      name: detail.name ?? '',
      // RefSelector 传整个 cloud 对象（包含 id/number/name 供 displayRender 使用）
      cloud: detail.cloud ?? null,
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
    // RefSelector 传整个对象，从中提取 cloud
    const cloud = values.cloud as { id: string } | null;
    if (!cloud?.id) throw new Error('所属云不能为空');
    const savedId = await appApi.save({
      id: billId ?? undefined,
      name,
      number,
      icon: (values.icon as string) ?? '',
      iconColor: (values.iconColor as string) ?? '',
      seq: (values.seq as number) ?? 0,
      description: (values.description as string) ?? '',
      // 雪花 ID 保持字符串，前端不转 Number
      cloudId: cloud.id,
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

  return (
    <EditPage
      title="应用管理"
      fields={fields}
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

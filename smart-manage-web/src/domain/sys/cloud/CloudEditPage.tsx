import { useMemo } from 'react';
import { message } from 'antd';
import { useQuery } from '@tanstack/react-query';
import ModalEditPage from '@/domain/common/page/ModalEditPage';
import type { EditField } from '@/domain/common/page/EditPage';
import { cloudApi } from './api';

interface Props {
  open: boolean;
  cloudId: string | null;
  onClose: () => void;
  onSaved: () => void;
}

/** 云编辑字段定义 */
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
  { label: '排序', dataIndex: 'seq', type: 'number' },
  { label: '启用', dataIndex: 'enableFlag', type: 'switch' },
];

/** 云编辑弹框 */
const CloudEditPage = ({ open, cloudId, onClose, onSaved }: Props) => {
  const isAddNew = cloudId === null;

  const detailQuery = useQuery({
    queryKey: ['cloud-detail', cloudId],
    queryFn: () => cloudApi.detail(cloudId!),
    enabled: Boolean(open && cloudId),
    staleTime: 0,
  });

  const detail = detailQuery.data;

  // Form 初始值，从详情数据派生
  const initialValues = useMemo(() => {
    if (!detail) return {};
    return {
      number: detail.number ?? '',
      name: detail.name ?? '',
      seq: detail.seq ?? undefined,
      enableFlag: detail.enableFlag ?? true,
    };
  }, [detail]);

  const handleSave = async (values: Record<string, unknown>) => {
    await cloudApi.save({
      id: cloudId ? Number(cloudId) : undefined,
      name: (values.name as string).trim(),
      number: (values.number as string).trim(),
      seq: (values.seq as number) ?? 0,
      enableFlag: Boolean(values.enableFlag),
    });
    message.success(isAddNew ? '新增成功' : '保存成功');
    onSaved();
  };

  return (
    <ModalEditPage
      title={isAddNew ? '新增云' : '编辑云'}
      open={open}
      onClose={onClose}
      fields={fields}
      initialValues={initialValues}
      onSave={handleSave}
      loading={detailQuery.isLoading}
      error={detailQuery.error as Error | null}
      onRetry={() => detailQuery.refetch()}
    />
  );
};

export default CloudEditPage;

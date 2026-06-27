import { useState } from 'react';
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
  { label: '编码', dataIndex: 'number', type: 'text', required: true },
  { label: '名称', dataIndex: 'name', type: 'text', required: true },
  { label: '排序', dataIndex: 'seq', type: 'number' },
  { label: '启用', dataIndex: 'enableFlag', type: 'switch' },
];

/** 云编辑弹框 */
const CloudEditPage = ({ open, cloudId, onClose, onSaved }: Props) => {
  const isAddNew = cloudId === null;
  const [saving, setSaving] = useState(false);
  const [edits, setEdits] = useState<Record<string, unknown>>({});

  const detailQuery = useQuery({
    queryKey: ['cloud-detail', cloudId],
    queryFn: () => cloudApi.detail(cloudId!),
    enabled: Boolean(open && cloudId),
    staleTime: 0,
  });

  const detail = detailQuery.data;

  const values: Record<string, unknown> = {
    number: edits.number ?? detail?.number ?? '',
    name: edits.name ?? detail?.name ?? '',
    seq: edits.seq ?? detail?.seq ?? null,
    enableFlag: edits.enableFlag ?? detail?.enableFlag ?? true,
  };

  const handleChange = (dataIndex: string, value: unknown) => {
    setEdits((prev) => ({ ...prev, [dataIndex]: value }));
  };

  const handleClose = () => {
    if (saving) return;
    setEdits({});
    onClose();
  };

  const handleSave = async () => {
    const name = (values.name as string) ?? '';
    const number = (values.number as string) ?? '';
    if (!name.trim() || !number.trim()) {
      message.warning('名称和编码不能为空');
      return;
    }
    setSaving(true);
    try {
      await cloudApi.save({
        id: cloudId ? Number(cloudId) : undefined,
        name: name.trim(),
        number: number.trim(),
        seq: (values.seq as number) ?? 0,
        enableFlag: Boolean(values.enableFlag),
      });
      message.success(isAddNew ? '新增成功' : '保存成功');
      setEdits({});
      onSaved();
      onClose();
    } catch {
      message.error('保存失败');
    } finally {
      setSaving(false);
    }
  };

  return (
    <ModalEditPage
      title={isAddNew ? '新增云' : '编辑云'}
      open={open}
      onClose={handleClose}
      fields={fields}
      values={values}
      onChange={handleChange}
      onSave={handleSave}
      loading={detailQuery.isLoading}
      error={detailQuery.error as Error | null}
      onRetry={() => detailQuery.refetch()}
    />
  );
};

export default CloudEditPage;

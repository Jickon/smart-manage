import { useState } from 'react';
import { Modal, Input, InputNumber, Switch, Spin, message } from 'antd';
import { useQuery } from '@tanstack/react-query';
import { cloudApi } from './api';

interface Props {
  open: boolean;
  cloudId: string | null;
  onClose: () => void;
  onSaved: () => void;
}

/** 云编辑 Modal — 无状态的基础数据，保存即生效 */
const CloudEditPage = ({ open, cloudId, onClose, onSaved }: Props) => {
  const isAddNew = cloudId === null;
  const [saving, setSaving] = useState(false);
  const [edits, setEdits] = useState<Record<string, unknown>>({});

  // 通过 TanStack Query 加载详情（仅编辑模式）
  const detailQuery = useQuery({
    queryKey: ['cloud-detail', cloudId],
    queryFn: () => cloudApi.detail(cloudId!),
    enabled: Boolean(open && cloudId),
    staleTime: 0,
  });

  const detail = detailQuery.data;

  // 派生值：用户编辑优先，否则用详情数据，再否则用默认值
  const number = (edits.number ?? detail?.number ?? '') as string;
  const name = (edits.name ?? detail?.name ?? '') as string;
  const seq = (edits.seq ?? detail?.seq ?? null) as number | null;
  const enableFlag = (edits.enableFlag ?? detail?.enableFlag ?? true) as boolean;

  const handleChange = (field: string, value: unknown) => {
    setEdits((prev) => ({ ...prev, [field]: value }));
  };

  const handleClose = () => {
    if (saving) return;
    // 重置编辑缓存
    setEdits({});
    onClose();
  };

  const handleSave = async () => {
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
        seq: seq ?? 0,
        enableFlag,
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
    <Modal
      title={isAddNew ? '新增云' : '编辑云'}
      open={open}
      onCancel={handleClose}
      onOk={handleSave}
      confirmLoading={saving}
      okText="保存"
      cancelText="取消"
      destroyOnClose
      width={480}
    >
      {detailQuery.isError ? (
        <div style={{ textAlign: 'center', padding: 16 }}>加载云信息失败，请重试。</div>
      ) : (
        <Spin spinning={detailQuery.isLoading}>
          <div className="sm-edit-fields">
            <div className="sm-edit-field">
              <label className="sm-edit-field-label">
                <span className="sm-edit-required">*</span>编码
              </label>
              <Input
                value={number}
                onChange={(e) => handleChange('number', e.target.value)}
                placeholder="请输入编码"
              />
            </div>
            <div className="sm-edit-field">
              <label className="sm-edit-field-label">
                <span className="sm-edit-required">*</span>名称
              </label>
              <Input
                value={name}
                onChange={(e) => handleChange('name', e.target.value)}
                placeholder="请输入名称"
              />
            </div>
            <div className="sm-edit-field">
              <label className="sm-edit-field-label">排序</label>
              <InputNumber
                value={seq}
                onChange={(v) => handleChange('seq', v)}
                placeholder="请输入排序"
                style={{ width: '100%' }}
              />
            </div>
            <div className="sm-edit-field">
              <label className="sm-edit-field-label">启用</label>
              <Switch
                checked={enableFlag}
                onChange={(v) => handleChange('enableFlag', v)}
              />
            </div>
          </div>
        </Spin>
      )}
    </Modal>
  );
};

export default CloudEditPage;

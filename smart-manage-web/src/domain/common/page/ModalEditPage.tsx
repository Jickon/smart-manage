import { useState } from 'react';
import { Spin, Button, Modal, Input, InputNumber, Select, Switch, Result } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import type { EditField } from './EditPage';
import './EditPage.css';
import './ModalEditPage.css';

const { TextArea } = Input;

interface ModalEditPageProps {
  title: string;
  open: boolean;
  onClose: () => void;
  fields: EditField[];
  values: Record<string, unknown>;
  onChange: (dataIndex: string, value: unknown) => void;
  onSave: () => Promise<void>;
  loading?: boolean;
  error?: Error | null;
  onRetry?: () => void;
  width?: number;
}

/** 通用 Modal 编辑模板 — 三段式布局：标题栏 + 可滚动字段区 + 底部按钮 */
const ModalEditPage = ({
  title,
  open,
  onClose,
  fields,
  values,
  onChange,
  onSave,
  loading = false,
  error = null,
  onRetry,
  width = 700,
}: ModalEditPageProps) => {
  const [saving, setSaving] = useState(false);

  const handleSave = async () => {
    setSaving(true);
    try {
      await onSave();
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      title={
        <div className="sm-modal-title-bar">
          <span style={{ flex: 1 }}>{title}</span>
          <Button type="text" icon={<CloseOutlined />} onClick={onClose} />
        </div>
      }
      closeIcon={null}
      open={open}
      onCancel={onClose}
      centered
      maskClosable={false}
      className="sm-modal"
      destroyOnClose
      width={width}
      footer={
        <div className="sm-modal-footer-inner">
          <Button onClick={onClose} disabled={saving}>
            取消
          </Button>
          <Button type="primary" loading={saving} onClick={handleSave}>
            保存
          </Button>
        </div>
      }
    >
      {error ? (
        <Result
          status="error"
          title="加载失败"
          subTitle={error.message || '请检查网络连接后重试'}
          extra={
            onRetry && (
              <Button type="primary" onClick={onRetry}>
                重试
              </Button>
            )
          }
        />
      ) : (
        <Spin spinning={loading}>
          <div className="sm-edit-fields">
            {fields.map((field) => {
              const value = values[field.dataIndex];

              const renderField = () => {
                switch (field.type) {
                  case 'text':
                    return (
                      <Input
                        value={(value as string) ?? ''}
                        onChange={(event) => onChange(field.dataIndex, event.target.value)}
                        placeholder={field.placeholder}
                      />
                    );
                  case 'number':
                    return (
                      <InputNumber
                        className="sm-edit-control-full"
                        value={(value as number) ?? undefined}
                        onChange={(nextValue) => onChange(field.dataIndex, nextValue)}
                        placeholder={field.placeholder}
                      />
                    );
                  case 'switch':
                    return (
                      <Switch
                        checked={Boolean(value)}
                        onChange={(nextValue) => onChange(field.dataIndex, nextValue)}
                      />
                    );
                  case 'textarea':
                    return (
                      <TextArea
                        value={(value as string) ?? ''}
                        onChange={(event) => onChange(field.dataIndex, event.target.value)}
                        placeholder={field.placeholder}
                        rows={3}
                      />
                    );
                  case 'select':
                    return (
                      <Select
                        className="sm-edit-control-full"
                        value={(value as string | number) ?? undefined}
                        onChange={(nextValue) => onChange(field.dataIndex, nextValue)}
                        placeholder={field.placeholder}
                        options={field.options}
                      />
                    );
                  default:
                    return null;
                }
              };

              return (
                <div
                  key={field.dataIndex}
                  className="sm-edit-field"
                  style={
                    field.type === 'switch'
                      ? { flex: '0 0 auto', width: 'auto' }
                      : field.width
                        ? { width: field.width }
                        : undefined
                  }
                >
                  <label className="sm-edit-field-label">
                    {field.label}
                    {field.required && <span className="sm-edit-required">*</span>}
                  </label>
                  <div className="sm-edit-field-control">{renderField()}</div>
                </div>
              );
            })}
          </div>
        </Spin>
      )}
    </Modal>
  );
};

export default ModalEditPage;

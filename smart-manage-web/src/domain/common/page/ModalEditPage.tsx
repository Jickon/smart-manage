import { useState, useEffect } from 'react';
import { Spin, Button, Modal, Input, InputNumber, Select, Switch, Result, Form } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import type { EditField } from './EditPage';
import RefSelector from '@/domain/common/component/RefSelector';
import './EditPage.css';
import './ModalEditPage.css';

const { TextArea } = Input;

interface ModalEditPageProps {
  title: string;
  open: boolean;
  onClose: () => void;
  fields: EditField[];
  /** 初始值（详情数据回显），Form 内部通过 setFieldsValue 同步 */
  initialValues?: Record<string, unknown>;
  /** 保存回调，接收 Form 校验通过后的字段值 */
  onSave: (values: Record<string, unknown>) => Promise<void>;
  loading?: boolean;
  error?: Error | null;
  onRetry?: () => void;
  width?: number;
}

/** 只读字段展示组件 — Form.Item 自动注入 value 属性 */
function ReadonlyText({ value }: { value?: unknown }) {
  return <span className="sm-edit-readonly">{value != null ? String(value) : '-'}</span>;
}

/** 根据字段类型渲染表单控件 */
function renderFormControl(field: EditField, disabled: boolean) {
  switch (field.type) {
    case 'text':
      return <Input variant="underlined" placeholder={field.placeholder} disabled={disabled} />;
    case 'number':
      return (
        <InputNumber
          variant="underlined"
          className="sm-edit-control-full"
          placeholder={field.placeholder}
          disabled={disabled}
        />
      );
    case 'switch':
      return <Switch disabled={disabled} />;
    case 'textarea':
      return (
        <TextArea
          variant="underlined"
          placeholder={field.placeholder}
          disabled={disabled}
          rows={3}
        />
      );
    case 'select':
      return (
        <Select
          variant="underlined"
          className="sm-edit-control-full"
          placeholder={field.placeholder}
          disabled={disabled}
          options={field.options}
        />
      );
    case 'ref-selector':
      return (
        <RefSelector<Record<string, unknown>>
          placeholder={field.placeholder}
          disabled={disabled}
          selectorKey={field.refSelector.selectorKey}
          modalTitle={field.refSelector.modalTitle}
          fetchFn={field.refSelector.fetchFn}
          displayRender={field.refSelector.displayRender}
          fieldNames={field.refSelector.fieldNames}
          columns={field.refSelector.columns}
          mode={field.refSelector.mode}
          pageSize={field.refSelector.pageSize}
          treeData={field.refSelector.treeData}
          treeFieldNames={field.refSelector.treeFieldNames}
        />
      );
    default:
      return null;
  }
}

/** 通用 Modal 编辑模板 — 三段式布局：标题栏 + 可滚动字段区 + 底部按钮，使用 antd Form 驱动校验 */
const ModalEditPage = ({
  title,
  open,
  onClose,
  fields,
  initialValues,
  onSave,
  loading = false,
  error = null,
  onRetry,
  width = 700,
}: ModalEditPageProps) => {
  const [form] = Form.useForm();
  const [saving, setSaving] = useState(false);

  // Modal 打开且数据加载完成后同步到 Form
  useEffect(() => {
    if (open && !loading && initialValues) {
      form.setFieldsValue(initialValues);
    }
  }, [form, initialValues, loading, open]);

  // Modal 关闭时重置 Form（处理新增场景，避免旧数据残留）
  useEffect(() => {
    if (!open) {
      form.resetFields();
    }
  }, [form, open]);

  const handleClose = () => {
    if (saving) return;
    onClose();
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const values = await form.validateFields();
      await onSave(values);
    } catch (err) {
      // 校验失败不向上传播
      if ((err as { errorFields?: unknown[] }).errorFields) return;
      throw err;
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      title={
        <div className="sm-modal-title-bar">
          <span style={{ flex: 1 }}>{title}</span>
          <Button type="link" icon={<CloseOutlined />} onClick={handleClose} />
        </div>
      }
      closeIcon={null}
      open={open}
      onCancel={handleClose}
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
          <Form form={form} layout="vertical" className="sm-edit-form">
            <div className="sm-edit-fields">
              {fields.map((field) => {
                const disabled = field.disabled || false;

                // readonly 字段用 ReadonlyText 组件展示纯文本
                if (field.type === 'readonly') {
                  return (
                    <Form.Item
                      key={field.dataIndex}
                      name={field.dataIndex}
                      label={field.label}
                      className="sm-edit-field"
                      style={field.width ? { width: field.width } : undefined}
                    >
                      <ReadonlyText />
                    </Form.Item>
                  );
                }

                const valuePropName = field.type === 'switch' ? 'checked' : undefined;

                return (
                  <Form.Item
                    key={field.dataIndex}
                    name={field.dataIndex}
                    label={field.label}
                    rules={field.rules}
                    valuePropName={valuePropName}
                    className="sm-edit-field"
                    style={
                      field.type === 'switch'
                        ? { flex: '0 0 auto', width: 'auto' }
                        : field.width
                          ? { width: field.width }
                          : undefined
                    }
                  >
                    {renderFormControl(field, disabled)}
                  </Form.Item>
                );
              })}
            </div>
          </Form>
        </Spin>
      )}
    </Modal>
  );
};

export default ModalEditPage;

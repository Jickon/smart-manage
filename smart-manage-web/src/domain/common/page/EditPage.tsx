import { useState, useEffect } from 'react';
import {
  Spin,
  Button,
  Space,
  Input,
  InputNumber,
  Select,
  Switch,
  Result,
  Collapse,
  Form,
} from 'antd';
import type { Rule } from 'antd/es/form';
import type { ReactNode } from 'react';
import { OperationType, BillStatus } from './types';
import './EditPage.css';

const { TextArea } = Input;

/** 编辑字段定义 */
export interface EditField {
  label: string;
  dataIndex: string;
  type: 'text' | 'number' | 'switch' | 'textarea' | 'select' | 'readonly';
  /** antd Form 校验规则，如 [{ required: true, message: '编码不能为空' }] */
  rules?: Rule[];
  disabled?: boolean;
  /** 占位提示 */
  placeholder?: string;
  /** select 选项 */
  options?: { label: string; value: string | number }[];
  /** 字段宽度，默认 260px；可设 "100%" 占整行、"50%" 占半行 等 */
  width?: string;
}

interface EditPageProps {
  title: string;
  fields: EditField[];
  /** 初始值（详情数据回显），Form 内部通过 setFieldsValue 同步 */
  initialValues?: Record<string, unknown>;
  /** 单据状态（无状态的基础数据不传） */
  billStatus?: BillStatus;
  operationType: OperationType;
  loading?: boolean;
  error?: Error | null;
  onRetry?: () => void;
  /** 保存回调，接收 Form 校验通过后的字段值 */
  onSave?: (values: Record<string, unknown>) => Promise<void>;
  /** 提交回调，接收 Form 校验通过后的字段值 */
  onSubmit?: (values: Record<string, unknown>) => Promise<void>;
  onExit?: () => void;
  /** 自定义头部操作区 */
  headerExtra?: ReactNode;
}

/** 是否可编辑：暂存或新增时允许编辑 */
function isEditable(opType: OperationType, status?: BillStatus): boolean {
  if (opType === OperationType.VIEW) return false;
  if (opType === OperationType.ADDNEW) return true;
  return status === BillStatus.SAVED || status === undefined;
}

/** 只读字段展示组件 — Form.Item 自动注入 value 属性 */
function ReadonlyText({ value }: { value?: unknown }) {
  return <span className="sm-edit-readonly">{value != null ? String(value) : '-'}</span>;
}

/** 根据字段类型渲染表单控件（不包裹 Form.Item），由外部 Form.Item 注入 value/onChange */
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
    default:
      return null;
  }
}

/** 通用编辑页 — 使用 antd Form 驱动校验与字段状态 */
const EditPage = ({
  fields,
  initialValues,
  billStatus,
  operationType,
  loading = false,
  error = null,
  onRetry,
  onSave,
  onSubmit,
  onExit,
  headerExtra,
}: EditPageProps) => {
  const [form] = Form.useForm();
  const editable = isEditable(operationType, billStatus);
  const [saving, setSaving] = useState(false);

  // 后端数据加载完成后同步到 Form
  useEffect(() => {
    if (!loading && initialValues) {
      form.setFieldsValue(initialValues);
    }
  }, [form, initialValues, loading]);

  const handleSave = async () => {
    if (!onSave) return;
    setSaving(true);
    try {
      const values = await form.validateFields();
      await onSave(values);
    } catch (err) {
      // 校验失败（errorFields 非空）不向上传播
      if ((err as { errorFields?: unknown[] }).errorFields) return;
      throw err;
    } finally {
      setSaving(false);
    }
  };

  const handleSubmit = async () => {
    if (!onSubmit) return;
    setSaving(true);
    try {
      const values = await form.validateFields();
      await onSubmit(values);
    } catch (err) {
      if ((err as { errorFields?: unknown[] }).errorFields) return;
      throw err;
    } finally {
      setSaving(false);
    }
  };

  if (error) {
    return (
      <section className="sm-common-page sm-edit-page">
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
      </section>
    );
  }

  return (
    <section className="sm-common-page sm-edit-page">
      {/* 顶部操作区固定展示，单据内容在下方独立滚动 */}
      <div className="sm-edit-header">
        <div className="sm-edit-header-actions">
          {headerExtra}
          {editable && (
            <Space>
              {onSave && (
                <Button type="primary" loading={saving} onClick={handleSave}>
                  保存
                </Button>
              )}
              {onSubmit && (
                <Button type="primary" loading={saving} onClick={handleSubmit}>
                  提交
                </Button>
              )}
              {onExit && <Button onClick={onExit}>退出</Button>}
            </Space>
          )}
        </div>
      </div>

      {/* 单据内容区 */}
      <div className="sm-edit-body">
        <Spin spinning={loading}>
          <Collapse
            className="sm-edit-collapse"
            defaultActiveKey={['basic']}
            items={[
              {
                key: 'basic',
                label: '基本信息',
                children: (
                  <Form form={form} layout="vertical" className="sm-edit-form">
                    <div className="sm-edit-fields">
                      {fields.map((field) => {
                        const disabled = field.disabled || !editable;

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

                        // Switch 字段需要 valuePropName="checked"
                        const valuePropName = field.type === 'switch' ? 'checked' : undefined;

                        return (
                          <Form.Item
                            key={field.dataIndex}
                            name={field.dataIndex}
                            label={field.label}
                            rules={field.rules}
                            valuePropName={valuePropName}
                            className="sm-edit-field"
                            style={field.width ? { width: field.width } : undefined}
                          >
                            {renderFormControl(field, disabled)}
                          </Form.Item>
                        );
                      })}
                    </div>
                  </Form>
                ),
              },
            ]}
          />
        </Spin>
      </div>
    </section>
  );
};

export default EditPage;

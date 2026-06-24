import { useState } from 'react';
import {
  Spin,
  Button,
  Space,
  Input,
  InputNumber,
  Select,
  Switch,
  Tag,
  Result,
  Collapse,
} from 'antd';
import type { ReactNode } from 'react';
import { OperationType, BillStatus } from './types';
import './EditPage.css';

const { TextArea } = Input;

/** 编辑字段定义 */
export interface EditField {
  label: string;
  dataIndex: string;
  type: 'text' | 'number' | 'switch' | 'textarea' | 'select' | 'readonly';
  required?: boolean;
  disabled?: boolean;
  /** 占位提示 */
  placeholder?: string;
  /** select 选项 */
  options?: { label: string; value: string | number }[];
}

interface EditPageProps {
  title: string;
  fields: EditField[];
  values: Record<string, unknown>;
  onChange: (dataIndex: string, value: unknown) => void;
  /** 单据状态（无状态的基础数据不传） */
  billStatus?: BillStatus;
  operationType: OperationType;
  loading?: boolean;
  error?: Error | null;
  onRetry?: () => void;
  onSave?: () => Promise<void>;
  onSubmit?: () => Promise<void>;
  onCancel?: () => void;
  /** 自定义头部操作区 */
  headerExtra?: ReactNode;
}

/** 单据状态文案映射 */
const billStatusMap: Record<string, { label: string; color: string }> = {
  [BillStatus.SAVED]: { label: '暂存', color: 'default' },
  [BillStatus.SUBMITTED]: { label: '已提交', color: 'processing' },
  [BillStatus.AUDITED]: { label: '审核通过', color: 'success' },
  [BillStatus.CLOSED]: { label: '已关闭', color: 'warning' },
};

/** 是否可编辑：暂存或新增时允许编辑 */
function isEditable(opType: OperationType, status?: BillStatus): boolean {
  if (opType === OperationType.VIEW) return false;
  if (opType === OperationType.ADDNEW) return true;
  // EDIT 模式只有暂存状态可编辑
  return status === BillStatus.SAVED || status === undefined;
}

/** 通用编辑页 — 不使用 Form 表单，直接字段布局 */
const EditPage = ({
  title,
  fields,
  values,
  onChange,
  billStatus,
  operationType,
  loading = false,
  error = null,
  onRetry,
  onSave,
  onSubmit,
  onCancel,
  headerExtra,
}: EditPageProps) => {
  const editable = isEditable(operationType, billStatus);
  const statusInfo = billStatus ? billStatusMap[billStatus] : null;
  const [saving, setSaving] = useState(false);

  const handleSave = async () => {
    if (!onSave) return;
    setSaving(true);
    try {
      await onSave();
    } finally {
      setSaving(false);
    }
  };

  const handleSubmit = async () => {
    if (!onSubmit) return;
    setSaving(true);
    try {
      await onSubmit();
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
      {/* 顶部操作区固定展示，单据内容在下方独立滚动。 */}
      <div className="sm-edit-header">
        <div className="sm-edit-header-actions">
          {headerExtra}
          {editable && (
            <Space>
              {onCancel && <Button onClick={onCancel}>取消</Button>}
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
            </Space>
          )}
        </div>
        <div className="sm-edit-header-meta">
          <h3 className="sm-edit-title">{title}</h3>
          {statusInfo && <Tag color={statusInfo.color}>{statusInfo.label}</Tag>}
          <Tag>
            {operationType === OperationType.ADDNEW
              ? '新增'
              : operationType === OperationType.VIEW
                ? '查看'
                : '编辑'}
          </Tag>
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
                  <div className="sm-edit-fields">
                    {fields.map((field) => {
                      const value = values[field.dataIndex];
                      const disabled = field.disabled || !editable;

                      const renderField = () => {
                        switch (field.type) {
                          case 'readonly':
                            return (
                              <span className="sm-edit-readonly">
                                {value != null ? String(value) : '-'}
                              </span>
                            );
                          case 'text':
                            return (
                              <Input
                                value={(value as string) ?? ''}
                                onChange={(event) => onChange(field.dataIndex, event.target.value)}
                                placeholder={field.placeholder}
                                disabled={disabled}
                              />
                            );
                          case 'number':
                            return (
                              <InputNumber
                                className="sm-edit-control-full"
                                value={(value as number) ?? undefined}
                                onChange={(nextValue) => onChange(field.dataIndex, nextValue)}
                                placeholder={field.placeholder}
                                disabled={disabled}
                              />
                            );
                          case 'switch':
                            return (
                              <Switch
                                checked={Boolean(value)}
                                onChange={(nextValue) => onChange(field.dataIndex, nextValue)}
                                disabled={disabled}
                              />
                            );
                          case 'textarea':
                            return (
                              <TextArea
                                value={(value as string) ?? ''}
                                onChange={(event) => onChange(field.dataIndex, event.target.value)}
                                placeholder={field.placeholder}
                                disabled={disabled}
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
                                disabled={disabled}
                                options={field.options}
                              />
                            );
                          default:
                            return null;
                        }
                      };

                      return (
                        <div key={field.dataIndex} className="sm-edit-field">
                          <label className="sm-edit-field-label">
                            {field.required && <span className="sm-edit-required">*</span>}
                            {field.label}
                          </label>
                          <div className="sm-edit-field-control">{renderField()}</div>
                        </div>
                      );
                    })}
                  </div>
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

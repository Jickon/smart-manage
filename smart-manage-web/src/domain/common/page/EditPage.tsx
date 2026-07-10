import { useEffect } from 'react';
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
import RefSelector from '@/domain/common/component/RefSelector';
import { useCommandMutation } from './useCommandMutation';
import './EditPage.css';

const { TextArea } = Input;

/** 编辑字段公共属性 */
export interface EditFieldBase {
  label: string;
  dataIndex: string;
  /** antd Form 校验规则，如 [{ required: true, message: '编码不能为空' }] */
  rules?: Rule[];
  disabled?: boolean;
  /** 占位提示 */
  placeholder?: string;
  /** 是否占满整行 */
  fullWidth?: boolean;
}

/** RefSelector 字段配置 — type === 'ref-selector' 时必填 */
export interface RefSelectorFieldConfig {
  /** 选择器标识，用于隔离不同实例的查询缓存 */
  selectorKey: string | readonly unknown[];
  mode?: 'default' | 'multiple' | 'tree-table';
  modalTitle: string;
  /** 数据获取函数，传入分页/搜索参数，返回分页结果 */
  fetchFn: (params: {
    pageNum: number;
    pageSize: number;
    keyword?: string;
    parentId?: string;
  }) => Promise<{ records: Record<string, unknown>[]; total: number }>;
  /** 选中记录的展示渲染 */
  displayRender: (record: Record<string, unknown>) => ReactNode;
  /** 字段名映射 */
  fieldNames: { key: string; label: string };
  /** 表格列定义 */
  columns: {
    title: string;
    dataIndex: string;
    width?: number | string;
    render?: (text: unknown, record: Record<string, unknown>, index: number) => ReactNode;
  }[];
  /** 每页条数，默认 20 */
  pageSize?: number;
  /** 树表模式：树形数据 */
  treeData?: Record<string, unknown>[];
  /** 树表模式：树字段映射 */
  treeFieldNames?: { key: string; title: string; children: string };
}

/** 编辑字段定义 — 按 type 分流为判别联合类型 */
export type EditField = EditFieldBase &
  (
    | { type: 'text' }
    | { type: 'password' }
    | { type: 'number' }
    | { type: 'switch' }
    | { type: 'textarea' }
    | { type: 'select'; options?: { label: string; value: string | number }[] }
    | { type: 'readonly' }
    | { type: 'ref-selector'; refSelector: RefSelectorFieldConfig }
  );

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
    case 'password':
      return (
        <Input.Password variant="underlined" placeholder={field.placeholder} disabled={disabled} />
      );
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
  const commandMutation = useCommandMutation();

  // 后端数据加载完成后同步到 Form
  useEffect(() => {
    if (!loading && initialValues) {
      form.setFieldsValue(initialValues);
    }
  }, [form, initialValues, loading]);

  const handleSave = async () => {
    if (!onSave) return;
    try {
      const values = await form.validateFields();
      await commandMutation.mutateAsync({ command: onSave, values });
    } catch (err) {
      // 表单校验和命令异常均已由 Form/useMutation 处理。
      if ((err as { errorFields?: unknown[] }).errorFields) return;
    }
  };

  const handleSubmit = async () => {
    if (!onSubmit) return;
    try {
      const values = await form.validateFields();
      await commandMutation.mutateAsync({ command: onSubmit, values });
    } catch (err) {
      if ((err as { errorFields?: unknown[] }).errorFields) return;
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
                <Button type="primary" loading={commandMutation.isPending} onClick={handleSave}>
                  保存
                </Button>
              )}
              {onSubmit && (
                <Button type="primary" loading={commandMutation.isPending} onClick={handleSubmit}>
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
                              className={`sm-edit-field${field.fullWidth ? ' sm-edit-field--full' : ''}`}
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
                            className={`sm-edit-field${field.fullWidth ? ' sm-edit-field--full' : ''}`}
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

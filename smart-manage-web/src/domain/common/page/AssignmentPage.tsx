import type { ReactNode } from 'react';
import { Button, Result, Space, Spin } from 'antd';
import './EditPage.css';

interface AssignmentPageProps {
  loading: boolean;
  saving: boolean;
  error?: Error | null;
  children: ReactNode;
  onSave: () => void;
  onExit: () => void;
  onRetry: () => void;
}

/** 关系分配专用页面框架，操作区与内容区严格分离。 */
export function AssignmentPage({
  loading,
  saving,
  error,
  children,
  onSave,
  onExit,
  onRetry,
}: AssignmentPageProps) {
  if (error) {
    return (
      <section className="sm-common-page sm-edit-page">
        <Result
          status="error"
          title="加载失败"
          subTitle={error.message}
          extra={<Button onClick={onRetry}>重试</Button>}
        />
      </section>
    );
  }
  return (
    <section className="sm-common-page sm-edit-page">
      <div className="sm-edit-header">
        <div className="sm-edit-header-actions">
          <Space>
            <Button type="primary" loading={saving} onClick={onSave}>
              保存
            </Button>
            <Button onClick={onExit}>退出</Button>
          </Space>
        </div>
      </div>
      <div className="sm-edit-body">
        <Spin spinning={loading}>{children}</Spin>
      </div>
    </section>
  );
}

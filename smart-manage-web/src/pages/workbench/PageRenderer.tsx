import { Suspense } from 'react';
import { Empty, Spin } from 'antd';
import { componentRegistry } from '@/cloud/common/registry/componentRegistry';
import type { OperationType } from '@/cloud/common/page/types';

interface Props {
  appNumber: string;
  tabKey: string;
  title: string;
  componentKey?: string;
  operationType?: OperationType;
  billId?: string;
  temporary?: boolean;
}

/** 页面渲染器 — 通过组件注册表映射真实组件 */
const PageRenderer = ({
  componentKey,
  appNumber,
  tabKey,
  title,
  operationType,
  billId,
  temporary,
}: Props) => {
  if (!componentKey) {
    return (
      <div className="sm-page-renderer-empty">
        <Empty description="页面未配置组件" />
      </div>
    );
  }

  const registration = componentRegistry[componentKey];
  if (!registration) {
    return (
      <div className="sm-page-renderer-empty">
        <Empty description={`未注册页面：${componentKey}`} />
      </div>
    );
  }

  const RegisteredComponent = registration.component;
  return (
    <Suspense
      fallback={
        <div className="sm-page-renderer-empty">
          <Spin />
        </div>
      }
    >
      <RegisteredComponent
        appNumber={appNumber}
        componentKey={componentKey}
        tabKey={tabKey}
        title={title}
        operationType={operationType}
        billId={billId}
        temporary={temporary}
      />
    </Suspense>
  );
};

export default PageRenderer;

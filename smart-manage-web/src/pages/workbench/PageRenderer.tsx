import { Empty } from 'antd';
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

/** 页面渲染器 — 占位实现，后续通过组件注册表映射真实组件 */
const PageRenderer = ({ componentKey }: Props) => {
  if (!componentKey) {
    return (
      <div className="sm-page-renderer-empty">
        <Empty description="页面未配置组件" />
      </div>
    );
  }

  // TODO: 通过 componentRegistry[componentKey] 获取注册组件并渲染
  return (
    <div className="sm-page-renderer-empty">
      <Empty description={`${componentKey} — 页面开发中`} />
    </div>
  );
};

export default PageRenderer;

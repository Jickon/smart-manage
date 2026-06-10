import * as Icons from '@ant-design/icons';
import type { ReactNode } from 'react';

/** 动态解析 icon 字段为 antd Icon 组件 — icon 值需与 @ant-design/icons 导出名一致 */
export function resolveIcon(name: string | undefined): ReactNode | undefined {
  if (!name) return undefined;
  const iconMap = Icons as unknown as Record<string, React.ComponentType>;
  const IconComponent = iconMap[name];
  return IconComponent ? <IconComponent /> : undefined;
}

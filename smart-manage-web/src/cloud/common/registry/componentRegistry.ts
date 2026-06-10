import type { ComponentType, LazyExoticComponent } from 'react';
import type { PageComponentProps, PageType } from '@/cloud/common/page/types';

/** 组件注册表 — 由 pageRegistration.ts 文件注册，禁止手动修改 */
export const componentRegistry: Record<
  string,
  {
    pageType: PageType;
    component:
      | ComponentType<PageComponentProps>
      | LazyExoticComponent<ComponentType<PageComponentProps>>;
  }
> = {};

/** 注册页面组件 */
export function definePageRegistration(
  componentKey: string,
  pageType: PageType,
  component:
    | ComponentType<PageComponentProps>
    | LazyExoticComponent<ComponentType<PageComponentProps>>,
) {
  componentRegistry[componentKey] = { pageType, component };
  return { componentKey, pageType, component };
}

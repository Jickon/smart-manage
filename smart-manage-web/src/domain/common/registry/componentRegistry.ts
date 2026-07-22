import type { ComponentType, LazyExoticComponent } from 'react';
import type { PageComponentProps, PageType } from '@/domain/common/page/types';

export interface PageRegistration {
  componentKey: string;
  pageType: PageType;
  component:
    | ComponentType<PageComponentProps>
    | LazyExoticComponent<ComponentType<PageComponentProps>>;
}

/** 页面组件白名单，由各业务模块的 pageRegistration.ts 显式声明。 */
export const componentRegistry: Record<string, Omit<PageRegistration, 'componentKey'>> = {};

/**
 * 声明一个业务模块包含的全部页面。
 * 页面组件保持一文件一组件，pageRegistration.ts 只承担模块级清单职责。
 */
export function definePageRegistrations(
  registrations: readonly PageRegistration[],
): readonly PageRegistration[] {
  if (registrations.length === 0) {
    throw new Error('[registry] 页面注册清单不能为空。');
  }
  const moduleKeys = new Set<string>();
  for (const { componentKey } of registrations) {
    if (moduleKeys.has(componentKey)) {
      throw new Error(`[registry] 模块清单内的 componentKey "${componentKey}" 重复。`);
    }
    moduleKeys.add(componentKey);
  }
  return registrations;
}

/** 汇总所有模块清单并写入运行时白名单。 */
export function registerPageRegistrationModules(
  modules: readonly (readonly PageRegistration[])[],
): void {
  if (modules.length === 0) {
    throw new Error('[registry] 未发现页面注册模块。');
  }
  for (const registrations of modules) {
    for (const { componentKey, pageType, component } of registrations) {
      if (componentRegistry[componentKey]) {
        throw new Error(`[registry] componentKey "${componentKey}" 重复注册。`);
      }
      componentRegistry[componentKey] = { pageType, component };
    }
  }
}

import type { ComponentType, LazyExoticComponent } from 'react';
import type { PageComponentProps, PageType } from '@/domain/common/page/types';

/** 组件注册表 — 由 pageRegistration.ts 文件注册，构建期 gen:registry 自动生成导入 */
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
  // 运行时重复 key 检测（构建期 gen:registry 也会检测，此处为双重保险）
  if (componentRegistry[componentKey]) {
    throw new Error(
      `[registry] componentKey "${componentKey}" 重复注册，构建期 gen:registry 应已拦截此错误。`,
    );
  }
  componentRegistry[componentKey] = { pageType, component };
  return { componentKey, pageType, component };
}

/** 构建期 gen:registry 完成后调用，校验所有导入均已注册 */
export function validateRegistry(): void {
  // 此函数由 registry.gen.ts 在导入所有 pageRegistration 后调用
  // 如果 gen:registry 扫描到的文件未正确调用 definePageRegistration，此处为空操作
  const count = Object.keys(componentRegistry).length;
  if (count === 0) {
    console.warn('[registry] 注册表为空，请检查 pageRegistration 文件是否正确。');
  }
}

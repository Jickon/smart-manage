import type { ComponentType, LazyExoticComponent } from 'react';
import type { PageComponentProps, PageRegistration, PageType } from '@/cloud/common/page/types';
import { generatedPageRegistrations } from '@/cloud/common/registry/componentRegistry.generated';

export function definePageRegistration(
  componentKey: string,
  pageType: PageType,
  component: ComponentType<PageComponentProps> | LazyExoticComponent<ComponentType<PageComponentProps>>,
): PageRegistration {
  return { componentKey, pageType, component };
}

export function buildComponentRegistry(registrations: PageRegistration[]): Record<string, PageRegistration> {
  return registrations.reduce<Record<string, PageRegistration>>((registry, registration) => {
    if (registry[registration.componentKey]) {
      throw new Error(`页面组件重复注册：${registration.componentKey}`);
    }
    registry[registration.componentKey] = registration;
    return registry;
  }, {});
}

export const componentRegistry: Record<string, PageRegistration> = buildComponentRegistry(generatedPageRegistrations);

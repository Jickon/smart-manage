import { lazy } from 'react';
import { definePageRegistration } from '@/cloud/common/registry/componentRegistry';

const AppListPage = lazy(() => import('./AppListPage'));
const AppEditPage = lazy(() => import('./AppEditPage'));

// 菜单 component 字段值为 sys/base/app，与注册 key 保持一致
definePageRegistration('sys/base/app', 'LIST', AppListPage);
definePageRegistration('sys/base/app/edit', 'EDIT', AppEditPage);

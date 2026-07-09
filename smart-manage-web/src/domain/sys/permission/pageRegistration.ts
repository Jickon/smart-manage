import { lazy } from 'react';
import { definePageRegistration } from '@/domain/common/registry/componentRegistry';

const PermissionListPage = lazy(() => import('./PermissionListPage'));
export default definePageRegistration('sys/base/permission', 'LIST', PermissionListPage);

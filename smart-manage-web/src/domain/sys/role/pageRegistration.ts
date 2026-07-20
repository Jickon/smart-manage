import { lazy } from 'react';
import { definePageRegistration } from '@/domain/common/registry/componentRegistry';

const RoleListPage = lazy(() => import('./RoleListPage'));
const RoleEditPage = lazy(() => import('./RoleEditPage'));
const RolePermissionAssignmentPage = lazy(() => import('./RolePermissionAssignmentPage'));

definePageRegistration('sys/base/role', 'LIST', RoleListPage);
definePageRegistration('sys/base/role/edit', 'EDIT', RoleEditPage);
definePageRegistration(
  'sys/base/role/permission-assignment',
  'CUSTOM',
  RolePermissionAssignmentPage,
);

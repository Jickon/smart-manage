import { lazy } from 'react';
import { definePageRegistration } from '@/domain/common/registry/componentRegistry';

const UserListPage = lazy(() => import('./UserListPage'));
const UserEditPage = lazy(() => import('./UserEditPage'));
const UserRoleAssignmentPage = lazy(() => import('./UserRoleAssignmentPage'));

definePageRegistration('sys/base/user', 'LIST', UserListPage);
definePageRegistration('sys/base/user/edit', 'EDIT', UserEditPage);
definePageRegistration('sys/base/user/role-assignment', 'CUSTOM', UserRoleAssignmentPage);

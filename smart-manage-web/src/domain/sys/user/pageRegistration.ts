import { lazy } from 'react';
import { definePageRegistration } from '@/domain/common/registry/componentRegistry';

const UserListPage = lazy(() => import('./UserListPage'));
const UserEditPage = lazy(() => import('./UserEditPage'));

definePageRegistration('sys/base/user', 'LIST', UserListPage);
definePageRegistration('sys/base/user/edit', 'EDIT', UserEditPage);

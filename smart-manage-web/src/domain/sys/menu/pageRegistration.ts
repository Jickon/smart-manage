import { lazy } from 'react';
import { definePageRegistration } from '@/domain/common/registry/componentRegistry';

const MenuListPage = lazy(() => import('./MenuListPage'));
const MenuEditPage = lazy(() => import('./MenuEditPage'));

definePageRegistration('sys/base/menu', 'LIST', MenuListPage);
definePageRegistration('sys/base/menu/edit', 'EDIT', MenuEditPage);

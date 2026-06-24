import { lazy } from 'react';
import { definePageRegistration } from '@/domain/common/registry/componentRegistry';

const CloudListPage = lazy(() => import('./CloudListPage'));
export default definePageRegistration('sys/base/cloud', 'LIST', CloudListPage);

import { lazy } from 'react';
import { definePageRegistration } from '@/cloud/common/registry/componentRegistry';

const CloudPage = lazy(() => import('./CloudPage'));
export default definePageRegistration('sys/base/cloud', 'LIST', CloudPage);

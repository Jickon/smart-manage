import { lazy } from 'react';
import { definePageRegistration } from '@/cloud/common/registry/componentRegistry';

const AppPage = lazy(() => import('./AppPage'));
export default definePageRegistration('sys/base/app', 'LIST', AppPage);

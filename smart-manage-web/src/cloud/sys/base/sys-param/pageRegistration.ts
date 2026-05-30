import { lazy } from 'react';
import { definePageRegistration } from '@/cloud/common/registry/componentRegistry';

const SysParamPage = lazy(() => import('./SysParamPage'));
export default definePageRegistration('sys/base/sys-param', 'LIST', SysParamPage);

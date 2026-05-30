import { lazy } from 'react';
import { definePageRegistration } from '@/cloud/common/registry/componentRegistry';

const FileConfigPage = lazy(() => import('./FileConfigPage'));
export default definePageRegistration('sys/base/file-config', 'CUSTOM', FileConfigPage);

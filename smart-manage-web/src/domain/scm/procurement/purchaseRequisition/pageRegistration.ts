import { lazy } from 'react';
import { definePageRegistration } from '@/domain/common/registry/componentRegistry';

const PurchaseRequisitionListPage = lazy(() => import('./PurchaseRequisitionListPage'));
const PurchaseRequisitionEditPage = lazy(() => import('./PurchaseRequisitionEditPage'));

definePageRegistration('scm/procurement/purchase-requisition', 'LIST', PurchaseRequisitionListPage);
definePageRegistration(
  'scm/procurement/purchase-requisition/edit',
  'EDIT',
  PurchaseRequisitionEditPage,
);

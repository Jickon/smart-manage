import { lazy } from 'react';
import { definePageRegistrations } from '@/domain/common/registry/componentRegistry';

export default definePageRegistrations([
  {
    componentKey: 'scm/procurement/purchase-requisition',
    pageType: 'LIST',
    component: lazy(() => import('./PurchaseRequisitionListPage')),
  },
  {
    componentKey: 'scm/procurement/purchase-requisition/edit',
    pageType: 'EDIT',
    component: lazy(() => import('./PurchaseRequisitionEditPage')),
  },
]);

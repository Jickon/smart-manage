import request from '@/api/request';
import type { PageData, Result } from '@/types/api';
import type {
  PurchaseRequisitionCreateNewDataVO,
  PurchaseRequisitionDetailVO,
  PurchaseRequisitionListForm,
  PurchaseRequisitionListVO,
  PurchaseRequisitionSaveForm,
} from './types';

const baseUrl = '/scm/procurement/purchase-requisition';

export const purchaseRequisitionApi = {
  listPage: (form: PurchaseRequisitionListForm) =>
    request
      .post<Result<PageData<PurchaseRequisitionListVO>>>(`${baseUrl}/listPage`, form)
      .then((response) => response.data.data),
  detail: (id: string) =>
    request
      .post<Result<PurchaseRequisitionDetailVO>>(`${baseUrl}/detail`, { id })
      .then((response) => response.data.data),
  createNewData: () =>
    request
      .get<Result<PurchaseRequisitionCreateNewDataVO>>(`${baseUrl}/createNewData`)
      .then((response) => response.data.data),
  save: (form: PurchaseRequisitionSaveForm) =>
    request.post<Result<string>>(`${baseUrl}/save`, form).then((response) => response.data.data),
  submit: (id: string) =>
    request
      .post<Result<string>>(`${baseUrl}/submit`, { id })
      .then((response) => response.data.data),
  delete: (id: string) =>
    request
      .post<Result<string>>(`${baseUrl}/delete`, { id })
      .then((response) => response.data.data),
};

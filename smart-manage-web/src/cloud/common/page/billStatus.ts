import { BillStatus, OperationType } from '@/cloud/common/page/types';

export function resolveOperationTypeByBillStatus(billStatus: BillStatus | string): OperationType {
  const statusValue = String(billStatus);
  if (statusValue === BillStatus.SAVED.valueOf()) {
    return OperationType.EDIT;
  }
  const readonlyStatusValues = [
    BillStatus.SUBMITTED.valueOf(),
    BillStatus.AUDITED.valueOf(),
    BillStatus.CLOSED.valueOf(),
  ];
  if (readonlyStatusValues.includes(statusValue)) {
    return OperationType.VIEW;
  }
  throw new Error(`未知单据状态：${billStatus}`);
}

const TAB_KEY_SEPARATOR = ':';

export function createListTabKey(componentKey: string) {
  return ['list', componentKey].join(TAB_KEY_SEPARATOR);
}

export function createBillTabKey(componentKey: string, billId: string) {
  return ['bill', componentKey, billId].join(TAB_KEY_SEPARATOR);
}

export function createAddNewTabKey(componentKey: string) {
  return ['addnew', componentKey, crypto.randomUUID()].join(TAB_KEY_SEPARATOR);
}

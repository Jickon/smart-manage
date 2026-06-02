import { useMemo, useState } from 'react';

interface HasId {
  id: string;
}

/**
 * 表格行选中状态管理 — 封装 selectedRowKeys / allSelected / toggleSelectAll
 */
export function useRowSelection<T extends HasId>(records: T[]) {
  const [selectedRowKeys, setSelectedRowKeys] = useState<string[]>([]);

  const allSelected = useMemo(
    () => records.length > 0 && selectedRowKeys.length === records.length,
    [records, selectedRowKeys],
  );

  const toggleSelectAll = (checked: boolean) => {
    setSelectedRowKeys(checked ? records.map((record) => record.id) : []);
  };

  return { selectedRowKeys, setSelectedRowKeys, allSelected, toggleSelectAll };
}

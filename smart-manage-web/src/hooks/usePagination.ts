import { useCallback, useState } from 'react';

interface UsePaginationOptions {
  /** 默认每页条数 */
  defaultPageSize?: number;
}

/**
 * 列表分页状态管理 — 封装 pageNum / pageSize 及搜索时重置页码的通用逻辑
 */
export function usePagination(options: UsePaginationOptions = {}) {
  const { defaultPageSize = 20 } = options;

  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(defaultPageSize);

  /** 搜索/筛选条件变化时，重置到第 1 页 */
  const resetPage = useCallback(() => {
    setPageNum(1);
  }, []);

  return { pageNum, pageSize, setPageNum, setPageSize, resetPage };
}

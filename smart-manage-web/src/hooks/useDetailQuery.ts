import { useQuery, type UseQueryOptions } from '@tanstack/react-query';

/**
 * 详情查询的 TanStack Query useQuery 封装
 * 统一 queryKey 命名规范 [实体名, 'detail', id]
 */
export function useDetailQuery<TData>(
  queryKeyPrefix: string,
  queryFn: (id: string) => Promise<TData>,
  id: string | undefined,
  options?: Omit<UseQueryOptions<TData, Error>, 'queryKey' | 'queryFn' | 'enabled'>,
) {
  return useQuery<TData, Error>({
    queryKey: [queryKeyPrefix, 'detail', id],
    queryFn: () => queryFn(id!),
    enabled: Boolean(id),
    ...options,
  });
}

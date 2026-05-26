import request from './request';
import type { Result, MenuVO } from '@/types/api';

export const menuApi = {
  getUserMenusByAppNumber: (number: string) =>
    request.post<Result<MenuVO>>('/sys/base/menu/getUserMenusByAppNumber', { number }).then((r) => r.data.data),
};

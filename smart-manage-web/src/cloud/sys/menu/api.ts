import request from '@/api/request';
import type { Result, MenuVO } from '@/types/api';

/** 根据应用 number 获取当前用户菜单树 */
export function getUserMenusByAppNumber(number: string) {
  return request
    .post<Result<MenuVO>>('/sys/base/menu/getUserMenusByAppNumber', { number })
    .then((res) => res.data.data);
}

/** 根据应用 id 获取当前用户菜单树 */
export function getUserMenusByAppId(id: string) {
  return request
    .post<Result<MenuVO>>('/sys/base/menu/getUserMenusByAppId', { id })
    .then((res) => res.data.data);
}

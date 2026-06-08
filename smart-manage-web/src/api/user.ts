import request from './request';
import type { Result, UserInfoVO, MenuVO } from '@/types/api';

/** 获取当前用户信息 */
export function getCurrentUser() {
  return request.get<Result<UserInfoVO>>('/sys/base/user/current').then((res) => res.data);
}

/** 获取当前用户菜单 */
export function getUserMenus() {
  return request.get<Result<MenuVO[]>>('/sys/base/menu/current').then((res) => res.data);
}

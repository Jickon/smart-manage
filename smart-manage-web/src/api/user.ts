import request from './request';
import type { Result, UserInfoVO } from '@/types/api';

/** 获取当前用户信息 */
export function getCurrentUser() {
  return request.get<Result<UserInfoVO>>('/sys/base/user/current').then((res) => res.data);
}

/** 按业务前缀获取当前用户权限，超级管理员返回通配符。 */
export function getCurrentPermissions(prefix: string) {
  return request
    .post<Result<string[]>>('/sys/base/user/permissions', { prefix })
    .then((response) => response.data.data);
}

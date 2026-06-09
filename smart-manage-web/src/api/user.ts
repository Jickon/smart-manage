import request from './request';
import type { Result, UserInfoVO } from '@/types/api';

/** 获取当前用户信息 */
export function getCurrentUser() {
  return request.get<Result<UserInfoVO>>('/sys/base/user/current').then((res) => res.data);
}

import request from '@/api/request';
import type { Result } from '@/types/api';
import type { AppVO, CloudAppsVO } from './types';

/** 获取当前用户可访问的应用列表（按领域分组） */
export function fetchApps() {
  return request.get<Result<CloudAppsVO[]>>('/sys/base/app/apps').then((res) => res.data.data);
}

/** 根据应用 number 打开应用详情 */
export function openByNumber(number: string) {
  return request
    .post<Result<AppVO>>('/sys/base/app/openByNumber', { number })
    .then((res) => res.data.data);
}

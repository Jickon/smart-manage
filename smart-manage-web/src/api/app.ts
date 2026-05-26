import request from './request';
import type { Result, CloudAppsVO, AppVO } from '@/types/api';

export const appApi = {
  apps: () => request.get<Result<CloudAppsVO[]>>('/sys/base/app/apps').then((r) => r.data.data),

  openByNumber: (number: string) =>
    request.post<Result<AppVO>>('/sys/base/app/openByNumber', { number }).then((r) => r.data.data),
};

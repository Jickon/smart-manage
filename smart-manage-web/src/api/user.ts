import request from './request';
import type { Result, UserInfoVO } from '@/types/api';

export const userApi = {
  info: () => request.get<Result<UserInfoVO>>('/sys/base/user/info').then((r) => r.data.data),
};

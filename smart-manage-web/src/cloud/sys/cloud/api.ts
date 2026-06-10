import request from '@/api/request';
import type { PageResult, Result } from '@/types/api';
import type { CloudListForm, CloudListVO } from './types';

export const cloudApi = {
  listPage: (form: CloudListForm) =>
    request
      .post<Result<PageResult<CloudListVO>>>('/sys/base/cloud/listPage', form)
      .then((res) => res.data.data),
};

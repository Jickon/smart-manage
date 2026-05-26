import axios from 'axios';
import type { Result } from '@/types/api';

const request = axios.create({
  baseURL: '/smart-manage-api',
  timeout: 30000,
});

/** 请求拦截器 — 注入 Sa-Token */
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers['smtoken'] = token;
  }
  return config;
});

/** 响应拦截器 — 统一错误处理 */
request.interceptors.response.use(
  (response) => {
    const result = response.data as Result;
    if (result.code !== 200) {
      // 未登录，跳转登录页
      if (result.code === 401) {
        const redirectUrl = encodeURIComponent(window.location.href);
        window.location.href = `/login.html?redirect=${redirectUrl}`;
      }
      return Promise.reject(new Error(result.msg));
    }
    return response;
  },
  (error) => Promise.reject(error instanceof Error ? error : new Error(String(error))),
);

export default request;

import type { RouteObject } from 'react-router-dom';
import MainLayout from '@/layouts/MainLayout';
import NotFound from '@/pages/errors/NotFound';

const routes: RouteObject[] = [
  {
    path: '/',
    element: <MainLayout />,
    children: [
      // 业务路由按领域/应用/单据动态注册，此处只定义基础路由占位
    ],
  },
  {
    path: '*',
    element: <NotFound />,
  },
];

export default routes;

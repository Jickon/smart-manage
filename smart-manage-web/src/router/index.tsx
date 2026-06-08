import { lazy, Suspense } from 'react';
import type { RouteObject } from 'react-router-dom';
import MainLayout from '@/layouts/MainLayout';

const NotFound = lazy(() => import('@/pages/errors/NotFound'));

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
    element: (
      <Suspense fallback={null}>
        <NotFound />
      </Suspense>
    ),
  },
];

export default routes;

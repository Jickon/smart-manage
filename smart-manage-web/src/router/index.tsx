import type { RouteObject } from 'react-router-dom';
import MainLayout from '@/layouts/MainLayout';

/** 路由仅包含 MainLayout 作为根布局，所有视图切换由 headerTabsStore 驱动 */
const routes: RouteObject[] = [
  {
    element: <MainLayout />,
    children: [{ path: '*', element: null }],
  },
];

export default routes;

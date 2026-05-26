import type { RouteObject } from 'react-router-dom';
import MainLayout from '@/layouts/MainLayout';

const routes: RouteObject[] = [
  {
    element: <MainLayout />,
    children: [{ path: '*', element: null }],
  },
];

export default routes;

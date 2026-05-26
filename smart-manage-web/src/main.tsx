import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
// Arco Design Less 入口 — CSS 变量覆盖见 src/styles/theme.less
import '@arco-design/web-react/dist/css/index.less';
import '@/styles/global.less';
import App from '@/App';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);

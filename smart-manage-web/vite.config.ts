import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  base: '/',
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  css: {
    preprocessorOptions: {
      less: {
        javascriptEnabled: true,
      },
    },
  },
  build: {
    // chunkSizeWarningLimit: 700,
    rolldownOptions: {
      checks: {
        pluginTimings: false,
      },
      output: {
        codeSplitting: {
          groups: [
            {
              name: 'react-vendor',
              test: /node_modules[\\/](react|react-dom|react-router-dom)[\\/]/,
              priority: 30,
            },
            {
              name: 'arco-vendor',
              test: /node_modules[\\/]@arco-design[\\/]/,
              priority: 20,
            },
            {
              name: 'query-vendor',
              test: /node_modules[\\/]@tanstack[\\/]react-query[\\/]/,
              priority: 10,
            },
            {
              name: 'vendor',
              test: /node_modules[\\/]/,
              priority: 0,
            },
          ],
        },
      },
    },
  },
  server: {
    port: 8000,
    proxy: {
      '/smart-manage-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});

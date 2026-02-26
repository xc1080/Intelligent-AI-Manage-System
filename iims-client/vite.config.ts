import { defineConfig, type UserConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import vueJsx from '@vitejs/plugin-vue-jsx';
import { visualizer } from 'rollup-plugin-visualizer';
import sass from 'vite-plugin-sass-dts';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig(({ command }) => {
  const isDevelopment = command === 'serve';

  const config: UserConfig = {
    base: '/',
    root: process.cwd(),
    build: {
      outDir: 'dist',
      assetsDir: 'static',
      sourcemap: false,
    },
    server: {
      port: 8089,
      open: true,
      host: '0.0.0.0'
    },
    resolve: {
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue'],
      alias: {
        '@': path.resolve(__dirname, 'src')
      },
    },
    plugins: [
      vue(),
      vueJsx(),
      sass(),
      tailwindcss()
    ],
    optimizeDeps: {
      include: ['element-plus']
    },
    esbuild: {
      jsxFactory: 'h',
      jsxFragment: 'Fragment',
      target: 'esnext'
    },
    css: {
      preprocessorOptions: {
        scss: {
          silenceDeprecations: ['legacy-js-api']
        }
      }
    }
  };

  // 只在生产环境添加 visualizer 插件
  if (!isDevelopment) {
    config.plugins?.push(visualizer());
  }

  return config;
});
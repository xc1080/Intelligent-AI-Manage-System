import { createApp } from 'vue';
import App from '../App.vue';
import SvgIcon from '@/components/v-svg-icon/Index.vue';

const app = createApp(App);

// 注册全局组件
app.component('svg-icon', SvgIcon);

// 动态加载 SVG 文件
const req = import.meta.glob('@/assets/svg/*.svg', { query: '?raw', eager: false });

Object.keys(req).forEach((key: string) => {
  req[key]().then((content: any) => {
    const svgElement = document.createElement('div');
    svgElement.innerHTML = content as string;
    document.body.appendChild(svgElement);
  });
});
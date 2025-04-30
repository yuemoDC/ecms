import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'; // 引入 Element Plus CSS


const app = createApp(App);
app.use(router);
app.use(ElementPlus); // 使用 Element Plus
app.mount('#app');

// 在 main.js 或入口文件中添加（确保只执行一次）
window.addEventListener('error', event => {
    if (event.message && event.message.includes('ResizeObserver loop')) {
        event.stopImmediatePropagation();
        event.preventDefault();
    }
});
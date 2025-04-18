import axios from 'axios';

// 创建 Axios 实例
const instance = axios.create({
    baseURL: 'http://localhost:8080', // API 基础地址
    timeout: 5000, // 请求超时时间设为 5000 毫秒
    headers: {
        'Content-Type': 'application/json' // 设置请求的默认 Content-Type 为 JSON
    }
});

// 请求拦截器
instance.interceptors.request.use(
    config => {
        // 在发送请求之前做些什么
        const token = localStorage.getItem('token'); // 从本地存储中获取 token
        if (token) {
            // 如果 token 存在，将其添加到请求头的 Authorization 字段中
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config; // 返回修改后的请求配置
    },
    error => {
        // 对请求错误做些什么
        return Promise.reject(error); // 返回一个拒绝的 Promise，表示请求出错
    }
);

// 响应拦截器
instance.interceptors.response.use(
    response => response, // 直接返回响应数据
    error => {
        // 对响应错误做些什么
        if (error.response) { // 如果有响应
            // 根据状态码处理不同的错误
            switch (error.response.status) {
                case 401:
                    // 未授权，清除 token 并跳转到登录页面
                    localStorage.removeItem('token'); // 清除 token
                    window.location.href = '/login'; // 跳转到登录页面
                    break;
                default:
                    // 处理其他错误
                    console.error('API请求错误:', error); // 打印错误信息
            }
        }
        return Promise.reject(error); // 返回一个拒绝的 Promise，表示响应出错
    }
);

export default instance; // 导出 Axios 实例，供全局使用
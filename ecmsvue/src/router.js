import { createRouter, createWebHistory } from 'vue-router';
import ProductsPage from './views/Products.vue';
import OrdersPage from './views/Orders.vue';
import HomePage from '../src/views/Home.vue';
import UserLoginPage from './views/UserLogin.vue';
import UserRegisterPage from './views/UserRegister.vue';
import MerchantApplicationList from './views/MerchantApplicationList.vue';  // 商家入驻申请管理


const routes = [
    {
        path: '/products',
        name: 'products',
        component: ProductsPage,
        meta: { requiresAuth: true }
    },
    {
        path: '/orders',
        name: 'orders',
        component: OrdersPage,
        meta: { requiresAuth: true }
    },
    {
        path: '/home',
        name: 'home',
        component: HomePage,
        meta: { requiresAuth: true }
    },
    {
        path: '/login',
        name: 'login',
        component: UserLoginPage
    },
    {
        path: '/register',
        name: 'register',
        component: UserRegisterPage
    },
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/merchant-applications',
        name: 'merchant-applications',
        component: MerchantApplicationList,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    const token = localStorage.getItem('token');

    if (requiresAuth && !token) {
        next('/login');
    } else if ((to.path === '/login' || to.path === '/register') && token) {
        next('/home');
    } else {
        next();
    }
});

export default router;

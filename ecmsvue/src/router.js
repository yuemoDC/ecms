import { createRouter, createWebHistory } from 'vue-router';
import ProductsPage from './views/Products.vue';
import OrdersPage from './views/Orders.vue';
import HomePage from '../src/views/Home.vue';
import UserLoginPage from './views/UserLogin.vue';
import UserRegisterPage from './views/UserRegister.vue';
import MerchantApplicationList from './views/MerchantApplicationList.vue';  // 商家入驻申请管理
import MerchantSales from './views/MerchantSales.vue';
import SalesPredictionView from './views/SalesPrediction.vue';
import AdminDataVisualizationView from './views/AdminDataVisualizationView.vue'
import MerchantApplication from "@/views/MerchantApplication.vue";
import BulkSalesPredictionView from'./views/BulkSalesPrediction.vue';
import AIhome from "@/views/AIhome.vue";
import SplashScreen from "@/animation/SplashScreen.vue";
import OutScreen from "@/animation/OutScreen.vue";
import MerchantPrediction  from "@/views/MerchantPrediction.vue";


const routes = [
    {
        path: '/products',
        name: 'products',
        component: ProductsPage,
        meta: { requiresAuth: true }
    },
    {
        path: '/merchant-prediction',
        name: 'merchant-prediction',
        component: MerchantPrediction,
        meta: { requiresAuth: true }
    },
    {
        path: '/orders',
        name: 'orders',
        component: OrdersPage,
        meta: { requiresAuth: true }
    },
    {
        path: '/merchant-sales',
        name: 'merchant-sales',
        component: MerchantSales,
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
    {
        path: '/merchant-application',
        name: 'merchant-application',
        component: MerchantApplication,
        meta: { requiresAuth: true }
    },
    {
        path: '/data-visualization',
        name: 'data-visualization',
        component: AdminDataVisualizationView,
    },
    {
        path: '/data-SalesPrediction',
        name: 'data-SalesPrediction',
        component: SalesPredictionView,
        meta: { transition: 'slide' }
    },
    {
        path: '/bulkPrediction',
        name: 'bulkPrediction',
        component: BulkSalesPredictionView,
        meta: { transition: 'slide' }
    },
    {
        path: '/AIhome',
        name: 'AIhome',
        component: AIhome,
        meta: { transition: 'slide' }
    },
    {
        path: '/SplashScreen',
        name: 'SplashScreen',
        component: SplashScreen,
    },
    {
        path: '/OutScreen',
        name: 'OutScreen',
        component: OutScreen,
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(to, from, savedPosition) {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({
                    ...savedPosition,
                    behavior: 'smooth' // 添加平滑滚动
                });
            }, 1200); // 延长等待时间
        });
    }
});

router.beforeEach((to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    const token = localStorage.getItem('token');

    if (requiresAuth && !token) {
        next('/login');
    } else if ((to.path === '/login' || to.path === '/register') && token) {
        next('/SplashScreen');
    } else {
        next();
    }
});

export default router;

<template>
  <el-header class="navbar-component">
    <div class="navbar"> <!-- 导航栏容器 -->
      <el-menu :default-active="activeIndex" class="menu" @select="handleSelect" mode="horizontal">
        <!-- 设置默认选中的菜单项 -->
        <el-menu-item index="home">首页</el-menu-item> <!-- 仪表盘菜单项 -->
        <el-menu-item index="merchant-applications">商家入驻审批</el-menu-item>
        <el-menu-item index="data-visualization">可视化管理</el-menu-item> <!-- 角色管理菜单项 -->
        <el-sub-menu @click="handleSubmenuClick">
          <template v-slot:title>AI助手</template>
          <el-menu-item index="data-SalesPrediction">商家AI预测</el-menu-item>
          <el-menu-item index="bulkPrediction">商家预测对比</el-menu-item>
        </el-sub-menu>

        <!-- 登出按钮放在最右边 -->
        <div class="logout-wrapper">
          <el-menu-item index="logout" @click="handleLogout">登出</el-menu-item> <!-- 登出菜单项 -->
        </div>
      </el-menu>
    </div>
  </el-header>
</template>

<script>
export default {
  name: 'AppAdminNavbar',
  data() {
    return {
      activeIndex: 'home' // 默认值设为 home
    };
  },
  created() {
    // 检查 localStorage 中是否有 activeIndex，如果没有则设置为 home
    if (!localStorage.getItem('activeIndex')) {
      localStorage.setItem('activeIndex', 'home');
    }
    this.updateActiveIndex(); // 初始化 activeIndex
  },
  watch: {
    '$route.path'() {
      this.updateActiveIndex(); // 路由变化时更新 activeIndex
    }
  },
  methods: {
    updateActiveIndex() {
      const path = this.$route.path;
      if (path === '/home') {
        this.activeIndex = 'home';
      } else if (path === '/merchant-applications') {
        this.activeIndex = 'merchant-applications';
      } else if (path === '/data-visualization') {
        this.activeIndex = 'data-visualization';
      } else if (path === '/systemSettings') {
        this.activeIndex = 'systemSettings';
      } else if (path === '/data-SalesPrediction') {
        this.activeIndex = 'data-SalesPrediction';
      } else {
        this.activeIndex = ''; // 其他情况设为空
      }
      localStorage.setItem('activeIndex', this.activeIndex); // 更新 localStorage
    },
    handleSelect(index) {
      if (index === 'logout') return;

      // 修正路由名称映射
      const routeMap = {
        'home': '/home',
        'merchant-applications': '/merchant-applications',
        'data-visualization': '/data-visualization',
        'data-SalesPrediction': '/data-SalesPrediction',
        'bulkPrediction': '/bulkPrediction'
      };

      // 触发自定义事件
      this.$emit('navigate', routeMap[index] || '/home');
    },
    handleLogout() {
      // 处理用户登出
      localStorage.removeItem('token'); // 清除本地存储中的 token
      localStorage.removeItem('activeIndex'); // 清除保存的状态
      this.activeIndex = 'home'; // 设置默认的激活菜单项
      this.$router.push('/login'); // 跳转到登录页面
    },
    handleSubmenuClick() {
      this.$router.push({
        path: '/AIhome',
        meta: { transition: 'slide' }
      });
    }

  }
};
</script>

<style scoped>
.navbar-component{
  will-change: transform, opacity;
}
</style>
<template>
  <el-header>
    <div class="navbar"> <!-- 导航栏容器 -->
      <el-menu :default-active="activeIndex"
               class="menu"
               @select="handleSelect"
               mode="horizontal"
      >
        <!-- 设置默认选中的菜单项 -->
        <!-- 处理菜单项选择事件 --><!-- 设置菜单模式为水平 -->
        <el-menu-item index="home">首页</el-menu-item> <!-- 首页菜单项 -->
        <el-menu-item index="products">产品管理</el-menu-item> <!-- 产品管理菜单项 -->
        <el-menu-item index="orders">订单管理</el-menu-item> <!-- 订单管理菜单项 -->
        <el-menu-item index="merchant-application">商家入驻申请</el-menu-item>


        <!-- 登出按钮放在最右边 -->
        <div class="logout-wrapper">
          <!-- 日夜模式按钮 -->
          <div class="theme-toggle" @click="toggleTheme">
            {{ isDark ? '☀️ 日间模式' : '🌙 夜间模式' }}
          </div>
          <!-- 登出按钮 -->
          <el-menu-item index="logout" @click="handleLogout">登出</el-menu-item>
        </div>


      </el-menu>
    </div>
  </el-header>
</template>

<script>
export default {
  name: 'AppMerchantNavbar', // 组件名称
  data() {
    return {
      activeIndex: 'home', // 默认值设为 home
      isDark: false
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
      } else if (path === '/products') {
        this.activeIndex = 'products';
      } else if (path === '/orders') {
        this.activeIndex = 'orders';
      }  else if (path === '/merchant-application') {
        this.activeIndex = 'merchant-application';
      }  else {
        this.activeIndex = ''; // 其他情况设为空
      }
      localStorage.setItem('activeIndex', this.activeIndex); // 更新 localStorage
    },
    handleSelect(index) {
      // 当选择菜单项时调用此方法
      if (index === 'logout') return; // 防止重复执行登出逻辑
      this.activeIndex = index; // 更新选中的菜单项
      localStorage.setItem('activeIndex', index);
      this.$router.push({ name: index }); // 根据选中项跳转到对应的路由
    },
    toggleTheme() {
      this.isDark = !this.isDark;
      localStorage.setItem('theme', this.isDark ? 'dark' : 'light');
      this.applyTheme();
    },
    handleLogout() {
      // 处理用户登出
      localStorage.removeItem('token'); // 清除本地存储中的 token
      localStorage.removeItem('activeIndex'); // 清除保存的状态
      this.activeIndex = 'home'; // 设置默认的激活菜单项
      this.$router.push('/login'); // 跳转到登录页面
    },
    applyTheme() {
      document.body.classList.toggle('dark-mode', this.isDark);
    }
  }
};
</script>

<style scoped>
.logout-wrapper {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 20px; /* 添加间隔让按钮有点距离 */
}

.navbar {
  background-color: #409eff;
  color: #ffffff;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 100%;
  border: none;
}

.menu {
  line-height: 60px;
  flex-grow: 1;
  display: flex;
  align-items: center;
  border: none;
}

.theme-toggle {
  padding: 0 20px;
  height: 60px;
  line-height: 60px;
  cursor: pointer;
  color: #000;
  font-size: 14px;
  transition: background-color 0.3s;
  white-space: nowrap;
  user-select: none;
  border: none;
  outline: none;
}

.theme-toggle:hover {
  background-color: rgba(0, 0, 0, 0.1); /* 悬停效果 */
}

.theme-toggle:active {
  background-color: transparent;
  color: inherit;
}

.el-menu .el-menu-item {
  outline: none;
  border: none;
  height: 60px;
  line-height: 60px;
}

.el-menu .el-menu-item:focus {
  outline: none;
  border: none;
}

.el-menu {
  border: none;
  outline: none;
}
</style>

<style>
/* 暗黑模式全局背景 */
body.dark-mode {
  background-color: #000000;
  color: #ffffff;
}
</style>

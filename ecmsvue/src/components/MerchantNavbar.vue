<template>
  <el-header class="navbar-component">
    <div class="navbar">
      <el-menu
          :default-active="activeIndex"
          class="menu"
          @select="handleSelect"
          mode="horizontal"
      >
        <el-menu-item index="home">首页</el-menu-item>
        <el-menu-item index="products">产品管理</el-menu-item>
        <el-menu-item index="orders">订单管理</el-menu-item>
        <el-menu-item index="merchant-application">商家入驻申请</el-menu-item>

        <div class="logout-wrapper">
          <div class="theme-toggle" @click="toggleTheme">
            {{ isDark ? '☀️ 日间模式' : '🌙 夜间模式' }}
          </div>
          <el-menu-item index="logout" @click="handleLogout">登出</el-menu-item>
        </div>
      </el-menu>
    </div>
  </el-header>
</template>

<script>
export default {
  name: 'AppMerchantNavbar',
  data() {
    return {
      activeIndex: 'home',
      isDark: false
    };
  },
  created() {
    if (!localStorage.getItem('activeIndex')) {
      localStorage.setItem('activeIndex', 'home');
    }
    this.updateActiveIndex();
    this.applyTheme();
  },
  watch: {
    '$route.path'() {
      this.updateActiveIndex();
    }
  },
  methods: {
    updateActiveIndex() {
      const path = this.$route.path;
      const indexMap = {
        '/home': 'home',
        '/products': 'products',
        '/orders': 'orders',
        '/merchant-application': 'merchant-application'
      };
      this.activeIndex = indexMap[path] || '';
      localStorage.setItem('activeIndex', this.activeIndex);
    },
    handleSelect(index) {
      if (index === 'logout') return;

      const routeMap = {
        'home': '/home',
        'products': '/products',
        'orders': '/orders',
        'merchant-application': '/merchant-application'
      };

      this.$emit('navigate', routeMap[index] || '/home');
    },
    handleLogout() {
      localStorage.removeItem('token');
      localStorage.removeItem('activeIndex');
      this.activeIndex = 'home';
      this.$router.push('/OutScreen');
    },
    toggleTheme() {
      this.isDark = !this.isDark;
      localStorage.setItem('theme', this.isDark ? 'dark' : 'light');
      this.applyTheme();
    },
    applyTheme() {
      document.body.classList.toggle('dark-mode', this.isDark);
    }
  }
};
</script>

<style scoped>
.navbar-component {
  will-change: transform, opacity;
}

.navbar {
  background-color: #409eff;
  color: #ffffff;
  display: flex;
  width: 100%;
}

.menu {
  flex-grow: 1;
  display: flex;
  align-items: center;
  border: none;
}

.logout-wrapper {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 20px;
}

.theme-toggle {
  padding: 0 20px;
  height: 60px;
  line-height: 60px;
  cursor: pointer;
  color: #000;
  white-space: nowrap;
}

.el-menu-item {
  height: 60px;
  line-height: 60px;
}
</style>

<style>
body.dark-mode {
  background-color: #1a1a1a;
  color: #ffffff;
}

body.dark-mode .navbar {
  background-color: #2d2d2d;
}
</style>
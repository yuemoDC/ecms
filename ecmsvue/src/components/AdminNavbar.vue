<template>
  <el-header>
    <div class="navbar"> <!-- 导航栏容器 -->
      <el-menu :default-active="activeIndex" class="menu" @select="handleSelect" mode="horizontal">
        <!-- 设置默认选中的菜单项 -->
        <el-menu-item index="dashboard">仪表盘</el-menu-item> <!-- 仪表盘菜单项 -->
        <el-menu-item index="userManagement">用户管理</el-menu-item> <!-- 用户管理菜单项 -->
        <el-menu-item index="roleManagement">角色管理</el-menu-item> <!-- 角色管理菜单项 -->
        <el-menu-item index="systemSettings">系统设置</el-menu-item> <!-- 系统设置菜单项 -->

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
  name: 'AdminNavbar', // 组件名称
  data() {
    return {
      activeIndex: 'dashboard' // 设定默认激活的菜单项为仪表盘
    };
  },
  methods: {
    handleSelect(index) {
      // 当选择菜单项时调用此方法
      if (index === 'logout') return; // 防止重复执行登出逻辑
      this.activeIndex = index; // 更新选中的菜单项
      this.$router.push({ name: index }); // 根据选中项跳转到对应的路由
    },
    handleLogout() {
      // 处理用户登出
      localStorage.removeItem('token'); // 清除本地存储中的 token
      this.$router.push('/login'); // 跳转到登录页面
    }
  }
};
</script>
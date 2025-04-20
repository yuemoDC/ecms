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
  name: 'AppMerchantNavbar', // 组件名称
  data() {
    return {
      activeIndex: 'home' // 设定默认激活的菜单项为首页
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

<style scoped>
.navbar {
  background-color: #409eff; /* 设置导航栏背景颜色 */
  color: #ffffff; /* 设置文本颜色 */
  display: flex; /* 使用 flexbox 布局 */
  justify-content: space-between; /* 两端对齐 */
  align-items: center; /* 垂直居中对齐 */
}

.menu {
  line-height: 60px; /* 设置菜单项的行高 */
  width: 100%; /* 菜单宽度为100% */
}

.logout-wrapper {
  margin-left: auto; /* 将登出按钮推到右侧 */
}
</style>
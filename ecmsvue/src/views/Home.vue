<template>
  <div>
    <AppNavbar />
    <div class="home-container">
      <h1>欢迎来到首页!</h1>
      <div class="user-info">
        <el-tag type="success" v-if="currentUser">
          当前用户ID: {{ currentUser.id }}
        </el-tag>
        <el-tag type="info" v-else>
          未登录
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '../components/Navbar.vue';

export default {
  name: 'HomePage',
  components: {
    AppNavbar
  },
  data() {
    return {
      currentUser: null
    }
  },
  created() {
    this.getCurrentUser();
  },
  methods: {
    getCurrentUser() {
      // 从 localStorage 获取用户信息
      const userData = localStorage.getItem('user');
      if (userData) {
        try {
          const parsedUserData = JSON.parse(userData);
          if (parsedUserData.success) {
            this.currentUser = {
              id: parsedUserData.userid, // 使用 userid 作为 id
              token: parsedUserData.token,
              username: parsedUserData.username
            };
          } else {
            console.error('用户信息不正确:', parsedUserData);
          }
        } catch (e) {
          console.error('解析用户信息失败:', e);
        }
      }
    }
  }
};
</script>

<style scoped>
.home-container {
  position: relative;
  padding: 20px;
}

.user-info {
  position: absolute;
  top: 20px;
  right: 20px;
}

/* 如果需要响应式设计，可以添加媒体查询 */
@media (max-width: 768px) {
  .user-info {
    position: static;
    margin-top: 20px;
    text-align: center;
  }
}
</style>
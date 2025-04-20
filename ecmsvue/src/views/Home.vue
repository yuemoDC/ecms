<template>
  <div>
    <component :is="navbarComponent" /> <!-- 根据用户角色动态加载导航栏 -->
    <div class="home-container">
      <h1>欢迎来到首页!</h1>
      <div class="user-info">
        <el-tag type="success" v-if="currentUser">
          你好{{ currentUser.role }}ID: {{ currentUser.id }}
        </el-tag>
        <el-tag type="info" v-else>
          未登录
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script>
import AppMerchantNavbar from '../components/MerchantNavbar.vue';
import AppAdminNavbar from '../components/AdminNavbar.vue'; // 引入管理员导航栏

export default {
  name: 'HomePage',
  components: {
    AppMerchantNavbar,
    AppAdminNavbar
  },
  data() {
    return {
      currentUser: null
    }
  },
  computed: {
    navbarComponent() {
      // 根据用户角色返回相应的导航栏组件
      if (this.currentUser && this.currentUser.role === 'admin') {
        return 'AppAdminNavbar'; // 如果是管理员，返回管理员导航栏
      }
      return 'AppMerchantNavbar'; // 默认返回商家导航栏
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
              role: parsedUserData.role,
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
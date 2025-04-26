<template>
  <div>
    <component :is="navbarComponent" /> <!-- 根据用户角色动态加载导航栏 -->
    <div class="home-container">
      <h1>欢迎来到首页!</h1>
      <p class="welcome-msg">
        欢迎回来，{{ currentUser?.username || '用户' }}！
      </p>
      <p class="time-msg">当前时间：{{ currentTime }}</p>

      <!-- 公告栏 -->
      <el-card class="notice-board" shadow="hover">
        <h3>📢 系统公告</h3>
        <ul class="notice-list">
          <li>🛠 系统维护时间：每周日凌晨 2:00 - 4:00</li>
          <li>📊 数据将在每天晚上自动备份</li>
          <li>📌 如遇页面问题，请刷新或重新登录</li>
        </ul>
      </el-card>

      <div class="user-info">
        <el-tag type="success" v-if="currentUser">
          你好，你的{{ currentUser.role }}ID为: {{ currentUser.id }}
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
      currentUser: null,
      currentTime: ''
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
    this.updateTime();
    setInterval(this.updateTime, 1000);
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
    },
    updateTime() {
      const now = new Date();
      this.currentTime = now.toLocaleString();
    }
  }
};
</script>

<style scoped>
.home-container {
  position: relative;
  padding: 20px;
  text-align: center;
}

.home-container h1 {
  font-size: 36px;
  color: #409EFF;
}

.welcome-msg {
  font-size: 20px;
  margin: 10px 0;
}

.time-msg {
  font-size: 16px;
  color: #888;
  margin-bottom: 30px;
}

.user-info {
  position: absolute;
  top: 20px;
  right: 20px;
}

.notice-board {
  max-width: 600px;
  margin: 0 auto;
  text-align: left;
  padding: 20px;
  border-left: 5px solid #409EFF;
}

.notice-list {
  list-style: none;
  padding-left: 0;
  margin-top: 10px;
}

.notice-list li {
  margin-bottom: 10px;
  font-size: 16px;
  color: #333;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .user-info {
    position: static;
    margin-top: 20px;
    text-align: center;
  }

  .notice-board {
    width: 90%;
  }
}
</style>
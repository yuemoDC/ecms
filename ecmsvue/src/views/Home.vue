<template>
  <div>
    <component :is="navbarComponent" @navigate="handleNavigation" /> <!-- 根据用户角色动态加载导航栏 -->
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
import {gsap} from "gsap"

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
  mounted() {
    this.initAnimations();
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
  async beforeUnmount() {
    await this.leaveAnimation();
  },
  methods: {
    initAnimations() {
      // 标题动画
      gsap.fromTo(".home-container h1",
          {opacity: 0, y: -50},
          {
            opacity: 1,
            y: 0,
            duration: 1,
            ease: "power4.out"
          }
      );

      // 欢迎信息动画
      gsap.fromTo(".welcome-msg",
          {opacity: 0, x: -50},
          {
            opacity: 1,
            x: 0,
            delay: 0.3,
            duration: 0.8,
            ease: "back.out(1.7)"
          }
      );

      // 公告栏动画
      gsap.fromTo(".notice-board",
          {
            scale: 0.8,
            rotation: -3,
            opacity: 0
          },
          {
            scale: 1,
            rotation: 0,
            opacity: 1,
            delay: 0.5,
            duration: 0.2,
            ease: "elastic.out(1, 0.5)"
          }
      );

      // 列表项动画
      gsap.fromTo(".notice-list li",
          {x: 100, opacity: 0},
          {
            x: 0,
            opacity: 1,
            stagger: 0.2,
            delay: 0.8,
            duration: 0.6,
            ease: "power2.out"
          }
      );

      // 用户标签浮动动画
      gsap.to(".user-info .el-tag", {
        y: -5,
        duration: 1,
        yoyo: true,
        repeat: -1,
        ease: "power1.inOut"
      });

      // 时间显示动画
      gsap.fromTo(".time-msg",
          {
            opacity: 0,
            x: 300,
            textShadow: "0 0 0px rgba(0,0,0,0)"
          },
          {
            opacity: 1,
            x: 0,
            duration: 1.5,
            delay: 0.4,
            textShadow: "0 0 10px rgba(64,158,255,0.5)",
            ease: "power4.out"
          }
      );
    },
    leaveAnimation() {
      return new Promise((resolve) => {
        const tl = gsap.timeline();

        // 使用更精确的选择器
        tl.to(".home-container", {
          duration: 0.8,
          opacity: 0,
          y: 100,
          ease: "power4.in"
        })
            .eventCallback("onComplete", resolve);
      });
    },
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
    },
    async handleNavigation(path) {
      await this.leaveAnimation();
      this.$router.push(path);
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
  width: 90%;
  opacity: 0;
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
}
</style>
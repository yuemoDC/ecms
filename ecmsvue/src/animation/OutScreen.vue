
```vue
<template>
  <div ref="logoutScreen" class="logout-screen">
    <!-- 用户图标 -->
    <div ref="userIcon" class="logo">
      <img src="../assets/LOG.png" alt="东华大学">
    </div>

    <!-- 登出图标 -->
    <div ref="logoutIcon" class="logout-icon">
      <svg width="60" height="60" viewBox="0 0 24 24" fill="none" stroke="#F56C6C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" xmlns="http://www.w3.org/2000/svg">
        <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"></path>
        <path d="M16 17l5-5-5-5"></path>
        <path d="M21 12H9"></path>
      </svg>
    </div>

    <!-- 标题文本 -->
    <h1 ref="title" class="title">正在退出系统</h1>

    <!-- 副标题/说明文本 -->
    <p ref="subtitle" class="subtitle">感谢您使用智能商家管理系统</p>

    <!-- 装饰元素 -->
    <div ref="decoration" class="decoration">
      <div ref="circle1" class="circle circle1"></div>
      <div ref="circle2" class="circle circle2"></div>
      <div ref="circle3" class="circle circle3"></div>
      <div ref="circle4" class="circle circle4"></div>
    </div>

    <!-- 进度条 -->
    <div ref="progressContainer" class="progress-container">
      <div ref="progressBar" class="progress-bar"></div>
    </div>

    <!-- 粒子效果 -->
    <div ref="particles" class="particles">
      <div v-for="i in 20" :key="i" :ref="el => particleRefs[i-1] = el" class="particle"></div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import gsap from 'gsap';
import { useRouter } from 'vue-router';

export default {
  name: 'LogoutAnimation',
  setup() {
    const router = useRouter();
    const logoutScreen = ref(null);
    const userIcon = ref(null);
    const logoutIcon = ref(null);
    const title = ref(null);
    const subtitle = ref(null);
    const decoration = ref(null);
    const circle1 = ref(null);
    const circle2 = ref(null);
    const circle3 = ref(null);
    const circle4 = ref(null);
    const progressContainer = ref(null);
    const progressBar = ref(null);
    const particles = ref(null);
    const particleRefs = [];

    onMounted(() => {
      // 初始化粒子位置和样式
      particleRefs.forEach((particle, index) => {
        const size = Math.random() * 10 + 5;
        const x = Math.random() * window.innerWidth;
        const y = Math.random() * window.innerHeight;
        const color = index % 3 === 0 ? '#409EFF' :
            index % 3 === 1 ? '#67C23A' : '#F56C6C';

        gsap.set(particle, {
          width: `${size}px`,
          height: `${size}px`,
          left: `${x}px`,
          top: `${y}px`,
          backgroundColor: color,
          opacity: 0,
        });
      });

      // 创建主时间线
      const tl = gsap.timeline({
        onComplete: () => {
          // 动画完成后跳转到登录页面
          setTimeout(() => {
            router.push('/login');
          }, 500);
        }
      });

      // 设置初始状态
      gsap.set([userIcon.value, logoutIcon.value, title.value, subtitle.value], {
        opacity: 0,
        y: 20
      });

      gsap.set([circle1.value, circle2.value, circle3.value, circle4.value], {
        scale: 0,
        opacity: 0
      });

      gsap.set(progressBar.value, {
        width: '0%'
      });

      // 第一阶段：装饰圆形展开
      tl.to([circle1.value, circle2.value, circle3.value, circle4.value], {
        scale: 1,
        opacity: 0.7,
        duration: 0.3,
        stagger: 0.15,
        ease: 'back.out(1.7)'
      })

          // 第二阶段：用户图标出现并下移
          .to(userIcon.value, {
            opacity: 1,
            y: 0,
            duration: 0.5,
            ease: 'power2.out'
          }, '-=0.4')

          // 第三阶段：用户图标上移并淡出，同时登出图标入场
          .to(userIcon.value, {
            y: -40,
            height: 10,
            opacity: 0,
            duration: 0.1,
            ease: 'power1.in'
          })
          .to(logoutIcon.value, {
            opacity: 1,
            y: -10,
            scale: 1.2,
            duration: 0.2,
            ease: 'back.out(1.7)'
          }, '-=0.3')

          // 在登出图标周围产生粒子爆发效果
          .to(particleRefs, {
            opacity: 1,
            scale: 1,
            stagger: 0.02,
            duration: 0.3,
            onComplete: () => {
              // 粒子向各个方向飞散
              particleRefs.forEach(particle => {
                const xMove = (Math.random() - 0.5) * 300;
                const yMove = (Math.random() - 0.5) * 300;

                gsap.to(particle, {
                  x: xMove,
                  y: yMove,
                  opacity: 0,
                  scale: 0,
                  duration: Math.random() * 1.5 + 0.5,
                  ease: 'power2.out'
                });
              });
            }
          }, '-=0.2')

          // 第四阶段：标题和副标题出现，登出图标缩小并稳定
          .to(logoutIcon.value, {
            scale: 1,
            duration: 0.2,
            ease: 'power1.out'
          }, '-=0.2')
          .to(title.value, {
            opacity: 1,
            y: 0,
            duration: 0.25,
            ease: 'power2.out'
          }, '-=0.2')
          .to(subtitle.value, {
            opacity: 1,
            y: 0,
            duration: 0.3,
            ease: 'power2.out'
          }, '-=0.3')

          // 第五阶段：进度条填充
          .to(progressBar.value, {
            width: '100%',
            duration: 1,
            ease: 'power1.inOut'
          }, '-=0.2')

          // 第六阶段：所有元素缩小并淡出
          .to([logoutIcon.value, title.value, subtitle.value, circle1.value, circle2.value, circle3.value, circle4.value, progressContainer.value], {
            opacity: 0,
            scale: 0.8,
            stagger: 0.05,
            duration: 0.2,
            ease: 'power2.in'
          }, '+=0.3')

          // 最后阶段：屏幕淡出
          .to(logoutScreen.value, {
            opacity: 0,
            duration: 0.2
          });
    });

    return {
      logoutScreen,
      userIcon,
      logoutIcon,
      title,
      subtitle,
      decoration,
      circle1,
      circle2,
      circle3,
      circle4,
      progressContainer,
      progressBar,
      particles,
      particleRefs
    };
  }
};
</script>

<style scoped>
.logout-screen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  overflow: hidden;
}

.user-icon, .logout-icon {
  margin-bottom: 20px;
  position: relative;
  z-index: 10;
}

.title {
  font-size: 36px;
  color: #409EFF;
  margin: 10px 0;
  font-weight: 600;
  position: relative;
  z-index: 10;
}

.subtitle {
  font-size: 18px;
  color: #606266;
  margin-bottom: 30px;
  position: relative;
  z-index: 10;
}

.decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.circle {
  position: absolute;
  border-radius: 50%;
}

.circle1 {
  width: 180px;
  height: 180px;
  background-color: rgba(64, 158, 255, 0.1);
  top: 20%;
  left: 15%;
}

.logo {
  margin-bottom: 20px;
}

.logo img {
  width: 120px;
  height: auto;
}

.circle2 {
  width: 220px;
  height: 220px;
  background-color: rgba(103, 194, 58, 0.1);
  bottom: 15%;
  right: 10%;
}

.circle3 {
  width: 120px;
  height: 120px;
  background-color: rgba(245, 108, 108, 0.1);
  top: 40%;
  right: 20%;
}

.circle4 {
  width: 150px;
  height: 150px;
  background-color: rgba(230, 162, 60, 0.1);
  bottom: 30%;
  left: 25%;
}

.progress-container {
  width: 300px;
  height: 4px;
  background-color: #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  margin-top: 30px;
  position: relative;
  z-index: 10;
}

.progress-bar {
  height: 100%;
  background-color: #F56C6C;
  border-radius: 4px;
  width: 0;
}

.particles {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.particle {
  position: absolute;
  border-radius: 50%;
  transform: scale(0);
  opacity: 0;
}
</style>
```

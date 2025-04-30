<template>
  <div ref="splashScreen" class="splash-screen">
    <!-- Logo 元素 -->
    <div ref="logo" class="logo">
      <img src="../assets/LOG.png" alt="Logo" />
    </div>

    <!-- 标题文本 -->
    <h1 ref="title" class="title">欢迎使用</h1>

    <!-- 副标题/说明文本 -->
    <p ref="subtitle" class="subtitle">您的智能商家管理系统</p>

    <!-- 装饰元素 -->
    <div ref="decoration" class="decoration">
      <div ref="circle1" class="circle circle1"></div>
      <div ref="circle2" class="circle circle2"></div>
      <div ref="circle3" class="circle circle3"></div>
    </div>

    <!-- 进度条 -->
    <div ref="progressContainer" class="progress-container">
      <div ref="progressBar" class="progress-bar"></div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import gsap from 'gsap';
import { useRouter } from 'vue-router';

export default {
  setup() {
    const splashScreen = ref(null);
    const logo = ref(null);
    const title = ref(null);
    const subtitle = ref(null);
    const decoration = ref(null);
    const circle1 = ref(null);
    const circle2 = ref(null);
    const circle3 = ref(null);
    const progressContainer = ref(null);
    const progressBar = ref(null);
    const router = useRouter();

    onMounted(() => {
      // 创建主时间线
      const tl = gsap.timeline({
        onComplete: () => {
          // 动画完成后跳转到首页
          router.push('/home');
        }
      });

      // 设置初始状态
      gsap.set([logo.value, title.value, subtitle.value], {
        opacity: 0,
        y: 30
      });

      gsap.set([circle1.value, circle2.value, circle3.value], {
        scale: 0,
        opacity: 0
      });

      gsap.set(progressBar.value, {
        width: '0%'
      });

      // 第一阶段：Logo 和装饰元素出现
      tl.to(logo.value, {
        opacity: 1,
        y: 0,
        duration: 0.5,
        ease: 'power2.out'
      })
          .to([circle1.value, circle2.value, circle3.value], {
            scale: 1,
            opacity: 0.7,
            duration: 0.5,
            stagger: 0.15,
            ease: 'back.out(1.7)'
          }, '-=0.5')

          // 第二阶段：标题和副标题出现
          .to(title.value, {
            opacity: 1,
            y: 0,
            duration: 0.3,
            ease: 'power2.out'
          }, '-=0.3')
          .to(subtitle.value, {
            opacity: 1,
            y: 0,
            duration: 0.3,
            ease: 'power2.out'
          }, '-=0.5')

          // 第三阶段：进度条填充
          .to(progressBar.value, {
            width: '100%',
            duration: 1,
            ease: 'power1.inOut'
          }, '-=0.2')

          // 最后阶段：所有元素淡出
          .to([logo.value, title.value, subtitle.value, decoration.value, progressContainer.value], {
            opacity: 0,
            y: -20,
            duration: 0.3,
            stagger: 0.1,
            ease: 'power2.in'
          }, '+=0.3')
          .to(splashScreen.value, {
            opacity: 0,
            duration: 0.2,
            onComplete: () => {
              splashScreen.value.style.display = 'none';
            }
          });

      // 总动画时长约为 4 秒
    });

    return {
      splashScreen,
      logo,
      title,
      subtitle,
      decoration,
      circle1,
      circle2,
      circle3,
      progressContainer,
      progressBar
    };
  }
};
</script>

<style scoped>
.splash-screen {
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

.logo {
  margin-bottom: 20px;
}

.logo img {
  width: 120px;
  height: auto;
}

.title {
  font-size: 36px;
  color: #409EFF;
  margin: 10px 0;
  font-weight: 600;
}

.subtitle {
  font-size: 18px;
  color: #606266;
  margin-bottom: 30px;
}

.decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: -1;
}

.circle {
  position: absolute;
  border-radius: 50%;
}

.circle1 {
  width: 150px;
  height: 150px;
  background-color: rgba(64, 158, 255, 0.1);
  top: 20%;
  left: 15%;
}

.circle2 {
  width: 200px;
  height: 200px;
  background-color: rgba(103, 194, 58, 0.1);
  bottom: 15%;
  right: 10%;
}

.circle3 {
  width: 100px;
  height: 100px;
  background-color: rgba(245, 108, 108, 0.1);
  top: 40%;
  right: 20%;
}

.progress-container {
  width: 300px;
  height: 4px;
  background-color: #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  margin-top: 30px;
}

.progress-bar {
  height: 100%;
  background-color: #409EFF;
  border-radius: 4px;
  width: 0;
}
</style>
<template>
  <AppNavbar @navigate="handleNavigation"/>
  <VantaBackground ref="vantaBg" class="vanta-container"/>
  <div class="content page-container">
    <!-- 气泡导航区域 -->
    <div id="bubbles-container" class="relative w-full h-full flex justify-center items-center">
      <!-- 气泡1: 商家AI预测 -->
      <div class="bubble cursor-pointer absolute flex flex-col justify-center items-center rounded-full bg-sky-500/40 backdrop-blur-sm shadow-lg text-white font-bold text-center hover:bg-sky-600/50 transition-colors duration-300"
           @click="handleNavigation('data-SalesPrediction')"
           :style="{ borderRadius: '50%' }">
        <span class="text-white text-xl">商家AI预测</span>
      </div>

      <!-- 气泡2: 商家预测对比 -->
      <div class="bubble cursor-pointer absolute flex flex-col justify-center items-center rounded-full bg-sky-500/40 backdrop-blur-sm shadow-lg text-white font-bold text-center hover:bg-sky-600/50 transition-colors duration-300"
           @click="handleNavigation('bulkPrediction')"
           :style="{ borderRadius: '50%' }">
        <span class="text-white text-xl">商家预测对比</span>
      </div>

      <!-- 气泡3: 回到首页 -->
      <div class="bubble cursor-pointer absolute flex flex-col justify-center items-center rounded-full bg-sky-500/40 backdrop-blur-sm shadow-lg text-white font-bold text-center hover:bg-sky-600/50 transition-colors duration-300"
           @click="handleNavigation('home')"
           :style="{ borderRadius: '50%' }">
        <span class="text-white text-xl">回到首页</span>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from "@/components/AdminNavbar.vue";
import VantaBackground from "@/assets/VantaBackground.vue";
import {gsap} from "gsap";
import { onMounted, onBeforeUnmount, ref, nextTick } from 'vue';

export default {
  name: 'MainLayout',
  components: {
    AppNavbar,
    VantaBackground
  },
  setup() {
    const bubbleProgress = ref([0, 120, 240]); // 每个气泡的初始位置角度
    let animationId = null;

    const initBubbles = () => {
      // 设置气泡初始尺寸
      const bubbles = document.querySelectorAll('.bubble');
      const sizes = [180, 160, 140]; // 各气泡的直径

      bubbles.forEach((bubble, index) => {
        // 确保气泡形状为圆形
        bubble.style.width = `${sizes[index]}px`;
        bubble.style.height = `${sizes[index]}px`;
        bubble.style.borderRadius = '50%';
        // 初始显示
        bubble.style.opacity = '0.7';
      });
    };

    const animateBubbles = () => {
      const container = document.getElementById('bubbles-container');
      const bubbles = document.querySelectorAll('.bubble');
      const speed = 0.1; // 运动速度

      if (!container || bubbles.length === 0) return;

      // 椭圆轨道中心
      const centerX = container.clientWidth / 2;
      const centerY = container.clientHeight / 2;

      // 椭圆轨道参数
      const radiusX = Math.min(centerX * 0.4, 300); // 横向半径，限制最大值
      const radiusY = Math.min(centerY * 0.3, 200); // 纵向半径，限制最大值
      const angle = 25 * (Math.PI / 180); // 倾斜角度（弧度）

      bubbles.forEach((bubble, index) => {
        // 更新角度位置
        bubbleProgress.value[index] = (bubbleProgress.value[index] + speed) % 360;
        const radian = bubbleProgress.value[index] * (Math.PI / 180);

        // 计算椭圆轨道上的位置（带倾斜）
        const x = radiusX * Math.cos(radian);
        const y = radiusY * Math.sin(radian);

        // 应用倾斜变换
        const transformedX = x * Math.cos(angle) - y * Math.sin(angle);
        const transformedY = x * Math.sin(angle) + y * Math.cos(angle);

        // 设置气泡位置
        bubble.style.left = `${centerX + transformedX - parseFloat(bubble.style.width) / 2}px`;
        bubble.style.top = `${centerY + transformedY - parseFloat(bubble.style.height) / 2}px`;

        // 随着位置变化的大小变化（呼吸效果）
        const scale = 0.9 + 0.2 * Math.sin(radian);
        bubble.style.transform = `scale(${scale})`;

        // 随着位置变化的透明度
        const opacity = 0.7 + 0.3 * Math.sin(radian);
        bubble.style.opacity = opacity;

        // 添加光晕效果
        bubble.style.boxShadow = `0 0 ${15 + 10 * Math.sin(radian)}px rgba(135, 206, 250, 0.6)`;
      });

      animationId = requestAnimationFrame(animateBubbles);
    };

    const handleResize = () => {
      // 窗口大小变化时，重新初始化气泡和动画参数
      initBubbles();
    };

    onMounted(() => {
      // 使用nextTick确保DOM已更新
      nextTick(() => {
        // 初始化气泡
        initBubbles();
        // 开始气泡动画
        animateBubbles();
      });

      // 窗口大小变化时重新调整
      window.addEventListener('resize', handleResize);
    });

    onBeforeUnmount(() => {
      // 清除动画和事件监听器
      if (animationId) {
        cancelAnimationFrame(animationId);
      }
      window.removeEventListener('resize', handleResize);
    });

    return {
      bubbleProgress,
      initBubbles,
      animateBubbles,
      handleResize
    };
  },
  mounted() {
    // 背景渐入动画
    gsap.fromTo(this.$refs.vantaBg.$el,
        { opacity: 0 },
        {
          opacity: 1,
          duration: 0.5,
          ease: "power2.inOut",
        }
    );

    // 内容入场动画
    gsap.fromTo(".content",
        { y: 0, opacity: 0 },
        {
          y: 0,
          opacity: 1,
          duration: 1,
          ease: "elastic.out(1, 0.5)",
        }
    );
  },
  methods: {
    async handleNavigation(path) {
      // 添加页面跳转确认
      if (this.$route.path === path) return;

      await this.leaveAnimation();
      this.$router.push(path);
    },
    leaveAnimation() {
      return new Promise((resolve) => {
        const tl = gsap.timeline();

        // 同时执行内容容器和背景动画
        tl.to(".page-container", {
          duration: 0.5,
          opacity: 0,
          y: 100,
          ease: "power4.in"
        })
            .to(this.$refs.vantaBg.$el, {
              opacity: 0,
              duration: 0.8,
              ease: "power2.in"
            }, 0) // 0表示同时开始动画
            .eventCallback("onComplete", resolve);
      });
    }
  }
}
</script>

<style scoped>
.content {
  position: relative;
  z-index: 1; /* 确保内容在背景之上 */
  padding: 20px;
  margin-top: 60px; /* 为导航栏留出空间 */
  width: 100%;
  height: calc(100vh - 60px);
}
.vanta-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  z-index: -1;
}
/* 气泡样式 */
.bubble {
  position: absolute;
  border-radius: 50% !important; /* 强制使用圆角 */
  opacity: 0.3;
  transition: background-color 0.3s;
  box-shadow: 0 0 15px rgba(135, 206, 250, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}
#bubbles-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}
</style>
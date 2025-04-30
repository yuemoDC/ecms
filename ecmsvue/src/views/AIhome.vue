<template>
  <AppNavbar @navigate="handleNavigation"/>
  <VantaBackground ref="vantaBg" class="vanta-container"/>
  <div class="content page-container">
    <slot></slot>
  </div>
</template>

<script>
import AppNavbar from "@/components/AdminNavbar.vue";
import VantaBackground from "@/assets/VantaBackground.vue";
import {gsap} from "gsap";

export default {
  name: 'MainLayout',
  components: {
    AppNavbar,
    VantaBackground
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
}
.vanta-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  z-index: -1;
}
</style>
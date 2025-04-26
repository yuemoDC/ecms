<template>
  <AppNavbar />
  <VantaBackground />
  <div class="content">
    <slot></slot>
  </div>
</template>

<script>
import AppNavbar from "@/components/AdminNavbar.vue";
import VantaBackground from "@/assets/VantaBackground.vue";

export default {
  name: 'MainLayout',
  components: {
    AppNavbar,
    VantaBackground
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
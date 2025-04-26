<!-- 文件: src/components/VantaBackground.vue -->
<template>
  <div ref="vantaContainer" class="vanta-container"></div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as THREE from 'three';
import GLOBE from 'vanta/dist/vanta.globe.min';

export default {
  name: 'VantaBackground',
  props: {
    backgroundColor: {
      type: String,
      default: '#f5f5f5'
    },
    color: {
      type: String,
      default: '#1976D2'
    },
    color2: {
      type: String,
      default: '#1565C0'
    },
    size: {
      type: Number,
      default: 1.0
    },
    speed: {
      type: Number,
      default: 1.0
    },
    wireframe: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const vantaContainer = ref(null);
    let vantaEffect = null;

    onMounted(() => {
      vantaEffect = GLOBE({
        el: vantaContainer.value,
        THREE: THREE,
        backgroundColor: props.backgroundColor,
        color: props.color,
        color2: props.color2,
        size: props.size,
        speed: props.speed,
        wireframe: props.wireframe
      });
    });

    onBeforeUnmount(() => {
      if (vantaEffect) {
        vantaEffect.destroy();
      }
    });

    return {
      vantaContainer
    };
  }
};
</script>

<style scoped>
.vanta-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  z-index: -1;
}
</style>
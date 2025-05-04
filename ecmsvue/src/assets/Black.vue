<template>
  <div ref="vantaContainer" class="vanta-container"></div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as THREE from 'three';
import BIRDS from 'vanta/dist/vanta.birds.min';

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
    colorMode: {
      type: String,
      default: 'variance'
    },
    quantity: {
      type: Number,
      default: 3.0
    },
    birdSize: {
      type: Number,
      default: 1.0
    },
    wingSpan: {
      type: Number,
      default: 30.0
    },
    speedLimit: {
      type: Number,
      default: 5.0
    },
    separation: {
      type: Number,
      default: 20.0
    },
    alignment: {
      type: Number,
      default: 20.0
    },
    cohesion: {
      type: Number,
      default: 20.0
    }
  },
  setup(props) {
    const vantaContainer = ref(null);
    let vantaEffect = null;

    onMounted(() => {
      vantaEffect = BIRDS({
        el: vantaContainer.value,
        THREE: THREE,
        backgroundColor: props.backgroundColor,
        color: props.color,
        color2: props.color2,
        colorMode: props.colorMode,
        quantity: props.quantity,
        birdSize: props.birdSize,
        wingSpan: props.wingSpan,
        speedLimit: props.speedLimit,
        separation: props.separation,
        alignment: props.alignment,
        cohesion: props.cohesion
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
<!-- src/components/ForecastChart.vue -->
<template>
  <div ref="chartRef" class="chart-container"></div>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import * as echarts from 'echarts';

export default {
  props: {
    data: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const chartRef = ref(null);
    let chartInstance = null;

    const renderChart = () => {
      if (!chartInstance && chartRef.value) {
        chartInstance = echarts.init(chartRef.value);
      }
      const option = {
        title: { text: '销量预测图' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: props.data.dates || [] },
        yAxis: { type: 'value' },
        series: [
          {
            name: '预测销量',
            type: 'line',
            data: props.data.sales || [],
            smooth: true,
            areaStyle: {},
          },
        ],
      };
      chartInstance.setOption(option);
    };

    watch(() => props.data, renderChart, { immediate: true });

    onMounted(() => {
      renderChart();
      window.addEventListener('resize', () => {
        chartInstance && chartInstance.resize();
      });
    });

    return {
      chartRef,
    };
  },
};
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 400px;
}
</style>

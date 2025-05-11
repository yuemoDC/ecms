<template>
  <AppNavbar @navigate="handleNavigation"/>
  <div class="page-container">

    <div style="text-align: center; margin-top: 50px;">
      <h2>销量预测</h2>

      <!-- 输入与按钮排布 -->
      <el-row justify="center" :gutter="20" style="margin-top: 30px;">
        <el-col :span="6">
          <el-input-number
              v-model="predictDays"
              :min="1"
              :max="365"
              label="预测天数"
              style="width: 100%;"
          ></el-input-number>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" block @click="startForecast" :loading="loading">
            {{ loading ? '预测中...' : '开始预测' }}
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button
              type="success"
              block
              @click="loadForecastData"
              :disabled="!filePath || loading"
          >
            查看预测结果
          </el-button>
        </el-col>
      </el-row>

      <!-- 图表展示 -->
      <div
          v-if="forecastData.length > 0"
          ref="chart"
          class="forecast-chart"
      ></div>

      <!-- 文件路径提示 -->
      <div v-if="filePath" style="margin-top: 30px;">
        <el-alert
            title="预测完成"
            type="success"
            :closable="false"
            show-icon
        >
          <template #default>
            <div>文件路径：{{ filePath }}</div>
          </template>
        </el-alert>
      </div>

      <!-- 错误提示 -->
      <div v-if="errorMsg" style="margin-top: 30px;">
        <el-alert
            title="出错了"
            type="error"
            :description="errorMsg"
            show-icon
            :closable="false"
        />
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/components/MerchantNavbar.vue';
import axios from 'axios';
import * as echarts from 'echarts';
import {gsap} from "gsap";

export default {
  components: { AppNavbar },
  mounted() {
    this.enterAnimation();
  },
  data() {
    return {
      predictDays: 90,
      filePath: '',
      errorMsg: '',
      loading: false,
      forecastData: [],
    };
  },
  methods: {
    async handleNavigation(path) {
      await this.leaveAnimation();
      this.$router.push(path);
    },
    leaveAnimation() {
      return new Promise((resolve) => {
        gsap.to(".page-container", {
          duration: 0.8,
          opacity: 0,
          y: 100,
          ease: "power4.in",
          onComplete: resolve
        });
      });
    },
    enterAnimation() {
      const tl = gsap.timeline();

      // 设置初始状态
      gsap.set(".page-container", {
        opacity: 0,
        y: 50
      });

      // 创建入场动画
      tl.to(".page-container", {
        duration: 0.8,
        opacity: 1,
        y: 0,
        ease: "power4.out"
      });

    },
    async startForecast() {
      this.loading = true;
      this.filePath = '';
      this.errorMsg = '';
      try {
        const user = JSON.parse(localStorage.getItem('user'));
        const merchantId = user?.userid;

        if (!merchantId) {
          throw new Error('无法获取商家ID，请重新登录');
        }

        const res = await axios.get('http://localhost:8080/api/prophet/forecast', {
          params: {
            merchant_id: merchantId,
            days: this.predictDays,
          },
        });

        if (res.data?.filePath) {
          this.filePath = res.data.filePath;
        } else {
          throw new Error(res.data?.message || '未返回有效的预测文件路径');
        }
      } catch (err) {
        this.errorMsg = err.response?.data?.message || err.message || '预测请求失败';
      } finally {
        this.loading = false;
      }
    },
    async loadForecastData() {
      this.loading = true;
      this.errorMsg = '';
      try {
        const user = JSON.parse(localStorage.getItem('user'));
        const merchantId = user?.userid;

        if (!merchantId) {
          throw new Error('无法获取商家ID，请重新登录');
        }

        const res = await axios.get('http://localhost:8080/api/prophet/result', {
          params: { merchant_id: merchantId },
        });

        if (!res.data || res.data.length === 0) {
          throw new Error('预测数据为空，请先执行预测');
        }

        this.forecastData = res.data;
        this.renderChart();
      } catch (err) {
        this.errorMsg = err.response?.data?.message || err.message || '读取预测数据失败';
      } finally {
        this.loading = false;
      }
    },
    renderChart() {
      this.$nextTick(() => {
        const chartDom = this.$refs.chart;
        if (!chartDom) {
          this.errorMsg = '图表容器未找到，无法渲染图表';
          return;
        }

        const myChart = echarts.init(chartDom);

        const dates = this.forecastData.map(item => item.ds);
        const yhat = this.forecastData.map(item => item.yhat);
        const lower = this.forecastData.map(item => item.yhat_lower);
        const upper = this.forecastData.map(item => item.yhat_upper);

        const option = {
          title: {
            text: '销量预测趋势图',
            left: 'center',
          },
          tooltip: {
            trigger: 'axis',
          },
          legend: {
            data: ['预测值', '预测区间上界', '预测区间下界'],
            top: 30,
          },
          xAxis: {
            type: 'category',
            data: dates,
            boundaryGap: false,
          },
          yAxis: {
            type: 'value',
          },
          series: [
            {
              name: '预测区间',
              type: 'line',
              data: upper,
              lineStyle: { width: 0 },
              showSymbol: false,
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(64,158,255,0.1)' },
                  { offset: 1, color: 'rgba(64,158,255,0.05)' }
                ]),
                origin: 'auto',
              },
              emphasis: { disabled: true },
              z: 1,
            },
            {
              name: '预测区间',
              type: 'line',
              data: lower,
              lineStyle: { width: 0 },
              showSymbol: false,
              areaStyle: {
                color: '#fff',
              },
              emphasis: { disabled: true },
              z: 2,
            },
            {
              name: '预测值',
              type: 'line',
              data: yhat,
              smooth: true,
              lineStyle: { color: '#409EFF' },
              symbol: 'circle',
              symbolSize: 4,
              z: 3,
            }
          ]
        };
        myChart.setOption(option);
      });
    }

  },
};
</script>
<style scoped>
.forecast-chart {
  width: 90%;
  height: 500px;
  margin: 30px auto;
}
</style>

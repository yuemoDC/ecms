<template>
  <AppNavbar @navigate="handleNavigation"/>
  <VantaBackground ref="vantaBg" class="vanta-container"/>
  <div class="sales-prediction-container page-container">
    <h1 class="page-title">批量商家销售预测对比</h1>

    <!-- 预测参数设置卡片 -->
    <div class="card filters-card">
      <div class="card-header">
        <div class="card-title">
          <i class="icon-settings"></i>预测参数设置
        </div>
        <p class="card-subtitle">选择多个商家进行总体销售预测对比分析</p>
      </div>

      <div class="filters">
        <div class="form-group">
          <label>商家选择<span class="required">*</span></label>
          <button @click="openMerchantModal" class="btn btn-outline merchant-select-btn">
            <i class="icon-merchants"></i>
            {{ selectedMerchants.length > 0 ? `已选择 ${selectedMerchants.length} 个商家` : '选择商家' }}
          </button>
        </div>
      </div>

      <!-- 商家选择弹窗 -->
      <div v-if="showMerchantModal" class="merchant-modal">
        <div class="merchant-modal-content large">
          <div class="merchant-modal-header">
            <h3>选择商家（可多选）</h3>
            <button @click="closeMerchantModal" class="modal-close-btn">&times;</button>
          </div>
          <div class="merchant-modal-body">
            <div v-if="loadingMerchants" class="loading-container">
              <div class="spinner"></div>
              <p>正在获取商家列表...</p>
            </div>
            <div v-else-if="merchants.length === 0" class="no-data">
              <i class="icon-info"></i> 没有找到商家信息
            </div>
            <div v-else class="merchant-selection">
              <div
                  v-for="merchant in merchants"
                  :key="merchant.merchantId"
                  class="merchant-selection-item"
                  :class="{ 'selected': isSelected(merchant.merchantId) }"
                  @click="toggleMerchant(merchant)"
              >
                <div class="merchant-selection-header">
                  <div class="merchant-checkbox">
                    <input type="checkbox" :checked="isSelected(merchant.merchantId)" />
                  </div>
                  <h4 class="merchant-title">{{ merchant.merchantName || `商家 #${merchant.merchantId}` }}</h4>
                </div>
                <div class="merchant-info">
                  <div class="merchant-contact"><i class="icon-contact"></i> {{ merchant.contactInfo || '暂无联系方式' }}</div>
                  <div class="merchant-scope"><i class="icon-scope"></i> {{ merchant.businessScope || '暂无经营范围' }}</div>
                </div>
                <div class="merchant-selection-footer">
                  <div class="merchant-id">ID: {{ merchant.merchantId }}</div>
                  <div class="selection-indicator">
                    <i v-if="isSelected(merchant.merchantId)" class="icon-check"></i>
                    <span v-else>点击选择</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="merchant-modal-footer">
            <button @click="fetchMerchants" class="btn btn-outline" :disabled="loadingMerchants">
              <i class="icon-refresh"></i>
              {{ loadingMerchants ? '刷新中...' : '刷新列表' }}
            </button>
            <div class="selected-count">已选择: {{ selectedMerchants.length }} 个商家</div>
            <button @click="confirmMerchantSelection" class="btn btn-primary" :disabled="selectedMerchants.length === 0">
              确认选择
            </button>
            <button @click="closeMerchantModal" class="btn btn-outline">
              取消
            </button>
          </div>
        </div>
      </div>

      <!-- 开始预测按钮 -->
      <div class="action-buttons action-predict">
        <button
            @click="startPrediction"
            class="btn btn-primary"
            :disabled="loading || !isFormValid"
        >
          <i class="icon-chart"></i>
          {{ loading ? '分析中...' : '开始预测对比' }}
        </button>
      </div>

      <div v-if="formError" class="form-error">
        <i class="icon-error"></i> {{ formError }}
      </div>
    </div>

    <!-- 已选商家展示 -->
    <div v-if="selectedMerchants.length > 0" class="card selected-merchants-card">
      <div class="card-header">
        <div class="card-title">
          <i class="icon-merchants"></i>已选择的商家
        </div>
        <p class="card-subtitle">点击商家名称可移除选择</p>
      </div>
      <div class="selected-merchants-list">
        <div
            v-for="merchant in selectedMerchants"
            :key="merchant.merchantId"
            class="selected-merchant-tag"
            @click="removeMerchant(merchant.merchantId)"
        >
          {{ merchant.merchantName || `商家 #${merchant.merchantId}` }}
          <span class="remove-icon">×</span>
        </div>
      </div>
    </div>

    <!-- 预测结果对比卡片 -->
    <div v-if="showResults" class="card data-card">
      <div class="card-header">
        <div class="card-title">
          <i class="icon-chart"></i>预测结果对比分析
        </div>
        <p class="card-subtitle">多商家总体销售趋势预测对比</p>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <p>正在分析多个商家数据，请稍候...</p>
      </div>

      <div v-else-if="error" class="error-container">
        <div class="alert alert-danger">
          <i class="icon-error"></i> {{ error }}
        </div>
        <button @click="startPrediction" class="btn btn-primary">
          <i class="icon-refresh"></i> 重新分析
        </button>
      </div>

      <div v-else-if="predictionData.length > 0" class="prediction-content">
        <!-- 图表区域 - 直接使用 Line 组件，与单商家版本相同 -->
        <div class="chart-container">
          <Line :data="chartData" :options="chartOptions" />
        </div>

        <!-- 商家预测对比表格 -->
        <div class="comparison-table-container">
          <h3 class="stats-title">
            <i class="icon-analytics"></i>商家预测数据对比
          </h3>
          <table class="comparison-table">
            <thead>
            <tr>
              <th>商家名称</th>
              <th>平均预测销售额</th>
              <th>最高预测销售额</th>
              <th>最低预测销售额</th>
              <th>总预测销售额</th>
              <th>预测趋势</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(data, index) in predictionData" :key="data.merchantId">
              <td :style="{ color: getColorForMerchant(index) }">
                {{ data.merchantName }}
              </td>
              <td>¥{{ calculateAverage(data.predictions).toFixed(2) }}</td>
              <td>¥{{ Math.max(...data.predictions).toFixed(2) }}</td>
              <td>¥{{ Math.min(...data.predictions).toFixed(2) }}</td>
              <td>¥{{ calculateSum(data.predictions).toFixed(2) }}</td>
              <td :class="calculateTrend(data.predictions) >= 0 ? 'positive' : 'negative'">
                <i :class="calculateTrend(data.predictions) >= 0 ? 'icon-trending-up' : 'icon-trending-down'"></i>
                {{ calculateTrend(data.predictions) >= 0 ? '上升' : '下降' }}
                {{ Math.abs(calculateTrend(data.predictions)).toFixed(2) }}%
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import axios from 'axios';
import { Chart, registerables } from 'chart.js';
import { Line } from 'vue-chartjs';
import AppNavbar from "@/components/AdminNavbar.vue";
import VantaBackground from "@/assets/VantaBackground.vue";
import {gsap} from "gsap";

// Register Chart.js components
Chart.register(...registerables);

export default {
  name: 'BatchSalesPredictionView',
  components: {
    VantaBackground,
    AppNavbar,
    Line
  },
  mounted() {
    // 背景渐入动画
    gsap.fromTo(this.$refs.vantaBg.$el,
        {opacity: 0},
        {
          opacity: 1,
          duration: 0.5,
          ease: "power2.inOut",
        }
    );

    gsap.fromTo(".page-container",{
      opacity: 0},
        {
          opacity: 1,
          duration: 0.5,
          ease: "power2.inOut",
    })
  },
  setup() {
    // 表单参数
    const selectedMerchants = ref([]);

    // 商家选择相关
    const merchants = ref([]);
    const loadingMerchants = ref(false);
    const showMerchantModal = ref(false);

    // 状态控制
    const loading = ref(false);
    const error = ref(null);
    const formError = ref(null);
    const showResults = ref(false);

    // 数据
    const predictionData = ref([]);

    // 颜色数组
    const colors = [
      'rgba(53, 162, 235, 1)',   // 蓝色
      'rgba(255, 99, 132, 1)',   // 红色
      'rgba(75, 192, 192, 1)',   // 青色
      'rgba(255, 159, 64, 1)',   // 橙色
      'rgba(153, 102, 255, 1)',  // 紫色
      'rgba(255, 205, 86, 1)',   // 黄色
      'rgba(201, 203, 207, 1)',  // 灰色
      'rgba(54, 162, 235, 1)',   // 深蓝色
    ];

    // 计算属性
    const isFormValid = computed(() => {
      return selectedMerchants.value.length > 0;
    });

    // 图表数据
    const chartData = computed(() => {
      if (predictionData.value.length === 0) return { labels: [], datasets: [] };

      const datasets = predictionData.value.map((data, index) => ({
        label: data.merchantName,
        data: data.predictions,
        borderColor: getColorForMerchant(index),
        backgroundColor: getColorForMerchant(index).replace('1)', '0.2)'),
        borderWidth: 2,
        tension: 0.3,
        pointBackgroundColor: getColorForMerchant(index),
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: getColorForMerchant(index)
      }));

      return {
        labels: predictionData.value[0].dates || [],
        datasets
      };
    });

    // 图表选项
    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        mode: 'index',
        intersect: false,
      },
      scales: {
        y: {
          beginAtZero: false,
          title: {
            display: true,
            text: '销售金额 (¥)',
            font: {
              size: 14,
              weight: 'bold'
            }
          },
          grid: {
            color: 'rgba(0, 0, 0, 0.05)'
          }
        },
        x: {
          title: {
            display: true,
            text: '日期',
            font: {
              size: 14,
              weight: 'bold'
            }
          },
          grid: {
            color: 'rgba(0, 0, 0, 0.05)'
          }
        }
      },
      plugins: {
        tooltip: {
          backgroundColor: 'rgba(0, 0, 0, 0.7)',
          titleFont: {
            size: 14
          },
          bodyFont: {
            size: 13
          },
          callbacks: {
            label: function(context) {
              return `${context.dataset.label}: ¥${context.raw.toFixed(2)}`;
            }
          }
        },
        legend: {
          position: 'top',
          labels: {
            font: {
              size: 14
            },
            usePointStyle: true,
          }
        }
      }
    };

    // 获取商家列表
    const fetchMerchants = async () => {
      loadingMerchants.value = true;
      formError.value = null;

      try {
        const response = await axios.get(`http://localhost:8080/api/merchants`, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        });

        if (!response.data || !Array.isArray(response.data)) {
          throw new Error('返回的数据格式不正确');
        }

        merchants.value = response.data;
      } catch (err) {
        console.error('获取商家列表失败:', err);
        formError.value = `无法获取商家列表: ${err.message || '请检查网络连接和服务器状态'}`;
      } finally {
        loadingMerchants.value = false;
      }
    };

    // 打开商家选择弹窗
    const openMerchantModal = () => {
      showMerchantModal.value = true;
      if (merchants.value.length === 0) {
        fetchMerchants();
      }
    };

    // 关闭商家选择弹窗
    const closeMerchantModal = () => {
      showMerchantModal.value = false;
    };

    // 切换商家选择
    const toggleMerchant = (merchant) => {
      const index = selectedMerchants.value.findIndex(m => m.merchantId === merchant.merchantId);
      if (index > -1) {
        selectedMerchants.value.splice(index, 1);
      } else {
        selectedMerchants.value.push({
          merchantId: merchant.merchantId,
          merchantName: merchant.merchantName || `商家 #${merchant.merchantId}`
        });
      }
    };

    // 检查商家是否已选择
    const isSelected = (merchantId) => {
      return selectedMerchants.value.some(m => m.merchantId === merchantId);
    };

    // 确认商家选择
    const confirmMerchantSelection = () => {
      closeMerchantModal();
    };

    // 移除已选商家
    const removeMerchant = (merchantId) => {
      const index = selectedMerchants.value.findIndex(m => m.merchantId === merchantId);
      if (index > -1) {
        selectedMerchants.value.splice(index, 1);
      }
    };

    // 获取商家颜色
    const getColorForMerchant = (index) => {
      return colors[index % colors.length];
    };

    // 开始预测
    const startPrediction = async () => {
      formError.value = null;

      if (selectedMerchants.value.length === 0) {
        formError.value = '请选择至少一个商家';
        return;
      }

      loading.value = true;
      error.value = null;
      showResults.value = false;

      try {
        const endpoint = 'http://localhost:8080/api/ai/multi-merchant-prediction';
        const requestData = selectedMerchants.value.map(m => m.merchantId);

        const response = await axios.post(endpoint, requestData);

        // 处理响应数据
        predictionData.value = response.data.map(data => {
          const merchant = selectedMerchants.value.find(m => m.merchantId === data.merchantId);
          return {
            ...data,
            merchantName: merchant ? merchant.merchantName : `商家 #${data.merchantId}`
          };
        });

        showResults.value = true;
      } catch (err) {
        console.error('获取预测数据失败:', err);
        error.value = err.response?.data?.message || '无法获取预测数据，请检查商家ID是否正确';
      } finally {
        loading.value = false;
      }
    };

    // 计算统计指标
    const calculateAverage = (data) => {
      if (!data || data.length === 0) return 0;
      return data.reduce((sum, val) => sum + val, 0) / data.length;
    };

    const calculateSum = (data) => {
      if (!data || data.length === 0) return 0;
      return data.reduce((sum, val) => sum + val, 0);
    };

    const calculateTrend = (data) => {
      if (!data || data.length < 2) return 0;
      const firstValue = data[0];
      const lastValue = data[data.length - 1];
      return ((lastValue - firstValue) / firstValue) * 100;
    };

    return {
      // 表单参数
      selectedMerchants,

      // 商家选择相关
      merchants,
      loadingMerchants,
      showMerchantModal,
      fetchMerchants,
      openMerchantModal,
      closeMerchantModal,
      toggleMerchant,
      isSelected,
      confirmMerchantSelection,
      removeMerchant,

      // 状态控制
      loading,
      error,
      formError,
      showResults,

      // 数据
      predictionData,

      // 计算属性
      isFormValid,
      chartData,
      chartOptions,

      // 方法
      startPrediction,
      calculateAverage,
      calculateSum,
      calculateTrend,
      getColorForMerchant
    };
  },
  methods:{
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
          y: 0,
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
};
</script>


<style scoped>
/* 基础样式 */

.page-title {
  margin-bottom: 24px;
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  text-align: center;
  position: relative;
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, #3498db, #2980b9);
  border-radius: 3px;
}

/* 卡片样式 */
.card {
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 24px;
  margin-bottom: 30px;
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.card-header {
  margin-bottom: 20px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #2c3e50;
  display: flex;
  align-items: center;
}

.card-title i {
  margin-right: 8px;
  color: #3498db;
}

.card-subtitle {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 16px;
}

/* 表单样式 */
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 24px;
}

.form-group {
  flex: 1;
  min-width: 220px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  font-size: 14px;
  color: #34495e;
}

.required {
  color: #e74c3c;
  margin-left: 4px;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-control:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.2);
  outline: none;
}

.form-control::placeholder {
  color: #bdc3c7;
}

.info-text {
  font-size: 13px;
  color: #7f8c8d;
  margin-top: 5px;
  font-style: italic;
}

.form-error {
  background-color: #fdedec;
  color: #e74c3c;
  padding: 12px;
  border-radius: 6px;
  margin-top: 16px;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.form-error i {
  margin-right: 8px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.action-predict {
  margin-top: 30px;
}

.btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn i {
  margin-right: 8px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2980b9;
}

.btn-outline {
  background-color: transparent;
  border: 1px solid #3498db;
  color: #3498db;
}

.btn-outline:hover:not(:disabled) {
  background-color: #f0f8ff;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 13px;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 错误状态 */
.error-container {
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
  padding: 32px;
}

.alert {
  width: 100%;
  padding: 16px;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.5;
}

.alert-danger {
  background-color: #fdedec;
  color: #e74c3c;
  border-left: 4px solid #e74c3c;
}

/* 图表区域 */
.chart-container {
  height: 400px;
  margin-bottom: 30px;
  border-radius: 8px;
  padding: 16px;
  background-color: #f8f9fa;
}

/* 统计摘要 */
.stats-container {
  background-color: #f8fafc;
  border-radius: 10px;
  padding: 20px;
}

.stats-title {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  color: #2c3e50;
  display: flex;
  align-items: center;
}

.stats-title i {
  margin-right: 8px;
  color: #3498db;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.stat-item {
  padding: 16px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.stat-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.stat-value.positive {
  color: #27ae60;
  display: flex;
  align-items: center;
}

.stat-value.negative {
  color: #e74c3c;
  display: flex;
  align-items: center;
}

.stat-value i {
  margin-right: 6px;
}

/* 商家选择按钮 */
.merchant-select-btn {
  width: 100%;
  justify-content: flex-start;
  padding: 12px 16px;
  font-size: 14px;
}

/* 商家选择弹窗 */
.merchant-modal-content.large {
  max-width: 900px;
}

/* 商家选择复选框 */
.merchant-checkbox {
  margin-right: 12px;
  pointer-events: none;
}

.merchant-checkbox input[type="checkbox"] {
  width: 16px;
  height: 16px;
}

/* 已选商家展示卡片 */
.selected-merchants-card {
  margin-bottom: 24px;
}

.selected-merchants-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.selected-merchant-tag {
  background-color: #e8f5ff;
  color: #2980b9;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #b3e0ff;
}

.selected-merchant-tag:hover {
  background-color: #d0ebff;
  transform: translateY(-1px);
}

.remove-icon {
  margin-left: 8px;
  font-size: 16px;
  font-weight: bold;
  color: #95a5a6;
  transition: color 0.2s;
}

.selected-merchant-tag:hover .remove-icon {
  color: #e74c3c;
}

/* 商家选择弹窗底部区域 */
.merchant-modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.selected-count {
  font-size: 14px;
  color: #34495e;
  font-weight: 600;
  flex-grow: 1;
  text-align: center;
}

/* 商家预测对比表格 */
.comparison-table-container {
  margin-top: 30px;
  background-color: #f8fafc;
  border-radius: 10px;
  padding: 20px;
  overflow-x: auto;
}

.comparison-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  margin-top: 16px;
}

.comparison-table th,
.comparison-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #ecf0f1;
}

.comparison-table th {
  background-color: #ffffff;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #ecf0f1;
}

.comparison-table tr:hover {
  background-color: #f1f8ff;
}

.comparison-table td:first-child {
  font-weight: 600;
}

/* 新增图标 */
.icon-merchants::before {
  content: "👥";
}

/* 商家选择相关样式 */
.merchant-input-group {
  display: flex;
  gap: 8px;
}

.merchant-fetch-btn {
  flex-shrink: 0;
}

.merchant-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.merchant-modal-content {
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.merchant-modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.merchant-modal-header h3 {
  margin: 0;
  font-size: 20px;
  color: #2c3e50;
  font-weight: 600;
}

.modal-close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #718096;
  transition: color 0.2s;
}

.modal-close-btn:hover {
  color: #e74c3c;
}

.merchant-modal-body {
  padding: 20px;
  overflow-y: auto;
  max-height: 60vh;
}

.merchant-modal-footer {
  padding: 16px 20px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.merchant-selection {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.merchant-selection-item {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.merchant-selection-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: #cbd5e0;
}

.merchant-selection-item.selected {
  border-color: #3498db;
  background-color: rgba(52, 152, 219, 0.05);
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.3);
}

.merchant-selection-header {
  display: flex;
  align-items: center;
}

.merchant-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.merchant-info {
  margin: 12px 0;
  font-size: 14px;
  color: #4a5568;
}

.merchant-contact, .merchant-scope {
  margin-bottom: 6px;
  display: flex;
  align-items: center;
}

.merchant-contact i, .merchant-scope i {
  margin-right: 8px;
  color: #718096;
}

.merchant-selection-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.merchant-id {
  font-size: 13px;
  color: #718096;
}

.selection-indicator {
  font-size: 13px;
  color: #718096;
}

.selection-indicator .icon-check {
  color: #3498db;
  font-weight: bold;
}

/* 图标样式 */
[class^="icon-"] {
  font-family: "Font Awesome 5 Free", Arial, sans-serif;
  font-weight: 900;
  font-style: normal;
}

.icon-settings::before {
  content: "⚙️";
}

.icon-chart::before {
  content: "📊";
}

.icon-refresh::before {
  content: "🔄";
}

.icon-error::before {
  content: "⚠️";
}

.icon-info::before {
  content: "ℹ️";
}

.icon-analytics::before {
  content: "📈";
}

.icon-trending-up::before {
  content: "📈";
}

.icon-trending-down::before {
  content: "📉";
}

.icon-check::before {
  content: "✓";
}

.icon-contact::before {
  content: "📞";
}

.icon-scope::before {
  content: "🏢";
}

/* 表格状态样式 */
.positive {
  color: #27ae60;
}

.negative {
  color: #e74c3c;
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #7f8c8d;
  font-style: italic;
  font-size: 15px;
  background-color: #f8fafc;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-data i {
  margin-right: 8px;
  color: #3498db;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filters {
    flex-direction: column;
    gap: 16px;
  }

  .form-group {
    width: 100%;
  }

  .action-buttons {
    flex-direction: column;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .merchant-selection {
    grid-template-columns: 1fr;
  }

  .merchant-modal-content.large {
    width: 95%;
    margin: 10px;
  }

  .selected-merchants-list {
    gap: 8px;
  }

  .selected-merchant-tag {
    font-size: 13px;
    padding: 5px 10px;
  }

  .comparison-table {
    font-size: 13px;
  }

  .comparison-table th,
  .comparison-table td {
    padding: 8px 12px;
  }
}
</style>

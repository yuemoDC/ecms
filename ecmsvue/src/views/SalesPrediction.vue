<template>
  <div class="sales-prediction-container page-container">
    <AppNavbar @navigate="handleNavigation"/> <!-- 引入导航栏组件 -->
    <VantaBackground ref="vantaBg" class="vanta-container"/>
    <h1 class="page-title">智能销售预测分析</h1>

    <!-- 预测参数设置卡片 -->
    <div class="card filters-card">
      <div class="card-header">
        <div class="card-title">
          <i class="icon-settings"></i>预测参数设置
        </div>
        <p class="card-subtitle">设置参数后点击预测按钮查看分析结果</p>
      </div>

      <div class="filters">
        <div class="form-group">
          <label for="merchantId">商家选择<span class="required">*</span></label>
          <div class="merchant-input-group">
            <input
                id="merchantId"
                v-model.number="merchantId"
                type="number"
                min="1"
                class="form-control"
                placeholder="请选择商家"
                readonly
            />
            <button
                @click="fetchMerchants"
                class="btn btn-outline merchant-fetch-btn"
                :disabled="loadingMerchants"
            >
              <i class="icon-refresh"></i>
              {{ loadingMerchants ? '获取中...' : '获取商家' }}
            </button>
          </div>
        </div>

        <div class="form-group">
          <label for="viewType">分析模式<span class="required">*</span></label>
          <select id="viewType" v-model="selectedView" class="form-control">
            <option value="all">所有产品总体预测</option>
            <option value="product">单个产品详细分析</option>
          </select>
        </div>

        <div v-if="selectedView === 'product'" class="form-group">
          <label for="daysPredict">预测天数</label>
          <input
              id="daysPredict"
              v-model.number="daysToPredict"
              type="number"
              min="1"
              max="365"
              class="form-control"
              placeholder="默认30天"
          />
        </div>
      </div>

      <!-- 商家选择弹窗 -->
      <div v-if="showMerchantModal" class="merchant-modal">
        <div class="merchant-modal-content">
          <div class="merchant-modal-header">
            <h3>选择商家</h3>
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
                  :class="{ selected: selectedMerchantId === merchant.merchantId }"
                  @click="selectMerchant(merchant)"
              >
                <div class="merchant-selection-header">
                  <h4 class="merchant-title">{{ merchant.merchantName || `商家 #${merchant.merchantId}` }}</h4>
                </div>
                <div class="merchant-info">
                  <div class="merchant-contact">
                    <i class="icon-contact"></i>
                    {{ merchant.contactInfo || '暂无联系方式' }}
                  </div>
                  <div class="merchant-scope">
                    <i class="icon-scope"></i>
                    {{ merchant.businessScope || '暂无经营范围' }}
                  </div>
                </div>
                <div class="merchant-selection-footer">
                  <div class="merchant-id">ID: {{ merchant.merchantId }}</div>
                  <div class="selection-indicator">
                    <i v-if="selectedMerchantId === merchant.merchantId" class="icon-check"></i>
                    <span v-else>点击选择</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="merchant-modal-footer">
            <button
                @click="confirmMerchantSelection"
                class="btn btn-primary"
                :disabled="!selectedMerchantId"
            >
              确认选择
            </button>
            <button @click="closeMerchantModal" class="btn btn-outline">取消</button>
          </div>
        </div>
      </div>

      <!-- 产品列表获取按钮 -->
      <div class="action-buttons" v-if="selectedView === 'product'">
        <button
            @click="fetchProducts"
            class="btn btn-outline"
            :disabled="loadingProducts || !merchantId"
        >
          <i class="icon-refresh"></i>
          {{ loadingProducts ? '获取中...' : '获取产品列表' }}
        </button>
      </div>

      <!-- 产品选择卡片区域 -->
      <div v-if="selectedView === 'product' && products.length > 0" class="product-selection-container">
        <h3 class="section-title">选择要预测的产品</h3>
        <div class="product-selection">
          <div
              v-for="product in products"
              :key="product.productId"
              class="product-selection-item"
              :class="{ selected: selectedProductId === product.productId }"
              @click="selectProduct(product.productId)"
          >
            <div class="product-selection-header">
              <h4 class="product-title">{{ product.productName || `产品 #${product.productId}` }}</h4>
              <div class="product-price">¥{{ product.price || '0.00' }}</div>
            </div>
            <div class="product-description">
              {{ product.description
                ? (product.description.length > 60
                    ? product.description.substring(0, 60) + '...'
                    : product.description)
                : '暂无描述' }}
            </div>
            <div class="product-selection-footer">
              <div class="product-id">ID: {{ product.productId }}</div>
              <div class="selection-indicator">
                <i v-if="selectedProductId === product.productId" class="icon-check"></i>
                <span v-else>点击选择</span>
              </div>
            </div>
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
          {{ loading ? '分析中...' : '开始预测分析' }}
        </button>
      </div>

      <div v-if="formError" class="form-error">
        <i class="icon-error"></i> {{ formError }}
      </div>
    </div>

    <!-- 预测结果卡片 -->
    <div v-if="showResults" class="card data-card">
      <div class="card-header">
        <div class="card-title">
          <i class="icon-chart"></i>预测结果分析
        </div>
        <p class="card-subtitle">
          {{ selectedView === 'all'
            ? '所有产品总体销售趋势预测'
            : (selectedProductName
                ? `${selectedProductName} 销售趋势预测`
                : `产品 #${selectedProductId} 销售趋势预测`) }}
        </p>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <p>正在分析数据，请稍候...</p>
      </div>

      <div v-else-if="error" class="error-container">
        <div class="alert alert-danger">
          <i class="icon-error"></i> {{ error }}
        </div>
        <button @click="startPrediction" class="btn btn-primary">
          <i class="icon-refresh"></i> 重新分析
        </button>
      </div>

      <div v-else-if="noDataMessage" class="no-data-container">
        <div class="alert alert-warning">
          <i class="icon-info"></i> {{ noDataMessage }}
        </div>
        <div class="no-data-actions">
          <button
              v-if="selectedView === 'product'"
              @click="fetchProducts"
              class="btn btn-outline"
          >
            <i class="icon-refresh"></i> 获取产品列表
          </button>
          <button
              v-else
              @click="fetchAllProductsPredictions"
              class="btn btn-primary"
          >
            <i class="icon-refresh"></i> 重新尝试
          </button>
        </div>
      </div>

      <div
          v-else-if="predictionData.dates && predictionData.dates.length > 0"
          class="prediction-content"
      >
        <!-- 图表区域 -->
        <div class="chart-container">
          <Line :data="chartData" :options="chartOptions" />
        </div>

        <!-- 分析摘要 -->
        <div class="stats-container">
          <h3 class="stats-title">
            <i class="icon-analytics"></i>预测数据摘要
          </h3>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-label">预测周期</div>
              <div class="stat-value">
                {{ predictionData.dates[0] }} 至
                {{ predictionData.dates[predictionData.dates.length - 1] }}
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">平均预测销售额</div>
              <div class="stat-value">
                ¥{{ calculateAverage(predictionData.predictions).toFixed(2) }}
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">最高预测销售额</div>
              <div class="stat-value">
                ¥{{ Math.max(...predictionData.predictions).toFixed(2) }}
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">最低预测销售额</div>
              <div class="stat-value">
                ¥{{ Math.min(...predictionData.predictions).toFixed(2) }}
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">总预测销售额</div>
              <div class="stat-value">
                ¥{{ calculateSum(predictionData.predictions).toFixed(2) }}
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">预测趋势</div>
              <div
                  class="stat-value"
                  :class="calculateTrend(predictionData.predictions) >= 0 ? 'positive' : 'negative'"
              >
                <i
                    :class="
                    calculateTrend(predictionData.predictions) >= 0
                      ? 'icon-trending-up'
                      : 'icon-trending-down'
                  "
                ></i>
                {{ calculateTrend(predictionData.predictions) >= 0 ? '上升' : '下降' }}
                {{ Math.abs(calculateTrend(predictionData.predictions)).toFixed(2) }}%
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 相关产品卡片 -->
    <div
        v-if="
        showResults &&
        selectedView === 'all' &&
        !loading &&
        !error &&
        !noDataMessage &&
        products.length > 0
      "
        class="card related-card"
    >
      <div class="card-header">
        <div class="card-title">
          <i class="icon-products"></i>相关产品销售情况
        </div>
        <p class="card-subtitle">点击查看单个产品的详细预测分析</p>
      </div>

      <div class="product-grid">
        <div
            v-for="product in products.slice(0, 4)"
            :key="product.productId"
            class="product-item"
        >
          <h4 class="product-name">
            {{ product.productName || `产品 #${product.productId}` }}
          </h4>
          <p class="product-description">
            {{
              product.description
                  ? (product.description.length > 50
                      ? product.description.substring(0, 50) + '...'
                      : product.description)
                  : '暂无描述'
            }}
          </p>
          <div class="product-price">¥{{ product.price || '0.00' }}</div>
          <button
              @click="viewProductPrediction(product.productId)"
              class="btn btn-sm btn-outline"
          >
            <i class="icon-chart"></i> 查看详细预测
          </button>
        </div>
      </div>
    </div>

    <!-- 历史订单数据 (产品视图) -->
    <div
        v-if="
        showResults &&
        selectedProductId &&
        selectedView === 'product' &&
        !loading &&
        !error &&
        !noDataMessage &&
        productOrders.length > 0
      "
        class="card orders-card"
    >
      <div class="card-header">
        <div class="card-title">
          <i class="icon-history"></i>历史订单数据
        </div>
        <p class="card-subtitle">近期订单记录分析</p>
      </div>

      <div class="order-list">
        <table class="table">
          <thead>
          <tr>
            <th>订单号</th>
            <th>订单日期</th>
            <th>数量</th>
            <th>订单金额</th>
            <th>状态</th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="order in productOrders.slice(0, 5)"
              :key="order.orderId"
          >
            <td>{{ order.orderNumber || '未知' }}</td>
            <td>{{ formatDate(order.createdAt) }}</td>
            <td>{{ getOrderQuantity(order) }}</td>
            <td>¥{{ order.totalAmount || '0.00' }}</td>
            <td>
                <span :class="getStatusClass(order)">
                  {{ getStatusText(order) }}
                </span>
            </td>
          </tr>
          </tbody>
        </table>
        <div v-if="productOrders.length > 5" class="view-more">
          <router-link
              :to="`/orders?productId=${selectedProductId}`"
              class="btn btn-sm btn-outline"
          >
            <i class="icon-more"></i> 查看更多订单
          </router-link>
        </div>
      </div>
    </div>

    <div
        v-if="
        showResults &&
        selectedProductId &&
        selectedView === 'product' &&
        !loading &&
        !error &&
        !noDataMessage &&
        productOrders.length === 0
      "
        class="card orders-card"
    >
      <div class="card-header">
        <div class="card-title">
          <i class="icon-history"></i>历史订单数据
        </div>
      </div>
      <div class="no-data">
        <i class="icon-info"></i> 暂无该产品的历史订单数据
      </div>
    </div>

    <!-- AI 建议卡片 -->
    <div
        v-if="showResults && predictionData.predictions.length > 0"
        class="card ai-suggestion-card"
    >
      <div class="card-header">
        <div class="card-title">
          <i class="icon-lightbulb"></i>AI 智能优化建议
        </div>
        <p class="card-subtitle">基于预测结果生成个性化运营建议</p>
      </div>

      <div class="action-buttons">
        <button
            @click="fetchAISuggestion"
            class="btn btn-primary"
            :disabled="loadingSuggestion"
        >
          <i class="icon-robot"></i>
          {{ loadingSuggestion ? '生成中...' : '点击生成 AI 建议' }}
        </button>
      </div>

      <div v-if="showSuggestion" class="ai-suggestion-content">
        <div v-if="loadingSuggestion" class="loading-container">
          <div class="spinner"></div>
          <p>AI 正在分析销售趋势并生成建议...</p>
        </div>
        <div v-else class="suggestion-text">
          <p style="white-space: pre-wrap;">{{ aiSuggestion }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue';
import axios from 'axios';
import { Chart, registerables } from 'chart.js';
import { Line } from 'vue-chartjs';
import AppNavbar from "@/components/AdminNavbar.vue";
import VantaBackground from "@/assets/VantaBackground.vue";
import {gsap} from "gsap";

// 注册 Chart.js 组件
Chart.register(...registerables);

export default {
  name: 'SalesPredictionView',
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
    const merchantId = ref(null);
    const selectedProductId = ref(null);
    const daysToPredict = ref(30);
    const selectedView = ref('all');

    // 商家选择相关
    const merchants = ref([]);
    const loadingMerchants = ref(false);
    const showMerchantModal = ref(false);
    const selectedMerchantId = ref(null);
    const selectedMerchantName = ref('');

    // 状态控制
    const loading = ref(false);
    const loadingProducts = ref(false);
    const error = ref(null);
    const formError = ref(null);
    const showResults = ref(false);
    const noDataMessage = ref(null);

    // AI 建议相关
    const aiSuggestion = ref('');
    const loadingSuggestion = ref(false);
    const showSuggestion = ref(false);

    // 数据
    const products = ref([]);
    const productOrders = ref([]);
    const predictionData = reactive({
      dates: [],
      predictions: []
    });

    // 获取商家列表
    const fetchMerchants = async () => {
      loadingMerchants.value = true;
      formError.value = null;
      showMerchantModal.value = true;
      try {
        const res = await axios.get('http://localhost:8080/api/merchants');
        if (Array.isArray(res.data)) {
          merchants.value = res.data;
        } else {
          throw new Error('返回数据格式不正确');
        }
      } catch (e) {
        formError.value = `无法获取商家列表: ${e.message}`;
      } finally {
        loadingMerchants.value = false;
      }
    };

    // 选择商家
    const selectMerchant = (m) => {
      selectedMerchantId.value = m.merchantId;
      selectedMerchantName.value = m.merchantName || `商家 #${m.merchantId}`;
    };

    const confirmMerchantSelection = () => {
      if (selectedMerchantId.value) {
        merchantId.value = selectedMerchantId.value;
        closeMerchantModal();
        selectedProductId.value = null;
        products.value = [];
        showResults.value = false;
        error.value = null;
        noDataMessage.value = null;
      }
    };

    const closeMerchantModal = () => {
      showMerchantModal.value = false;
    };

    // 获取所有产品的预测
    const fetchAllProductsPredictions = async () => {
      loading.value = true;
      error.value = null;
      noDataMessage.value = null;
      try {
        const res = await axios.get(`http://localhost:8080/api/ai/sales-prediction/${merchantId.value}`);
        if (res.data.message) {
          noDataMessage.value = res.data.message;
          predictionData.dates = [];
          predictionData.predictions = [];
          if (res.data.message.includes('未找到此商家的产品')) {
            await fetchProducts();
          }
        } else {
          predictionData.dates = res.data.dates || [];
          predictionData.predictions = res.data.predictions || [];
        }
        showResults.value = true;
      } catch (e) {
        error.value = e.response?.data?.message || '无法获取预测数据';
      } finally {
        loading.value = false;
      }
    };

    // 获取单个产品的预测
    const fetchProductPredictions = async () => {
      if (!selectedProductId.value) {
        formError.value = '请选择要预测的产品';
        return;
      }
      loading.value = true;
      error.value = null;
      noDataMessage.value = null;
      try {
        const res = await axios.get(
            `http://localhost:8080/api/ai/sales-prediction-by-product/${merchantId.value}/${selectedProductId.value}/${daysToPredict.value}`
        );
        if (res.data.message) {
          noDataMessage.value = res.data.message;
          predictionData.dates = [];
          predictionData.predictions = [];
        } else {
          predictionData.dates = res.data.dates || [];
          predictionData.predictions = res.data.predictions || [];
        }
        await fetchProductOrders();
        showResults.value = true;
      } catch (e) {
        error.value = e.response?.data?.message || '无法获取产品预测数据';
      } finally {
        loading.value = false;
      }
    };

    // 获取产品列表
    const fetchProducts = async () => {
      if (!merchantId.value) {
        formError.value = '请先选择商家';
        return;
      }
      loadingProducts.value = true;
      formError.value = null;
      try {
        const res = await axios.get(`http://localhost:8080/api/products/merchant/${merchantId.value}`);
        if (!Array.isArray(res.data)) throw new Error('返回数据格式不正确');
        products.value = res.data;
        if (products.value.length === 0) {
          formError.value = '未找到该商家的产品';
          noDataMessage.value = selectedView.value === 'product'
              ? '该商家没有产品可供分析'
              : null;
        } else if (products.value.length === 1) {
          selectProduct(products.value[0].productId);
        } else {
          selectedProductId.value = null;
        }
      } catch (e) {
        formError.value = `无法获取产品列表: ${e.message}`;
        products.value = [];
      } finally {
        loadingProducts.value = false;
      }
    };

    // 选择产品
    const selectProduct = (id) => {
      selectedProductId.value = id;
      noDataMessage.value = null;
      formError.value = null;
    };

    // 获取订单数据
    const fetchProductOrders = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/api/orders/${merchantId.value}`);
        productOrders.value = res.data.filter(o => o.merchantId === merchantId.value);
      } catch {
        productOrders.value = [];
      }
    };

    // 开始预测
    const startPrediction = () => {
      formError.value = null;
      error.value = null;
      noDataMessage.value = null;
      if (!merchantId.value) {
        formError.value = '请选择商家';
        return;
      }
      if (selectedView.value === 'all') {
        fetchAllProductsPredictions();
      } else {
        if (!selectedProductId.value) {
          formError.value = '请选择要预测的产品';
        } else {
          fetchProductPredictions();
        }
      }
    };

    // 查看单个产品预测
    const viewProductPrediction = (id) => {
      selectedProductId.value = id;
      selectedView.value = 'product';
      fetchProductPredictions();
    };

    // AI 建议调用
    const fetchAISuggestion = async () => {
      loadingSuggestion.value = true;
      showSuggestion.value = true;
      aiSuggestion.value = '';
      try {
        let url = '';
        if (selectedView.value === 'product') {
          url = `http://localhost:8080/api/ai/suggestion/${merchantId.value}/${selectedProductId.value}/${daysToPredict.value}`;
        } else {
          url = `http://localhost:8080/api/ai/suggestion/${merchantId.value}`;
        }
        const res = await axios.get(url);
        aiSuggestion.value = res.data.suggestion || '未能生成建议';
      } catch {
        aiSuggestion.value = 'AI建议获取失败，请稍后重试。';
      } finally {
        loadingSuggestion.value = false;
      }
    };

    // 统计计算
    const calculateAverage = (arr) =>
        arr.length ? arr.reduce((s, v) => s + v, 0) / arr.length : 0;
    const calculateSum = (arr) => (arr.length ? arr.reduce((s, v) => s + v, 0) : 0);
    const calculateTrend = (arr) =>
        arr.length > 1 ? ((arr[arr.length - 1] - arr[0]) / arr[0]) * 100 : 0;
    const formatDate = (s) => {
      try {
        const d = new Date(s);
        return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(
            d.getDate()
        ).padStart(2, '0')}`;
      } catch {
        return '日期格式错误';
      }
    };
    const getOrderQuantity = (o) => o.quantity || 1;
    const statusMap = {
      PENDING: '待处理',
      PROCESSING: '处理中',
      SHIPPED: '已发货',
      DELIVERED: '已送达',
      COMPLETED: '已完成',
      CANCELLED: '已取消'
    };
    const getStatusText = (o) => statusMap[o.status] || o.status || '未知状态';
    const statusClassMap = {
      PENDING: 'status-pending',
      PROCESSING: 'status-processing',
      SHIPPED: 'status-shipped',
      DELIVERED: 'status-delivered',
      COMPLETED: 'status-completed',
      CANCELLED: 'status-cancelled'
    };
    const getStatusClass = (o) => statusClassMap[o.status] || '';

    // 图表数据与配置
    const chartData = computed(() => ({
      labels: predictionData.dates,
      datasets: [
        {
          label:
              selectedView.value === 'all'
                  ? '所有产品销售预测'
                  : selectedProductName.value
                      ? `${selectedProductName.value} 销售预测`
                      : `产品 #${selectedProductId.value} 销售预测`,
          data: predictionData.predictions,
          tension: 0.3,
          borderWidth: 2,
          pointBackgroundColor: 'rgba(53,162,235,1)',
          pointBorderColor: '#fff',
          borderColor: 'rgba(53,162,235,1)',
          backgroundColor: 'rgba(53,162,235,0.2)'
        }
      ]
    }));
    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: { beginAtZero: false, title: { display: true, text: '销售金额 (¥)' }, grid: { color: 'rgba(0,0,0,0.05)' } },
        x: { title: { display: true, text: '日期' }, grid: { color: 'rgba(0,0,0,0.05)' } }
      },
      plugins: {
        tooltip: {
          callbacks: {
            label(ctx) {
              return `销售额: ¥${ctx.raw.toFixed(2)}`;
            }
          }
        },
        legend: { position: 'top' }
      }
    };

    // 计算属性
    const isFormValid = computed(
        () => merchantId.value && (selectedView.value === 'all' || selectedProductId.value)
    );
    const selectedProductName = computed(() => {
      const p = products.value.find((x) => x.productId === selectedProductId.value);
      return p ? p.productName : '';
    });

    return {
      merchantId,
      selectedProductId,
      daysToPredict,
      selectedView,
      merchants,
      loadingMerchants,
      showMerchantModal,
      selectedMerchantId,
      selectedMerchantName,
      fetchMerchants,
      selectMerchant,
      confirmMerchantSelection,
      closeMerchantModal,
      loading,
      loadingProducts,
      error,
      formError,
      showResults,
      noDataMessage,
      aiSuggestion,
      loadingSuggestion,
      showSuggestion,
      fetchAISuggestion,
      products,
      productOrders,
      predictionData,
      fetchProducts,
      startPrediction,
      viewProductPrediction,
      calculateAverage,
      calculateSum,
      calculateTrend,
      formatDate,
      getOrderQuantity,
      getStatusText,
      getStatusClass,
      chartData,
      chartOptions,
      isFormValid,
      selectedProductName,
      selectProduct
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

/* 产品选择样式 */
.product-selection-container {
  margin-top: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #2c3e50;
  display: flex;
  align-items: center;
}

.product-selection {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.product-selection-item {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  height: 180px;
}

.product-selection-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: #cbd5e0;
}

.product-selection-item.selected {
  border-color: #3498db;
  background-color: rgba(52, 152, 219, 0.05);
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.3);
}

.product-selection-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.product-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.product-selection-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 8px;
  border-top: 1px solid #edf2f7;
}

.product-id {
  font-size: 13px;
  color: #a0aec0;
}

.selection-indicator {
  font-size: 13px;
  color: #718096;
}

.selection-indicator .icon-check {
  color: #3498db;
  font-weight: bold;
}

/* 产品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 24px;
}

.product-item {
  padding: 20px;
  background-color: #f8fafc;
  border-radius: 10px;
  transition: all 0.3s;
  border: 1px solid #ecf0f1;
  position: relative;
  overflow: hidden;
}

.product-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  border-color: #d0d7de;
}

.product-name {
  margin-top: 0;
  margin-bottom: 12px;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.product-description {
  color: #7f8c8d;
  margin-bottom: 16px;
  line-height: 1.5;
  font-size: 14px;
  height: 42px;
  overflow: hidden;
}

.product-price {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 16px;
  font-size: 18px;
}

/* 订单列表 */
.order-list {
  overflow-x: auto;
  margin-top: 10px;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th, .table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #ecf0f1;
}

.table th {
  background-color: #f8fafc;
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.table td {
  font-size: 14px;
}

.table tr:hover {
  background-color: #f8fafc;
}

.status-pending {
  color: #f39c12;
  padding: 4px 8px;
  border-radius: 4px;
  background-color: rgba(243, 156, 18, 0.1);
  font-weight: 600;
  display: inline-block;
}

.status-processing {
  color: #3498db;
  padding: 4px 8px;
  border-radius: 4px;
  background-color: rgba(52, 152, 219, 0.1);
  font-weight: 600;
  display: inline-block;
}

.status-shipped {
  color: #9b59b6;
  padding: 4px 8px;
  border-radius: 4px;
  background-color: rgba(155, 89, 182, 0.1);
  font-weight: 600;
  display: inline-block;
}

.status-delivered {
  color: #2ecc71;
  padding: 4px 8px;
  border-radius: 4px;
  background-color: rgba(46, 204, 113, 0.1);
  font-weight: 600;
  display: inline-block;
}

.status-completed {
  color: #27ae60;
  padding: 4px 8px;
  border-radius: 4px;
  background-color: rgba(39, 174, 96, 0.1);
  font-weight: 600;
  display: inline-block;
}

.status-cancelled {
  color: #e74c3c;
  padding: 4px 8px;
  border-radius: 4px;
  background-color: rgba(231, 76, 60, 0.1);
  font-weight: 600;
  display: inline-block;
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

.view-more {
  text-align: right;
  margin-top: 16px;
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

.icon-products::before {
  content: "📦";
}

.icon-history::before {
  content: "🕒";
}

.icon-more::before {
  content: "👉";
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

  .product-grid {
    grid-template-columns: 1fr;
  }

  .product-selection {
    grid-template-columns: 1fr;
  }

  .merchant-selection {
    grid-template-columns: 1fr;
  }

  .merchant-input-group {
    flex-direction: column;
  }
}

.page-title {
  margin-bottom: 24px;
  font-size: 32px;
  font-weight: 700;
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
  width: 100px;
  height: 4px;
  background: linear-gradient(90deg, #1abc9c, #16a085);
  border-radius: 3px;
}

.card {
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.5), #f0f4f8);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 24px;
  margin-bottom: 30px;
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.5s ease forwards;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 10px;
  color: #34495e;
  display: flex;
  align-items: center;
}

.card-subtitle {
  font-size: 14px;
  color: #6c7a89;
  font-style: italic;
  margin-bottom: 16px;
}

.btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn i {
  font-size: 16px;
  margin-right: 6px;
}

.btn:hover {
  transform: scale(1.03);
}

.btn-primary {
  background-color: #1abc9c;
  color: white;
  border: none;
}

.btn-primary:hover:not(:disabled) {
  background-color: #16a085;
}

.btn-outline {
  background: transparent;
  color: #1abc9c;
  border: 1px solid #1abc9c;
}

.btn-outline:hover:not(:disabled) {
  background: #e8f8f5;
}

.ai-suggestion-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(8px);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.1);
}

.suggestion-text p {
  font-family: 'Courier New', Courier, monospace;
  font-size: 15px;
  line-height: 1.6;
  color: #2c3e50;
  white-space: pre-wrap;
}

.chart-container {
  height: 400px;
  padding: 16px;
  background-color: #f9f9fb;
  border-radius: 12px;
  box-shadow: inset 0 0 8px rgba(0,0,0,0.03);
  margin-bottom: 32px;
}

.stats-grid .stat-item {
  background-color: #ffffff;
  border: 1px solid #ecf0f1;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s;
}

.stats-grid .stat-item:hover {
  box-shadow: 0 4px 10px rgba(0,0,0,0.08);
  transform: translateY(-3px);
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
  font-size: 16px;
}
</style>

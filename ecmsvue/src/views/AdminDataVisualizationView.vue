<template>
  <AppNavbar />
  <div class="admin-visualization-view">
    <div class="view-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>数据分析</el-breadcrumb-item>
        <el-breadcrumb-item>平台数据可视化</el-breadcrumb-item>
      </el-breadcrumb>
      <h2>平台数据可视化</h2>
    </div>

    <div class="admin-visualization-dashboard">
      <div class="dashboard-header">
        <h1 class="dashboard-title">平台数据分析仪表板</h1>

        <div class="toolbar">
          <el-select
              v-model="timeFilter"
              placeholder="选择时间周期"
              @change="handleTimeFilterChange"
              class="time-filter">
            <el-option label="每日" value="daily"></el-option>
            <el-option label="每周" value="weekly"></el-option>
            <el-option label="每月" value="monthly"></el-option>
          </el-select>

          <el-button
              type="primary"
              @click="fetchDashboardData"
              :loading="loading.dashboard"
              class="refresh-btn">
            <el-icon><Refresh /></el-icon> 刷新数据
          </el-button>
        </div>
      </div>

      <!-- 销售概览卡片 -->
      <el-row :gutter="24" class="dashboard-cards">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-card shadow="hover" class="data-card">
            <template #header>
              <div class="card-header">
                <span>总销售额</span>
                <el-tag type="success" effect="plain" size="small">统计</el-tag>
              </div>
            </template>
            <div class="card-content">
              <h2 class="card-value">{{ formatCurrency(salesOverview.totalSales) }}</h2>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-card shadow="hover" class="data-card">
            <template #header>
              <div class="card-header">
                <span>总订单数</span>
                <el-tag type="primary" effect="plain" size="small">统计</el-tag>
              </div>
            </template>
            <div class="card-content">
              <h2 class="card-value">{{ salesOverview.totalOrders || 0 }}</h2>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-card shadow="hover" class="data-card">
            <template #header>
              <div class="card-header">
                <span>平均订单金额</span>
                <el-tag type="warning" effect="plain" size="small">统计</el-tag>
              </div>
            </template>
            <div class="card-content">
              <h2 class="card-value">{{ formatCurrency(salesOverview.avgOrderValue) }}</h2>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-card shadow="hover" class="data-card">
            <template #header>
              <div class="card-header">
                <span>最近30天销售额</span>
                <el-tag type="info" effect="plain" size="small">统计</el-tag>
              </div>
            </template>
            <div class="card-content">
              <h2 class="card-value">{{ formatCurrency(salesOverview.recentSales) }}</h2>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-card shadow="hover" class="data-card">
            <template #header>
              <div class="card-header">
                <span>商家总数</span>
                <el-tag type="danger" effect="plain" size="small">统计</el-tag>
              </div>
            </template>
            <div class="card-content">
              <h2 class="card-value">{{ salesOverview.merchantCount || 0 }}</h2>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-card shadow="hover" class="data-card">
            <template #header>
              <div class="card-header">
                <span>活跃商家</span>
                <el-tag type="success" effect="plain" size="small">统计</el-tag>
              </div>
            </template>
            <div class="card-content">
              <h2 class="card-value">{{ salesOverview.activeMerchantCount || 0 }}</h2>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表选择器 -->
      <div class="chart-selector">
        <el-radio-group v-model="selectedChartType" @change="handleChartTypeChange" size="large">
          <el-radio-button label="all">全部图表</el-radio-button>
          <el-radio-button label="trend">销售趋势</el-radio-button>
          <el-radio-button label="merchant">商家排行</el-radio-button>
          <el-radio-button label="product">产品排行</el-radio-button>
          <el-radio-button label="region">区域分布</el-radio-button>
          <el-radio-button label="status">订单状态</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 图表区域 -->
      <el-row :gutter="24" class="dashboard-charts">
        <!-- 销售趋势图 -->
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" v-if="isChartVisible('trend')">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="card-header">
                <span>平台销售趋势</span>
                <el-tag type="primary">{{ timeFilterLabel }}</el-tag>
              </div>
            </template>
            <div class="chart-container" v-loading="loading.salesTrend">
              <div ref="salesTrendChart" class="chart"></div>
            </div>
          </el-card>
        </el-col>

        <!-- 商家销售排行图 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12" v-if="isChartVisible('merchant')">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="card-header">
                <span>商家销售排行</span>
                <el-tag type="success">排名</el-tag>
              </div>
            </template>
            <div class="chart-container" v-loading="loading.merchantRanking">
              <div ref="merchantRankingChart" class="chart"></div>
            </div>
          </el-card>
        </el-col>

        <!-- 产品销售排行图 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12" v-if="isChartVisible('product')">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="card-header">
                <span>产品销售排行</span>
                <el-tag type="warning">排名</el-tag>
              </div>
            </template>
            <div class="chart-container" v-loading="loading.productRanking">
              <div ref="productRankingChart" class="chart"></div>
            </div>
          </el-card>
        </el-col>

        <!-- 区域销售分布图 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12" v-if="isChartVisible('region')">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="card-header">
                <span>区域销售分布</span>
                <el-tag type="info">分布</el-tag>
              </div>
            </template>
            <div class="chart-container" v-loading="loading.regionalDistribution">
              <div ref="regionalDistributionChart" class="chart"></div>
            </div>
          </el-card>
        </el-col>

        <!-- 订单状态分布图 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12" v-if="isChartVisible('status')">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="card-header">
                <span>订单状态分布</span>
                <el-tag type="danger">分布</el-tag>
              </div>
            </template>
            <div class="chart-container" v-loading="loading.orderStatusDistribution">
              <div ref="orderStatusDistributionChart" class="chart"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, nextTick, computed } from 'vue';
import { ElMessage, ElNotification } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';
import * as echarts from 'echarts';
import axios from 'axios';
import AppAdminNavbar from "@/components/AdminNavbar.vue";

export default {
  name: 'AdminDataVisualizationView',
  components: {
    AppNavbar: AppAdminNavbar,
    Refresh
  },
  setup() {
    // 图表引用
    const salesTrendChart = ref(null);
    const merchantRankingChart = ref(null);
    const productRankingChart = ref(null);
    const regionalDistributionChart = ref(null);
    const orderStatusDistributionChart = ref(null);

    // 图表实例
    let salesTrendChartInstance = null;
    let merchantRankingChartInstance = null;
    let productRankingChartInstance = null;
    let regionalDistributionChartInstance = null;
    let orderStatusDistributionChartInstance = null;

    // 图表数据缓存
    const chartData = reactive({
      salesTrend: null,
      merchantRanking: null,
      productRanking: null,
      regionalDistribution: null,
      orderStatusDistribution: null
    });

    // 数据加载状态
    const loading = reactive({
      dashboard: false,
      salesTrend: false,
      merchantRanking: false,
      productRanking: false,
      regionalDistribution: false,
      orderStatusDistribution: false
    });

    // 时间筛选状态和标签
    const timeFilter = ref('monthly');
    const timeFilterLabel = computed(() => {
      switch (timeFilter.value) {
        case 'daily': return '每日';
        case 'weekly': return '每周';
        case 'monthly': return '每月';
        default: return '每月';
      }
    });

    // 时间筛选变更处理函数
    const handleTimeFilterChange = (value) => {
      // 更新时间筛选
      timeFilter.value = value;

      // 使用选定的时间周期获取新数据
      fetchDashboardData();

      // 额外的视觉反馈
      ElMessage({
        message: `已切换到${timeFilterLabel.value}视图`,
        type: 'success',
        duration: 1500
      });
    };

    // 图表选择
    const selectedChartType = ref('all');

    // 检查图表是否应该可见
    const isChartVisible = (chartType) => {
      return selectedChartType.value === 'all' || selectedChartType.value === chartType;
    };

    // 处理图表类型变更
    const handleChartTypeChange = (value) => {
      // 等待DOM更新
      nextTick(async () => {
        // 重新初始化并渲染需要显示的图表
        if (value === 'all' || value === 'trend') {
          await initAndRenderChart('salesTrend', salesTrendChart, chartData.salesTrend);
        }
        if (value === 'all' || value === 'merchant') {
          await initAndRenderChart('merchantRanking', merchantRankingChart, chartData.merchantRanking);
        }
        if (value === 'all' || value === 'product') {
          await initAndRenderChart('productRanking', productRankingChart, chartData.productRanking);
        }
        if (value === 'all' || value === 'region') {
          await initAndRenderChart('regionalDistribution', regionalDistributionChart, chartData.regionalDistribution);
        }
        if (value === 'all' || value === 'status') {
          await initAndRenderChart('orderStatusDistribution', orderStatusDistributionChart, chartData.orderStatusDistribution);
        }
      });

      // 通知用户
      ElNotification({
        title: '图表显示已更新',
        message: value === 'all' ? '显示所有图表' : `仅显示${getChartTypeName(value)}图表`,
        type: 'success',
        duration: 2000
      });
    };

    // 初始化并渲染特定图表
    const initAndRenderChart = async (chartType, chartRef, data) => {
      if (!chartRef.value || !data) return;

      // 销毁旧实例
      disposeChart(chartType);

      // 创建新实例
      await nextTick();

      switch (chartType) {
        case 'salesTrend':
          salesTrendChartInstance = echarts.init(chartRef.value, 'ecmsTheme');
          renderSalesTrendChart(data);
          break;
        case 'merchantRanking':
          merchantRankingChartInstance = echarts.init(chartRef.value, 'ecmsTheme');
          renderMerchantRankingChart(data);
          break;
        case 'productRanking':
          productRankingChartInstance = echarts.init(chartRef.value, 'ecmsTheme');
          renderProductRankingChart(data);
          break;
        case 'regionalDistribution':
          regionalDistributionChartInstance = echarts.init(chartRef.value, 'ecmsTheme');
          renderRegionalDistributionChart(data);
          break;
        case 'orderStatusDistribution':
          orderStatusDistributionChartInstance = echarts.init(chartRef.value, 'ecmsTheme');
          renderOrderStatusDistributionChart(data);
          break;
      }
    };

    // 销毁特定图表实例
    const disposeChart = (chartType) => {
      switch (chartType) {
        case 'salesTrend':
          if (salesTrendChartInstance) {
            salesTrendChartInstance.dispose();
            salesTrendChartInstance = null;
          }
          break;
        case 'merchantRanking':
          if (merchantRankingChartInstance) {
            merchantRankingChartInstance.dispose();
            merchantRankingChartInstance = null;
          }
          break;
        case 'productRanking':
          if (productRankingChartInstance) {
            productRankingChartInstance.dispose();
            productRankingChartInstance = null;
          }
          break;
        case 'regionalDistribution':
          if (regionalDistributionChartInstance) {
            regionalDistributionChartInstance.dispose();
            regionalDistributionChartInstance = null;
          }
          break;
        case 'orderStatusDistribution':
          if (orderStatusDistributionChartInstance) {
            orderStatusDistributionChartInstance.dispose();
            orderStatusDistributionChartInstance = null;
          }
          break;
      }
    };

    // 销毁所有图表实例
    const disposeAllCharts = () => {
      disposeChart('salesTrend');
      disposeChart('merchantRanking');
      disposeChart('productRanking');
      disposeChart('regionalDistribution');
      disposeChart('orderStatusDistribution');
    };

    // 获取图表类型名称
    const getChartTypeName = (chartType) => {
      switch (chartType) {
        case 'trend': return '销售趋势';
        case 'merchant': return '商家销售排行';
        case 'product': return '产品销售排行';
        case 'region': return '区域销售分布';
        case 'status': return '订单状态分布';
        default: return '';
      }
    };

    // 销售概览数据
    const salesOverview = reactive({
      totalSales: 0,
      totalOrders: 0,
      avgOrderValue: 0,
      pendingOrders: 0,
      completedOrders: 0,
      recentSales: 0,
      merchantCount: 0,
      activeMerchantCount: 0
    });

    // 初始化图表主题
    const initChartTheme = () => {
      const theme = {
        color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#9254de', '#36cfc9', '#ff9c6e'],
        backgroundColor: 'rgba(252,252,252,0.6)',
        textStyle: {
          fontFamily: 'Arial, "Microsoft YaHei", sans-serif'
        },
        title: {
          textStyle: {
            color: '#303133',
            fontSize: 16,
            fontWeight: 'normal'
          },
          left: 'center'
        },
        legend: {
          textStyle: {
            color: '#606266'
          }
        },
        grid: {
          containLabel: true,
          left: '3%',
          right: '4%',
          bottom: '3%'
        }
      };

      echarts.registerTheme('ecmsTheme', theme);
    };

    // 处理窗口大小变化
    const handleResize = () => {
      if (isChartVisible('trend') && salesTrendChartInstance) {
        salesTrendChartInstance.resize();
      }
      if (isChartVisible('merchant') && merchantRankingChartInstance) {
        merchantRankingChartInstance.resize();
      }
      if (isChartVisible('product') && productRankingChartInstance) {
        productRankingChartInstance.resize();
      }
      if (isChartVisible('region') && regionalDistributionChartInstance) {
        regionalDistributionChartInstance.resize();
      }
      if (isChartVisible('status') && orderStatusDistributionChartInstance) {
        orderStatusDistributionChartInstance.resize();
      }
    };

    // 获取仪表板数据
    const fetchDashboardData = async () => {
      try {
        loading.dashboard = true;

        // 显示所有图表的加载指示器
        loading.salesTrend = true;
        loading.merchantRanking = true;
        loading.productRanking = true;
        loading.regionalDistribution = true;
        loading.orderStatusDistribution = true;

        // 通知用户正在基于所选时间段更新数据
        ElMessage({
          message: `正在加载${timeFilterLabel.value}数据...`,
          type: 'info',
          duration: 1000
        });

        // 销毁所有现有图表实例
        disposeAllCharts();

        // 使用当前时间筛选获取新数据
        const response = await axios.get(`http://localhost:8080/api/admin/data-visualization/dashboard?period=${timeFilter.value}`);
        const dashboardData = response.data;

        // 更新销售概览数据
        Object.assign(salesOverview, dashboardData.salesOverview);

        // 缓存图表数据
        Object.assign(chartData, {
          salesTrend: dashboardData.salesTrend,
          merchantRanking: dashboardData.merchantRanking,
          productRanking: dashboardData.productRanking,
          regionalDistribution: dashboardData.regionalDistribution,
          orderStatusDistribution: dashboardData.orderStatusDistribution
        });

        // 等待DOM更新
        await nextTick();

        // 重新初始化并渲染当前可见的图表
        const promises = [];

        if (isChartVisible('trend')) {
          promises.push(initAndRenderChart('salesTrend', salesTrendChart, chartData.salesTrend));
        }

        if (isChartVisible('merchant')) {
          promises.push(initAndRenderChart('merchantRanking', merchantRankingChart, chartData.merchantRanking));
        }

        if (isChartVisible('product')) {
          promises.push(initAndRenderChart('productRanking', productRankingChart, chartData.productRanking));
        }

        if (isChartVisible('region')) {
          promises.push(initAndRenderChart('regionalDistribution', regionalDistributionChart, chartData.regionalDistribution));
        }

        if (isChartVisible('status')) {
          promises.push(initAndRenderChart('orderStatusDistribution', orderStatusDistributionChart, chartData.orderStatusDistribution));
        }

        await Promise.all(promises);

        // 成功通知，提及时间周期
        ElNotification({
          title: '数据更新成功',
          message: `${timeFilterLabel.value}数据已成功加载`,
          type: 'success',
          duration: 2000
        });

      } catch (error) {
        console.error('获取仪表板数据失败', error);
        ElMessage({
          message: '获取仪表板数据失败: ' + (error.response?.data?.message || error.message),
          type: 'error'
        });
      } finally {
        // 清除所有加载状态
        loading.dashboard = false;
        loading.salesTrend = false;
        loading.merchantRanking = false;
        loading.productRanking = false;
        loading.regionalDistribution = false;
        loading.orderStatusDistribution = false;
      }
    };

    // 渲染销售趋势图，增强的时间周期处理
    const renderSalesTrendChart = (data) => {
      if (!data || !salesTrendChartInstance) {
        console.error('渲染销售趋势图失败：数据或图表实例不存在');
        return;
      }

      try {
        const option = {
          title: {
            text: data.title || '平台销售趋势',
            subtext: `按${timeFilterLabel.value}统计`,
            left: 'center',
            textStyle: {
              fontSize: 16
            },
            subtextStyle: {
              fontSize: 12,
              color: '#909399'
            }
          },
          tooltip: {
            trigger: 'axis',
            formatter: function(params) {
              const param = params[0];
              let formattedValue = param.value.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
              let timeUnit = '';

              switch (timeFilter.value) {
                case 'daily':
                  timeUnit = '日';
                  break;
                case 'weekly':
                  timeUnit = '周';
                  break;
                case 'monthly':
                  timeUnit = '月';
                  break;
              }

              return `${param.name} ${timeUnit}<br/>${param.seriesName}: ¥${formattedValue}`;
            },
            backgroundColor: 'rgba(255,255,255,0.8)',
            borderColor: '#eee',
            borderWidth: 1,
            textStyle: {
              color: '#333'
            },
            extraCssText: 'box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: data.xAxis || [],
            boundaryGap: false,
            axisTick: {
              alignWithLabel: true
            },
            axisLine: {
              lineStyle: {
                color: '#dcdfe6'
              }
            },
            axisLabel: {
              color: '#606266',
              rotate: data.xAxis && data.xAxis.length > 10 ? 30 : 0
            }
          },
          yAxis: {
            type: 'value',
            name: '销售额(元)',
            nameTextStyle: {
              color: '#606266'
            },
            axisLine: {
              show: true,
              lineStyle: {
                color: '#dcdfe6'
              }
            },
            splitLine: {
              lineStyle: {
                color: '#ebeef5'
              }
            }
          },
          series: [{
            name: '销售额',
            type: 'line',
            data: data.series || [],
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            itemStyle: {
              color: '#409EFF'
            },
            lineStyle: {
              width: 3
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
              ])
            },
            markPoint: {
              data: [
                { type: 'max', name: '最大值' },
                { type: 'min', name: '最小值' }
              ]
            },
            markLine: {
              data: [
                { type: 'average', name: '平均值' }
              ]
            }
          }]
        };

        salesTrendChartInstance.setOption(option, true);
      } catch (error) {
        console.error('渲染销售趋势图出错:', error);
      }
    };

    // 渲染商家销售排行图
    const renderMerchantRankingChart = (data) => {
      if (!data || !merchantRankingChartInstance) {
        console.error('渲染商家销售排行图失败：数据或图表实例不存在');
        return;
      }

      try {
        const option = {
          title: {
            text: data.title || '商家销售排行',
            subtext: `按${timeFilterLabel.value}统计`,
            left: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            },
            formatter: '{b}: {c} 元',
            backgroundColor: 'rgba(255,255,255,0.8)',
            borderColor: '#eee',
            borderWidth: 1,
            textStyle: {
              color: '#333'
            },
            extraCssText: 'box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'value',
            name: '销售额(元)',
            nameTextStyle: {
              color: '#606266'
            },
            axisLine: {
              show: true,
              lineStyle: {
                color: '#dcdfe6'
              }
            },
            splitLine: {
              lineStyle: {
                color: '#ebeef5'
              }
            }
          },
          yAxis: {
            type: 'category',
            data: data.merchantNames || [],
            axisLabel: {
              interval: 0,
              rotate: 0,
              color: '#606266'
            },
            axisLine: {
              lineStyle: {
                color: '#dcdfe6'
              }
            }
          },
          visualMap: {
            min: 0,
            max: Math.max(...(data.salesValues || [0])),
            text: ['高', '低'],
            inRange: {
              color: ['#67C23A', '#409EFF']
            },
            calculable: true,
            orient: 'horizontal',
            left: 'center',
            bottom: '0%',
            show: false
          },
          series: [{
            name: '销售额',
            type: 'bar',
            data: data.salesValues || [],
            barWidth: '50%',
            itemStyle: {
              color: function (params) {
                const value = params.value;
                const max = Math.max(...(data.salesValues || [0]));
                return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: '#67C23A' },
                  { offset: value / max, color: '#409EFF' }
                ]);
              },
              borderRadius: [0, 4, 4, 0]
            },
            label: {
              show: true,
              position: 'right',
              formatter: '{c} 元'
            }
          }]
        };

        merchantRankingChartInstance.setOption(option, true);
      } catch (error) {
        console.error('渲染商家销售排行图出错:', error);
      }
    };

    // 渲染产品销售排行图
    const renderProductRankingChart = (data) => {
      if (!data || !productRankingChartInstance) {
        console.error('渲染产品销售排行图失败：数据或图表实例不存在');
        return;
      }

      try {
        const option = {
          title: {
            text: data.title || '产品销售排行',
            subtext: `按${timeFilterLabel.value}统计`,
            left: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            },
            formatter: '{b}: {c} 元',
            backgroundColor: 'rgba(255,255,255,0.8)',
            borderColor: '#eee',
            borderWidth: 1,
            textStyle: {
              color: '#333'
            },
            extraCssText: 'box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'value',
            name: '销售额(元)',
            nameTextStyle: {
              color: '#606266'
            },
            axisLine: {
              show: true,
              lineStyle: {
                color: '#dcdfe6'
              }
            },
            splitLine: {
              lineStyle: {
                color: '#ebeef5'
              }
            }
          },
          yAxis: {
            type: 'category',
            data: data.productNames || [],
            axisLabel: {
              interval: 0,
              rotate: 0,
              color: '#606266'
            },
            axisLine: {
              lineStyle: {
                color: '#dcdfe6'
              }
            }
          },
          series: [{
            name: '销售额',
            type: 'bar',
            data: data.salesValues || [],
            barWidth: '50%',
            itemStyle: {
              color: function (params) {
                const colorList = ['#67C23A', '#409EFF', '#E6A23C', '#F56C6C', '#909399'];
                return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: '#E6A23C' },
                  { offset: 1, color: colorList[params.dataIndex % colorList.length] }
                ]);
              },
              borderRadius: [0, 4, 4, 0]
            },
            label: {
              show: true,
              position: 'right',
              formatter: '{c} 元'
            }
          }]
        };

        productRankingChartInstance.setOption(option, true);
      } catch (error) {
        console.error('渲染产品销售排行图出错:', error);
      }
    };

    // 渲染区域销售分布图
    const renderRegionalDistributionChart = (data) => {
      if (!data || !regionalDistributionChartInstance) {
        console.error('渲染区域销售分布图失败：数据或图表实例不存在');
        return;
      }

      try {
        const option = {
          title: {
            text: data.title || '区域销售分布',
            subtext: `按${timeFilterLabel.value}统计`,
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} 元 ({d}%)',
            backgroundColor: 'rgba(255,255,255,0.8)',
            borderColor: '#eee',
            borderWidth: 1,
            textStyle: {
              color: '#333'
            },
            extraCssText: 'box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'
          },
          legend: {
            type: 'scroll',
            orient: 'vertical',
            right: 10,
            top: 20,
            bottom: 20,
            textStyle: {
              color: '#606266'
            }
          },
          series: [{
            name: '区域销售',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['40%', '50%'],
            avoidLabelOverlap: true,
            itemStyle: {
              borderRadius: 4,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data.regions ? data.regions.map((region, index) => {
              return {
                name: region,
                value: data.values[index]
              };
            }) : []
          }]
        };

        regionalDistributionChartInstance.setOption(option, true);
      } catch (error) {
        console.error('渲染区域销售分布图出错:', error);
      }
    };

    // 渲染订单状态分布图
    const renderOrderStatusDistributionChart = (data) => {
      if (!data || !orderStatusDistributionChartInstance) {
        console.error('渲染订单状态分布图失败：数据或图表实例不存在');
        return;
      }

      try {
        const option = {
          title: {
            text: data.title || '订单状态分布',
            subtext: `按${timeFilterLabel.value}统计`,
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)',
            backgroundColor: 'rgba(255,255,255,0.8)',
            borderColor: '#eee',
            borderWidth: 1,
            textStyle: {
              color: '#333'
            },
            extraCssText: 'box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'
          },
          legend: {
            orient: 'vertical',
            left: 10,
            top: 'center',
            textStyle: {
              color: '#606266'
            }
          },
          series: [{
            name: '订单状态',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 4,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data.data ? data.data.map(item => {
              // 为不同状态设置不同颜色
              const colorMap = {
                '待处理': '#E6A23C',
                '已发货': '#409EFF',
                '已完成': '#67C23A',
                '已退款': '#F56C6C'
              };

              return {
                ...item,
                itemStyle: {
                  color: colorMap[item.name] || undefined
                }
              };
            }) : []
          }]
        };

        orderStatusDistributionChartInstance.setOption(option, true);
      } catch (error) {
        console.error('渲染订单状态分布图出错:', error);
      }
    };

    // 格式化货币
    const formatCurrency = (value) => {
      if (value === undefined || value === null) return '¥0.00';
      return '¥' + parseFloat(value).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ','); // 添加千位分隔符
    };

    // 组件挂载
    onMounted(async () => {
      await nextTick();

      // 初始化图表主题
      initChartTheme();

      // 添加窗口大小变化监听
      window.addEventListener('resize', handleResize);

      // 获取初始数据
      fetchDashboardData();

      // 显示欢迎提示
      ElNotification({
        title: '数据仪表板已加载',
        message: '欢迎使用平台数据可视化系统',
        type: 'success',
        duration: 3000
      });
    });

    // 组件卸载
    onUnmounted(() => {
      // 销毁所有图表实例
      disposeAllCharts();

      // 移除窗口大小变化监听
      window.removeEventListener('resize', handleResize);
    });

    return {
      salesTrendChart,
      merchantRankingChart,
      productRankingChart,
      regionalDistributionChart,
      orderStatusDistributionChart,
      loading,
      timeFilter,
      timeFilterLabel,
      salesOverview,
      fetchDashboardData,
      formatCurrency,
      selectedChartType,
      isChartVisible,
      handleChartTypeChange,
      handleTimeFilterChange
    };
  }
};
</script>

<style scoped>
.admin-visualization-view {
  padding: 20px;
}

.view-header {
  margin-bottom: 20px;
}

.view-header h2 {
  margin-top: 10px;
}

.admin-visualization-dashboard {
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  gap: 10px;
}

.time-filter {
  min-width: 120px;
}

.dashboard-cards {
  margin-bottom: 20px;
}

.data-card {
  margin-bottom: 20px;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  text-align: center;
  padding: 10px 0;
}

.card-value {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.chart-selector {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
  height: 100%;
}

.chart-container {
  height: 350px;
  width: 100%;
}

.chart {
  height: 100%;
  width: 100%;
}

.dashboard-charts {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .toolbar {
    margin-top: 10px;
    width: 100%;
  }

  .time-filter {
    flex-grow: 1;
  }

  .chart-container {
    height: 300px;
  }

  .card-value {
    font-size: 20px;
  }
}
</style>

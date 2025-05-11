<template>
  <AppNavbar @navigate="handleNavigation" />
  <div class="page-container">


    <h1>可视化销售分析</h1>

    <!-- 日期范围与按钮区域 -->
    <el-card shadow="hover" style="margin: 20px;">
      <div style="text-align: center; margin-bottom: 20px;">
        <el-date-picker
            v-model="dateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :picker-options="datePickerOptions"
            @change="filterDataByDateRange"
        ></el-date-picker>
      </div>
      <div style="display: flex; justify-content: center; gap: 20px; margin-bottom: 20px;">
        <el-button type="primary" @click="updateData">更新数据</el-button>
        <el-button type="success" @click="loadCsvData">读取数据</el-button>
        <el-button type="info" @click="exportCsv">导出CSV</el-button>
      </div>

    </el-card>

    <!-- 图表展示 -->
    <el-card shadow="hover" style="margin: 20px;">
      <div ref="salesChart1" style="width: 100%; height: 300px;"></div>
    </el-card>
    <el-card shadow="hover" style="margin: 20px;">
      <div ref="salesChart2" style="width: 100%; height: 300px;"></div>
    </el-card>
    <el-card shadow="hover" style="margin: 20px;">
      <div ref="salesChart3" style="width: 100%; height: 300px;"></div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios';
import AppNavbar from "@/components/MerchantNavbar.vue";
import * as echarts from 'echarts';
import {gsap} from "gsap";

export default {
  components: {AppNavbar},
  data() {
    return {
      salesData: [],
      chart1: null,
      chart2: null,
      chart3: null,
      dateRange: [],  // 日期范围
      datePickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now(); // 禁止选择未来的日期
        }
      }
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
    async updateData() {
      try {
        const response = await axios.post('http://localhost:8080/api/merchant-sales/export');
        if (response.status === 200) {
          this.$message.success('数据更新成功');
        } else {
          this.$message.error('数据更新失败');
        }
      } catch (error) {
        console.error(error);
        this.$message.error('请求失败，请重试');
      }
    },

    async loadCsvData() {
      try {
        const user = JSON.parse(localStorage.getItem('user'));
        const response = await axios.get(`http://localhost:8080/api/merchant-sales/read-csv?merchantId=${user.userid}`);
        this.salesData = response.data.filter(item => item.salesDate && item.totalSales !== null); // 去除缺失数据
        this.$message.success('数据读取成功');
        this.renderCharts();  // 渲染图表
      } catch (error) {
        console.error(error);
        this.$message.error('读取CSV数据失败');
      }
    },

    async exportCsv() {
      const user = JSON.parse(localStorage.getItem('user'));
      try {
        const response = await axios.get(`http://localhost:8080/api/merchant-sales/export-csv?merchantId=${user.userid}`, {
          responseType: 'blob'
        });

        const blob = new Blob([response.data], {type: 'text/csv;charset=utf-8;'});
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.setAttribute('download', `sales_${user.userid}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      } catch (error) {
        console.error(error);
        this.$message.error('导出失败');
      }
    },

    // 根据日期范围过滤数据
    filterDataByDateRange() {
      if (this.dateRange.length === 2) {
        const [startDate, endDate] = this.dateRange;
        const filteredData = this.salesData.filter(item => {
          const salesDate = new Date(item.salesDate);
          return salesDate >= startDate && salesDate <= endDate;
        });
        this.salesData = filteredData; // 更新展示的数据
        this.renderCharts();  // 重新渲染图表
      }
    },

    renderCharts() {
      const dates = this.salesData.map(item => item.salesDate);
      const totalSales = this.salesData.map(item => item.totalSales);
      const totalOrders = this.salesData.map(item => item.totalOrders);
      const avgOrderValue = this.salesData.map(item => item.avgOrderValue);

      // 图 1: Total Sales
      this.chart1 = echarts.init(this.$refs.salesChart1);
      this.chart1.setOption({
        title: { text: 'Total Sales 趋势' },
        tooltip: {
          trigger: 'item',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          },
          formatter: function (params) {
            return `
        <strong>${params.seriesName}</strong><br>
        日期: ${params.name}<br>
        销售额: ${params.value}
      `;
          }
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: 'Total Sales',
          type: 'line',
          data: totalSales,
          smooth: true,
          symbolSize: 8,  // 设置数据点的大小
          lineStyle: {
            width: 3  // 线条宽度
          },
          itemStyle: {
            color: '#3398DB'  // 设置数据点的颜色
          }
        }]
      });

      const commonTooltip = {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          crossStyle: {
            color: '#999'
          }
        },
        formatter: function (params) {
          const p = params[0];
          return `
      <strong>${p.seriesName}</strong><br>
      日期: ${p.name}<br>
      值: ${p.value}
    `;
        }
      };

      const commonXAxis = {
        type: 'category',
        data: dates
      };

      const commonYAxis = {
        type: 'value'
      };

      const commonSeriesStyle = {
        smooth: true,
        symbolSize: 8,
        lineStyle: {
          width: 3
        }
      };

// 图 2: Total Orders
      this.chart2 = echarts.init(this.$refs.salesChart2);
      this.chart2.setOption({
        title: { text: 'Total Orders 趋势' },
        tooltip: commonTooltip,
        xAxis: commonXAxis,
        yAxis: commonYAxis,
        series: [{
          name: 'Total Orders',
          type: 'line',
          data: totalOrders,
          ...commonSeriesStyle,
          itemStyle: {
            color: '#67C23A'  // 可按需求调整颜色
          }
        }]
      });

// 图 3: Avg Order Value
      this.chart3 = echarts.init(this.$refs.salesChart3);
      this.chart3.setOption({
        title: { text: 'Avg Order Value 趋势' },
        tooltip: commonTooltip,
        xAxis: commonXAxis,
        yAxis: commonYAxis,
        series: [{
          name: 'Avg Order Value',
          type: 'line',
          data: avgOrderValue,
          ...commonSeriesStyle,
          itemStyle: {
            color: '#E6A23C'  // 可按需求调整颜色
          }
        }]
      });

    }
  },
  beforeUnmount() {
    // 销毁图表实例
    if (this.chart1) this.chart1.dispose();
    if (this.chart2) this.chart2.dispose();
    if (this.chart3) this.chart3.dispose();
  },
  mounted() {
    this.enterAnimation();
  }
};
</script>
<style scoped>
h1 {
  margin-top: 20px;
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  font-family: 'Segoe UI', sans-serif;
}

.el-button {
  margin-bottom: 10px;
}

</style>
<template>
<!--  <AppNavbar @navigate="handleNavigation"/> &lt;!&ndash; 引入导航栏组件 &ndash;&gt;-->
  <div class="container">
    <h1>订单管理</h1> <!-- 页面标题 -->

    <div class="controls-wrapper">
      <el-input
          v-model="searchKeyword"
          placeholder="请输入订单编号进行搜索"
          style="width: 300px; margin-right: 20px;"
          @input="fetchOrdersByKeyword"
      />
      <!-- 绑定搜索关键词 -->
      <!-- 输入框占位符 -->
      <!-- 设置输入框样式 -->
      <!-- 输入变化时调用搜索方法 -->

      <el-button type="primary" @click="showOrderForm">添加订单</el-button> <!-- 添加订单按钮 -->
    </div>

    <!-- 订单表格 -->
    <el-table :data="orders" style="width: 100%; margin-top: 20px;" border>
      <el-table-column prop="orderNumber" label="订单编号" /> <!-- 订单编号列 -->
      <el-table-column prop="totalAmount" label="总金额" width="120">
        <template #default="scope">
          ¥{{ scope.row.totalAmount.toFixed(2) }} <!-- 格式化总金额 -->
        </template>
      </el-table-column>
      <el-table-column prop="orderStatus" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.orderStatus)">
            {{ getStatusText(scope.row.orderStatus) }} <!-- 根据状态获取文本和标签类型 -->
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="editOrder(scope.row)">编辑</el-button> <!-- 编辑按钮 -->
          <el-button size="small" type="danger" @click="confirmDelete(scope.row.orderId)">删除</el-button> <!-- 删除按钮 -->
        </template>
      </el-table-column>
    </el-table>

    <!-- 订单表单对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="selectedOrder ? '编辑订单' : '添加订单'"
        width="50%"
        @close="clearForm"
        :close-on-click-modal="false"
        append-to-body
        destroy-on-close
        center
    >
      <!-- 控制对话框的显示 -->
      <!-- 动态设置对话框标题 -->
      <!-- 关闭对话框时清空表单 -->
      <!-- 点击遮罩层不关闭 -->
      <!-- 添加到body中以避免嵌套和z-index问题 -->
      <!-- 关闭时销毁内容 -->
      <!-- 居中对话框 -->

      <el-form :model="orderForm" label-width="120px" size="default" style="max-height: 60vh; overflow-y: auto;"> <!-- 表单 -->
        <el-form-item label="订单编号" prop="orderNumber">
          <el-input v-model="orderForm.orderNumber" placeholder="请输入订单编号" /> <!-- 订单编号输入 -->
        </el-form-item>
        <el-form-item label="总金额" prop="totalAmount">
          <el-input-number v-model="orderForm.totalAmount" :min="0" :precision="2" /> <!-- 总金额输入 -->
        </el-form-item>
        <el-form-item label="状态" prop="orderStatus">
          <el-select v-model="orderForm.orderStatus" placeholder="选择状态"> <!-- 订单状态选择 -->
            <el-option label="待处理" value="pending" />
            <el-option label="已发货" value="shipped" />
            <el-option label="已完成" value="completed" />
            <el-option label="已退款" value="refunded" />
          </el-select>
        </el-form-item>
        <el-form-item label="商家ID" prop="merchantId" v-if="false"> <!-- 隐藏此字段，使用当前商家ID -->
          <el-input-number v-model="orderForm.merchantId" :min="1" disabled /> <!-- 商家ID输入 -->
        </el-form-item>
        <el-form-item label="顾客ID" prop="customerId">
          <el-input-number v-model="orderForm.customerId" :min="1" /> <!-- 顾客ID输入 -->
        </el-form-item>
        <el-form-item label="产品ID" prop="productId">
          <el-input-number v-model="orderForm.productId" :min="1" /> <!-- 产品ID输入 -->
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button> <!-- 取消按钮 -->
          <el-button type="primary" @click="saveOrder">保存</el-button> <!-- 保存按钮 -->
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框使用 MessageBox 组件 -->
  </div>
</template>

<script>
import axios from 'axios'; // 引入 axios 库进行 HTTP 请求
import { ElMessage, ElMessageBox } from 'element-plus'; // 引入 Element Plus 的消息和确认框组件
import AppMerchantNavbar from "@/components/MerchantNavbar.vue";
import {gsap} from "gsap"; // 引入GSAP动画库

export default {
  name: 'OrderManagement', // 组件名称
  components: { AppNavbar: AppMerchantNavbar }, // 注册导航栏组件
  data() {
    return {
      orders: [], // 订单数据列表
      searchKeyword: '', // 新增搜索关键词
      orderForm: { // 订单表单数据模型
        orderNumber: '',
        totalAmount: 0,
        orderStatus: 'pending',
        merchantId: null,
        customerId: null,
        productId: null
      },
      selectedOrder: null, // 当前选择的订单
      dialogVisible: false, // 控制表单对话框的显隐
      currentUserId: null, // 当前登录的用户ID
      currentMerchantId: null // 当前商家ID
    };
  },
  created() {
    // 在组件创建时，获取当前用户ID
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const userData = JSON.parse(userStr);
        this.currentUserId = userData.userid;
        console.log('当前用户ID:', this.currentUserId);
        // 在获取用户ID后立即获取商家ID
        this.fetchCurrentMerchantId();
      } catch (e) {
        console.error('解析用户数据失败:', e);
      }
    }
  },
  methods: {
    enterAnimation() {
      const tl = gsap.timeline();

      // 设置初始状态
      gsap.set(".container", {
        opacity: 0,
        y: 50
      });

      // 创建入场动画
      tl.to(".container", {
        duration: 0.8,
        opacity: 1,
        y: 0,
        ease: "power4.out"
      });
    },
    async handleNavigation(path) {
      await this.leaveAnimation();
      this.$router.push(path);
    },
    leaveAnimation() {
      return new Promise((resolve) => {
        const tl = gsap.timeline();

        // 使用更精确的选择器
        tl.to(".container", {
          duration: 0.8,
          opacity: 0,
          y: 100,
          ease: "power4.in"
        })
            .eventCallback("onComplete", resolve);
      });
    },
    getStatusTagType(status) {
      // 根据订单状态返回对应的标签类型
      const map = {
        pending: 'warning',
        shipped: 'primary',
        completed: 'success',
        refunded: 'danger'
      };
      return map[status] || '';
    },
    getStatusText(status) {
      // 根据订单状态返回对应的状态文本
      const map = {
        pending: '待处理',
        shipped: '已发货',
        completed: '已完成',
        refunded: '已退款'
      };
      return map[status] || status;
    },
    fetchOrders() {
      // 如果没有设置商家ID，则不获取订单
      if (!this.currentMerchantId) {
        console.log('未设置商家ID，无法获取订单');
        return;
      }

      // 获取当前商家的订单数据
      const url = `http://localhost:8080/api/orders/${this.currentMerchantId}`;
      console.log('获取订单的URL:', url);

      axios.get(url)
          .then(response => {
            this.orders = response.data; // 更新订单列表
            console.log('获取到订单数据:', this.orders);
          })
          .catch(error => {
            ElMessage.error('获取订单列表失败: ' + (error.response?.data?.message || error.message));
            console.error('获取订单时出错:', error);
          });
    },
    fetchOrdersByKeyword() {
      // 如果没有设置商家ID，则不搜索订单
      if (!this.currentMerchantId) {
        console.log('未设置商家ID，无法搜索订单');
        return;
      }

      // 如果没有关键词，则获取该商家的所有订单
      if (!this.searchKeyword) {
        this.fetchOrders();
        return;
      }

      // 根据搜索关键词获取订单数据
      const url = `http://localhost:8080/api/orders/search?keyword=${this.searchKeyword}`;
      console.log('搜索订单的URL:', url);

      axios.get(url)
          .then(response => {
            // 过滤出当前商家的订单
            this.orders = response.data.filter(order => order.merchantId === this.currentMerchantId);
            console.log('搜索到订单数据:', this.orders);
          })
          .catch(error => {
            ElMessage.error('搜索订单失败: ' + (error.response?.data?.message || error.message));
            console.error('搜索订单时出错:', error);
          });
    },
    showOrderForm() {
      // 显示添加订单表单
      this.selectedOrder = null; // 清空选择的订单
      this.orderForm = {
        orderNumber: this.generateOrderNumber(), // 生成随机订单编号
        totalAmount: 0,
        orderStatus: 'pending',
        merchantId: this.currentMerchantId, // 使用当前商家ID
        customerId: null,
        productId: null
      };
      this.dialogVisible = true; // 显示对话框
    },
    // 生成随机订单编号
    generateOrderNumber() {
      const prefix = 'ORD';
      const timestamp = new Date().getTime().toString().slice(-8);
      const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
      return `${prefix}${timestamp}${random}`;
    },
    editOrder(order) {
      // 显示编辑订单表单
      this.selectedOrder = order; // 设置选择的订单
      this.orderForm = {
        orderNumber: order.orderNumber,
        totalAmount: order.totalAmount,
        orderStatus: order.orderStatus,
        merchantId: order.merchantId,
        customerId: order.customerId,
        productId: order.productId
      };
      this.dialogVisible = true; // 显示对话框
    },
    clearForm() {
      // 清空表单
      this.selectedOrder = null; // 取消选择的订单
      this.orderForm = {
        orderNumber: '',
        totalAmount: 0,
        orderStatus: 'pending',
        merchantId: this.currentMerchantId,
        customerId: null,
        productId: null
      };
    },
    saveOrder() {
      // 保存订单前，确保设置了商家ID
      if (!this.orderForm.merchantId && this.currentMerchantId) {
        this.orderForm.merchantId = this.currentMerchantId;
      }

      // 如果没有商家ID，显示错误并返回
      if (!this.orderForm.merchantId) {
        ElMessage.error('无法确定商家ID，请刷新页面或重新登录');
        return;
      }

      // 保存订单（添加或更新）
      const url = this.selectedOrder
          ? `http://localhost:8080/api/orders/${this.selectedOrder.orderId}` // 更新订单
          : 'http://localhost:8080/api/orders'; // 添加订单

      const method = this.selectedOrder ? 'put' : 'post'; // 确定使用 PUT 或 POST 方法

      const orderData = { // 订单数据
        merchantId: this.orderForm.merchantId,
        customerId: this.orderForm.customerId,
        productId: this.orderForm.productId,
        orderNumber: this.orderForm.orderNumber,
        totalAmount: this.orderForm.totalAmount,
        orderStatus: this.orderForm.orderStatus
      };

      axios[method](url, orderData)
          .then(() => {
            ElMessage.success(this.selectedOrder ? '订单更新成功' : '订单添加成功'); // 显示成功消息
            this.fetchOrders(); // 重新获取订单列表
            this.dialogVisible = false; // 关闭对话框
          })
          .catch(error => {
            ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message)); // 显示错误消息
            console.error('保存订单时出错:', error);
          });
    },
    confirmDelete(orderId) {
      // 确认删除订单
      ElMessageBox.confirm(
          '确定要删除这个订单吗？此操作不可撤销。',
          '警告',
          {
            confirmButtonText: '确认删除',
            cancelButtonText: '取消',
            type: 'warning',
            appendTo: document.body // 添加到body，避免层级问题
          }
      )
          .then(() => {
            this.deleteOrder(orderId); // 调用删除方法
          })
          .catch(() => {
            ElMessage.info('已取消删除'); // 显示取消消息
          });
    },
    deleteOrder(orderId) {
      // 删除订单
      axios.delete(`http://localhost:8080/api/orders/${orderId}`)
          .then(() => {
            ElMessage.success('订单删除成功'); // 显示成功消息
            this.fetchOrders(); // 重新获取订单列表
          })
          .catch(error => {
            ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message)); // 显示错误消息
            console.error('删除订单时出错:', error);
          });
    },
    // 获取当前用户的商家ID
    fetchCurrentMerchantId() {
      if (!this.currentUserId) {
        console.error('未找到当前用户ID');
        return;
      }

      console.log('正在获取商家ID...');
      // 请求获取商家ID
      axios.get(`http://localhost:8080/api/merchants/user/${this.currentUserId}`)
          .then(response => {
            console.log('获取商家ID响应:', response.data);

            if (response.data.success) {
              this.currentMerchantId = response.data.merchantId;
              console.log('设置当前商家ID:', this.currentMerchantId);

              // 获取商家ID后加载订单
              this.fetchOrders();
            } else {
              console.error('获取商家ID失败:', response.data.message);
              ElMessage.warning('未找到您的商家信息，请联系管理员');
            }
          })
          .catch(error => {
            console.error('获取商家ID请求失败:', error);
            ElMessage.error('获取商家信息失败: ' + (error.response?.data?.message || error.message));
          });
    }
  },
  mounted() {
    // 应用入场动画
    this.enterAnimation();
  }
};
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  overflow-x: hidden; /* 防止水平溢出 */
}

h1 {
  margin-bottom: 30px;
  color: #303133;
}

.controls-wrapper {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.el-table {
  margin-top: 20px;
}

/* 添加一些过渡效果 */
.el-button {
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end; /* 将对话框底部内容右对齐 */
}

/* 确保对话框内容可滚动，避免超出屏幕 */
:deep(.el-dialog__body) {
  max-height: 60vh;
  overflow-y: auto;
  padding: 20px;
}

/* 确保对话框层级高于其他元素 */
:deep(.el-dialog) {
  z-index: 3000 !important;
}

/* 确保表格在小屏幕上也能正常显示 */
@media screen and (max-width: 768px) {
  .container {
    padding: 10px;
  }

  .controls-wrapper {
    flex-direction: column;
    align-items: flex-start;
  }

  .controls-wrapper .el-input {
    width: 100% !important;
    margin-bottom: 10px;
    margin-right: 0;
  }
}
</style>

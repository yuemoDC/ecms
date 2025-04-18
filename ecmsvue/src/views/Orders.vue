<template>
  <div>
    <AppNavbar /> <!-- 引入导航栏组件 -->
    <h1>订单管理</h1> <!-- 页面标题 -->

    <el-input v-model="searchKeyword"
    placeholder="请输入订单编号进行搜索"
    style="width: 300px; margin-bottom: 20px;"
    @input="fetchOrdersByKeyword"
    />
    <!-- 绑定搜索关键词 -->
    <!-- 输入框占位符 -->
    <!-- 设置输入框样式 -->
    <!-- 输入变化时调用搜索方法 -->
    <el-button type="primary" @click="showOrderForm" style="margin-bottom: 20px;">添加订单</el-button> <!-- 添加订单按钮 -->

    <!-- 订单表格 -->
    <el-table :data="orders" style="width: 100%;">
      <el-table-column prop="orderNumber" label="订单编号" /> <!-- 订单编号列 -->
      <el-table-column prop="totalAmount" label="总金额">
        <template #default="scope">
          ¥{{ scope.row.totalAmount.toFixed(2) }} <!-- 格式化总金额 -->
        </template>
      </el-table-column>
      <el-table-column prop="orderStatus" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.orderStatus)">
            {{ getStatusText(scope.row.orderStatus) }} <!-- 根据状态获取文本和标签类型 -->
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="editOrder(scope.row)">编辑</el-button> <!-- 编辑按钮 -->
          <el-button size="small" type="danger" @click="confirmDelete(scope.row.orderId)">删除</el-button> <!-- 删除按钮 -->
        </template>
      </el-table-column>
    </el-table>

    <!-- 订单表单对话框 -->
    <el-dialog v-model="dialogVisible"
    :title="selectedOrder ? '编辑订单' : '添加订单'"
    width="50%"
    @close="clearForm"
    >
    <!-- 控制对话框的显示 -->
    <!-- 动态设置对话框标题 -->
      <!-- 关闭对话框时清空表单 -->

    <el-form :model="orderForm" label-width="120px" size="default"> <!-- 表单 -->
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
      <el-form-item label="商家ID" prop="merchantId">
        <el-input-number v-model="orderForm.merchantId" :min="1" disabled v-if="selectedOrder" /> <!-- 商家ID输入 -->
        <el-input-number v-model="orderForm.merchantId" :min="1" v-else />
      </el-form-item>
      <el-form-item label="顾客ID" prop="customerId">
        <el-input-number v-model="orderForm.customerId" :min="1" disabled v-if="selectedOrder" /> <!-- 顾客ID输入 -->
        <el-input-number v-model="orderForm.customerId" :min="1" v-else />
      </el-form-item>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button> <!-- 取消按钮 -->
          <el-button type="primary" @click="saveOrder">保存</el-button> <!-- 保存按钮 -->
        </span>
    </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'; // 引入 axios 库进行 HTTP 请求
import { ElMessage, ElMessageBox } from 'element-plus'; // 引入 Element Plus 的消息和确认框组件
import AppNavbar from "@/components/Navbar.vue"; // 引入导航栏组件

export default {
  name: 'OrderManagement', // 组件名称
  components: { AppNavbar }, // 注册导航栏组件
  data() {
    return {
      orders: [], // 订单数据列表
      searchKeyword: '', // 新增搜索关键词
      orderForm: { // 订单表单数据模型
        orderNumber: '',
        totalAmount: 0,
        orderStatus: 'pending',
        merchantId: null,
        customerId: null
      },
      selectedOrder: null, // 当前选择的订单
      dialogVisible: false // 控制表单对话框的显隐
    };
  },
  methods: {
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
      // 获取所有订单数据
      axios.get('http://localhost:8080/api/orders')
          .then(response => {
            this.orders = response.data; // 更新订单列表
          })
          .catch(error => {
            ElMessage.error('获取订单列表失败: ' + error.message); // 显示错误消息
            console.error('获取订单时出错:', error);
          });
    },
    fetchOrdersByKeyword() {
      // 根据搜索关键词获取订单数据
      const url = this.searchKeyword
          ? `http://localhost:8080/api/orders/search?keyword=${this.searchKeyword}`
          : 'http://localhost:8080/api/orders';

      axios.get(url)
          .then(response => {
            this.orders = response.data; // 更新订单列表
          })
          .catch(error => {
            ElMessage.error('获取订单列表失败: ' + error.message); // 显示错误消息
            console.error('获取订单时出错:', error);
          });
    },
    showOrderForm() {
      // 显示添加订单表单
      this.selectedOrder = null; // 清空选择的订单
      this.orderForm = {
        orderNumber: '',
        totalAmount: 0,
        orderStatus: 'pending',
        merchantId: null,
        customerId: null
      };
      this.dialogVisible = true; // 显示对话框
    },
    editOrder(order) {
      // 显示编辑订单表单
      this.selectedOrder = order; // 设置选择的订单
      this.orderForm = {
        orderNumber: order.orderNumber,
        totalAmount: order.totalAmount,
        orderStatus: order.orderStatus,
        merchantId: order.merchantId,
        customerId: order.customerId
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
        merchantId: null,
        customerId: null
      };
    },
    saveOrder() {
      // 保存订单（添加或更新）
      const url = this.selectedOrder
          ? `http://localhost:8080/api/orders/${this.selectedOrder.orderId}` // 更新订单
          : 'http://localhost:8080/api/orders'; // 添加订单

      const method = this.selectedOrder ? 'put' : 'post'; // 确定使用 PUT 或 POST 方法

      const orderData = { // 订单数据
        merchantId: this.orderForm.merchantId,
        customerId: this.orderForm.customerId,
        orderNumber: this.orderForm.orderNumber,
        totalAmount: this.orderForm.totalAmount,
        orderStatus: this.orderForm.orderStatus
      };

      axios[method](url,orderData)
          .then(() => {
            ElMessage.success(this.selectedOrder ? '订单更新成功' : '订单添加成功'); // 显示成功消息
            this.fetchOrders(); // 重新获取订单列表
            this.dialogVisible = false; // 关闭对话框
          })
          .catch(error => {
            ElMessage.error('操作失败: ' + error.message); // 显示错误消息
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
            ElMessage.error('删除失败: ' + error.message); // 显示错误消息
            console.error('删除订单时出错:', error);
          });
    }
  },
  mounted() {
    this.fetchOrders(); // 初始加载所有订单
  }
};
</script>

<style scoped>

.dialog-footer {
  display: flex;
  justify-content: flex-end; /* 将对话框底部内容右对齐 */
}
</style>
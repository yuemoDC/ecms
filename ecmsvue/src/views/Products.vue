<template>
  <div>
    <AppNavbar /> <!-- 引入导航栏组件 -->
    <h1>产品管理</h1> <!-- 页面标题 -->

    <el-input
        v-model="searchKeyword"
    placeholder="请输入产品名称或描述进行搜索"
    style="width: 300px; margin-bottom: 20px;"
    @input="fetchProductsByKeyword"
    />
    <!-- 绑定搜索关键词 -->
    <!-- 输入框占位符 -->
    <!-- 设置输入框样式 -->
    <!-- 输入变化时调用搜索方法 -->

    <el-button type="primary" @click="showProductForm" style="margin-bottom: 20px;">添加产品</el-button> <!-- 添加产品按钮 -->

    <!-- 产品表格 -->
    <el-table :data="products" style="width: 100%;">
      <el-table-column prop="productName" label="产品名称" /> <!-- 产品名称列 -->
      <el-table-column prop="price" label="价格" /> <!-- 产品价格列 -->
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'"> <!-- 根据状态显示标签 -->
            {{ scope.row.status === 'active' ? '可用' : '不可用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="editProduct(scope.row)">编辑</el-button> <!-- 编辑按钮 -->
          <el-button size="small" type="danger" @click="confirmDelete(scope.row.productId)">删除</el-button> <!-- 删除按钮 -->
        </template>
      </el-table-column>
    </el-table>

    <!-- 产品表单对话框 -->
    <el-dialog
        v-model="dialogVisible"
    :title="selectedProduct ? '编辑产品' : '添加产品'"
    width="50%"
    @close="clearForm"
    >
    <!-- 控制对话框的显示 -->
    <!-- 动态设置对话框标题 -->
      <!-- 关闭对话框时清空表单 -->

    <el-form :model="productForm" label-width="120px" size="default"> <!-- 表单 -->
      <el-form-item label="产品名称" prop="productName">
        <el-input v-model="productForm.productName" placeholder="请输入产品名称" /> <!-- 产品名称输入 -->
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input-number v-model="productForm.price" :min="0" :precision="2" /> <!-- 产品价格输入 -->
      </el-form-item>
      <el-form-item label="库存数量" prop="stockQuantity">
        <el-input-number v-model="productForm.stockQuantity" :min="0" /> <!-- 产品库存输入 -->
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="productForm.status" placeholder="选择状态"> <!-- 产品状态选择 -->
          <el-option label="可用" value="active" />
          <el-option label="不可用" value="inactive" />
        </el-select>
      </el-form-item>
      <el-form-item label="商家ID" prop="merchantId">
        <el-input-number v-model="productForm.merchantId" :min="1" disabled v-if="selectedProduct" /> <!-- 商家ID输入 -->
        <el-input-number v-model="productForm.merchantId" :min="1" v-else />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="productForm.description" type="textarea" :rows="3" /> <!-- 产品描述输入 -->
      </el-form-item>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button> <!-- 取消按钮 -->
          <el-button type="primary" @click="saveProduct">保存</el-button> <!-- 保存按钮 -->
        </span>
    </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog
        v-model="deleteDialogVisible"
    title="确认删除"
    width="30%"
    >
      <!-- 控制删除对话框的显示 -->
    <span>确定要删除这个产品吗？此操作不可撤销。</span> <!-- 删除确认提示 -->
    <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button> <!-- 取消按钮 -->
          <el-button type="danger" @click="deleteProduct">确认删除</el-button> <!-- 确认删除按钮 -->
        </span>
    </template>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'; // 引入 axios 库进行 HTTP 请求
import { ElMessage } from 'element-plus'; // 引入 Element Plus 的消息组件
import AppNavbar from "@/components/Navbar.vue"; // 引入导航栏组件

export default {
  name: 'ProductManagement', // 组件名称
  components: { AppNavbar }, // 注册导航栏组件
  data() {
    return {
      products: [], // 产品数组
      searchKeyword: '', // 新增搜索关键词
      productForm: { // 表单数据模型
        productName: '',
        price: 0,
        stockQuantity: 0,
        status: 'active',
        merchantId: null,
        description: ''
      },
      selectedProduct: null, // 当前选择的产品
      dialogVisible: false, // 控制添加/编辑产品对话框的显示
      deleteDialogVisible: false, // 控制删除确认对话框的显示
      productIdToDelete: null // 当前待删除产品的ID
    };
  },
  methods: {
    fetchProducts() {
      // 获取所有产品
      const url = 'http://localhost:8080/api/products';
      axios.get(url)
          .then(response => {
            this.products = response.data; // 更新产品列表
          })
          .catch(error => {
            ElMessage.error('获取产品列表失败: ' + error.message); // 显示失败消息
            console.error('获取产品时出错:', error);
          });
    },
    fetchProductsByKeyword() {
      // 根据搜索关键词获取产品
      const url = this.searchKeyword
          ? `http://localhost:8080/api/products/search?keyword=${this.searchKeyword}`
          : 'http://localhost:8080/api/products';

      axios.get(url)
          .then(response => {
            this.products = response.data; // 更新产品列表
          })
          .catch(error => {
            ElMessage.error('获取产品列表失败: ' + error.message); // 显示失败消息
            console.error('获取产品时出错:', error);
          });
    },
    showProductForm() {
      // 显示添加产品表单
      this.selectedProduct = null; // 清空选择的产品
      this.productForm = { // 重置表单
        productName: '',
        price: 0,
        stockQuantity: 0,
        status: 'active',
        merchantId: null,
        description: ''
      };
      this.dialogVisible = true; // 显示对话框
    },
    editProduct(product) {
      // 显示编辑产品表单
      this.selectedProduct = product; // 设置选择的产品
      this.productForm = {
        productName: product.productName,
        price: product.price,
        stockQuantity: product.stockQuantity,
        status: product.status,
        merchantId: product.merchantId,
        description: product.description || ''
      };
      this.dialogVisible = true; // 显示对话框
    },
    clearForm() {
      // 清空表单
      this.selectedProduct = null; // 取消选择的产品
      this.productForm = { // 重置表单
        productName: '',
        price: 0,
        stockQuantity: 0,
        status: 'active',
        merchantId: null,
        description: ''
      };
    },
    saveProduct() {
      // 保存产品（添加或更新）
      const url = this.selectedProduct
          ? `http://localhost:8080/api/products/${this.selectedProduct.productId}` // 更新产品
          : 'http://localhost:8080/api/products'; // 添加产品

      const method = this.selectedProduct ? 'put' : 'post'; // 确定使用的请求方法

      const productData = { // 整理待提交的产品数据
        merchantId: this.productForm.merchantId,
        productName: this.productForm.productName,
        price: this.productForm.price,
        stockQuantity: this.productForm.stockQuantity,
        status: this.productForm.status,
        description: this.productForm.description
      };

      axios[method](url,productData)
          .then(() => {
            ElMessage.success(this.selectedProduct ? '产品更新成功' : '产品添加成功'); // 显示成功消息
            this.fetchProducts(); // 重新获取产品列表
            this.dialogVisible = false; // 关闭对话框
          })
          .catch(error => {
            ElMessage.error('操作失败: ' + error.message); // 显示错误消息
            console.error('保存产品时出错:', error);
          });
    },
    confirmDelete(productId) {
      // 确认删除产品
      this.productIdToDelete = productId; // 存储待删除产品的ID
      this.deleteDialogVisible = true; // 显示删除确认对话框
    },
    deleteProduct() {
      // 删除产品
      axios.delete(`http://localhost:8080/api/products/${this.productIdToDelete}`)
          .then(() => {
            ElMessage.success('产品删除成功'); // 显示成功消息
            this.fetchProducts(); // 重新获取产品列表
            this.deleteDialogVisible = false; // 关闭删除对话框
          })
          .catch(error => {
            ElMessage.error('删除失败: ' + error.message); // 显示错误消息
            console.error('删除产品时出错:', error);
          });
    }
  },
  mounted() {
    this.fetchProducts(); // 初始加载所有产品
  }
};
</script>

<style scoped>
.el-table {
  width: 100%; /* 设置表格宽度 */
}

.dialog-footer {
  display: flex;
  justify-content: flex-end; /* 确保对话框底部按钮右对齐 */
}
</style>
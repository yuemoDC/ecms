<template>
<!--  <AppNavbar @navigate="handleNavigation"/> &lt;!&ndash; 引入导航栏组件 &ndash;&gt;-->
  <div class="container">

    <h1>产品管理</h1> <!-- 页面标题 -->

    <div class="controls-wrapper">
      <el-input
          v-model="searchKeyword"
          placeholder="请输入产品名称或描述进行搜索"
          style="width: 300px; margin-right: 20px;"
          @input="fetchProductsByKeyword"
      />
      <!-- 绑定搜索关键词 -->
      <!-- 输入框占位符 -->
      <!-- 设置输入框样式 -->
      <!-- 输入变化时调用搜索方法 -->

      <el-button type="primary" @click="showProductForm">添加产品</el-button> <!-- 添加产品按钮 -->
    </div>

    <!-- 产品表格 -->
    <el-table :data="products" style="width: 100%; margin-top: 20px;" border>
      <el-table-column prop="productName" label="产品名称" /> <!-- 产品名称列 -->
      <el-table-column prop="price" label="价格" /> <!-- 产品价格列 -->
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'"> <!-- 根据状态显示标签 -->
            {{ scope.row.status === 'active' ? '可用' : '不可用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
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

      <el-form :model="productForm" label-width="120px" size="default" style="max-height: 60vh; overflow-y: auto;"> <!-- 表单 -->
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
        append-to-body
        destroy-on-close
        center
    >
      <!-- 控制删除对话框的显示 -->
      <!-- 添加到body中以避免嵌套和z-index问题 -->
      <!-- 关闭时销毁内容 -->
      <!-- 居中对话框 -->

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
import AppMerchantNavbar from "@/components/MerchantNavbar.vue";
import {gsap} from "gsap"; // 引入GSAP动画库

export default {
  name: 'ProductManagement', // 组件名称
  components: { AppNavbar: AppMerchantNavbar }, // 注册导航栏组件
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
      productIdToDelete: null, // 当前待删除产品的ID
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
    fetchProducts() {
      // 如果没有设置商家ID，则不获取产品
      if (!this.currentMerchantId) {
        console.log('未设置商家ID，无法获取产品');
        return;
      }

      // 获取当前登录商家的产品
      const url = `http://localhost:8080/api/products/merchant/${this.currentMerchantId}`;

      axios.get(url)
          .then(response => {
            this.products = response.data; // 更新产品列表
          })
          .catch(error => {
            ElMessage.error('获取产品列表失败: ' + (error.response?.data?.message || error.message));
            console.error('获取产品时出错:', error);
          });
    },
    fetchProductsByKeyword() {
      // 如果没有设置商家ID，则不搜索产品
      if (!this.currentMerchantId) {
        console.log('未设置商家ID，无法搜索产品');
        return;
      }

      // 根据搜索关键词获取当前商家的产品
      const url = this.searchKeyword
          ? `http://localhost:8080/api/products/merchant/${this.currentMerchantId}/search?keyword=${this.searchKeyword}`
          : `http://localhost:8080/api/products/merchant/${this.currentMerchantId}`;

      axios.get(url)
          .then(response => {
            this.products = response.data; // 更新产品列表
          })
          .catch(error => {
            ElMessage.error('搜索产品失败: ' + (error.response?.data?.message || error.message));
            console.error('搜索产品时出错:', error);
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
        merchantId: this.currentMerchantId, // 使用当前商家ID
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
        merchantId: this.currentMerchantId,
        description: ''
      };
    },
    saveProduct() {
      // 保存产品前，确保设置了商家ID
      if (!this.productForm.merchantId && this.currentMerchantId) {
        this.productForm.merchantId = this.currentMerchantId;
      }

      // 如果没有商家ID，显示错误并返回
      if (!this.productForm.merchantId) {
        ElMessage.error('无法确定商家ID，请刷新页面或重新登录');
        return;
      }

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

      axios[method](url, productData)
          .then(() => {
            ElMessage.success(this.selectedProduct ? '产品更新成功' : '产品添加成功'); // 显示成功消息
            this.fetchProducts(); // 重新获取产品列表
            this.dialogVisible = false; // 关闭对话框
          })
          .catch(error => {
            ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message)); // 显示错误消息
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
            ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message)); // 显示错误消息
            console.error('删除产品时出错:', error);
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

              // 获取商家ID后加载产品
              this.fetchProducts();
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

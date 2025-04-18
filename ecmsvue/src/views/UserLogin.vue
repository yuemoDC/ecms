<template>
  <div class="login-container"> <!-- 登录组件的容器 -->
    <el-card class="login-card"> <!-- Element Plus 的卡片组件 -->
      <template #header>
        <h2>登录</h2> <!-- 登录标题 -->
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef"> <!-- 表单组件 -->
        <el-form-item prop="username"> <!-- 用户名输入项 -->
          <el-input v-model="loginForm.username" placeholder="请输入用户名"> <!-- 用户名输入框 -->
            <template #prefix>
              <el-icon><User /></el-icon> <!-- 用户名图标 -->
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password"> <!-- 密码输入项 -->
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"> <!-- 密码输入框 -->
            <template #prefix>
              <el-icon><Lock /></el-icon> <!-- 密码图标 -->
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button> <!-- 登录按钮 -->
        </el-form-item>
        <div class="register-link"> <!-- 注册链接 -->
          <router-link to="/register">还没有账号？立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup name="UserLogin">
import { ref, reactive } from 'vue' // 导入Vue的响应性API
import { User, Lock } from '@element-plus/icons-vue' // 导入Element Plus的图标
import { useRouter } from 'vue-router' // 导入路由
import axios from '../utils/axios' // 导入自定义的axios实例
import { ElMessage } from 'element-plus' // 导入Element Plus的消息提示组件

const router = useRouter() // 获取路由实例
const loginFormRef = ref(null) // 创建用于引用表单的ref

const loginForm = reactive({ // 创建响应式登录表单数据
  username: '',
  password: ''
})

const rules = { // 定义表单校验规则
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }], // 用户名必填规则
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }] // 密码必填规则
}

const handleLogin = async () => { // 登录处理函数
  if (!loginFormRef.value) return // 确保表单引用存在

  await loginFormRef.value.validate(async (valid) => { // 校验表单
    if (valid) { // 如果表单有效
      try {
        const response = await axios.post('/api/auth/login', loginForm) // 发送登录请求
        if (response.data.success) { // 登录成功处理
          ElMessage.success('登录成功') // 显示成功消息
          localStorage.setItem('token', response.data.token) // 存储token
          localStorage.setItem('user', JSON.stringify(response.data)); // 存储用户信息（转成字符串）
          router.push('/home') // 导航到主页
        } else {
          ElMessage.error(response.data.message || '登录失败') // 显示错误消息
        }
      } catch (error) {
        ElMessage.error('登录失败，请稍后重试') // 处理请求错误
        console.error('Login error:', error) // 输出错误信息
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh; /* 设置容器高度为视口高度 */
  display: flex; /* 使用flex布局 */
  justify-content: center; /* 居中对齐 */
  align-items: center; /* 垂直居中 */
  background-color: #f5f5f5; /* 背景色 */
}

.login-card {
  width: 400px; /* 卡片宽度 */
}

.register-link {
  text-align: center; /* 注册链接居中对齐 */
  margin-top: 15px; /* 上外边距 */
}

.register-link a {
  color: #409eff; /* 链接颜色 */
  text-decoration: none; /* 移除下划线 */
}

.register-link a:hover {
  text-decoration: underline; /* 悬停时添加下划线 */
}
</style>
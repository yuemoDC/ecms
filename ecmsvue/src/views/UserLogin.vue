
<template>
  <div class="login-container"> <!-- 登录组件的容器 -->
    <div class="vanta-bg" ref="vantaRef"></div>
    <!-- 备用背景，防止vanta加载失败 -->
    <div class="fallback-bg"></div>

    <div class="login-content">
      <!-- 添加LOGO和系统名称 -->
      <div class="system-header" ref="systemHeader">
        <img src="../assets/LOG.png" alt="东华大学" class="system-logo">
        <h1 class="system-title">智慧电商管理系统</h1>
      </div>

      <div class="login-card" ref="loginCard"> <!-- 登录卡片 -->
        <div class="card-header">
          <h2 class="login-title">登录</h2> <!-- 登录标题 -->
        </div>
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef"> <!-- 表单组件 -->
          <el-form-item prop="username" class="form-item"> <!-- 用户名输入项 -->
            <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                @focus="handleInputFocus"
                @blur="handleInputBlur"> <!-- 用户名输入框 -->
              <template #prefix>
                <el-icon><User /></el-icon> <!-- 用户名图标 -->
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password" class="form-item"> <!-- 密码输入项 -->
            <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                @focus="handleInputFocus"
                @blur="handleInputBlur"> <!-- 密码输入框 -->
              <template #prefix>
                <el-icon><Lock /></el-icon> <!-- 密码图标 -->
              </template>
            </el-input>
          </el-form-item>
          <el-form-item class="form-item">
            <el-button
                type="primary"
                @click="handleLogin"
                class="login-button"
                @mouseenter="animateButton"
                @mouseleave="resetButton">登录</el-button> <!-- 登录按钮 -->
          </el-form-item>
          <div class="register-link"> <!-- 注册链接 -->
            <router-link to="/register">还没有账号？立即注册</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup name="UserLogin">
import { ref, reactive, onMounted, onUnmounted } from 'vue' // 导入Vue的响应性API
import { User, Lock } from '@element-plus/icons-vue' // 导入Element Plus的图标
import { useRouter } from 'vue-router' // 导入路由
import axios from '../utils/axios' // 导入自定义的axios实例
import { ElMessage } from 'element-plus' // 导入Element Plus的消息提示组件
import gsap from 'gsap' // 导入GSAP动画库
// 导入Vanta库（需要在项目中安装）
// 通过npm install three vanta 安装

const router = useRouter() // 获取路由实例
const loginFormRef = ref(null) // 创建用于引用表单的ref
const loginCard = ref(null) // 登录卡片引用
const systemHeader = ref(null) // 系统头部引用
const vantaRef = ref(null) // Vanta背景引用
let vantaEffect = null // 保存Vanta效果实例

const loginForm = reactive({ // 创建响应式登录表单数据
  username: '',
  password: ''
})

const rules = { // 定义表单校验规则
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }], // 用户名必填规则
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }] // 密码必填规则
}

// 初始化Vanta背景
const initVantaBackground = async () => {
  if (!vantaRef.value) return

  try {
    // 动态导入Vanta.js和THREE.js
    const THREE = await import('three')
    const VANTA = await import('vanta/dist/vanta.net.min')

    // 初始化Vanta效果
    vantaEffect = VANTA.NET({
      el: vantaRef.value,
      THREE: THREE,
      mouseControls: true,
      touchControls: true,
      gyroControls: false,
      minHeight: 200.00,
      minWidth: 200.00,
      scale: 1.00,
      scaleMobile: 1.00,
      color: 0x3e9eff,
      backgroundColor: 0xf5f5f5,
      points: 12.00,
      maxDistance: 25.00,
      spacing: 17.00
    })

    // 成功加载Vanta时隐藏备用背景
    document.querySelector('.fallback-bg').style.opacity = '0';
  } catch (error) {
    console.error('无法加载Vanta背景:', error)
    // 加载失败时显示备用背景
    document.querySelector('.fallback-bg').style.opacity = '1';
  }
}

// 初始动画
onMounted(() => {
  // 初始化背景
  initVantaBackground()

  // 创建主时间线以便更好地控制动画顺序
  const mainTimeline = gsap.timeline()

  // 系统头部动画（首先执行）
  mainTimeline.from(systemHeader.value, {
    y: -50,
    opacity: 1,
    duration: 1,
    ease: "power2.out"
  })


      // 卡片动画 (卡片已在CSS中设置为初始透明状态)
      .to(loginCard.value, {
        y: 0,
        opacity: 1,
        scale: 1,
        duration: 1,
        ease: "back.out(1.7)"
      }, "-=0.3") // 系统标题和登录卡片动画略微重叠

      // 内部元素动画
      .add(() => {
        // 获取所有需要动画的元素
        const title = document.querySelector('.login-title')
        const formItems = document.querySelectorAll('.form-item')
        const registerLink = document.querySelector('.register-link')

        // 创建子时间线
        const contentTimeline = gsap.timeline()

        // 标题动画
        contentTimeline.to(title, {
          y: 0,
          opacity: 1,
          duration: 0.5,
          ease: "power2.out"
        })

            // 表单项依次动画
            .to(formItems, {
              x: 0,
              opacity: 1,
              stagger: 0.15,
              duration: 0.5,
              ease: "power2.out"
            })

            // 注册链接动画
            .to(registerLink, {
              x: 0,
              opacity: 1,
              duration: 0.5,
              ease: "power2.out"
            })

        return contentTimeline
      })
})

// 在组件卸载时清理Vanta效果
onUnmounted(() => {
  if (vantaEffect) {
    vantaEffect.destroy()
  }
})

// 按钮悬停动画
const animateButton = (e) => {
  gsap.to(e.currentTarget, {
    scale: 1.05,
    boxShadow: '0 5px 15px rgba(64, 158, 255, 0.4)',
    duration: 0.3
  })
}

// 重置按钮动画
const resetButton = (e) => {
  gsap.to(e.currentTarget, {
    scale: 1,
    boxShadow: 'none',
    duration: 0.3
  })
}

// 输入框焦点动画
const handleInputFocus = (e) => {
  const wrapper = e.currentTarget.querySelector('.el-input__wrapper')
  if (wrapper) {
    gsap.to(wrapper, {
      boxShadow: '0 0 0 3px rgba(64, 158, 255, 0.2)',
      scale: 1.01,
      duration: 0.3
    })
  }
}

// 输入框失去焦点动画
const handleInputBlur = (e) => {
  const wrapper = e.currentTarget.querySelector('.el-input__wrapper')
  if (wrapper) {
    gsap.to(wrapper, {
      boxShadow: 'none',
      scale: 1,
      duration: 0.3
    })
  }
}

// 错误抖动动画 - 独立函数
const shakeAnimation = (element) => {
  // 停止任何正在进行的动画
  gsap.killTweensOf(element)

  // 创建抖动时间线
  const shakeTl = gsap.timeline()

  // 红色背景闪烁
  shakeTl.to(element, {
    backgroundColor: 'rgba(255, 235, 235, 0.8)',
    borderColor: '#ff4d4f',
    boxShadow: '0 0 15px rgba(255, 77, 79, 0.3)',
    duration: 0.2
  })

      // 强烈抖动效果
      .to(element, {
        x: -15,
        duration: 0.1
      })
      .to(element, {
        x: 15,
        duration: 0.1
      })
      .to(element, {
        x: -10,
        duration: 0.1
      })
      .to(element, {
        x: 10,
        duration: 0.1
      })
      .to(element, {
        x: -5,
        duration: 0.1
      })
      .to(element, {
        x: 0,
        duration: 0.1
      })

      // 恢复正常背景
      .to(element, {
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        borderColor: 'transparent',
        boxShadow: '0 10px 30px rgba(0, 0, 0, 0.1)',
        duration: 0.3,
        delay: 0.2
      })

  return shakeTl
}

// 登录成功动画
const playSuccessAnimation = () => {
  // 卡片成功动画
  gsap.to(loginCard.value, {
    boxShadow: '0 0 30px rgba(0, 200, 83, 0.5)',
    borderColor: '#00C853',
    duration: 0.5,
    onComplete: () => {
      // 完成后向上退出动画
      gsap.to([loginCard.value, systemHeader.value], {
        y: -50,
        opacity: 0,
        scale: 0.9,
        duration: 0.8,
        delay: 0.3,
        ease: "power2.in",
        onComplete: () => router.push('/SplashScreen')
      })
    }
  })

  // 按钮成功动画
  const loginButton = document.querySelector('.login-button')
  if (loginButton) {
    gsap.to(loginButton, {
      backgroundColor: '#00C853',
      scale: 1.05,
      duration: 0.3
    })
  }
}

const handleLogin = async () => { // 登录处理函数
  if (!loginFormRef.value) return // 确保表单引用存在

  // 点击按钮动画
  const loginButton = document.querySelector('.login-button')
  if (loginButton) {
    gsap.to(loginButton, {
      scale: 0.95,
      duration: 0.1,
      yoyo: true,
      repeat: 1
    })
  }

  await loginFormRef.value.validate(async (valid) => { // 校验表单
    if (valid) { // 如果表单有效
      try {
        const response = await axios.post('/api/auth/login', loginForm) // 发送登录请求
        if (response.data.success) { // 登录成功处理
          ElMessage.success('登录成功') // 显示成功消息
          localStorage.setItem('token', response.data.token) // 存储token
          localStorage.setItem('user', JSON.stringify(response.data)); // 存储用户信息（转成字符串）

          // 播放成功动画，然后跳转
          playSuccessAnimation()
        } else {
          // 显示错误动画和消息
          shakeAnimation(loginCard.value)
          ElMessage.error(response.data.message || '登录失败') // 显示错误消息
        }
      } catch (error) {
        // 显示错误动画和消息
        shakeAnimation(loginCard.value)
        ElMessage.error('登录失败，请稍后重试') // 处理请求错误
        console.error('Login error:', error) // 输出错误信息
      }
    } else {
      // 验证失败时摇动卡片
      shakeAnimation(loginCard.value)
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
  overflow: hidden; /* 防止内容溢出 */
  perspective: 1000px; /* 添加3D视角效果 */
  position: relative; /* 相对定位 */
}

.login-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 1;
  position: relative;
}

/* 系统头部样式 */
.system-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.system-logo {
  width: 120px;
  height: auto;
  margin-bottom: 15px;
  filter: drop-shadow(0 5px 10px rgba(0, 0, 0, 0.1));
}

.system-title {
  color: #2c3e50;
  font-size: 28px;
  margin: 0;
  text-align: center;
  font-weight: 600;
  letter-spacing: 1px;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.vanta-bg {
  position: absolute; /* 绝对定位 */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0; /* 背景层 */
}

/* 备用背景，在Vanta加载失败时使用 */
.fallback-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  background: linear-gradient(125deg, #3a7bd5, #00d2ff);
  overflow: hidden;
  opacity: 0; /* 默认隐藏 */
  transition: opacity 0.5s;
}

.fallback-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 20% 35%, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0) 25%),
      radial-gradient(circle at 75% 44%, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0) 20%),
      radial-gradient(circle at 46% 52%, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 30%),
      linear-gradient(to bottom, transparent 0%, rgba(0, 0, 0, 0.4) 100%);
}

.fallback-bg::after {
  content: '';
  position: absolute;
  width: 200%;
  height: 200%;
  left: -50%;
  top: -50%;
  background-image:
      url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='rgba(255,255,255,.075)' fill-rule='evenodd'/%3E%3C/svg%3E"),
      url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='rgba(255,255,255,.045)' fill-opacity='0.5'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  animation: animateBg 30s linear infinite;
}

@keyframes animateBg {
  0% {
    transform: translate(0, 0) rotate(0deg);
  }
  100% {
    transform: translate(-20px, -20px) rotate(360deg);
  }
}

.login-card {
  position: relative; /* 相对定位 */
  z-index: 1; /* 内容层 */
  width: 400px; /* 卡片宽度 */
  background-color: rgba(255, 255, 255, 0.9); /* 半透明背景 */
  border-radius: 12px; /* 圆角 */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15); /* 阴影效果 */
  padding: 30px; /* 内边距 */
  /* 初始状态设置 - 在CSS中直接设置，避免闪烁 */
  opacity: 0;
  transform: translateY(100px) scale(0.8);
  will-change: transform, opacity, background-color, box-shadow; /* 优化动画性能 */
  transition: box-shadow 0.3s, border-color 0.3s, background-color 0.3s; /* 过渡效果 */
  border: 2px solid transparent; /* 添加边框用于动画 */
  backdrop-filter: blur(3px); /* 模糊背景效果 */
}

.card-header {
  text-align: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(238, 238, 238, 0.8);
}

.login-title {
  margin: 0;
  color: #409eff;
  font-size: 26px;
  /* 初始状态 */
  opacity: 0;
  transform: translateY(-20px);
}

.form-item {
  margin-bottom: 20px;
  /* 初始状态 */
  opacity: 0;
  transform: translateX(-30px);
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  will-change: transform, box-shadow, background-color;
  position: relative;
  overflow: hidden;
}

.login-button::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 5px;
  height: 5px;
  background: rgba(255, 255, 255, 0.5);
  opacity: 0;
  border-radius: 100%;
  transform: scale(1, 1) translate(-50%, -50%);
  transform-origin: 50% 50%;
}

.login-button:active::after {
  opacity: 0.3;
  transform: scale(50, 50) translate(-50%, -50%);
  transition: all 1s;
}

.register-link {
  text-align: center; /* 注册链接居中对齐 */
  margin-top: 20px; /* 上外边距 */
  /* 初始状态 */
  opacity: 0;
  transform: translateX(-30px);
}

.register-link a {
  color: #409eff; /* 链接颜色 */
  text-decoration: none; /* 移除下划线 */
  transition: all 0.3s; /* 添加过渡效果 */
}

.register-link a:hover {
  color: #66b1ff; /* 悬停颜色 */
  text-decoration: underline; /* 悬停时添加下划线 */
}

:deep(.el-input__wrapper) {
  transition: all 0.3s;
  box-shadow: 0 0 0 1px rgba(220, 223, 230, 0.6) inset;
  background-color: rgba(255, 255, 255, 0.7); /* 半透明背景 */
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409eff inset;
  background-color: rgba(255, 255, 255, 0.9); /* 悬停时更不透明 */
}

:deep(.el-input__inner) {
  height: 44px;
}

:deep(.el-icon) {
  color: #409eff;
  transition: color 0.3s;
}

:deep(.el-input:focus-within .el-icon) {
  color: #409eff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    width: 85%;
    max-width: 400px;
    padding: 20px;
  }

  .system-logo {
    width: 90px;
  }

  .system-title {
    font-size: 22px;
  }
}
</style>

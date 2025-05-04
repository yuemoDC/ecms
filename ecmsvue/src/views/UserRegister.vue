
<template>
  <div class="register-container"> <!-- 注册组件的容器 -->
    <div class="animated-bg"></div>
    <div class="register-card" ref="registerCard"> <!-- 注册卡片 -->
      <div class="card-header">
        <h2 class="register-title">注册</h2> <!-- 注册标题 -->
      </div>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef"> <!-- 表单组件 -->
        <el-form-item prop="username" class="form-item"> <!-- 用户名输入项 -->
          <el-input
              v-model="registerForm.username"
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
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              @focus="handleInputFocus"
              @blur="handleInputBlur"> <!-- 密码输入框 -->
            <template #prefix>
              <el-icon><Lock /></el-icon> <!-- 密码图标 -->
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword" class="form-item"> <!-- 确认密码输入项 -->
          <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              @focus="handleInputFocus"
              @blur="handleInputBlur"> <!-- 确认密码输入框 -->
            <template #prefix>
              <el-icon><Lock /></el-icon> <!-- 密码图标 -->
            </template>
          </el-input>
        </el-form-item>
        <el-form-item class="form-item">
          <el-button
              type="primary"
              @click="handleRegister"
              class="register-button"
              @mouseenter="animateButton"
              @mouseleave="resetButton">注册</el-button> <!-- 注册按钮 -->
        </el-form-item>
        <div class="login-link"> <!-- 登录链接 -->
          <router-link to="/login">已有账号？立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup name="UserRegister">
import { ref, reactive, onMounted } from 'vue' // 导入Vue的响应性API
import { User, Lock } from '@element-plus/icons-vue' // 导入Element Plus的图标
import { useRouter } from 'vue-router' // 导入路由
import axios from '../utils/axios' // 导入自定义的axios实例
import { ElMessage } from 'element-plus' // 导入Element Plus的消息提示组件
import gsap from 'gsap' // 导入GSAP动画库

const router = useRouter() // 获取路由实例
const registerFormRef = ref(null) // 创建用于引用表单的ref
const registerCard = ref(null) // 注册卡片引用

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
}

// 初始动画
onMounted(() => {
  // 创建主时间线以便更好地控制动画顺序
  const mainTimeline = gsap.timeline()

  // 卡片动画 (卡片已在CSS中设置为初始透明状态)
  mainTimeline.to(registerCard.value, {
    y: 0,
    opacity: 1,
    scale: 1,
    duration: 1,
    ease: "back.out(1.7)"
  })

      // 内部元素动画
      .add(() => {
        // 获取所有需要动画的元素
        const title = document.querySelector('.register-title')
        const formItems = document.querySelectorAll('.form-item')
        const loginLink = document.querySelector('.login-link')

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
              stagger: 0.12, // 速度略快于登录页，因为有更多表单项
              duration: 0.5,
              ease: "power2.out"
            })

            // 登录链接动画
            .to(loginLink, {
              x: 0,
              opacity: 1,
              duration: 0.5,
              ease: "power2.out"
            })

        return contentTimeline
      })
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

// 错误抖动动画
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

// 注册成功动画
const playSuccessAnimation = () => {
  // 卡片成功动画
  gsap.to(registerCard.value, {
    boxShadow: '0 0 30px rgba(0, 200, 83, 0.5)',
    borderColor: '#00C853',
    duration: 0.5,
    onComplete: () => {
      // 完成后向上退出动画
      gsap.to(registerCard.value, {
        y: -50,
        opacity: 0,
        scale: 0.9,
        duration: 0.8,
        delay: 0.3,
        ease: "power2.in",
        onComplete: () => router.push('/login')
      })
    }
  })

  // 按钮成功动画
  const registerButton = document.querySelector('.register-button')
  if (registerButton) {
    gsap.to(registerButton, {
      backgroundColor: '#00C853',
      scale: 1.05,
      duration: 0.3
    })
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  // 点击按钮动画
  const registerButton = document.querySelector('.register-button')
  if (registerButton) {
    gsap.to(registerButton, {
      scale: 0.95,
      duration: 0.1,
      yoyo: true,
      repeat: 1
    })
  }

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await axios.post('/api/auth/register', {
          username: registerForm.username,
          password: registerForm.password
        })
        if (response.data.success) {
          ElMessage.success('注册成功')

          // 播放成功动画，然后跳转
          playSuccessAnimation()
        } else {
          // 显示错误动画和消息
          shakeAnimation(registerCard.value)
          ElMessage.error(response.data.message || '注册失败')
        }
      } catch (error) {
        // 显示错误动画和消息
        shakeAnimation(registerCard.value)
        ElMessage.error('注册失败，请稍后重试')
        console.error('Register error:', error)
      }
    } else {
      // 验证失败时摇动卡片
      shakeAnimation(registerCard.value)
    }
  })
}
</script>

<style scoped>
.register-container {
  height: 100vh; /* 设置容器高度为视口高度 */
  display: flex; /* 使用flex布局 */
  justify-content: center; /* 居中对齐 */
  align-items: center; /* 垂直居中 */
  overflow: hidden; /* 防止内容溢出 */
  perspective: 1000px; /* 添加3D视角效果 */
  position: relative; /* 相对定位 */
}

/* 创建一个纯CSS动画背景，替代Vanta.js */
.animated-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  background: linear-gradient(125deg, #3a7bd5, #00d2ff);
  overflow: hidden;
}

.animated-bg::before {
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
  filter: url(#gooey);
}

.animated-bg::after {
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

.register-card {
  position: relative; /* 相对定位 */
  z-index: 1; /* 内容层 */
  width: 400px; /* 卡片宽度 */
  background-color: rgba(255, 255, 255, 0.85); /* 白色背景，稍微透明 */
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

.register-title {
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

.register-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  will-change: transform, box-shadow, background-color;
  position: relative;
  overflow: hidden;
}

.register-button::after {
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

.register-button:active::after {
  opacity: 0.3;
  transform: scale(50, 50) translate(-50%, -50%);
  transition: all 1s;
}

.login-link {
  text-align: center; /* 登录链接居中对齐 */
  margin-top: 20px; /* 上外边距 */
  /* 初始状态 */
  opacity: 0;
  transform: translateX(-30px);
}

.login-link a {
  color: #409eff; /* 链接颜色 */
  text-decoration: none; /* 移除下划线 */
  transition: all 0.3s; /* 添加过渡效果 */
}

.login-link a:hover {
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
</style>

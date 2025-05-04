<template>
  <AppNavbar @navigate="handleNavigation"/> <!-- 引入导航栏组件 -->
  <div class="container">

    <el-container>
      <el-header>
        <h2>商家入驻申请</h2>
      </el-header>
      <el-main>
        <!-- 搜索框和状态筛选 -->
        <el-row class="toolbar" style="margin-bottom: 20px;">
          <el-col :span="8">
            <el-input
                v-model="searchQuery"
                placeholder="搜索商家名称或联系方式"
                prefix-icon="el-icon-search"
                clearable
                @input="searchApplications"
            ></el-input>
          </el-col>
          <el-col :span="8">
            <el-select v-model="selectedStatus" placeholder="选择申请状态" @change="filterByStatus">
              <el-option label="所有" value=""></el-option>
              <el-option label="待审核" value="pending"></el-option>
              <el-option label="已通过" value="approved"></el-option>
              <el-option label="已拒绝" value="rejected"></el-option>
            </el-select>
          </el-col>
        </el-row>

        <!-- 商家入驻申请列表 -->
        <el-table :data="filteredApplications" style="width: 100%">
          <el-table-column prop="merchantName" label="商家申请名称" width="180" />
          <el-table-column prop="contactInfo" label="联系方式" width="220" />
          <el-table-column prop="businessScope" label="经营范围" width="180" />
          <el-table-column prop="license" label="资质证明" width="180" />
          <el-table-column prop="status" label="申请状态" width="200">
            <template #default="scope">
              <span style="margin-right: 10px">{{ formatStatus(scope.row.status) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button
                  @click="editApplication(scope.row)"
                  type="primary"
                  size="small"
              >
                编辑
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 添加/编辑商家入驻申请表单 -->
        <el-dialog
            :title="newApplication.applicationId ? '编辑商家入驻申请' : '新增商家入驻申请'"
            v-model="dialogVisible"
            width="40%"
        >
          <el-form
              :model="newApplication"
              :rules="rules"
              ref="formRef"
              label-width="120px"
          >
            <el-form-item label="商家名称" prop="merchantName">
              <el-input v-model="newApplication.merchantName" />
            </el-form-item>
            <el-form-item label="联系方式" prop="contactInfo">
              <el-input v-model="newApplication.contactInfo" />
            </el-form-item>
            <el-form-item label="经营范围" prop="businessScope">
              <el-input v-model="newApplication.businessScope" />
            </el-form-item>
            <el-form-item label="资质证明" prop="license">
              <el-input v-model="newApplication.license" placeholder="请输入资质证明文件路径或编号" />
            </el-form-item>
            <!-- 隐藏用户ID字段，不让用户看到 -->
            <el-form-item prop="userId">
              <el-input v-model="newApplication.userId" type="hidden" />
            </el-form-item>
          </el-form>

          <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="saveApplication">保存</el-button>
          </template>
        </el-dialog>


        <el-button type="primary" @click="showNewApplicationForm" style="margin-top: 20px;">
          新增商家入驻申请
        </el-button>
      </el-main>
    </el-container>

    <!-- 显示当前用户的ID -->
    <div class="user-info">
      <el-tag type="success" v-if="currentUser">
        当前登录用户 ID: {{ currentUser.id }}
      </el-tag>
      <el-tag type="info" v-else>
        未登录
      </el-tag>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import AppAdminNavbar from "@/components/MerchantNavbar.vue";
import {gsap} from "gsap"; // 引入导航栏组件

export default {
  name: 'MerchantApplication',
  components: { AppNavbar: AppAdminNavbar },
  setup() {
    const currentUser = ref(null); // 用来存储当前登录的用户信息
    let currentUserId = localStorage.getItem('userId'); // 获取当前登录用户的 userId
    const applications = ref([]);
    const filteredApplications = ref([]);
    const dialogVisible = ref(false);
    const searchQuery = ref('');
    const selectedStatus = ref('');
    const newApplication = ref({
      merchantName: '',
      contactInfo: '',
      businessScope: '',
      license: '',
      userId: currentUserId, // 设置为当前登录用户的 userId
      status: 'pending',
    });
    const formRef = ref(null);

    const getCurrentUser = () => {
      const userData = localStorage.getItem('user');
      if (userData) {
        try {
          const parsedUserData = JSON.parse(userData);
          if (parsedUserData.success) {
            currentUser.value = {
              role: parsedUserData.role,
              id: parsedUserData.userid, // 设置为当前登录用户的 id
              token: parsedUserData.token,
              username: parsedUserData.username,
            };
            currentUserId = parsedUserData.userid;
          } else {
            console.error('用户信息不正确:', parsedUserData);
          }
        } catch (e) {
          console.error('解析用户信息失败:', e);
        }
      }
    };
    const formatStatus = (status) => {
      const statusMap = {
        pending: '待审核',
        approved: '已通过',
        rejected: '已拒绝'
      }
      return statusMap[status] || '未知状态'
    }
    const rules = {
      merchantName: [
        { required: true, message: '商家名称不能为空', trigger: 'blur' }
      ],
      contactInfo: [
        { required: true, message: '联系方式不能为空', trigger: 'blur' }
      ],
      businessScope: [
        { required: true, message: '经营范围不能为空', trigger: 'blur' }
      ],
      license: [
        { required: true, message: '资质证明不能为空', trigger: 'blur' }
      ]
    };


    // 获取商家入驻申请
    const fetchApplications = async () => {
      try {
        console.log("Fetching with userId:", currentUserId);
        // 将 userId 作为参数传递给后端 API，确保只获取当前用户的商家申请
        const { data } = await axios.get('http://localhost:8080/api/merchant-applications', {
          params: { userId: currentUserId },
        });
        applications.value = data;
        filteredApplications.value = data; // 设置默认显示当前用户的商家申请
      } catch (error) {
        ElMessage.error('加载商家入驻申请失败');
      }
    };

    // 搜索商家申请
    const searchApplications = () => {
      const query = searchQuery.value.toLowerCase();
      filteredApplications.value = applications.value.filter(application => {
        return (
            application.merchantName.toLowerCase().includes(query) ||
            application.contactInfo.toLowerCase().includes(query)
        );
      });
    };

    // 根据状态筛选
    const filterByStatus = () => {
      if (selectedStatus.value === '') {
        filteredApplications.value = applications.value;
      } else {
        filteredApplications.value = applications.value.filter(application => application.status === selectedStatus.value);
      }
    };

    // 审核通过操作
    const approveApplication = async (application) => {
      try {
        const response = await axios.put(
            `http://localhost:8080/api/merchant-applications/${application.applicationId}/approve`
        );
        if (response.status === 200) {
          ElMessage.success('审核通过成功');
          await fetchApplications();
        } else {
          ElMessage.error(response.data || '审核操作失败');
        }
      } catch (error) {
        ElMessage.error(error.response?.data || '审核操作失败');
      }
    };

    // 拒绝操作
    const rejectApplication = async (application) => {
      try {
        await axios.put(`http://localhost:8080/api/merchant-applications/${application.applicationId}/reject`);
        ElMessage.success('已拒绝申请');
        await fetchApplications();
      } catch (error) {
        ElMessage.error('拒绝操作失败');
      }
    };

    // 编辑商家申请
    const editApplication = (application) => {
      newApplication.value = { ...application };
      dialogVisible.value = true;
    };

    // 显示新增商家申请表单
    const showNewApplicationForm = () => {
      newApplication.value = {
        merchantName: '',
        contactInfo: '',
        businessScope: '',
        license: '',
        userId: currentUserId, // 确保 userId 设置为当前用户
        status: 'pending',
      };
      dialogVisible.value = true;
    };

    // 保存商家申请
    const saveApplication = async () => {
      try {
        await formRef.value.validate(); // 验证表单
        if (!newApplication.value.userId) {
          newApplication.value.userId = currentUserId;
        }

        if (newApplication.value.applicationId) {
          await axios.put(
              `http://localhost:8080/api/merchant-applications/${newApplication.value.applicationId}`,
              newApplication.value
          );
          ElMessage.success('更新成功');
        } else {
          await axios.post(
              'http://localhost:8080/api/merchant-applications',
              newApplication.value
          );
          ElMessage.success('新增成功');
        }
        dialogVisible.value = false;
        await fetchApplications();
      } catch (error) {
        if (error.response && error.response.data) {
          ElMessage.error('保存失败: ' + error.response.data.message || '请检查表单输入');
        } else {
          ElMessage.error('保存失败: ' + (error.message || '发生未知错误'));
        }
      }
    };

    // 删除商家申请
    const deleteApplication = async (application) => {
      try {
        await axios.delete(
            `http://localhost:8080/api/merchant-applications/${application.applicationId}`
        );
        ElMessage.success('删除成功');
        await fetchApplications();
      } catch (error) {
        ElMessage.error('删除失败');
      }
    };

    // 初始化数据
    onMounted(() => {
      getCurrentUser(); // 获取当前用户
      fetchApplications();
    });

    return {
      rules,
      currentUser,
      applications,
      filteredApplications,
      dialogVisible,
      newApplication,
      searchQuery,
      selectedStatus,
      formRef,
      formatStatus,
      fetchApplications,
      approveApplication,
      rejectApplication,
      editApplication,
      showNewApplicationForm,
      saveApplication,
      deleteApplication,
      searchApplications,
      filterByStatus,
    };
  },
  methods:{
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
  },
  mounted () {
    this.enterAnimation();
  }
};
</script>

<style scoped>
.toolbar {
  margin-bottom: 20px;
}
.user-info {
  position: absolute;
  top: 20px;
  right: 20px;
}
</style>

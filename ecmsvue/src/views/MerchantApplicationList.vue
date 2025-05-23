<template>
  <AppNavbar @navigate="handleNavigation"/>
  <div class="page-container">
  <el-container>
    <el-header>
      <h2>商家入驻申请管理</h2>
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
        <el-table-column prop="merchantName" label="商家名称" width="180" />
        <el-table-column prop="contactInfo" label="联系方式" width="220" />
        <el-table-column prop="businessScope" label="经营范围" width="180" />
        <el-table-column prop="license" label="资质证明" width="180" />
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column prop="status" label="申请状态" width="200">
          <template #default="scope">
            <span style="margin-right: 10px">{{ formatStatus(scope.row.status) }}</span>
            <el-button
                v-if="scope.row.status === 'pending'"
                @click="approveApplication(scope.row)"
                type="success"
                size="small"
            >
              通过
            </el-button>
            <el-button
                v-if="scope.row.status === 'pending'"
                @click="rejectApplication(scope.row)"
                type="danger"
                size="small"
            >
              拒绝
            </el-button>
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
          append-to-body
      >
        <el-form :model="newApplication">
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
          <el-form-item label="用户ID" prop="userId">
            <el-input v-model.number="newApplication.userId" type="number" />
          </el-form-item>
          <el-form-item label="状态" prop="status" v-if="newApplication.applicationId">
            <el-select v-model="newApplication.status">
              <el-option label="待审核" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
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
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import AppNavbar from "@/components/AdminNavbar.vue";
import {gsap} from "gsap"

export default {
  name: 'MerchantApplicationList',
  components: {
    AppNavbar
  },
  async beforeUnmount() {
    await this.leaveAnimation();
  },
  setup() {
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
      userId: null,
      status: 'pending',
    });

    onMounted(() => {
      // 入场动画
      gsap.fromTo(".page-container",
          {
            y: 100,
            opacity: 0
          },
          {
            y: 0,
            opacity: 1,
            duration: 1,
            ease: "power4.out",
            delay: 0.3
          }
      );

      fetchApplications();

    })

    const formatStatus = (status) => {
      const statusMap = {
        pending: '待审核',
        approved: '已通过',
        rejected: '已拒绝'
      }
      return statusMap[status] || '未知状态'
    }

    const fetchApplications = async () => {
      try {
        const { data } = await axios.get('http://localhost:8080/api/merchant-applications');
        applications.value = data;
        filteredApplications.value = data; // 设置默认显示所有应用
      } catch (error) {
        ElMessage.error('加载商家入驻申请失败');
      }
    };

    const searchApplications = () => {
      const query = searchQuery.value.toLowerCase();
      filteredApplications.value = applications.value.filter(application => {
        return (
            application.merchantName.toLowerCase().includes(query) ||
            application.contactInfo.toLowerCase().includes(query)
        );
      });
    };

    const filterByStatus = () => {
      if (selectedStatus.value === '') {
        filteredApplications.value = applications.value;
      } else {
        filteredApplications.value = applications.value.filter(application => application.status === selectedStatus.value);
      }
    };

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

    const rejectApplication = async (application) => {
      try {
        await axios.put(`http://localhost:8080/api/merchant-applications/${application.applicationId}/reject`);
        ElMessage.success('已拒绝申请');
        await fetchApplications();
      } catch (error) {
        ElMessage.error('拒绝操作失败');
      }
    };

    const editApplication = (application) => {
      newApplication.value = { ...application };
      dialogVisible.value = true;
    };

    const showNewApplicationForm = () => {
      newApplication.value = {
        merchantName: '',
        contactInfo: '',
        businessScope: '',
        license: '',
        userId: null,
        status: 'pending',
      };
      dialogVisible.value = true;
    };

    const saveApplication = async () => {
      try {
        if (!newApplication.value.userId) {
          ElMessage.error('用户ID不能为空');
          return;
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
        ElMessage.error('保存失败: ' + (error.response?.data || error.message));
      }
    };

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

    onMounted(() => {
      fetchApplications();
    });

    return {
      applications,
      filteredApplications,
      dialogVisible,
      newApplication,
      searchQuery,
      selectedStatus,
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
    }
  }
};
</script>

<style scoped>

.toolbar {
  margin-bottom: 20px;
}

.page-container {
  position: relative;
  min-height: 100vh;
  will-change: transform, opacity;
}

/* 优化动画性能 */
.el-container {
  transform: translateZ(0);
  backface-visibility: hidden;
  perspective: 1000px;
}
</style>

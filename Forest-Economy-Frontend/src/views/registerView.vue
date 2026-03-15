<template>
  <div class="register-page">
    <div class="page-content">
      <!-- 左侧展示区 -->
      <div class="showcase-container">
        <div class="logo-section">
          <span class="logo-icon">🌿</span>
          <span class="logo-text">林下经济</span>
        </div>
        <div class="showcase-content">
          <h2 class="showcase-title">智能管理林下资源</h2>
          <p class="showcase-description">提高产量，优化管理，连接市场</p>
          <div class="showcase-features">
            <div class="feature-item">
              <div class="feature-icon">📊</div>
              <div class="feature-text">数据可视化分析</div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🔄</div>
              <div class="feature-text">一体化资源管理</div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">📱</div>
              <div class="feature-text">多端同步操作</div>
            </div>
          </div>
        </div>
        <div class="showcase-footer">
          <p>© 2025 林下经济管理系统 · 所有权利保留</p>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="register-container">
        <div class="back-button-container">
          <router-link to="/" class="back-button">
            <span>←</span> 返回主页
          </router-link>
        </div>

        <div class="form-header">
          <h1 class="title">创建账户</h1>
          <p class="subtitle">注册一个账户以使用林下经济管理系统</p>
        </div>

        <form @submit.prevent="handleRegister">
          <div class="form-row">
            <div class="form-group full-width">
              <label class="form-label">手机号码</label>
              <div class="phoneNumber-container">
                <input
                  type="tel"
                  class="form-input phoneNumber-input"
                  placeholder="请输入手机号码"
                  v-model="formData.phoneNumber"
                  :class="{ 'error-input': errors.phoneNumber }"
                >
                <button
                  type="button"
                  class="code-button"
                  @click="getVerificationCode"
                  :disabled="cooldown > 0"
                >
                  {{ cooldown > 0 ? `${cooldown}秒后重试` : '获取验证码' }}
                </button>
              </div>
              <p v-if="errors.phoneNumber" class="error-message">{{ errors.phoneNumber }}</p>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group full-width">
              <label class="form-label">验证码</label>
              <input
                type="text"
                class="form-input"
                placeholder="请输入验证码"
                v-model="formData.verificationCode"
                :class="{ 'error-input': errors.verificationCode }"
              >
              <p v-if="errors.verificationCode" class="error-message">{{ errors.verificationCode }}</p>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group full-width">
              <label class="form-label">用户名</label>
              <input
                type="text"
                class="form-input"
                placeholder="请设置用户名"
                v-model="formData.username"
                :class="{ 'error-input': errors.username }"
              >
              <p v-if="errors.username" class="error-message">{{ errors.username }}</p>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group half-width">
              <label class="form-label">密码</label>
              <div class="password-input-container">
                <input
                  :type="showPassword ? 'text' : 'password'"
                  class="form-input"
                  placeholder="请设置密码"
                  v-model="formData.password"
                  :class="{ 'error-input': errors.password }"
                >
                <span class="password-toggle" @click="togglePassword('password')">
                  {{ showPassword ? '😮' : '😑' }}
                </span>
              </div>
              <p v-if="errors.password" class="error-message">{{ errors.password }}</p>
            </div>

            <div class="form-group half-width">
              <label class="form-label">确认密码</label>
              <div class="password-input-container">
                <input
                  :type="showConfirmPassword ? 'text' : 'password'"
                  class="form-input"
                  placeholder="请再次输入密码"
                  v-model="formData.confirmPassword"
                  :class="{ 'error-input': errors.confirmPassword }"
                >
                <span class="password-toggle" @click="togglePassword('confirm')">
                  {{ showConfirmPassword ? '😮' : '😑' }}
                </span>
              </div>
              <p v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</p>
            </div>
          </div>

          <div class="password-strength" v-if="formData.password">
            <div class="strength-label">密码强度:</div>
            <div class="strength-bars">
              <div class="strength-bar" :class="{ active: passwordStrength >= 1 }"></div>
              <div class="strength-bar" :class="{ active: passwordStrength >= 2 }"></div>
              <div class="strength-bar" :class="{ active: passwordStrength >= 3 }"></div>
              <div class="strength-bar" :class="{ active: passwordStrength >= 4 }"></div>
            </div>
            <div class="strength-text">{{ passwordStrengthText }}</div>
          </div>

          <button type="submit" class="register-button" :disabled="isSubmitting">
            <span v-if="isSubmitting" class="spinner"></span>
            {{ isSubmitting ? '注册中...' : '注册' }}
          </button>

          <div class="agreement">
            点击"注册"，即表示您同意<a href="#" @click.prevent="openAgreement('terms')">《用户协议》</a>和<a href="#" @click.prevent="openAgreement('privacy')">《隐私政策》</a>
          </div>

          <div class="login-link">
            已有账户？<a href="#" @click.prevent="goToLogin">立即登录</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus'
import { registerApi } from '@/api/register'


const router = useRouter();

// 表单数据
const formData = reactive({
  phoneNumber: '',
  verificationCode: '',
  username: '',
  password: '',
  confirmPassword: ''
});

// 错误信息
const errors = reactive({
  phoneNumber: '',
  verificationCode: '',
  username: '',
  password: '',
  confirmPassword: ''
});

// 验证码冷却相关
const cooldown = ref(0);
const cooldownTimer = ref(null);
const isSubmitting = ref(false);
const showPassword = ref(false);
const showConfirmPassword = ref(false);

// 计算属性：密码强度
// 计算属性：密码强度
const passwordStrength = computed(() => {
  const password = formData.password;
  if (!password) return 0;

  let strength = 0;
  // 长度超过8
  if (password.length >= 8) strength++;
  // 包含小写字母
  if (/[a-z]/.test(password)) strength++;
  // 包含大写字母
  if (/[A-Z]/.test(password)) strength++;
  // 包含数字
  if (/[0-9]/.test(password)) strength++;
  // 包含特殊字符
  if (/[^a-zA-Z0-9]/.test(password)) strength++;

  return strength; // 移除Math.min限制，让它返回0-5范围的值
});

// 计算属性：密码强度文本
const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value;
  const texts = ['极弱', '弱', '一般', '良好', '强'];
  return texts[Math.min(4, strength)] || '极弱'; // 调整索引范围为0-4
});

// 切换密码显示/隐藏
const togglePassword = (type) => {
  if (type === 'password') {
    showPassword.value = !showPassword.value;
  } else {
    showConfirmPassword.value = !showConfirmPassword.value;
  }
};

// 表单验证
const validateForm = () => {
  let isValid = true;
  // 重置错误消息
  for (let key in errors) {
    errors[key] = '';
  }

  // 手机号验证
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!formData.phoneNumber) {
    errors.phoneNumber = '请输入手机号码';
    isValid = false;
  } else if (!phoneRegex.test(formData.phoneNumber)) {
    errors.phoneNumber = '请输入有效的手机号码';
    isValid = false;
  }

  // 验证码验证
  if (!formData.verificationCode) {
    errors.verificationCode = '请输入验证码';
    isValid = false;
  }

  // 用户名验证
  if (!formData.username) {
    errors.username = '请输入用户名';
    isValid = false;
  } else if (formData.username.length < 3) {
    errors.username = '用户名长度不能少于3个字符';
    isValid = false;
  }

  // 密码验证
  if (!formData.password) {
    errors.password = '请输入密码';
    isValid = false;
  } else if (formData.password.length < 8) {
    errors.password = '密码长度不能少于8个字符';
    isValid = false;
  }

  // 确认密码验证
  if (!formData.confirmPassword) {
    errors.confirmPassword = '请确认密码';
    isValid = false;
  } else if (formData.confirmPassword !== formData.password) {
    errors.confirmPassword = '两次输入的密码不一致';
    isValid = false;
  }

  return isValid;
};

// 获取验证码
const getVerificationCode = () => {
  if (cooldown.value > 0) return;

  // 验证手机号
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!formData.phoneNumber) {
    errors.phoneNumber = '请输入手机号码';
    return;
  } else if (!phoneRegex.test(formData.phoneNumber)) {
    errors.phoneNumber = '请输入有效的手机号码';
    return;
  }

  // 开始倒计时
  cooldown.value = 60;
  cooldownTimer.value = setInterval(() => {
    cooldown.value--;
    if (cooldown.value <= 0) {
      clearInterval(cooldownTimer.value);
    }
  }, 1000);

  // 这里可以添加获取验证码的API调用
  console.log('获取验证码，手机号:', formData.phoneNumber);

  // 模拟API调用成功
  ElMessage.success('验证码已发送到您的手机');
};

// 处理注册
const handleRegister = async() => {
  if (!validateForm()) return;

  isSubmitting.value = true;

  console.log('提交注册表单:', formData);

  try {
    const result = await registerApi(formData);

    if (result.code === 1 || result.success) {

      ElMessage.success('注册成功');
      router.push('/login');
    } else {
      ElMessage.error(result.message || '注册失败');
    }
  } catch (error) {
    console.error('注册错误:', error);
    ElMessage.error(error.message || '注册失败，请检查网络连接');
  } finally {
    isSubmitting.value = false;
  }
};

// 打开协议
const openAgreement = (type) => {
  const title = type === 'terms' ? '用户协议' : '隐私政策';
  ElMessageBox.alert(`这是${title}的占位内容。开源版本暂未提供完整协议文案。`, title, {
    confirmButtonText: '我知道了'
  });
};

// 跳转到登录页
const goToLogin = () => {
  router.push('/login');
};

// 组件卸载前清除定时器
onBeforeUnmount(() => {
  if (cooldownTimer.value) {
    clearInterval(cooldownTimer.value);
  }
});

// 保持组件名称与原始组件一致
defineOptions({
  name: 'RegisterView'
});
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background-color: #e5f8b9;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30px;
}

.page-content {
  display: flex;
  width: 100%;
  max-width: 1200px;
  min-height: 700px;
  background-color: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* 左侧展示区样式 */
.showcase-container {
  flex: 1;
  background: linear-gradient(135deg, #556B2F, #7B904B);
  color: white;
  padding: 40px;
  display: flex;
  flex-direction: column;
  position: relative;
}

.logo-section {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
}

.logo-icon {
  font-size: 42px;
  margin-right: 16px;
}

.logo-text {
  font-size: 32px;
  font-weight: 600;
  letter-spacing: 1px;
}

.showcase-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.showcase-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 16px;
}

.showcase-description {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 40px;
  line-height: 1.5;
}

.showcase-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.feature-icon {
  font-size: 24px;
  background: rgba(255, 255, 255, 0.2);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-text {
  font-size: 16px;
  font-weight: 500;
}

.showcase-footer {
  margin-top: auto;
  font-size: 14px;
  opacity: 0.7;
}

/* 右侧注册表单样式 */
.register-container {
  flex: 1;
  padding: 40px 60px;
  overflow-y: auto;
  background-color: #FDFBF7;
}

.back-button-container {
  text-align: left;
  margin-bottom: 30px;
}

.back-button {
  display: inline-flex;
  align-items: center;
  color: #556B2F;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.2s;
}

.back-button:hover {
  opacity: 0.8;
}

.back-button span {
  margin-right: 8px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.title {
  font-size: 28px;
  color: #2B3F2B;
  margin-bottom: 10px;
  font-weight: 600;
}

.subtitle {
  font-size: 16px;
  color: #5A4A3A;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  position: relative;
}

.full-width {
  width: 100%;
}

.half-width {
  width: calc(50% - 10px);
}

.form-label {
  display: block;
  font-size: 15px;
  color: #3C2A1A;
  margin-bottom: 10px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #E8E0D0;
  border-radius: 10px;
  font-size: 15px;
  color: #2B3F2B;
  background-color: #ffffff;
  box-sizing: border-box;
  transition: all 0.3s;
}

.form-input:focus {
  border-color: #98C9A3;
  outline: none;
  box-shadow: 0 0 0 3px rgba(152, 201, 163, 0.2);
}

.error-input {
  border-color: #D75650;
}

.error-message {
  color: #D75650;
  font-size: 13px;
  margin-top: 6px;
  margin-bottom: 0;
}

.phoneNumber-container {
  display: flex;
  gap: 12px;
}

.phoneNumber-input {
  flex: 1;
}

.code-button {
  min-width: 120px;
  background-color: #EFF4ED;
  border: 1px solid #D9E5D6;
  border-radius: 10px;
  color: #556B2F;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.code-button:hover:not(:disabled) {
  background-color: #D9E5D6;
}

.code-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.password-input-container {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  font-size: 18px;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.password-toggle:hover {
  opacity: 1;
}

.password-strength {
  margin: 15px 0 25px;
}

.strength-label {
  font-size: 14px;
  color: #5A4A3A;
  margin-bottom: 8px;
}

.strength-bars {
  display: flex;
  gap: 5px;
}

.strength-bar {
  height: 6px;
  flex: 1;
  background-color: #E8E0D0;
  border-radius: 3px;
  transition: all 0.3s;
}

.strength-bar.active:nth-child(1) {
  background-color: #D75650;
}

.strength-bar.active:nth-child(2) {
  background-color: #F0AD4E;
}

.strength-bar.active:nth-child(3) {
  background-color: #5BC0DE;
}

.strength-bar.active:nth-child(4) {
  background-color: #5CB85C;
}

.strength-text {
  font-size: 13px;
  text-align: right;
  margin-top: 5px;
  color: #5A4A3A;
}

.register-button {
  width: 100%;
  padding: 16px;
  background-color: #556B2F;
  border: none;
  border-radius: 10px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  box-shadow: 0 4px 12px rgba(85, 107, 47, 0.3);
}

.register-button:hover:not(:disabled) {
  background-color: #455A20;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(85, 107, 47, 0.4);
}

.register-button:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 4px 8px rgba(85, 107, 47, 0.3);
}

.register-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s linear infinite;
  margin-right: 10px;
  vertical-align: middle;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.agreement {
  font-size: 13px;
  color: #5A4A3A;
  text-align: center;
  margin-top: 25px;
  line-height: 1.5;
}

.agreement a {
  color: #556B2F;
  text-decoration: none;
  font-weight: 500;
}

.agreement a:hover {
  text-decoration: underline;
}

.login-link {
  text-align: center;
  margin-top: 25px;
  font-size: 15px;
  color: #5A4A3A;
}

.login-link a {
  color: #556B2F;
  text-decoration: none;
  font-weight: 600;
}

.login-link a:hover {
  text-decoration: underline;
}

/* 响应式处理 */
@media (max-width: 1200px) {
  .page-content {
    max-width: 900px;
  }

  .showcase-container {
    padding: 30px;
  }

  .register-container {
    padding: 30px 40px;
  }
}

@media (max-width: 900px) {
  .page-content {
    flex-direction: column;
    max-width: 600px;
  }

  .showcase-container {
    padding: 30px;
    min-height: 300px;
  }

  .showcase-features {
    flex-direction: row;
    justify-content: space-between;
  }

  .feature-item {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .register-container {
    padding: 30px;
  }
}

@media (max-width: 600px) {
  .register-page {
    padding: 0;
  }

  .page-content {
    border-radius: 0;
    max-width: none;
    min-height: 100vh;
  }

  .showcase-container {
    display: none;
  }

  .form-row {
    flex-direction: column;
    gap: 20px;
  }

  .half-width {
    width: 100%;
  }
}
</style>




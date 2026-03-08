<template>
  <div class="search-content" :class="{ 'chat-history-active': chatHistory.length > 0 }">
    <!-- 对话标题 -->
    <!-- <div class="conversation-title" v-if="chatHistory.length > 0 && conversationTitle">
      {{ conversationTitle }}
    </div> -->
    <!-- 页面标题 -->
    <div class="search-title">
      <h1 v-if="!conversationTitle">您想了解哪些林下经济知识？</h1>
      <h1 v-else>{{ conversationTitle }}</h1>
      <!-- 不再需要清除历史按钮 -->
    </div>

    <!-- 聊天内容区域 -->
    <div class="chat-container" v-if="chatHistory.length > 0">
      <div class="chat-messages" ref="chatMessagesContainer">
        <div v-for="(message, index) in chatHistory" :key="index"
          :class="['chat-message', message.type === 'user' ? 'user-message' : 'ai-message']">
          <div class="message-content">
            <div class="message-avatar">
              <span v-if="message.type === 'user'">👤</span>
              <span v-else>🌳</span>
            </div>
            <div class="message-text" v-html="message.content"></div>
          </div>
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
      </div>
    </div>

    <!-- 加载状态指示器 -->
    <div v-if="isLoading" class="loading-indicator">
      <span class="loading-dot"></span>
      <span class="loading-dot"></span>
      <span class="loading-dot"></span>
      <span class="loading-text">正在思考中...</span>
    </div>

    <!-- 搜索区域 -->
    <div class="search-container" :class="{ 'search-minimized': chatHistory.length > 0 }">
      <el-input
        v-model="userInput"
        class="search-input-el"
        :placeholder="chatHistory.length > 0 ? '继续提问...' : '随意提问...'"
        @keyup.enter="sendMessage"
      >
        <template #append>
          <el-button
            type="primary"
            :icon="chatHistory.length > 0 ? ArrowUp : Search"
            @click="sendMessage"
            :disabled="isLoading || userInput.trim() === ''"
          ></el-button>
        </template>
        <!-- 右侧图标 -->
        <template #suffix>
          <el-icon v-if="userInput.trim().length > 0" class="input-icon"><Right /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 推荐搜索或最近搜索（可选）-->
    <div class="suggested-searches" v-if="chatHistory.length === 0">
      <h3>热门问题</h3>
      <div class="suggested-items">
        <el-card v-for="(question, index) in suggestedQuestions" :key="index" class="suggested-item-card" shadow="hover"
          @click="usePresetQuestion(question)">
          {{ question }}
        </el-card>
      </div>
    </div>

    <!-- 错误提示 -->
    <el-alert v-if="errorInfo.show" :title="errorInfo.title" :description="errorInfo.message" type="error" show-icon
      class="error-alert" @close="errorInfo.show = false" />

    <!-- 底部状态或版权信息 -->
    <div class="footer">
      <div class="footer-links">
        <a href="#" class="footer-link">关于我们</a>
        <a href="#" class="footer-link">联系方式</a>
        <a href="#" class="footer-link">使用条款</a>
        <a href="#" class="footer-link">隐私政策</a>
      </div>
      <div class="copyright">
        © 2025 林下经济研究中心
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useHistoryStore } from '@/stores/historyStore';
import {
  ElMessage,
} from 'element-plus';
import {
  Delete,
  ArrowUp,
  Search,
} from '@element-plus/icons-vue';

// 常量定义
const STORAGE_KEY = 'forest_economy_chat_history';
const SESSION_ID = Date.now().toString();
const MAX_RETRY_COUNT = 3;

export default {
  name: 'ForestEconomyMainContent',
  components: {},
  setup() {
    // 路由相关
    const route = useRoute();

    // 历史记录存储
    const historyStore = useHistoryStore();

    // 响应式数据
    const userInput = ref('');
    const isLoading = ref(false);
    const chatHistory = ref([]);
    const showClearHistoryDialog = ref(false);
    const chatMessagesContainer = ref(null);
    const retryCount = ref(0);
    const conversationTitle = ref('');
    const conversationId = ref(SESSION_ID);

    // 错误信息对象
    const errorInfo = reactive({
      show: false,
      title: '',
      message: '',
      type: 'error'
    });

    // 预设问题
    const suggestedQuestions = [
      '林下经济有哪些主要类型？',
      '如何评估林下种植的经济效益？',
      '林下养蜂的最佳实践是什么？',
      '林下食用菌培育的环境要求有哪些？'
    ];

    // API配置
    const apiConfig = {
      apiKey: import.meta.env.VITE_XAI_API_KEY || '',
      apiEndpoint: import.meta.env.VITE_XAI_API_ENDPOINT || 'https://api.x.ai/v1/chat/completions',
      model: import.meta.env.VITE_XAI_MODEL || 'grok-3-beta',
    };

    const useMockAi = computed(() => !apiConfig.apiKey);

    // 加载聊天历史
    const loadChatHistory = () => {
      try {
        const savedHistory = localStorage.getItem(STORAGE_KEY);
        if (savedHistory) {
          const parsedData = JSON.parse(savedHistory);

          // 新格式保存了完整的消息和标题
          if (parsedData.messages && parsedData.title) {
            chatHistory.value = parsedData.messages;
            conversationTitle.value = parsedData.title;
            conversationId.value = parsedData.id || SESSION_ID;
          }
          // 宅格式兼容
          else if (Array.isArray(parsedData)) {
            chatHistory.value = parsedData;
          }
        }
      } catch (error) {
        console.error('加载历史记录失败:', error);
        // 加载失败时显示提示但不阻止使用
        ElMessage.warning('历史记录加载失败，将开始新的对话');
      }
    };

    // 保存聊天历史
    const saveChatHistory = () => {
      try {
        // 将对话内容保存到本地存储
        localStorage.setItem(STORAGE_KEY, JSON.stringify({
          messages: chatHistory.value,
          title: conversationTitle.value,
          id: conversationId.value
        }));

        // 如果已有对话标题和内容，保存到历史记录中
        if (conversationTitle.value && chatHistory.value.length > 0) {
          historyStore.addHistory({
            id: conversationId.value,
            title: conversationTitle.value,
            timestamp: Date.now(),
            messages: chatHistory.value  // 保存完整的对话历史
          });
        }
      } catch (error) {
        console.error('保存历史记录失败:', error);
        ElMessage.warning('历史记录保存失败');
      }
    };

    // 清除聊天历史
    const clearChatHistory = () => {
      chatHistory.value = [];
      localStorage.removeItem(STORAGE_KEY);
      showClearHistoryDialog.value = false;
      conversationTitle.value = '';
    };

    // 加载特定历史记录
    const loadSpecificHistory = (history) => {
      // 重置当前会话
      clearChatHistory();

      // 设置会话ID和标题
      conversationId.value = history.id;
      conversationTitle.value = history.title;

      // 如果历史记录包含完整的消息历史，直接加载
      if (history.messages && history.messages.length > 0) {
        chatHistory.value = history.messages;

        // 保存到本地存储，确保在页面刷新后也能记得
        saveChatHistory();
      }
      // 兼容旧版仅保存第一条消息的格式
      else if (history.firstMessage) {
        sendSpecificMessage(history.firstMessage);
      }
    };



    // 监听路由变化
    watch(() => route.query, (newQuery) => {
      if (newQuery.new) {
        // 新建项目
        clearChatHistory();
        conversationId.value = Date.now().toString();
        conversationTitle.value = '';
      } else if (newQuery.historyId) {
        // 加载特定历史
        const history = historyStore.chatHistory.find(h => h.id === newQuery.historyId);
        if (history) {
          loadSpecificHistory(history);
        }
      }
    }, { immediate: true });

    // 初始化时加载历史记录
    onMounted(() => {
      // 如果不是新项目或加载特定历史，则加载本地存储的聊天记录
      if (!route.query.new && !route.query.historyId) {
        loadChatHistory();
      }

      // 如果有历史记录，滚动到最新消息
      if (chatHistory.value.length > 0) {
        nextTick(() => {
          scrollToBottom();
        });
      }
    });


    // 发送特定消息（用于加载历史记录）
    const sendSpecificMessage = async (message) => {
      // 添加用户消息到聊天历史
      chatHistory.value.push({
        type: 'user',
        content: message,
        timestamp: Date.now()
      });

      // 保存更新后的历史记录
      saveChatHistory();

      // 滚动到最新消息
      nextTick(() => {
        scrollToBottom();
      });

      // 设置加载状态
      isLoading.value = true;
      retryCount.value = 0; // 重置重试计数

      try {
        // 调用XAI API
        const response = await callXaiApi(message);
        console.log('API响应成功:', response); // 调试日志

        // 添加AI响应到聊天历史
        chatHistory.value.push({
          type: 'ai',
          content: formatResponse(response),
          timestamp: Date.now()
        });

        // 保存更新后的历史记录
        saveChatHistory();

        // 错误已解决，隐藏任何显示的错误
        errorInfo.show = false;
      } catch (error) {
        // 处理错误情况
        handleApiError(error, message);
      } finally {
        // 关闭加载状态
        isLoading.value = false;

        // 滚动到最新消息
        nextTick(() => {
          scrollToBottom();
        });
      }
    };

    // 发送消息到API
    const sendMessage = async () => {
      // 检查输入是否为空
      if (!userInput.value.trim()) return;

      const userMessage = userInput.value.trim();
      userInput.value = ''; // 清空输入框

      // 添加用户消息到聊天历史
      chatHistory.value.push({
        type: 'user',
        content: userMessage,
        timestamp: Date.now()
      });

      // 保存更新后的历史记录
      saveChatHistory();

      // 滚动到最新消息
      nextTick(() => {
        scrollToBottom();
      });

      // 设置加载状态
      isLoading.value = true;
      retryCount.value = 0; // 重置重试计数

      try {
        // 如果是第一条消息，同时调用总结API生成标题
        if (chatHistory.value.length === 1) {
          // 异步调用总结API
          generateTitle(userMessage);
        }

        // 调用XAI API获取回答
        const response = await callXaiApi(userMessage);

        // 添加AI响应到聊天历史
        chatHistory.value.push({
          type: 'ai',
          content: formatResponse(response),
          timestamp: Date.now()
        });

        // 保存更新后的历史记录
        saveChatHistory();

        // 错误已解决，隐藏任何显示的错误
        errorInfo.show = false;
      } catch (error) {
        // 处理错误情况
        handleApiError(error, userMessage);
      } finally {
        // 关闭加载状态
        isLoading.value = false;

        // 滚动到最新消息
        nextTick(() => {
          scrollToBottom();
        });
      }
    };

    // 生成对话标题
    const generateTitle = async (userMessage) => {
      // Use local title generation when API key is not configured
      if (useMockAi.value) {
        const defaultTitle = userMessage.length > 10 ? userMessage.substring(0, 10) + '...' : userMessage;
        conversationTitle.value = defaultTitle;
        historyStore.addHistory({
          id: conversationId.value,
          title: defaultTitle,
          timestamp: Date.now(),
          firstMessage: userMessage
        });
        return;
      }

      try {
        // 构建请求体
        const requestOptions = {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${apiConfig.apiKey}`
          },
          body: JSON.stringify({
            model: apiConfig.model,
            messages: [
              {
                role: "user",
                content: `根据以下问题生成一个简短的标题（不超过15个字）："${userMessage}"`
              }
            ],
            temperature: 0.7,
            max_tokens: 50
          })
        };

        // 发送请求
        const response = await fetch(apiConfig.apiEndpoint, requestOptions);

        if (!response.ok) {
          throw new Error(`标题生成API错误: ${response.status}`);
        }

        const data = await response.json();
        let title = data.choices?.[0]?.message?.content?.trim();

        // 确保标题不超过15个字
        if (title && title.length > 15) {
          title = title.substring(0, 15) + '...';
        }

        // 移除可能的引号
        title = title.replace(/["']/g, '');

        conversationTitle.value = title;
        console.log('生成的对话标题:', title);

        // 更新历史记录
        historyStore.addHistory({
          id: conversationId.value,
          title: title,
          timestamp: Date.now(),
          firstMessage: userMessage
        });
      } catch (error) {
        console.error('生成标题失败:', error);
        // 使用默认标题
        const defaultTitle = userMessage.length > 10 ? userMessage.substring(0, 10) + '...' : userMessage;
        conversationTitle.value = defaultTitle;

        // 仍然添加到历史记录
        historyStore.addHistory({
          id: conversationId.value,
          title: defaultTitle,
          timestamp: Date.now(),
          firstMessage: userMessage
        });
      }
    };

    // 调用XAI API
    const callXaiApi = async (message) => {
      if (useMockAi.value) {
        return `Open-source demo response.\nQuestion received: ${message}\n\nNo external model key is bundled. Set VITE_XAI_API_KEY to enable real responses.`;
      }

      // 构建API请求
      const requestOptions = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${apiConfig.apiKey}`
        },
        body: JSON.stringify({
          model: apiConfig.model,
          messages: [
            {
              role: "user",
              content: message
            }
          ],
          temperature: 0.7,
          max_tokens: 800
        })
      };

      try {
        // 发送请求
        const response = await fetch(apiConfig.apiEndpoint, requestOptions);

        if (!response.ok) {
          // 尝试读取错误响应内容
          let errorDetail = '';
          try {
            const errorData = await response.json();
            errorDetail = JSON.stringify(errorData);
          } catch (e) {
            // 记录具体错误信息
            console.error('解析错误响应失败:', e.message);
            errorDetail = `无法解析错误详情: ${e.message}`;
          }

          throw new Error(`API错误: ${response.status} ${response.statusText}. 详情: ${errorDetail}`);
        }

        const data = await response.json();

        // 从响应中提取内容
        const messageContent = data.choices?.[0]?.message?.content;
        if (!messageContent) {
          return '未能从API响应中获取到回答';
        }

        return messageContent;
      } catch (error) {
        // 特定处理超时和网络错误
        if (error.name === 'AbortError' || error.message.includes('network') || error.message.includes('timeout')) {
          error.isNetworkError = true;
        }
        throw error;
      }
    };


    // 处理API错误
    const handleApiError = async (error, originalMessage) => {
      // 如果是网络错误且未超过最大重试次数，则自动重试
      if ((error.isNetworkError || error.message.includes('network') || error.message.includes('timeout'))
        && retryCount.value < MAX_RETRY_COUNT) {

        retryCount.value++;

        // 添加重试消息
        chatHistory.value.push({
          type: 'system',
          content: `网络请求失败，正在进行第${retryCount.value}次重试...`,
          timestamp: Date.now()
        });

        // 保存历史并滚动
        saveChatHistory();
        nextTick(() => {
          scrollToBottom();
        });

        // 等待一秒后重试
        await new Promise(resolve => setTimeout(resolve, 1000));

        // 设置加载状态
        isLoading.value = true;

        try {
          // 重新调用API
          const response = await callXaiApi(originalMessage);

          // 移除系统消息（重试消息）
          chatHistory.value = chatHistory.value.filter(msg => msg.type !== 'system');

          // 添加AI响应
          chatHistory.value.push({
            type: 'ai',
            content: formatResponse(response),
            timestamp: Date.now()
          });

          // 保存更新后的历史记录
          saveChatHistory();

          // 错误已解决，隐藏任何显示的错误
          errorInfo.show = false;
        } catch (retryError) {
          // 如果重试也失败，调用自身（递归）
          handleApiError(retryError, originalMessage);
        } finally {
          // 关闭加载状态
          isLoading.value = false;
        }
      } else {
        // 超过重试次数或非网络错误，显示错误消息
        let errorMessage = '抱歉，我暂时无法回答您的问题。';
        let errorTitle = '请求失败';

        if (error.isNetworkError || error.message.includes('network')) {
          errorTitle = '网络连接错误';
          errorMessage += '请检查您的网络连接并稍后再试。';
        } else if (error.message.includes('timeout')) {
          errorTitle = '请求超时';
          errorMessage += '服务器响应时间过长，请稍后再试。';
        } else if (error.message.includes('401')) {
          errorTitle = '身份验证失败';
          errorMessage += '请联系管理员检查API密钥是否有效。';
        } else if (error.message.includes('429')) {
          errorTitle = '请求过于频繁';
          errorMessage += '已超过API使用限制，请稍后再试。';
        } else if (error.message.includes('500')) {
          errorTitle = '服务器错误';
          errorMessage += '服务器内部错误，请稍后再试。';
        }

        // 为开发人员显示技术详情
        const technicalDetails = `技术详情: ${error.message}`;

        // 显示错误信息
        errorInfo.title = errorTitle;
        errorInfo.message = `${errorMessage} (${technicalDetails})`;
        errorInfo.show = true;

        // 添加错误响应到聊天
        chatHistory.value.push({
          type: 'ai',
          content: errorMessage,
          timestamp: Date.now(),
          isError: true
        });

        // 保存更新后的历史记录
        saveChatHistory();
      }
    };

    // 格式化API响应
    const formatResponse = (response) => {
      const escaped = response
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

      // 简单的Markdown处理
      let html = escaped
        // 处理代码块
        .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
        // 处理单行代码
        .replace(/`([^`]+)`/g, '<code>$1</code>')
        // 处理标题
        .replace(/^### (.*$)/gm, '<h3>$1</h3>')
        .replace(/^## (.*$)/gm, '<h2>$1</h2>')
        .replace(/^# (.*$)/gm, '<h1>$1</h1>')
        // 处理加粗和斜体
        .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
        .replace(/\*([^*]+)\*/g, '<em>$1</em>')
        // 处理行间转换为<br>
        .replace(/\n/g, '<br>');

      return html;
    };

    // 格式化时间戳
    const formatTime = (timestamp) => {
      const date = new Date(timestamp);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    };

    // 滚动到聊天底部
    const scrollToBottom = () => {
      if (chatMessagesContainer.value) {
        chatMessagesContainer.value.scrollTop = chatMessagesContainer.value.scrollHeight;
      }
    };

    // 使用预设问题
    const usePresetQuestion = (question) => {
      userInput.value = question;
      sendMessage();
    };

    return {
      userInput,
      isLoading,
      chatHistory,
      suggestedQuestions,
      errorInfo,
      showClearHistoryDialog,
      chatMessagesContainer,
      conversationTitle,
      Delete,
      ArrowUp,
      Search,
      sendMessage,
      clearChatHistory,
      formatTime,
      usePresetQuestion
    };
  }
};
</script>

<style scoped>
/* 基础样式 */
.search-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  min-height: 100vh;
  background-color: #FDFBF7;
  /* 奶油色背景 */
  padding: 150px 40px 80px;
  box-sizing: border-box;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', sans-serif;
  text-align: center;
  width: 100%;
  position: relative;
  /* 删除overflow-y: auto，避免双滚动条 */
  overflow: hidden;
}

/* 对话标题样式 */
.conversation-title {
  position: absolute;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(253, 251, 247, 0.9);
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  color: #556B2F;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 10;
  backdrop-filter: blur(2px);
  max-width: 90%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 标题区域 */
.search-title {
  position: relative;
  margin-bottom: 40px;
  text-align: center;
  margin-top: 60px;
  /* 增加上边距 */
  transition: all 0.5s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-title h1 {
  font-size: 32px;
  font-weight: 600;
  color: #3C2A1A;
  /* 深木色文字 */
}

.clear-history-btn {
  position: absolute;
  right: -40px;
  top: 50%;
  transform: translateY(-50%);
}

/* 聊天历史存在时的标题样式 */
.chat-history-active .search-title {
  position: sticky;
  top: 0;
  margin-top: 20px;
  margin-bottom: 20px;
  z-index: 10;
  background-color: rgba(253, 251, 247, 0.9);
  backdrop-filter: blur(5px);
  padding: 10px 0;
  width: 100%;
}

.chat-history-active .search-title h1 {
  font-size: 24px;
}

/* 搜索输入区域 */
.search-container {
  width: 100%;
  max-width: 900px;
  /* 增加宽度与聊天容器一致 */
  margin: 0 auto;
  margin-top: 60px;
  /* 增加上边距 */
  transition: all 0.5s ease;
}

/* 搜索框最小化样式 */
.search-minimized {
  margin-top: 20px;
  position: sticky;
  bottom: 20px;
  z-index: 10;
}

/* Element Plus 输入框样式覆盖 */
.search-input-el {
  width: 100%;
  --el-input-border-radius: 12px;
  --el-input-height: 50px;
  --el-input-font-size: 16px;
}

.input-icon {
  color: #556B2F; /* 与主题颜色相匹配 */
  font-size: 18px;
  margin-right: 8px;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05) !important;
  background-color: white;
  padding: 0 10px;
}

:deep(.el-input__inner) {
  color: #3C2A1A;
}

:deep(.el-input-group__append) {
  padding: 0;
}

:deep(.el-input-group__append .el-button) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  height: 100%;
  margin: 0;
  background-color: #556B2F;
  border-color: #556B2F;
}

:deep(.el-input-group__append .el-button:hover) {
  background-color: #475A27;
  border-color: #475A27;
}

:deep(.el-input__placeholder) {
  color: #A89E8D;
}

/* 推荐搜索样式 */
.suggested-searches {
  width: 100%;
  max-width: 900px;
  /* 与其他容器宽度一致 */
  margin: 30px auto;
}

.suggested-searches h3 {
  font-size: 16px;
  font-weight: 600;
  color: #3C2A1A;
  /* 深木色文字 */
  margin-bottom: 20px;
  text-align: center;
  /* 确保标题居中 */
}

.suggested-items {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

/* Element Plus 卡片样式覆盖 */
.suggested-item-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  color: #3C2A1A;
  /* 深木色文字 */
  font-size: 14px;
  background-color: #EFF4ED !important;
  border: 1px solid #D9E5D6 !important;
}

.suggested-item-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
  background-color: #D9E5D6 !important;
  border-color: #98C9A3 !important;
  /* 薄荷绿边框 */
}

:deep(.suggested-item-card .el-card__body) {
  padding: 12px 16px;
}

/* 底部区域 */
.footer {
  margin-top: 60px;
  width: 100%;
  text-align: center;
  padding: 20px 0;
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 10px;
}

.footer-link {
  color: #5A4A3A;
  text-decoration: none;
  font-size: 14px;
}

.footer-link:hover {
  color: #D9984A;
  /* 秋叶橙高亮 */
}

.copyright {
  color: #8A7A6A;
  font-size: 12px;
}

/* 聊天容器样式 */
.chat-container {
  width: 100%;
  max-width: 900px;
  /* 增加容器最大宽度 */
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 400px);
  min-height: 300px;
  transition: all 0.3s ease;
}

.chat-messages {
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  padding: 20px 0;
  flex-grow: 1;
  scroll-behavior: smooth;
}

.chat-message {
  margin-bottom: 20px;
  max-width: 80%;
  animation: fadeIn 0.3s ease;
  position: relative;
}

.user-message {
  align-self: flex-end;
}

.ai-message {
  align-self: flex-start;
}

.message-content {
  display: flex;
  align-items: flex-start;
}

.user-message .message-content {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 10px;
  background-color: #EFF4ED;
  font-size: 20px;
}

.ai-message .message-avatar {
  background-color: #556B2F;
  color: white;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 15px;
  line-height: 1.5;
  text-align: left;
  word-wrap: break-word;
  word-break: keep-all; /* 防止单字断行 */
  overflow-wrap: break-word; /* 确保长单词可以折行 */
  width: calc(100% - 60px); /* 改为width而非max-width，让消息文本占据更大空间 */
  max-width: 600px; /* 增加最大宽度 */
}

.user-message .message-text {
  background-color: #556B2F;
  color: white;
  border-top-right-radius: 4px;
}

.ai-message .message-text {
  background-color: #EFF4ED;
  color: #3C2A1A;
  border-top-left-radius: 4px;
}

/* 错误消息样式 */
.chat-message[data-is-error="true"] .message-text {
  background-color: #FDECEC;
  color: #F56C6C;
  border: 1px solid #F56C6C;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  padding: 0 10px;
}

.user-message .message-time {
  text-align: right;
}

.ai-message .message-time {
  text-align: left;
}

/* 系统消息样式 */
.chat-message[data-type="system"] {
  align-self: center;
  max-width: 90%;
}

.chat-message[data-type="system"] .message-text {
  background-color: #F2F6FC;
  color: #909399;
  font-style: italic;
  border: 1px dashed #DCDFE6;
}

/* 错误提示样式 */
.error-alert {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  max-width: 500px;
  z-index: 9999;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Element Plus 对话框样式覆盖 */
:deep(.el-dialog__header) {
  padding: 20px;
  text-align: center;
  margin: 0;
  background-color: #F5F7FA;
  border-bottom: 1px solid #EBEEF5;
}

:deep(.el-dialog__title) {
  color: #556B2F;
  font-weight: 600;
}

:deep(.el-dialog__body) {
  padding: 30px;
  color: #606266;
}

:deep(.el-dialog__footer) {
  padding: 15px 20px;
  text-align: center;
  border-top: 1px solid #EBEEF5;
}

:deep(.dialog-footer) {
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 15px;
}

/* 加载指示器样式 */
.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(85, 107, 47, 0.1);
  border-radius: 20px;
  padding: 8px 16px;
  margin: 20px auto;
  width: auto;
  max-width: 200px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.loading-dot {
  width: 8px;
  height: 8px;
  background-color: #556B2F;
  border-radius: 50%;
  margin: 0 3px;
  display: inline-block;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dot:nth-child(2) {
  animation-delay: -0.16s;
}

.loading-text {
  color: #556B2F;
  font-size: 14px;
  font-weight: 500;
  margin-left: 8px;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1.0);
  }
}

/* Markdown 样式 */
.ai-message .message-text code {
  background-color: rgba(0, 0, 0, 0.05);
  padding: 0.2em 0.4em;
  border-radius: 3px;
  font-family: SFMono-Regular, Consolas, Liberation Mono, Menlo, monospace;
  font-size: 85%;
}

.ai-message .message-text pre {
  background-color: rgba(0, 0, 0, 0.05);
  padding: 16px;
  border-radius: 6px;
  overflow: auto;
  margin: 10px 0;
}

.ai-message .message-text pre code {
  background-color: transparent;
  padding: 0;
  font-size: 90%;
  line-height: 1.45;
  display: block;
  word-break: normal;
}

.ai-message .message-text h1,
.ai-message .message-text h2,
.ai-message .message-text h3 {
  margin-top: 1.5em;
  margin-bottom: 0.5em;
  font-weight: 600;
  line-height: 1.25;
}

.ai-message .message-text h1 {
  font-size: 1.5em;
}

.ai-message .message-text h2 {
  font-size: 1.3em;
}

.ai-message .message-text h3 {
  font-size: 1.1em;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .search-content {
    padding: 100px 20px 60px;
  }

  .search-container,
  .suggested-searches,
  .chat-container {
    padding: 0 10px;
  }

  .suggested-items {
    grid-template-columns: 1fr;
    /* 在小屏幕上改为单列 */
  }

  .chat-message {
    max-width: 90%;
  }

  .search-title h1 {
    font-size: 24px;
  }

  .chat-history-active .search-title h1 {
    font-size: 20px;
  }

  .clear-history-btn {
    right: -30px;
  }
}

/* 适配较小的屏幕 */
@media (max-width: 480px) {
  .search-title h1 {
    font-size: 20px;
  }

  .chat-history-active .search-title h1 {
    font-size: 18px;
  }

  .clear-history-btn {
    right: -25px;
  }

  .chat-container {
    height: calc(100vh - 300px);
  }

  .message-text {
    font-size: 14px;
    padding: 10px 12px;
  }
}
</style>

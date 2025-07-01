document.addEventListener('DOMContentLoaded', () => {
    // 获取DOM元素
    const chatMessages = document.getElementById('chatMessages');
    const messageInput = document.getElementById('messageInput');
    const sendBtn = document.getElementById('sendBtn');
    const newChatBtn = document.getElementById('newChat');
    const chatLogo = document.getElementById('chatLogo');
    const themeToggle = document.getElementById('themeToggle');
    const featureItems = document.querySelectorAll('.feature-item');
    const historyItems = document.querySelectorAll('.history-item');
    const clearHistoryBtn = document.querySelector('.clear-history');

    // 随机生成品牌颜色
    const brandColors = ['#667eea', '#764ba2', '#6B46C1', '#2563EB', '#db2777', '#9333ea'];
    const selectedColor = brandColors[Math.floor(Math.random() * brandColors.length)];
    document.documentElement.style.setProperty('--primary-color', selectedColor);
    
    // 根据颜色自动生成渐变色
    const lighterColor = adjustColorBrightness(selectedColor, 20);
    document.documentElement.style.setProperty('--primary-gradient', 
        `linear-gradient(135deg, ${selectedColor} 0%, ${lighterColor} 100%)`);

    // 会话ID（随机生成）
    let conversationId = Math.floor(Math.random() * 1000000);
    
    // 自定义logo - 如果需要变更logo，更改此URL
    chatLogo.src = "https://via.placeholder.com/40/667eea/FFFFFF?text=AI";
    
    // 发送按钮脉动动画
    function pulseSendButton() {
        sendBtn.classList.add('pulse-animation');
        setTimeout(() => {
            sendBtn.classList.remove('pulse-animation');
        }, 1000);
    }
    
    // 每分钟调用一次脉动动画，提示用户
    setInterval(pulseSendButton, 60000);

    // 发送消息函数
    async function sendMessage() {
        const question = messageInput.value.trim();
        if (question === '') return;

        // 清空输入框
        messageInput.value = '';

        // 添加用户消息到聊天窗口（带动画）
        addMessage(question, 'user', false, 'animate__fadeInRight');

        // 显示加载指示器
        const loadingMessage = addMessage('思考中', 'bot', true, 'animate__fadeIn');
        
        try {
            // 发送请求到后端
            const response = await fetch('/qwen', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    id: conversationId,
                    question: question
                })
            });

            if (!response.ok) {
                throw new Error('网络请求失败');
            }

            // 获取响应文本
            const answer = await response.text();
            
            // 移除加载消息
            chatMessages.removeChild(loadingMessage);
            
            // 添加机器人回复（带动画）
            addMessage(answer, 'bot', false, 'animate__fadeInLeft');
            
            // 更新对话历史列表（模拟）
            updateHistoryList(question);
        } catch (error) {
            // 移除加载消息
            chatMessages.removeChild(loadingMessage);
            
            // 添加错误消息
            addMessage('抱歉，发生了错误：' + error.message, 'bot', false, 'animate__shakeX');
        }

        // 滚动到底部
        scrollToBottom();
    }

    // 添加消息到聊天窗口
    function addMessage(text, sender, isLoading = false, animationClass = 'animate__fadeIn') {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${sender} animate__animated ${animationClass}`;

        const contentDiv = document.createElement('div');
        contentDiv.className = 'message-content';
        
        if (isLoading) {
            contentDiv.innerHTML = `<span>${text}</span><span class="loading-dots"></span>`;
        } else {
            contentDiv.textContent = text;
        }
        
        messageDiv.appendChild(contentDiv);
        chatMessages.appendChild(messageDiv);

        scrollToBottom();
        return messageDiv;
    }

    // 滚动到底部
    function scrollToBottom() {
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    // 创建新会话（带动画效果）
    function startNewChat() {
        // 添加淡出动画
        const messages = chatMessages.querySelectorAll('.message');
        messages.forEach(msg => {
            msg.classList.add('animate__animated', 'animate__fadeOut');
        });
        
        // 等待动画完成后清空
        setTimeout(() => {
            // 生成新的会话ID
            conversationId = Math.floor(Math.random() * 1000000);
            
            // 清空聊天记录
            chatMessages.innerHTML = '';
            
            // 添加欢迎消息（带动画）
            addMessage('您好！我是您的AI助手，有什么可以帮您的吗？', 'bot', false, 'animate__bounceIn');
            
            // 切换品牌颜色
            const newColor = brandColors[Math.floor(Math.random() * brandColors.length)];
            document.documentElement.style.setProperty('--primary-color', newColor);
            
            // 更新渐变色
            const lighterNewColor = adjustColorBrightness(newColor, 20);
            document.documentElement.style.setProperty('--primary-gradient', 
                `linear-gradient(135deg, ${newColor} 0%, ${lighterNewColor} 100%)`);
        }, 500);
    }

    // 输入框动画
    messageInput.addEventListener('focus', () => {
        document.querySelector('.chat-input').classList.add('input-active');
    });
    
    messageInput.addEventListener('blur', () => {
        document.querySelector('.chat-input').classList.remove('input-active');
    });
    
    // 辅助函数：调整颜色亮度
    function adjustColorBrightness(hex, percent) {
        // 将十六进制颜色转换为RGB
        let r = parseInt(hex.substring(1, 3), 16);
        let g = parseInt(hex.substring(3, 5), 16);
        let b = parseInt(hex.substring(5, 7), 16);

        // 调整亮度
        r = Math.min(255, r + (percent / 100) * 255);
        g = Math.min(255, g + (percent / 100) * 255);
        b = Math.min(255, b + (percent / 100) * 255);

        // 转换回十六进制
        return "#" + ((1 << 24) + (Math.round(r) << 16) + (Math.round(g) << 8) + Math.round(b)).toString(16).slice(1);
    }
    
    // 更新历史列表（模拟功能）
    function updateHistoryList(question) {
        // 创建新的历史项
        const historyList = document.querySelector('.history-list');
        if (historyList) {
            const newItem = document.createElement('div');
            newItem.className = 'history-item animate__animated animate__fadeIn';
            
            // 截取问题作为标题
            const title = question.length > 20 ? question.substring(0, 20) + '...' : question;
            
            newItem.innerHTML = `
                <div class="history-title">${title}</div>
                <div class="history-date">刚刚</div>
            `;
            
            // 添加到列表顶部
            historyList.prepend(newItem);
            
            // 如果列表项超过5个，删除最旧的
            if (historyList.children.length > 5) {
                historyList.removeChild(historyList.lastChild);
            }
            
            // 添加点击事件
            newItem.addEventListener('click', () => {
                // 这里是模拟功能，实际应用中应该加载对应的对话
                startNewChat();
            });
        }
    }
    
    // 侧边栏交互
    featureItems.forEach(item => {
        item.addEventListener('click', () => {
            // 移除所有激活状态
            featureItems.forEach(fi => fi.classList.remove('active'));
            // 添加当前项的激活状态
            item.classList.add('active');
            
            // 这里可以添加切换功能的逻辑
            // 由于这是演示，目前仅支持聊天功能
            if (!item.querySelector('i').classList.contains('fa-comment-dots')) {
                addMessage('该功能正在开发中，敬请期待！', 'bot', false, 'animate__bounceIn');
            }
        });
    });
    
    // 清空历史
    if (clearHistoryBtn) {
        clearHistoryBtn.addEventListener('click', () => {
            const historyList = document.querySelector('.history-list');
            if (historyList) {
                // 添加淡出动画
                const items = historyList.querySelectorAll('.history-item');
                items.forEach(item => {
                    item.classList.add('animate__fadeOut');
                });
                
                // 等待动画完成后清空
                setTimeout(() => {
                    historyList.innerHTML = '';
                }, 500);
            }
        });
    }
    
    // 深色模式切换
    if (themeToggle) {
        themeToggle.addEventListener('change', () => {
            document.body.classList.toggle('dark-mode');
            
            // 这里可以添加完整的深色模式逻辑
            if (themeToggle.checked) {
                // 切换到深色模式
                document.documentElement.style.setProperty('--sidebar-bg', '#1a1a2e');
                document.documentElement.style.setProperty('--sidebar-hover', '#2a2a42');
                document.documentElement.style.setProperty('--sidebar-active', '#32324e');
                document.documentElement.style.setProperty('--text-color', '#e1e1e1');
                document.documentElement.style.setProperty('--secondary-color', '#2a2a3a');
                document.body.style.backgroundColor = '#151521';
                document.body.style.backgroundImage = 'radial-gradient(circle at top right, #252535 0%, #151525 100%)';
            } else {
                // 切换到浅色模式
                document.documentElement.style.setProperty('--sidebar-bg', '#fff');
                document.documentElement.style.setProperty('--sidebar-hover', '#f5f7ff');
                document.documentElement.style.setProperty('--sidebar-active', '#eef0fd');
                document.documentElement.style.setProperty('--text-color', '#333');
                document.documentElement.style.setProperty('--secondary-color', '#f8f9fa');
                document.body.style.backgroundColor = '#f5f7fa';
                document.body.style.backgroundImage = 'radial-gradient(circle at top right, #e6e9f0 0%, #eef1f5 100%)';
            }
        });
    }

    // 事件监听器
    sendBtn.addEventListener('click', sendMessage);
    
    messageInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    newChatBtn.addEventListener('click', startNewChat);
    
    // 初始化页面动画
    document.querySelectorAll('.sidebar').forEach(sidebar => {
        sidebar.classList.add('animate__animated', 'animate__fadeInLeft');
    });
}); 
document.addEventListener('DOMContentLoaded', () => {
    // 获取DOM元素
    const featureItems = document.querySelectorAll('.feature-item');
    const chatFeature = document.getElementById('chatFeature');
    const vacationFeature = document.getElementById('vacationFeature');
    
    const destination = document.getElementById('destination');
    const peopleCount = document.getElementById('peopleCount');
    const duration = document.getElementById('duration');
    const budget = document.getElementById('budget');
    const travelDate = document.getElementById('travelDate');
    const interests = document.getElementById('interests');
    const generatePlanBtn = document.getElementById('generatePlanBtn');
    const newVacationBtn = document.getElementById('newVacation');
    const backToFormBtn = document.getElementById('backToForm');
    
    const vacationForm = document.querySelector('.vacation-form');
    const vacationResult = document.querySelector('.vacation-result');
    const loadingIndicator = document.querySelector('.loading-indicator');
    const planResult = document.getElementById('planResult');
    
    // 切换功能界面
    featureItems.forEach(item => {
        item.addEventListener('click', () => {
            const feature = item.getAttribute('data-feature');
            
            // 更新活动状态
            featureItems.forEach(fi => fi.classList.remove('active'));
            item.classList.add('active');
            
            // 显示相应功能界面
            if (feature === 'chat') {
                chatFeature.style.display = 'flex';
                vacationFeature.style.display = 'none';
            } else if (feature === 'vacation') {
                chatFeature.style.display = 'none';
                vacationFeature.style.display = 'flex';
            } else {
                // 其他未实现的功能
                chatFeature.style.display = 'flex';
                vacationFeature.style.display = 'none';
                
                // 显示提示
                const chatMessages = document.getElementById('chatMessages');
                const messageDiv = document.createElement('div');
                messageDiv.className = 'message bot animate__animated animate__fadeIn';
                
                const contentDiv = document.createElement('div');
                contentDiv.className = 'message-content';
                contentDiv.textContent = '该功能正在开发中，敬请期待！';
                
                messageDiv.appendChild(contentDiv);
                chatMessages.appendChild(messageDiv);
                chatMessages.scrollTop = chatMessages.scrollHeight;
            }
        });
    });
    
    // 生成出行计划
    generatePlanBtn.addEventListener('click', async () => {
        // 表单验证
        if (!destination.value.trim()) {
            alert('请输入目的地');
            destination.focus();
            return;
        }
        
        if (!peopleCount.value || peopleCount.value < 1) {
            alert('请输入有效的人数');
            peopleCount.focus();
            return;
        }
        
        if (!duration.value || duration.value < 1) {
            alert('请输入有效的出行天数');
            duration.focus();
            return;
        }
        
        if (!budget.value.trim()) {
            alert('请输入预算范围');
            budget.focus();
            return;
        }
        
        // 显示加载界面
        vacationForm.style.display = 'none';
        vacationResult.style.display = 'block';
        loadingIndicator.style.display = 'flex';
        planResult.innerHTML = '';
        
        try {
            // 准备请求数据
            const requestData = {
                destination: destination.value.trim(),
                peopleCount: parseInt(peopleCount.value),
                duration: parseInt(duration.value),
                budget: budget.value.trim(),
                travelDate: travelDate.value.trim() || null,
                interests: interests.value.trim() || null
            };
            
            // 发送API请求
            const response = await fetch('/vacation/plan', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestData)
            });
            
            if (!response.ok) {
                throw new Error('网络请求失败');
            }
            
            // 获取响应文本
            const result = await response.text();
            
            // 显示结果
            loadingIndicator.style.display = 'none';
            planResult.innerHTML = formatPlanResult(result);
            
        } catch (error) {
            loadingIndicator.style.display = 'none';
            planResult.innerHTML = `<div class="error-message">
                <p><strong>抱歉，生成计划时出错：</strong></p>
                <p>${error.message}</p>
                <p>请稍后重试或联系客服</p>
            </div>`;
        }
    });
    
    // 返回表单
    backToFormBtn.addEventListener('click', () => {
        vacationForm.style.display = 'block';
        vacationResult.style.display = 'none';
    });
    
    // 清空表单
    newVacationBtn.addEventListener('click', () => {
        destination.value = '';
        peopleCount.value = '2';
        duration.value = '3';
        budget.value = '';
        travelDate.value = '';
        interests.value = '';
        
        // 如果当前在结果页，返回表单页
        vacationForm.style.display = 'block';
        vacationResult.style.display = 'none';
        
        destination.focus();
    });
    
    // 格式化出行计划结果
    function formatPlanResult(text) {
        // 简单的Markdown转HTML处理
        let html = text
            .replace(/\n\n/g, '<br><br>')
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>')
            .replace(/#{3} (.*?)\n/g, '<h3>$1</h3>')
            .replace(/#{2} (.*?)\n/g, '<h2>$1</h2>')
            .replace(/#{1} (.*?)\n/g, '<h1>$1</h1>');
        
        // 处理无序列表
        const listRegex = /- (.*?)(?=\n- |\n\n|$)/gs;
        html = html.replace(listRegex, (match, item) => {
            return `<li>${item.trim()}</li>`;
        });
        html = html.replace(/<li>(.*?)<\/li>(?:\s*<li>)/g, '<ul><li>$1</li><li>');
        html = html.replace(/<li>(.*?)<\/li>(?!\s*<li>)/g, '<li>$1</li></ul>');
        
        return html;
    }
}); 
package com.liu.ai.service.impl;

import com.liu.ai.model.VacationRequest;
import com.liu.ai.service.VacationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VacationServiceImpl implements VacationService {
    private static final Logger logger = LoggerFactory.getLogger(VacationServiceImpl.class);

    private final ChatClient vacationClient;
    
    public VacationServiceImpl(ChatClient vacationClient) {
        this.vacationClient = vacationClient;
    }

    @Override
    public String generateVacationPlan(VacationRequest request) {
        // 构建系统提示词
        String systemPromptText = String.format(
                "你是一个专业的出行计划顾问。请根据用户提供的信息，制定详细的旅游计划。\n" +
                "当前用户信息：目的地%s，出行人数%d人，游玩天数%d天，预算%s。" +
                (request.getInterests() != null ? "兴趣爱好：" + request.getInterests() + "。" : "") +
                (request.getTravelDate() != null ? "出行日期：" + request.getTravelDate() + "。" : "") +
                "\n请提供完整的行程安排，包括:\n" +
                "1. 景点推荐和游玩顺序\n" +
                "2. 住宿建议\n" +
                "3. 餐饮推荐\n" +
                "4. 交通安排\n" +
                "5. 每日具体行程安排",
                request.getDestination(), 
                request.getPeopleCount(), 
                request.getDuration(), 
                request.getBudget()
        );

        // 构建用户提示词
        String userPromptText = String.format(
                "请为我制定一份去%s的%d天旅游计划，我们一共%d人，预算%s。" +
                (request.getInterests() != null ? "我们的兴趣是：" + request.getInterests() + "。" : "") +
                (request.getTravelDate() != null ? "我们计划在" + request.getTravelDate() + "出发。" : ""),
                request.getDestination(),
                request.getDuration(),
                request.getPeopleCount(),
                request.getBudget()
        );

        try {
            // 发送请求并获取响应 - 使用Spring AI 1.0.0兼容的API
            return vacationClient.prompt()
                .advisors(new SimpleLoggerAdvisor())
                .user(userPromptText)
                .system(systemPromptText)
                .call()
                .content();
        } catch (Exception e) {
            logger.error("生成出行计划失败", e);
            return "很抱歉，生成出行计划时出错: " + e.getMessage();
        }
    }
} 
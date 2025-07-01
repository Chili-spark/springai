package com.liu.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VacationChatConfig {
    private final ChatClient vacationClient;
    public VacationChatConfig(ChatClient.Builder builder ) {
        this.vacationClient = builder
                .defaultSystem("你是一个专业的出行计划安排者，你需要根据用户输入的人数，地点，预算，天数信息提供专业可靠的出行计划")
                .build();
    }

    @Bean
    public ChatClient vacationClient() {
        return this.vacationClient;
    }
}

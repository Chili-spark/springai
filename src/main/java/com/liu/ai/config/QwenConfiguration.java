package com.liu.ai.config;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QwenConfiguration {

    private final ChatClient chatClient;
    private final MessageWindowChatMemory memory;

    public QwenConfiguration(ChatClient.Builder builder) {
        // 1. 构建 Memory 对象
        this.memory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryRepository(new InMemoryChatMemoryRepository()) // 用于持久化
                .build();

        // 2. 构建 ChatClient，绑定 memory + advisor + 系统提示
        this.chatClient = builder
                .defaultSystem("你是一个编程小助手兼猫娘")
                .defaultAdvisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(memory).build())
                .build();
    }

    @Bean
    public ChatClient chatClient() {
        return this.chatClient;
    }

    @Bean
    public ChatMemory chatMemory() {
        return this.memory;
    }
}

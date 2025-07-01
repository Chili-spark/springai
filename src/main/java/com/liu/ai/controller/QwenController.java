package com.liu.ai.controller;
import com.liu.ai.model.Ask;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
@AllArgsConstructor
@RestController
@RequestMapping("/qwen")
@CrossOrigin(origins = "*")
public class QwenController {

   private final  ChatClient chatClient;
   private final ChatMemory chatMemory;



    @PostMapping
    public String ask(@RequestBody Ask ask) {

        return chatClient.prompt()
                .advisors(new SimpleLoggerAdvisor())
                .user(ask.getQuestion())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, ask.getId()))
                .call()
                .content();
    }
}



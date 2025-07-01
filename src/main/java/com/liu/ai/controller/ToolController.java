package com.liu.ai.controller;

import com.liu.ai.service.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tool")
@CrossOrigin(origins = "*")
public class ToolController {

    private final ChatClient chatClient;
    private final WeatherService weatherService;
    private final CurrencyService currencyService;
    private final SearchService searchService;
    private final SchedulerService schedulerService;

    public ToolController(ChatClient.Builder chatClientBuilder,
                          WeatherService weatherService,
                          CurrencyService currencyService,
                          SearchService searchService,
                          SchedulerService schedulerService) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.weatherService = weatherService;
        this.currencyService = currencyService;
        this.searchService = searchService;
        this.schedulerService = schedulerService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
//        String systemMessage = """
//                你是一个智能助手，能够回答用户的各种问题。
//                如果用户询问天气信息，请使用天气工具获取实时天气数据。
//                如果用户询问货币转换，请使用货币转换工具进行计算。
//                如果用户询问需要搜索的信息，请使用搜索工具获取相关数据。
//                如果用户需要管理日程安排，请使用日程安排工具进行操作。
//                请尽可能详细地回答用户问题，并根据需要使用适当的工具。
//                """;
//
//        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemMessage);
        
        ChatResponse chatResponse = chatClient.prompt()
                .system("你是一个智能助手，能够回答用户的各种问题。\n" +
                        "                如果用户询问天气信息，请使用天气工具获取实时天气数据。\n" +
                        "                如果用户询问货币转换，请使用货币转换工具进行计算。\n" +
                        "                如果用户询问需要搜索的信息，请使用搜索工具获取相关数据。\n" +
                        "                如果用户需要管理日程安排，请使用日程安排工具进行操作。\n" +
                        "                请尽可能详细地回答用户问题，并根据需要使用适当的工具。")
                .user(message)
                .tools(weatherService, currencyService, searchService, schedulerService) // 注册所有工具
                .call()
                .chatResponse();
        
        return chatResponse.getResult().getOutput().getText();
    }
    
    @GetMapping("/weather/{city}")
    public String getWeather(@PathVariable String city) {
        String message = "请告诉我" + city + "的天气情况";
        
        return chatClient.prompt()
                .user(message)
                .tools(weatherService) // 只注册天气工具
                .call()
                .content();
    }
    
    @GetMapping("/currency")
    public String convertCurrency(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam double amount) {
        
        String message = String.format("请将%s %s转换为%s", amount, from, to);
        
        return chatClient.prompt()
                .user(message)
                .tools(currencyService) // 只注册货币转换工具
                .call()
                .content();
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String query) {
        String message = "请搜索关于 " + query + " 的信息";
        
        return chatClient.prompt()
                .user(message)
                .tools(searchService) // 只注册搜索工具
                .call()
                .content();
    }
    
    @GetMapping("/schedule")
    public String schedule(@RequestParam String action, @RequestParam(required = false) String details) {
        String message;
        
        switch (action.toLowerCase()) {
            case "list":
                message = "列出我所有的日程安排";
                break;
            case "add":
                message = "添加一个新的日程：" + details;
                break;
            case "search":
                message = "查找包含关键词 '" + details + "' 的日程";
                break;
            case "date":
                message = "查询" + details + "的日程安排";
                break;
            default:
                message = "请帮我管理我的日程安排";
        }
        
        return chatClient.prompt()
                .user(message)
                .tools(schedulerService) // 只注册日程工具
                .call()
                .content();
    }
    
    @GetMapping("/multi-tool")
    public String multiTool(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .tools(weatherService, currencyService, searchService, schedulerService) // 注册所有工具
                .call()
                .content();
    }
    
    @GetMapping("/personal-assistant")
    public String personalAssistant(@RequestParam String message) {
        String systemMessage = """
                你是一个个人助理，可以帮助用户处理各种日常任务，包括：
                1. 查询天气信息
                2. 进行货币转换
                3. 搜索互联网信息
                4. 管理日程安排
                
                请根据用户的需求，使用适当的工具来完成任务。
                回答要简洁、专业，并且要尽可能满足用户的需求。
                """;
        
        return chatClient.prompt()
                .system(systemMessage)
                .user(message)
                .tools(weatherService, currencyService, searchService, schedulerService)
                .call()
                .content();
    }
} 
package com.liu.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    
    // 模拟搜索结果数据库
    private final Map<String, List<SearchResult>> searchDatabase = new HashMap<>();
    
    public SearchService() {
        // 初始化一些模拟搜索数据
        searchDatabase.put("java", Arrays.asList(
            new SearchResult("Java编程语言介绍", 
                "Java是一种广泛使用的计算机编程语言，拥有跨平台、面向对象、泛型编程的特性。",
                "https://example.com/java-intro"),
            new SearchResult("Java Spring框架教程", 
                "Spring框架是Java平台上的开源应用框架，用于简化企业级应用开发。",
                "https://example.com/spring-tutorial"),
            new SearchResult("Java与人工智能", 
                "Java在人工智能领域的应用越来越广泛，包括机器学习库和框架的开发。",
                "https://example.com/java-ai")
        ));
        
        searchDatabase.put("python", Arrays.asList(
            new SearchResult("Python编程入门", 
                "Python是一种解释型、高级编程语言，以其简洁、易读的语法而闻名。",
                "https://example.com/python-intro"),
            new SearchResult("Python数据分析", 
                "Python在数据分析领域非常流行，拥有pandas、numpy等强大的库。",
                "https://example.com/python-data-analysis"),
            new SearchResult("Python机器学习", 
                "Python是机器学习和人工智能领域的首选语言，有TensorFlow、PyTorch等框架。",
                "https://example.com/python-ml")
        ));
        
        searchDatabase.put("ai", Arrays.asList(
            new SearchResult("人工智能简介", 
                "人工智能(AI)是计算机科学的一个分支，致力于创建能够模拟人类智能的系统。",
                "https://example.com/ai-intro"),
            new SearchResult("机器学习基础", 
                "机器学习是人工智能的一个子领域，专注于开发能从数据中学习的算法。",
                "https://example.com/ml-basics"),
            new SearchResult("深度学习技术", 
                "深度学习是机器学习的一个分支，使用神经网络处理复杂的数据模式。",
                "https://example.com/deep-learning")
        ));
    }
    
    @Tool(description = "搜索互联网获取信息，返回相关的搜索结果")
    public SearchResponse search(@ToolParam(description = "搜索关键词") String query, 
                              @ToolParam(description = "最大结果数量", required = false) Integer maxResults) {
        
        logger.info("正在搜索关键词: {}, 最大结果数: {}", query, maxResults);
        
        // 默认最多返回3条结果
        int limit = (maxResults != null && maxResults > 0) ? Math.min(maxResults, 5) : 3;
        
        // 查找匹配的关键词
        List<SearchResult> results = new ArrayList<>();
        for (Map.Entry<String, List<SearchResult>> entry : searchDatabase.entrySet()) {
            if (query.toLowerCase().contains(entry.getKey()) || 
                entry.getKey().contains(query.toLowerCase())) {
                results.addAll(entry.getValue());
            }
        }
        
        // 如果没有找到结果，返回一个通用消息
        if (results.isEmpty()) {
            return new SearchResponse(
                query, 
                Collections.singletonList(new SearchResult(
                    "没有找到相关结果", 
                    "尝试使用不同的关键词进行搜索。", 
                    "https://example.com/search")),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
        }
        
        // 限制结果数量
        if (results.size() > limit) {
            results = results.subList(0, limit);
        }
        
        return new SearchResponse(
            query, 
            results,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
    
    public record SearchResult(String title, String snippet, String url) {}
    
    public record SearchResponse(String query, List<SearchResult> results, String searchTime) {}
} 
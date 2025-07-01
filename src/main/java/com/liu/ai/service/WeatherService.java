package com.liu.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    
    private final RestClient restClient;
    
    public WeatherService() {
        this.restClient = RestClient.builder().build();
    }
    
    @Tool(description = "获取指定城市的天气信息，包括温度、湿度和天气状况")
    public WeatherInfo getWeather(@ToolParam(description = "城市名称，例如：北京、上海、广州") String city) {
        logger.info("正在查询城市[{}]的天气信息", city);
        
        // 这里使用模拟数据，实际应用中可以调用真实的天气API
        return switch (city.toLowerCase()) {
            case "北京" -> new WeatherInfo(city, 20, 45, "晴天");
            case "上海" -> new WeatherInfo(city, 22, 60, "多云");
            case "广州" -> new WeatherInfo(city, 28, 70, "小雨");
            default -> new WeatherInfo(city, 25, 50, "未知");
        };
    }
    
    public record WeatherInfo(String city, int temperature, int humidity, String condition) {
        @Override
        public String toString() {
            return String.format("%s的天气：%d°C，湿度%d%%，%s", city, temperature, humidity, condition);
        }
    }
} 
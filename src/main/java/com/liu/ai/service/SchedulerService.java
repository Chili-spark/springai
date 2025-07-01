package com.liu.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class SchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);
    
    // 用于存储日程的内存数据库
    private final Map<Long, ScheduleEvent> scheduleDatabase = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public SchedulerService() {
        // 添加一些示例日程
        addScheduleEvent("会议", "与产品团队讨论新功能", "2025-05-20 14:00", "2025-05-20 15:30");
        addScheduleEvent("午餐", "与客户共进午餐", "2025-05-21 12:00", "2025-05-21 13:30");
        addScheduleEvent("演示", "新产品演示", "2025-05-22 10:00", "2025-05-22 11:00");
    }
    
    @Tool(description = "添加新的日程安排")
    public ScheduleEvent addScheduleEvent(
            @ToolParam(description = "日程标题") String title,
            @ToolParam(description = "日程详细描述", required = false) String description,
            @ToolParam(description = "开始时间，格式：yyyy-MM-dd HH:mm") String startTime,
            @ToolParam(description = "结束时间，格式：yyyy-MM-dd HH:mm") String endTime) {
        
        logger.info("添加日程：{}, 开始时间：{}, 结束时间：{}", title, startTime, endTime);
        
        try {
            LocalDateTime start = LocalDateTime.parse(startTime, DATETIME_FORMATTER);
            LocalDateTime end = LocalDateTime.parse(endTime, DATETIME_FORMATTER);
            
            if (end.isBefore(start)) {
                throw new IllegalArgumentException("结束时间不能早于开始时间");
            }
            
            long id = idGenerator.getAndIncrement();
            ScheduleEvent event = new ScheduleEvent(
                id, 
                title, 
                description != null ? description : "", 
                start, 
                end
            );
            
            scheduleDatabase.put(id, event);
            return event;
            
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期时间格式错误，请使用格式：yyyy-MM-dd HH:mm");
        }
    }
    
    @Tool(description = "查询指定日期的所有日程安排")
    public List<ScheduleEvent> getScheduleByDate(@ToolParam(description = "日期，格式：yyyy-MM-dd") String date) {
        logger.info("查询日期{}的日程", date);
        
        try {
            LocalDate targetDate = LocalDate.parse(date, DATE_FORMATTER);
            
            return scheduleDatabase.values().stream()
                .filter(event -> {
                    LocalDate eventDate = event.startTime().toLocalDate();
                    return eventDate.equals(targetDate);
                })
                .sorted(Comparator.comparing(ScheduleEvent::startTime))
                .collect(Collectors.toList());
                
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期格式错误，请使用格式：yyyy-MM-dd");
        }
    }
    
    @Tool(description = "查询所有日程安排")
    public List<ScheduleEvent> getAllSchedules() {
        logger.info("查询所有日程");
        
        return scheduleDatabase.values().stream()
            .sorted(Comparator.comparing(ScheduleEvent::startTime))
            .collect(Collectors.toList());
    }
    
    @Tool(description = "删除指定ID的日程安排")
    public ScheduleOperationResult deleteSchedule(@ToolParam(description = "日程ID") long id) {
        logger.info("删除日程ID: {}", id);
        
        ScheduleEvent removed = scheduleDatabase.remove(id);
        
        if (removed != null) {
            return new ScheduleOperationResult(true, "成功删除日程：" + removed.title());
        } else {
            return new ScheduleOperationResult(false, "未找到ID为" + id + "的日程");
        }
    }
    
    @Tool(description = "查找包含指定关键词的日程安排")
    public List<ScheduleEvent> searchSchedules(@ToolParam(description = "搜索关键词") String keyword) {
        logger.info("搜索包含关键词'{}'的日程", keyword);
        
        String lowerKeyword = keyword.toLowerCase();
        
        return scheduleDatabase.values().stream()
            .filter(event -> 
                event.title().toLowerCase().contains(lowerKeyword) || 
                event.description().toLowerCase().contains(lowerKeyword))
            .sorted(Comparator.comparing(ScheduleEvent::startTime))
            .collect(Collectors.toList());
    }
    
    // 日程事件记录
    public record ScheduleEvent(
        long id, 
        String title, 
        String description, 
        LocalDateTime startTime, 
        LocalDateTime endTime
    ) {
        @Override
        public String toString() {
            return String.format("ID: %d, %s (%s - %s): %s", 
                id, 
                title, 
                startTime.format(DATETIME_FORMATTER),
                endTime.format(DATETIME_FORMATTER),
                description);
        }
    }
    
    // 操作结果记录
    public record ScheduleOperationResult(boolean success, String message) {}
} 
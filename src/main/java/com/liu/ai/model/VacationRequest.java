package com.liu.ai.model;

import lombok.Data;

@Data
public class VacationRequest {
    private String destination; // 目的地
    private int peopleCount;    // 人数
    private int duration;       // 天数
    private String budget;      // 预算
    private String interests;   // 兴趣爱好（可选）
    private String travelDate;  // 出行日期（可选）
} 
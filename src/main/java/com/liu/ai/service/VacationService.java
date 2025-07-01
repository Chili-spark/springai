package com.liu.ai.service;

import com.liu.ai.model.VacationRequest;

public interface VacationService {
    /**
     * 生成出行计划
     * @param request 出行请求信息
     * @return 出行计划文本
     */
    String generateVacationPlan(VacationRequest request);
} 
package com.liu.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    
    // 模拟汇率数据，实际应用中可以从外部API获取
    private final Map<String, BigDecimal> exchangeRates = new HashMap<>();
    
    public CurrencyService() {
        // 初始化汇率数据（相对于CNY人民币）
        exchangeRates.put("CNY", BigDecimal.ONE);
        exchangeRates.put("USD", new BigDecimal("0.14"));
        exchangeRates.put("EUR", new BigDecimal("0.13"));
        exchangeRates.put("JPY", new BigDecimal("20.84"));
        exchangeRates.put("GBP", new BigDecimal("0.11"));
    }
    
    @Tool(description = "将一种货币转换为另一种货币，支持CNY、USD、EUR、JPY、GBP")
    public CurrencyConversionResult convertCurrency(
            @ToolParam(description = "源货币代码，如CNY、USD、EUR") String fromCurrency,
            @ToolParam(description = "目标货币代码，如CNY、USD、EUR") String toCurrency,
            @ToolParam(description = "要转换的金额") BigDecimal amount) {
        
        logger.info("正在将{}{}转换为{}", amount, fromCurrency, toCurrency);
        
        // 将两种货币都转换为大写以便查找
        fromCurrency = fromCurrency.toUpperCase();
        toCurrency = toCurrency.toUpperCase();
        
        // 检查货币代码是否有效
        if (!exchangeRates.containsKey(fromCurrency) || !exchangeRates.containsKey(toCurrency)) {
            throw new IllegalArgumentException("不支持的货币代码: " + 
                    (!exchangeRates.containsKey(fromCurrency) ? fromCurrency : toCurrency));
        }
        
        // 将源货币转换为CNY
        BigDecimal amountInCNY = amount.divide(exchangeRates.get(fromCurrency), 6, RoundingMode.HALF_UP);
        
        // 将CNY转换为目标货币
        BigDecimal result = amountInCNY.multiply(exchangeRates.get(toCurrency))
                .setScale(2, RoundingMode.HALF_UP);
        
        return new CurrencyConversionResult(fromCurrency, toCurrency, amount, result);
    }
    
    public record CurrencyConversionResult(String fromCurrency, String toCurrency, 
                                          BigDecimal originalAmount, BigDecimal convertedAmount) {
        @Override
        public String toString() {
            return String.format("%s %s = %s %s", 
                    originalAmount, fromCurrency, convertedAmount, toCurrency);
        }
    }
} 
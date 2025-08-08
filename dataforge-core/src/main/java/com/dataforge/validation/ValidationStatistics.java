package com.dataforge.validation;

import java.util.Map;

/**
 * 验证统计信息
 */
public class ValidationStatistics {
    
    private final int totalCount;
    private final int validCount;
    private final int invalidCount;
    private final Map<String, Integer> ruleViolationCounts;
    private final Map<String, Integer> fieldViolationCounts;
    
    public ValidationStatistics(int totalCount, int validCount, int invalidCount,
                               Map<String, Integer> ruleViolationCounts,
                               Map<String, Integer> fieldViolationCounts) {
        this.totalCount = totalCount;
        this.validCount = validCount;
        this.invalidCount = invalidCount;
        this.ruleViolationCounts = ruleViolationCounts;
        this.fieldViolationCounts = fieldViolationCounts;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public int getValidCount() {
        return validCount;
    }
    
    public int getInvalidCount() {
        return invalidCount;
    }
    
    public double getValidRate() {
        return totalCount == 0 ? 0.0 : (double) validCount / totalCount;
    }
    
    public double getInvalidRate() {
        return 1.0 - getValidRate();
    }
    
    public Map<String, Integer> getRuleViolationCounts() {
        return ruleViolationCounts;
    }
    
    public Map<String, Integer> getFieldViolationCounts() {
        return fieldViolationCounts;
    }
    
    /**
     * 获取最常见的违规规则
     */
    public String getMostViolatedRule() {
        return ruleViolationCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    /**
     * 获取问题最多的字段
     */
    public String getMostProblematicField() {
        return fieldViolationCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
}
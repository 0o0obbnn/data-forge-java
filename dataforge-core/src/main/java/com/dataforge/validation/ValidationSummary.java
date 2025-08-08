package com.dataforge.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量验证汇总报告
 */
public class ValidationSummary {
    
    private int validCount = 0;
    private final Map<Integer, ValidationReport> invalidRecords = new HashMap<>();
    
    /**
     * 增加有效记录数
     */
    public void incrementValidCount() {
        validCount++;
    }
    
    /**
     * 添加无效记录
     */
    public void addInvalidRecord(int recordIndex, ValidationReport report) {
        invalidRecords.put(recordIndex, report);
    }
    
    /**
     * 获取总记录数
     */
    public int getTotalCount() {
        return validCount + invalidRecords.size();
    }
    
    /**
     * 获取有效记录数
     */
    public int getValidCount() {
        return validCount;
    }
    
    /**
     * 获取无效记录数
     */
    public int getInvalidCount() {
        return invalidRecords.size();
    }
    
    /**
     * 获取有效率
     */
    public double getValidRate() {
        int total = getTotalCount();
        return total == 0 ? 0.0 : (double) validCount / total;
    }
    
    /**
     * 是否全部验证通过
     */
    public boolean isAllValid() {
        return invalidRecords.isEmpty();
    }
    
    /**
     * 获取无效记录的详细信息
     */
    public Map<Integer, ValidationReport> getInvalidRecords() {
        return new HashMap<>(invalidRecords);
    }
    
    /**
     * 生成汇总统计信息
     */
    public ValidationStatistics getStatistics() {
        Map<String, Integer> ruleViolationCounts = new HashMap<>();
        Map<String, Integer> fieldViolationCounts = new HashMap<>();
        
        for (ValidationReport report : invalidRecords.values()) {
            for (ValidationViolation violation : report.getViolations()) {
                String ruleName = violation.getRuleName();
                String fieldName = violation.getFieldName();
                
                ruleViolationCounts.put(ruleName, 
                    ruleViolationCounts.getOrDefault(ruleName, 0) + 1);
                fieldViolationCounts.put(fieldName, 
                    fieldViolationCounts.getOrDefault(fieldName, 0) + 1);
            }
        }
        
        return new ValidationStatistics(
            getTotalCount(), validCount, getInvalidCount(),
            ruleViolationCounts, fieldViolationCounts
        );
    }
    
    /**
     * 生成文本报告
     */
    public String generateTextReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("=== 数据验证汇总报告 ===\n");
        report.append(String.format("总记录数: %d\n", getTotalCount()));
        report.append(String.format("有效记录: %d (%.2f%%)\n", validCount, getValidRate() * 100));
        report.append(String.format("无效记录: %d (%.2f%%)\n", getInvalidCount(), (1 - getValidRate()) * 100));
        report.append("\n");
        
        if (!invalidRecords.isEmpty()) {
            report.append("=== 无效记录详情 ===\n");
            
            for (Map.Entry<Integer, ValidationReport> entry : invalidRecords.entrySet()) {
                int recordIndex = entry.getKey();
                ValidationReport recordReport = entry.getValue();
                
                report.append(String.format("记录 #%d (%d个问题):\n", 
                    recordIndex + 1, recordReport.getViolationCount()));
                
                for (ValidationViolation violation : recordReport.getViolations()) {
                    report.append(String.format("  - %s\n", violation.toString()));
                }
                report.append("\n");
            }
            
            // 统计信息
            ValidationStatistics stats = getStatistics();
            report.append("=== 问题统计 ===\n");
            report.append("按规则统计:\n");
            for (Map.Entry<String, Integer> entry : stats.getRuleViolationCounts().entrySet()) {
                report.append(String.format("  %s: %d次\n", entry.getKey(), entry.getValue()));
            }
            
            report.append("\n按字段统计:\n");
            for (Map.Entry<String, Integer> entry : stats.getFieldViolationCounts().entrySet()) {
                report.append(String.format("  %s: %d次\n", entry.getKey(), entry.getValue()));
            }
        }
        
        return report.toString();
    }
    
    @Override
    public String toString() {
        return generateTextReport();
    }
}
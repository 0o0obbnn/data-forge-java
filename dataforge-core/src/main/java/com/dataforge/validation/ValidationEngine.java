package com.dataforge.validation;

import java.util.*;
import java.util.function.Predicate;

/**
 * 数据验证规则引擎
 * 支持自定义验证规则和批量数据验证
 */
public class ValidationEngine {
    
    private final Map<String, List<ValidationRule>> fieldRules = new HashMap<>();
    private final List<ValidationRule> globalRules = new ArrayList<>();
    
    /**
     * 验证结果类 - 简化版本用于ValidationEngine
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { 
            return valid; 
        }
        
        public String getMessage() { 
            return message; 
        }
        
        @Override
        public String toString() {
            return valid ? "Valid" : "Invalid: " + message;
        }
    }
    
    /**
     * 添加字段验证规则
     */
    public ValidationEngine addFieldRule(String fieldName, ValidationRule rule) {
        fieldRules.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(rule);
        return this;
    }
    
    /**
     * 添加全局验证规则
     */
    public ValidationEngine addGlobalRule(ValidationRule rule) {
        globalRules.add(rule);
        return this;
    }
    
    /**
     * 验证单条记录
     */
    public ValidationReport validateRecord(Map<String, Object> record) {
        ValidationReport report = new ValidationReport();
        
        // 验证字段规则
        for (Map.Entry<String, Object> entry : record.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            
            List<ValidationRule> rules = fieldRules.get(fieldName);
            if (rules != null) {
                for (ValidationRule rule : rules) {
                    ValidationResult result = rule.validate(fieldName, value, record);
                    if (!result.isValid()) {
                        report.addViolation(new ValidationViolation(
                            fieldName, value, rule.getRuleName(), result.getMessage()
                        ));
                    }
                }
            }
        }
        
        // 验证全局规则
        for (ValidationRule rule : globalRules) {
            ValidationResult result = rule.validate(null, record, record);
            if (!result.isValid()) {
                report.addViolation(new ValidationViolation(
                    "GLOBAL", record, rule.getRuleName(), result.getMessage()
                ));
            }
        }
        
        return report;
    }
    
    /**
     * 批量验证数据
     */
    public ValidationSummary validateBatch(List<Map<String, Object>> records) {
        ValidationSummary summary = new ValidationSummary();
        
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> record = records.get(i);
            ValidationReport report = validateRecord(record);
            
            if (!report.isValid()) {
                summary.addInvalidRecord(i, report);
            } else {
                summary.incrementValidCount();
            }
        }
        
        return summary;
    }
    
    /**
     * 创建预定义的验证引擎（包含常用规则）
     */
    public static ValidationEngine createDefault() {
        ValidationEngine engine = new ValidationEngine();
        
        // 邮箱验证规则
        engine.addFieldRule("email", new RegexValidationRule(
            "EMAIL_FORMAT", 
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            "邮箱格式不正确"
        ));
        
        // 手机号验证规则
        engine.addFieldRule("phone", new RegexValidationRule(
            "PHONE_FORMAT",
            "^1[3-9]\\d{9}$",
            "手机号格式不正确"
        ));
        
        // 身份证验证规则
        engine.addFieldRule("idcard", new RegexValidationRule(
            "IDCARD_FORMAT",
            "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$",
            "身份证号格式不正确"
        ));
        
        // 年龄范围验证规则
        engine.addFieldRule("age", new RangeValidationRule(
            "AGE_RANGE", 0, 150, "年龄必须在0-150之间"
        ));
        
        // 非空验证规则
        engine.addFieldRule("name", new NotNullValidationRule(
            "NAME_NOT_NULL", "姓名不能为空"
        ));
        
        return engine;
    }
    
    /**
     * 验证规则接口
     */
    public interface ValidationRule {
        ValidationResult validate(String fieldName, Object value, Map<String, Object> context);
        String getRuleName();
    }
    
    /**
     * 正则表达式验证规则
     */
    public static class RegexValidationRule implements ValidationRule {
        private final String ruleName;
        private final String regex;
        private final String errorMessage;
        
        public RegexValidationRule(String ruleName, String regex, String errorMessage) {
            this.ruleName = ruleName;
            this.regex = regex;
            this.errorMessage = errorMessage;
        }
        
        @Override
        public ValidationResult validate(String fieldName, Object value, Map<String, Object> context) {
            if (value == null) {
                return new ValidationResult(true, null);
            }
            
            String strValue = value.toString();
            boolean isValid = strValue.matches(regex);
            return new ValidationResult(isValid, isValid ? null : errorMessage);
        }
        
        @Override
        public String getRuleName() {
            return ruleName;
        }
    }
    
    /**
     * 范围验证规则
     */
    public static class RangeValidationRule implements ValidationRule {
        private final String ruleName;
        private final double minValue;
        private final double maxValue;
        private final String errorMessage;
        
        public RangeValidationRule(String ruleName, double minValue, double maxValue, String errorMessage) {
            this.ruleName = ruleName;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.errorMessage = errorMessage;
        }
        
        @Override
        public ValidationResult validate(String fieldName, Object value, Map<String, Object> context) {
            if (value == null) {
                return new ValidationResult(true, null);
            }
            
            try {
                double numValue = Double.parseDouble(value.toString());
                boolean isValid = numValue >= minValue && numValue <= maxValue;
                return new ValidationResult(isValid, isValid ? null : errorMessage);
            } catch (NumberFormatException e) {
                return new ValidationResult(false, "数值格式不正确");
            }
        }
        
        @Override
        public String getRuleName() {
            return ruleName;
        }
    }
    
    /**
     * 非空验证规则
     */
    public static class NotNullValidationRule implements ValidationRule {
        private final String ruleName;
        private final String errorMessage;
        
        public NotNullValidationRule(String ruleName, String errorMessage) {
            this.ruleName = ruleName;
            this.errorMessage = errorMessage;
        }
        
        @Override
        public ValidationResult validate(String fieldName, Object value, Map<String, Object> context) {
            boolean isValid = value != null && !value.toString().trim().isEmpty();
            return new ValidationResult(isValid, isValid ? null : errorMessage);
        }
        
        @Override
        public String getRuleName() {
            return ruleName;
        }
    }
    
    /**
     * 自定义谓词验证规则
     */
    public static class PredicateValidationRule implements ValidationRule {
        private final String ruleName;
        private final Predicate<Object> predicate;
        private final String errorMessage;
        
        public PredicateValidationRule(String ruleName, Predicate<Object> predicate, String errorMessage) {
            this.ruleName = ruleName;
            this.predicate = predicate;
            this.errorMessage = errorMessage;
        }
        
        @Override
        public ValidationResult validate(String fieldName, Object value, Map<String, Object> context) {
            boolean isValid = predicate.test(value);
            return new ValidationResult(isValid, isValid ? null : errorMessage);
        }
        
        @Override
        public String getRuleName() {
            return ruleName;
        }
    }
}
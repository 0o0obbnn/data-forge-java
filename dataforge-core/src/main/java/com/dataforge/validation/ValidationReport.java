package com.dataforge.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * 单条记录的验证报告
 */
public class ValidationReport {
    
    private final List<ValidationViolation> violations = new ArrayList<>();
    
    /**
     * 添加违规项
     */
    public void addViolation(ValidationViolation violation) {
        violations.add(violation);
    }
    
    /**
     * 是否验证通过
     */
    public boolean isValid() {
        return violations.isEmpty();
    }
    
    /**
     * 获取所有违规项
     */
    public List<ValidationViolation> getViolations() {
        return new ArrayList<>(violations);
    }
    
    /**
     * 获取违规数量
     */
    public int getViolationCount() {
        return violations.size();
    }
    
    /**
     * 生成文本报告
     */
    public String generateTextReport() {
        if (isValid()) {
            return "验证通过";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("验证失败，发现 ").append(violations.size()).append(" 个问题:\n");
        
        for (int i = 0; i < violations.size(); i++) {
            ValidationViolation violation = violations.get(i);
            report.append(String.format("  %d. [%s] %s - %s\n", 
                i + 1,
                violation.getFieldName(),
                violation.getRuleName(),
                violation.getMessage()
            ));
        }
        
        return report.toString();
    }
    
    @Override
    public String toString() {
        return generateTextReport();
    }
}
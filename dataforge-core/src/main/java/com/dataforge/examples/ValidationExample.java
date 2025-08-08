package com.dataforge.examples;

import com.dataforge.validation.*;
import java.util.*;

/**
 * 数据验证系统演示
 */
public class ValidationExample {
    
    public static void main(String[] args) {
        System.out.println("=== DataForge 数据验证系统演示 ===\n");
        
        // 创建默认验证引擎
        ValidationEngine engine = ValidationEngine.createDefault();
        
        // 添加自定义验证规则
        engine.addFieldRule("salary", new ValidationEngine.RangeValidationRule(
            "SALARY_RANGE", 3000, 100000, "薪资必须在3000-100000之间"
        ));
        
        // 创建测试数据
        List<Map<String, Object>> testData = createTestData();
        
        System.out.println("测试数据 (" + testData.size() + " 条记录):");
        for (int i = 0; i < testData.size(); i++) {
            System.out.println("  记录 " + (i + 1) + ": " + testData.get(i));
        }
        System.out.println();
        
        // 批量验证
        ValidationSummary summary = engine.validateBatch(testData);
        
        // 输出验证结果
        System.out.println(summary.generateTextReport());
        
        // 演示单条记录验证
        System.out.println("\n=== 单条记录验证演示 ===");
        Map<String, Object> singleRecord = new HashMap<>();
        singleRecord.put("name", "张三");
        singleRecord.put("email", "invalid-email");
        singleRecord.put("phone", "1234567890");
        singleRecord.put("age", 25);
        singleRecord.put("salary", 15000);
        
        ValidationReport report = engine.validateRecord(singleRecord);
        System.out.println("记录: " + singleRecord);
        System.out.println(report.generateTextReport());
    }
    
    private static List<Map<String, Object>> createTestData() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        // 有效记录1
        Map<String, Object> record1 = new HashMap<>();
        record1.put("name", "李明");
        record1.put("email", "liming@example.com");
        record1.put("phone", "13812345678");
        record1.put("age", 28);
        record1.put("salary", 12000);
        data.add(record1);
        
        // 有效记录2
        Map<String, Object> record2 = new HashMap<>();
        record2.put("name", "王芳");
        record2.put("email", "wangfang@company.cn");
        record2.put("phone", "15987654321");
        record2.put("age", 32);
        record2.put("salary", 18000);
        data.add(record2);
        
        // 无效记录1 - 邮箱格式错误
        Map<String, Object> record3 = new HashMap<>();
        record3.put("name", "张伟");
        record3.put("email", "zhangwei@");
        record3.put("phone", "13698765432");
        record3.put("age", 45);
        record3.put("salary", 25000);
        data.add(record3);
        
        // 无效记录2 - 手机号格式错误，年龄超出范围
        Map<String, Object> record4 = new HashMap<>();
        record4.put("name", "刘红");
        record4.put("email", "liuhong@test.com");
        record4.put("phone", "12345678901");
        record4.put("age", 200);
        record4.put("salary", 8000);
        data.add(record4);
        
        // 无效记录3 - 姓名为空，薪资超出范围
        Map<String, Object> record5 = new HashMap<>();
        record5.put("name", "");
        record5.put("email", "empty@name.com");
        record5.put("phone", "13711112222");
        record5.put("age", 35);
        record5.put("salary", 150000);
        data.add(record5);
        
        return data;
    }
}
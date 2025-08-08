package com.dataforge.examples;

import com.dataforge.config.ConfigurationManager;
import com.dataforge.core.GenerationContext;
import com.dataforge.core.GeneratorFactory;
import com.dataforge.generators.identifiers.EnhancedUuidGenerator;
import com.dataforge.generators.identifiers.CustomIdGenerator;
import com.dataforge.generators.location.EnhancedAddressGenerator;
import com.dataforge.validation.DataValidator;
import com.dataforge.output.JsonOutputWriter;
import com.dataforge.core.DataGenerator;

import java.util.*;

/**
 * 增强功能示例
 * 演示新添加的地址、企业信息、UUID生成器和数据验证功能
 */
public class EnhancedFeaturesExample {

    public static void main(String[] args) {
        System.out.println("=== DataForge 增强功能示例 ===\n");
        
        // 1. 增强版地址生成器示例
        demonstrateEnhancedAddress();
        
        // 2. 增强版UUID生成器示例
        demonstrateEnhancedUuid();
        
        // 3. 自定义ID生成器示例
        demonstrateCustomId();
        
        // 4. 数据验证功能示例
        demonstrateValidation();
        
        // 5. 配置文件使用示例
        demonstrateConfiguration();
        
        // 6. 综合使用示例
        demonstrateComprehensiveUsage();
    }

    private static void demonstrateEnhancedAddress() {
        System.out.println("1. 增强版地址生成器示例:");
        System.out.println("-".repeat(50));
        
        EnhancedAddressGenerator addressGen = new EnhancedAddressGenerator(
            EnhancedAddressGenerator.AddressFormat.STRUCTURED, true, true);
        
        GenerationContext context = new GenerationContext(1);
        Map<String, Object> address = addressGen.generate(context);
        
        System.out.println("结构化地址:");
        address.forEach((key, value) -> System.out.println("  " + key + ": " + value));
        System.out.println();
    }

    private static void demonstrateEnhancedUuid() {
        System.out.println("2. 增强版UUID生成器示例:");
        System.out.println("-".repeat(50));
        
        EnhancedUuidGenerator uuidGen = new EnhancedUuidGenerator(
            EnhancedUuidGenerator.UuidVersion.V4_RANDOM,
            EnhancedUuidGenerator.UuidFormat.STANDARD,
            true,
            true
        );
        
        GenerationContext context = new GenerationContext(1);
        Map<String, Object> uuidInfo = uuidGen.generate(context);
        
        System.out.println("UUID信息:");
        uuidInfo.forEach((key, value) -> System.out.println("  " + key + ": " + value));
        System.out.println();
    }

    private static void demonstrateCustomId() {
        System.out.println("3. 自定义ID生成器示例:");
        System.out.println("-".repeat(50));
        
        CustomIdGenerator customIdGen = new CustomIdGenerator(
            CustomIdGenerator.IdType.ORDER_NUMBER,
            "ORD",
            CustomIdGenerator.DateFormat.YYYYMMDD,
            6,
            true,
            4,
            "-"
        );
        
        GenerationContext context = new GenerationContext(5);
        System.out.println("自定义订单号:");
        for (int i = 0; i < 5; i++) {
            String orderId = customIdGen.generate(context);
            System.out.println("  " + (i + 1) + ". " + orderId);
        }
        System.out.println();
    }

    private static void demonstrateValidation() {
        System.out.println("4. 数据验证功能示例:");
        System.out.println("-".repeat(50));
        
        // 测试数据
        Map<String, Object> testData = new HashMap<>();
        testData.put("email", "test@example.com");
        testData.put("phone", "13800138000");
        testData.put("idcard", "110105199003078888");
        testData.put("url", "https://www.example.com");
        testData.put("uuid", "550e8400-e29b-41d4-a716-446655440000");
        testData.put("company", "北京科技有限公司");
        
        System.out.println("验证测试数据:");
        testData.forEach((key, value) -> {
            boolean isValid = false;
            switch (key) {
                case "email":
                    isValid = DataValidator.isValidEmail(value.toString());
                    break;
                case "phone":
                    isValid = DataValidator.isValidPhoneNumber(value.toString());
                    break;
                case "idcard":
                    isValid = DataValidator.isValidIdCard(value.toString());
                    break;
                case "url":
                    isValid = DataValidator.isValidUrl(value.toString());
                    break;
                case "uuid":
                    isValid = DataValidator.isValidUuid(value.toString());
                    break;
                case "company":
                    isValid = DataValidator.isValidCompanyName(value.toString());
                    break;
            }
            System.out.println("  " + key + " (" + value + "): " + (isValid ? "✓有效" : "✗无效"));
        });
        
        // 整体数据验证
        DataValidator.ValidationResult result = DataValidator.validateDataIntegrity(testData);
        System.out.println("\n整体验证结果: " + (result.isValid() ? "✓有效" : "✗无效"));
        if (!result.isValid()) {
            System.out.println("错误信息: " + result.getErrorMessage());
        }
        System.out.println();
    }

    private static void demonstrateConfiguration() {
        System.out.println("5. 配置文件使用示例:");
        System.out.println("-".repeat(50));
        
        // 创建示例配置
        ConfigurationManager.Configuration config = ConfigurationManager.createExampleConfiguration();
        config.setName("用户信息生成任务");
        config.setDescription("生成包含地址、企业信息、UUID的完整用户信息");
        config.setOutputFile("enhanced_users.json");
        
        // 添加地址生成任务
        ConfigurationManager.GenerationTask addressTask = new ConfigurationManager.GenerationTask();
        addressTask.setName("address");
        addressTask.setType("enhanced_address");
        addressTask.setCount(1);
        addressTask.getParameters().put("format", "COMPLETE");
        addressTask.getParameters().put("includePostcode", true);
        addressTask.getParameters().put("includeCoordinates", true);
        config.getTasks().add(addressTask);
        
        // 添加企业信息生成任务
        ConfigurationManager.GenerationTask companyTask = new ConfigurationManager.GenerationTask();
        companyTask.setName("company");
        companyTask.setType("company");
        companyTask.setCount(1);
        companyTask.getParameters().put("unique", true);
        companyTask.getParameters().put("prefixRegion", true);
        config.getTasks().add(companyTask);
        
        // 添加UUID生成任务
        ConfigurationManager.GenerationTask uuidTask = new ConfigurationManager.GenerationTask();
        uuidTask.setName("uuid");
        uuidTask.setType("enhanced_uuid");
        uuidTask.setCount(1);
        uuidTask.getParameters().put("version", "V4_RANDOM");
        uuidTask.getParameters().put("format", "STANDARD");
        config.getTasks().add(uuidTask);
        
        System.out.println("配置文件内容:");
        System.out.println("  名称: " + config.getName());
        System.out.println("  描述: " + config.getDescription());
        System.out.println("  输出文件: " + config.getOutputFile());
        System.out.println("  任务数量: " + config.getTasks().size());
        System.out.println();
    }

    private static void demonstrateComprehensiveUsage() {
        System.out.println("6. 综合使用示例:");
        System.out.println("-".repeat(50));
        
        GenerationContext context = new GenerationContext(1);
        
        // 生成企业完整信息
        Map<String, Object> companyInfo = new HashMap<>();
        
        // 公司名称
        DataGenerator<String> companyGen = GeneratorFactory.createGenerator("company");
        companyInfo.put("company_name", companyGen.generate(context));
        
        // 统一社会信用代码
        DataGenerator<String> usccGen = GeneratorFactory.createGenerator("uscc");
        companyInfo.put("uscc", usccGen.generate(context));
        
        // 公司地址
        DataGenerator<Map<String, Object>> addressGen = GeneratorFactory.createGenerator("enhanced_address");
        companyInfo.put("address", addressGen.generate(context));
        
        // 公司ID
        DataGenerator<String> customIdGen = GeneratorFactory.createGenerator("custom_id");
        companyInfo.put("company_id", customIdGen.generate(context));
        
        // 联系方式
        DataGenerator<String> phoneGen = GeneratorFactory.createGenerator("phone");
        companyInfo.put("contact_phone", phoneGen.generate(context));
        
        DataGenerator<String> emailGen = GeneratorFactory.createGenerator("email");
        companyInfo.put("contact_email", emailGen.generate(context));
        
        // 验证数据
        DataValidator.ValidationResult validation = DataValidator.validateDataIntegrity(companyInfo);
        
        System.out.println("企业完整信息:");
        companyInfo.forEach((key, value) -> {
            if (value instanceof Map) {
                System.out.println("  " + key + ":");
                ((Map<?, ?>) value).forEach((k, v) -> 
                    System.out.println("    " + k + ": " + v));
            } else {
                System.out.println("  " + key + ": " + value);
            }
        });
        
        System.out.println("\n数据验证结果: " + (validation.isValid() ? "✓有效" : "✗无效"));
        System.out.println();
    }

    /**
     * 使用示例命令行:
     * 
     * # 生成企业信息
     * java -jar dataforge-cli.jar company_name:name,uscc:uscc,email:email,phone:phone 100 companies.json
     * 
     * # 生成增强地址信息
     * java -jar dataforge-cli.jar address:enhanced_address 50 addresses.json
     * 
     * # 生成自定义ID
     * java -jar dataforge-cli.jar order_id:custom_id 1000 orders.json
     */
}

/**
 * 使用CLI命令行示例:
 * 
 * # 基础使用
 * java -jar dataforge-cli.jar name:name,company:company,address:enhanced_address 100 output.json
 * 
 * # 使用配置文件
 * java -cp dataforge-cli.jar com.dataforge.config.ConfigurationManager config.yaml
 * 
 * # 验证数据
 * java -cp dataforge-core.jar com.dataforge.validation.DataValidator sample_data.json
 * 
 * # 批量生成
 * java -jar dataforge-cli.jar company_name:name,uscc:uscc,email:email,phone:phone,address:enhanced_address 1000 enterprise_data.json
 */
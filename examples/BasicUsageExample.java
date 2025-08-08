package com.dataforge.examples;

import com.dataforge.core.*;
import com.dataforge.generators.media.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * DataForge 基础使用示例
 */
public class BasicUsageExample {
    
    public static void main(String[] args) {
        BasicUsageExample example = new BasicUsageExample();
        example.runExamples();
    }
    
    public void runExamples() {
        System.out.println("=== DataForge 基础使用示例 ===\n");
        
        // 创建生成器工厂
        GeneratorFactory factory = new GeneratorFactory();
        
        // 1. 基础数据生成
        demonstrateBasicGeneration(factory);
        
        // 2. 带参数的数据生成
        demonstrateParameterizedGeneration(factory);
        
        // 3. 批量数据生成
        demonstrateBatchGeneration(factory);
        
        // 4. 媒体文件数据生成
        demonstrateMediaGeneration(factory);
        
        // 5. JSON数据生成
        demonstrateJSONGeneration(factory);
    }
    
    private void demonstrateBasicGeneration(GeneratorFactory factory) {
        System.out.println("1. 基础数据生成示例:");
        
        GenerationContext context = new GenerationContext();
        
        // 生成随机姓名
        DataGenerator<String> nameGen = factory.createGenerator("random_name");
        String name = nameGen.generate(context);
        System.out.println("随机姓名: " + name);
        
        // 生成随机邮箱
        DataGenerator<String> emailGen = factory.createGenerator("random_email");
        String email = emailGen.generate(context);
        System.out.println("随机邮箱: " + email);
        
        // 生成随机整数
        DataGenerator<Integer> intGen = factory.createGenerator("random_int");
        int number = intGen.generate(context);
        System.out.println("随机整数: " + number);
        
        System.out.println();
    }
    
    private void demonstrateParameterizedGeneration(GeneratorFactory factory) {
        System.out.println("2. 带参数的数据生成示例:");
        
        // 创建带参数的上下文
        GenerationContext context = new GenerationContext();
        
        // 生成指定范围的随机数
        context.setParameter("min", 18);
        context.setParameter("max", 65);
        DataGenerator<Integer> ageGen = factory.createGenerator("random_int");
        int age = ageGen.generate(context);
        System.out.println("年龄 (18-65): " + age);
        
        // 生成指定长度的随机字符串
        context.clearParameters();
        context.setParameter("minLength", 10);
        context.setParameter("maxLength", 20);
        DataGenerator<String> stringGen = factory.createGenerator("random_string");
        String randomString = stringGen.generate(context);
        System.out.println("随机字符串 (10-20字符): " + randomString);
        
        System.out.println();
    }
    
    private void demonstrateBatchGeneration(GeneratorFactory factory) {
        System.out.println("3. 批量数据生成示例:");
        
        DataGenerator<String> nameGen = factory.createGenerator("random_name");
        GenerationContext context = new GenerationContext();
        
        // 生成10个用户姓名
        List<String> names = IntStream.range(0, 10)
            .mapToObj(i -> nameGen.generate(context))
            .collect(Collectors.toList());
        
        System.out.println("批量生成的姓名:");
        names.forEach(name -> System.out.println("  - " + name));
        System.out.println();
    }
    
    private void demonstrateMediaGeneration(GeneratorFactory factory) {
        System.out.println("4. 媒体文件数据生成示例:");
        
        GenerationContext context = new GenerationContext();
        
        // 生成文件扩展名
        DataGenerator<String> extGen = factory.createGenerator("file_extension");
        String extension = extGen.generate(context);
        System.out.println("文件扩展名: " + extension);
        
        // 生成带分类的文件扩展名
        context.setParameter("category", "image");
        String imageExt = extGen.generate(context);
        System.out.println("图片扩展名: " + imageExt);
        
        // 生成文件大小
        DataGenerator<String> sizeGen = factory.createGenerator("file_size");
        String fileSize = sizeGen.generate(context);
        System.out.println("文件大小: " + fileSize);
        
        // 生成图片尺寸
        DataGenerator<String> dimGen = factory.createGenerator("image_dimensions");
        String dimensions = dimGen.generate(context);
        System.out.println("图片尺寸: " + dimensions);
        
        System.out.println();
    }
    
    private void demonstrateJSONGeneration(GeneratorFactory factory) {
        System.out.println("5. JSON数据生成示例:");
        
        GenerationContext context = new GenerationContext();
        
        // 生成简单的用户JSON
        DataGenerator<String> userJsonGen = factory.createGenerator("json_user");
        String userJson = userJsonGen.generate(context);
        System.out.println("用户JSON: " + userJson);
        
        // 生成产品JSON
        DataGenerator<String> productJsonGen = factory.createGenerator("json_product");
        String productJson = productJsonGen.generate(context);
        System.out.println("产品JSON: " + productJson);
        
        System.out.println();
    }
}
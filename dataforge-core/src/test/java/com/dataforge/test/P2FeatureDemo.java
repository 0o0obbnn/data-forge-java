package com.dataforge.test;

import com.dataforge.core.*;
import com.dataforge.generators.advanced.*;

import java.util.*;

/**
 * P2优先级功能演示
 * 展示扩展接口、性能优化、缓存机制等高级功能
 */
public class P2FeatureDemo {
    
    public static void main(String[] args) {
        System.out.println("=== DataForge P2 高级功能演示 ===\n");
        
        // 1. 扩展数据生成器接口演示
        demonstrateEnhancedInterface();
        
        // 2. 批量数据生成演示
        demonstrateBatchGeneration();
        
        // 3. 相似数据生成演示
        demonstrateSimilarDataGeneration();
        
        // 4. 数据关联生成演示
        demonstrateDataRelationship();
        
        // 5. 性能监控演示
        demonstratePerformanceMonitoring();
        
        // 6. 缓存机制演示
        demonstrateCaching();
    }
    
    /**
     * 演示增强数据生成器接口
     */
    private static void demonstrateEnhancedInterface() {
        System.out.println("1. 增强数据生成器接口演示");
        System.out.println("--------------------------------");
        
        // 创建一个简单的字符串生成器
        EnhancedDataGenerator<String> stringGenerator = new SimpleStringGenerator();
        GenerationContext context = new GenerationContext(100);
        
        // 批量生成
        List<String> batch = stringGenerator.generateBatch(context, 5);
        System.out.println("批量生成结果: " + batch);
        
        // 生成唯一集合
        Set<String> uniqueSet = stringGenerator.generateUniqueSet(context, 3, 10);
        System.out.println("唯一集合: " + uniqueSet);
        
        // 数据统计
        EnhancedDataGenerator.DataStatistics stats = stringGenerator.getStatistics(batch);
        System.out.println("数据统计: " + stats);
        
        System.out.println();
    }
    
    /**
     * 演示批量数据生成
     */
    private static void demonstrateBatchGeneration() {
        System.out.println("2. 批量数据生成演示");
        System.out.println("-------------------------");
        
        // 创建批量生成器
        EnhancedDataGenerator<String> baseGenerator = new SimpleStringGenerator();
        
        // 使用进度回调
        BatchDataGenerator.ProgressCallback callback = new BatchDataGenerator.ProgressCallback() {
            @Override
            public void onProgress(int completed, int total) {
                if (completed % 100 == 0) {
                    System.out.printf("进度: %d/%d (%.1f%%)%n", completed, total, 
                        (double) completed / total * 100);
                }
            }
            
            @Override
            public void onCompleted(int total, long timeMillis) {
                System.out.printf("完成: 生成 %d 项，耗时 %d ms%n", total, timeMillis);
            }
        };
        
        BatchDataGenerator<String> batchGenerator = new BatchDataGenerator<>(baseGenerator, true, 50, callback);
        GenerationContext context = new GenerationContext(100);
        
        // 生成大量数据
        List<String> largeDataset = batchGenerator.generateBatch(context, 500);
        System.out.println("大数据集大小: " + largeDataset.size());
        
        // 生成性能报告
        int[] testSizes = {100, 500, 1000};
        BatchDataGenerator.PerformanceReport report = batchGenerator.generatePerformanceReport(context, testSizes);
        System.out.println("性能报告:");
        System.out.println(report);
        
        batchGenerator.shutdown();
        System.out.println();
    }
    
    /**
     * 演示相似数据生成
     */
    private static void demonstrateSimilarDataGeneration() {
        System.out.println("3. 相似数据生成演示");
        System.out.println("-------------------------");
        
        EnhancedDataGenerator<String> baseGenerator = new SimpleStringGenerator();
        SimilarDataGenerator<String> similarGenerator = SimilarDataGenerator.createStringSimilarGenerator(
            baseGenerator, 0.8 // 80%相似度
        );
        
        GenerationContext context = new GenerationContext(100);
        String seedData = "DataForge2024";
        
        // 生成相似数据
        List<String> similarData = similarGenerator.generateSimilar(context, seedData, 5);
        System.out.println("种子数据: " + seedData);
        System.out.println("相似数据: " + similarData);
        
        // 计算相似度
        for (String data : similarData) {
            double similarity = similarGenerator.calculateSimilarity(seedData, data);
            System.out.printf("  %s -> 相似度: %.2f%%n", data, similarity * 100);
        }
        
        System.out.println();
    }
    
    /**
     * 演示数据关联生成
     */
    private static void demonstrateDataRelationship() {
        System.out.println("4. 数据关联生成演示");
        System.out.println("-------------------------");
        
        // 创建用户资料关联生成器
        DataRelationshipGenerator generator = new DataRelationshipGenerator();
        
        // 添加基础字段生成器（模拟）
        generator.addFieldGenerator("age", new MockAgeGenerator());
        generator.addFieldGenerator("gender", new MockGenderGenerator());
        
        // 添加关联规则
        generator.addRelationshipRule(new DataRelationshipGenerator.ConditionalRule("gender", "female", "title", "Ms."));
        generator.addRelationshipRule(new DataRelationshipGenerator.ConditionalRule("gender", "male", "title", "Mr."));
        
        generator.addRelationshipRule(new DataRelationshipGenerator.RangeRule("age", 0, 18, "category", "minor"));
        generator.addRelationshipRule(new DataRelationshipGenerator.RangeRule("age", 18, 65, "category", "adult"));
        generator.addRelationshipRule(new DataRelationshipGenerator.RangeRule("age", 65, 150, "category", "senior"));
        
        GenerationContext context = new GenerationContext(100);
        
        // 生成关联数据
        for (int i = 0; i < 3; i++) {
            Map<String, Object> userData = generator.generate(context);
            System.out.println("用户 " + (i + 1) + ": " + userData);
        }
        
        System.out.println();
    }
    
    /**
     * 演示性能监控
     */
    private static void demonstratePerformanceMonitoring() {
        System.out.println("5. 性能监控演示");
        System.out.println("----------------------");
        
        PerformanceMonitor monitor = PerformanceMonitor.getInstance();
        monitor.setEnabled(true);
        
        // 模拟不同生成器的操作
        simulateGeneratorOperations("string_generator", 1000, 10);
        simulateGeneratorOperations("number_generator", 2000, 5);
        simulateGeneratorOperations("date_generator", 1500, 8);
        
        // 生成性能报告
        PerformanceMonitor.OverallPerformanceReport overallReport = monitor.getOverallReport();
        System.out.println("总体性能报告:");
        System.out.println(overallReport);
        
        System.out.println("\n生成器性能排行榜:");
        List<PerformanceMonitor.GeneratorRanking> rankings = monitor.getPerformanceRanking();
        for (int i = 0; i < Math.min(3, rankings.size()); i++) {
            System.out.println((i + 1) + ". " + rankings.get(i));
        }
        
        System.out.println();
    }
    
    /**
     * 演示缓存机制
     */
    private static void demonstrateCaching() {
        System.out.println("6. 缓存机制演示");
        System.out.println("--------------------");
        
        GeneratorCacheManager cacheManager = GeneratorCacheManager.getInstance();
        
        // 配置缓存
        GeneratorCacheManager.CacheConfiguration config = 
            GeneratorCacheManager.CacheConfiguration.highPerformance();
        cacheManager.configure(config);
        
        GenerationContext context = new GenerationContext(100);
        
        // 模拟缓存操作
        for (int i = 0; i < 10; i++) {
            String cacheKey = cacheManager.generateCacheKey("test_generator", context);
            
            // 尝试从缓存获取
            String cachedData = cacheManager.getCachedData(cacheKey);
            if (cachedData == null) {
                // 缓存未命中，生成新数据
                String newData = "Generated_" + i + "_" + System.currentTimeMillis();
                cacheManager.cacheData(cacheKey, newData);
                System.out.println("缓存未命中，生成新数据: " + newData);
            } else {
                System.out.println("缓存命中: " + cachedData);
            }
            
            cacheManager.recordGeneration();
        }
        
        // 显示缓存统计
        GeneratorCacheManager.CacheStatistics cacheStats = cacheManager.getStatistics();
        System.out.println("\n缓存统计:");
        System.out.println(cacheStats);
        
        System.out.println();
    }
    
    /**
     * 模拟生成器操作
     */
    private static void simulateGeneratorOperations(String generatorName, int operations, int avgDelayMs) {
        PerformanceMonitor monitor = PerformanceMonitor.getInstance();
        Random random = new Random();
        
        for (int i = 0; i < operations; i++) {
            PerformanceMonitor.OperationContext context = monitor.startOperation(generatorName);
            
            try {
                // 模拟生成时间
                int delay = Math.max(1, avgDelayMs + random.nextInt(10) - 5);
                Thread.sleep(delay);
                
                monitor.endOperation(context, 1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    // 辅助类 - 简单字符串生成器
    private static class SimpleStringGenerator implements EnhancedDataGenerator<String> {
        private final Random random = new Random();
        
        @Override
        public String generate(GenerationContext context) {
            return "Data_" + random.nextInt(10000);
        }
    }
    
    // 模拟年龄生成器
    private static class MockAgeGenerator implements EnhancedDataGenerator<Integer> {
        private final Random random = new Random();
        
        @Override
        public Integer generate(GenerationContext context) {
            return random.nextInt(80) + 10; // 10-90岁
        }
    }
    
    // 模拟性别生成器
    private static class MockGenderGenerator implements EnhancedDataGenerator<String> {
        private final Random random = new Random();
        private final String[] genders = {"male", "female"};
        
        @Override
        public String generate(GenerationContext context) {
            return genders[random.nextInt(genders.length)];
        }
    }
}
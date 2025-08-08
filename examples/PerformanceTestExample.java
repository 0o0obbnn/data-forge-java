package com.dataforge.examples;

import com.dataforge.core.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * DataForge 性能测试示例
 */
public class PerformanceTestExample {
    
    public static void main(String[] args) {
        PerformanceTestExample example = new PerformanceTestExample();
        example.runPerformanceTests();
    }
    
    public void runPerformanceTests() {
        System.out.println("=== DataForge 性能测试示例 ===\n");
        
        GeneratorFactory factory = new GeneratorFactory();
        
        // 测试不同规模的数据生成性能
        testBasicGeneration(factory, 1000);
        testBasicGeneration(factory, 10000);
        testBasicGeneration(factory, 100000);
        
        // 测试并发性能
        testConcurrentGeneration(factory, 10000);
        testConcurrentGeneration(factory, 100000);
        
        // 测试内存优化性能
        testMemoryOptimizedGeneration(factory, 1000000);
        
        // 测试流式处理性能
        testStreamingGeneration(factory, 10000000);
    }
    
    private void testBasicGeneration(GeneratorFactory factory, int count) {
        System.out.println(String.format("测试基础生成 %d 条记录:" , count));
        
        DataGenerator<String> generator = factory.createGenerator("random_name");
        GenerationContext context = new GenerationContext();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < count; i++) {
            generator.generate(context);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        double rate = count / (duration / 1000.0);
        
        System.out.println(String.format("  耗时: %d ms", duration));
        System.out.println(String.format("  速度: %.2f records/sec", rate));
        System.out.println();
    }
    
    private void testConcurrentGeneration(GeneratorFactory factory, int count) {
        System.out.println(String.format("测试并发生成 %d 条记录:" , count));
        
        ConcurrentDataGenerator concurrentGen = new ConcurrentDataGenerator();
        DataGenerator<String> generator = factory.createGenerator("random_name");
        GenerationContext context = new GenerationContext();
        
        long startTime = System.currentTimeMillis();
        
        List<String> results = concurrentGen.generate(
            generator, context, count, 1000
        );
        
        long duration = System.currentTimeMillis() - startTime;
        double rate = count / (duration / 1000.0);
        
        System.out.println(String.format("  耗时: %d ms", duration));
        System.out.println(String.format("  速度: %.2f records/sec", rate));
        System.out.println(String.format("  并发线程: %d", concurrentGen.getThreadCount()));
        
        concurrentGen.shutdown();
        System.out.println();
    }
    
    private void testMemoryOptimizedGeneration(GeneratorFactory factory, int count) {
        System.out.println(String.format("测试内存优化生成 %d 条记录:" , count));
        
        MemoryOptimizedGenerator memoryGen = new MemoryOptimizedGenerator();
        DataGenerator<String> generator = factory.createGenerator("random_name");
        GenerationContext context = new GenerationContext();
        
        AtomicLong processedCount = new AtomicLong(0);
        
        long startTime = System.currentTimeMillis();
        
        memoryGen.generateStreaming(
            generator, 
            context, 
            count,
            data -> processedCount.incrementAndGet()
        );
        
        long duration = System.currentTimeMillis() - startTime;
        double rate = count / (duration / 1000.0);
        
        System.out.println(String.format("  耗时: %d ms", duration));
        System.out.println(String.format("  速度: %.2f records/sec", rate));
        
        // 显示内存使用情况
        MemoryOptimizedGenerator.MemoryStats stats = memoryGen.getMemoryStats();
        System.out.println(String.format("  内存使用: %s", stats));
        System.out.println();
    }
    
    private void testStreamingGeneration(GeneratorFactory factory, int count) {
        System.out.println(String.format("测试流式处理 %d 条记录:" , count));
        
        MemoryOptimizedGenerator memoryGen = new MemoryOptimizedGenerator();
        DataGenerator<String> generator = factory.createGenerator("random_name");
        GenerationContext context = new GenerationContext();
        
        AtomicLong processedCount = new AtomicLong(0);
        
        // 使用推荐批次大小
        int recommendedBatchSize = memoryGen.getRecommendedBatchSize(count);
        System.out.println(String.format("  推荐批次大小: %d", recommendedBatchSize));
        
        long startTime = System.currentTimeMillis();
        
        memoryGen.generateWithValidation(
            generator,
            context,
            count,
            data -> {
                if (processedCount.incrementAndGet() % 100000 == 0) {
                    System.gc(); // 手动触发垃圾回收
                }
            }
        );
        
        long duration = System.currentTimeMillis() - startTime;
        double rate = count / (duration / 1000.0);
        
        System.out.println(String.format("  耗时: %d ms", duration));
        System.out.println(String.format("  速度: %.2f records/sec", rate));
        
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println(String.format("  最终内存使用: %.2f MB", usedMemory / (1024.0 * 1024.0)));
        System.out.println();
    }
    
    public void benchmarkAllGenerators() {
        System.out.println("=== 所有生成器性能基准测试 ===\n");
        
        GeneratorFactory factory = new GeneratorFactory();
        GenerationContext context = new GenerationContext();
        
        String[] generators = {
            "random_name", "random_email", "random_phone", 
            "random_int", "random_double", "random_date",
            "uuid", "idcard", "bankcard"
        };
        
        int testCount = 10000;
        
        for (String generatorName : generators) {
            try {
                DataGenerator<?> generator = factory.createGenerator(generatorName);
                
                long startTime = System.currentTimeMillis();
                
                for (int i = 0; i < testCount; i++) {
                    generator.generate(context);
                }
                
                long duration = System.currentTimeMillis() - startTime;
                double rate = testCount / (duration / 1000.0);
                
                System.out.println(String.format("%-15s: %6d ms, %8.2f records/sec", 
                    generatorName, duration, rate));
                    
            } catch (Exception e) {
                System.out.println(String.format("%-15s: 测试失败 - %s", 
                    generatorName, e.getMessage()));
            }
        }
    }
    
    public void memoryUsageTest() {
        System.out.println("=== 内存使用测试 ===\n");
        
        GeneratorFactory factory = new GeneratorFactory();
        DataGenerator<String> generator = factory.createGenerator("random_string");
        
        int[] testSizes = {1000, 10000, 100000, 1000000};
        
        for (int size : testSizes) {
            System.out.println(String.format("测试数据量: %,d", size));
            
            // 普通List方式
            long startMemory = getUsedMemory();
            long startTime = System.currentTimeMillis();
            
            List<String> results = IntStream.range(0, size)
                .mapToObj(i -> generator.generate(new GenerationContext()))
                .collect(Collectors.toList());
            
            long listDuration = System.currentTimeMillis() - startTime;
            long listMemory = getUsedMemory() - startMemory;
            
            // 流式处理方式
            startMemory = getUsedMemory();
            startTime = System.currentTimeMillis();
            
            MemoryOptimizedGenerator memoryGen = new MemoryOptimizedGenerator();
            AtomicLong count = new AtomicLong(0);
            
            memoryGen.generateStreaming(
                generator,
                new GenerationContext(),
                size,
                data -> count.incrementAndGet()
            );
            
            long streamDuration = System.currentTimeMillis() - startTime;
            long streamMemory = getUsedMemory() - startMemory;
            
            System.out.println(String.format("  普通List: %6d ms, %8.2f MB", 
                listDuration, listMemory / (1024.0 * 1024.0)));
            System.out.println(String.format("  流式处理: %6d ms, %8.2f MB", 
                streamDuration, streamMemory / (1024.0 * 1024.0)));
            System.out.println(String.format("  内存节省: %6.2f%%", 
                (1.0 - (double)streamMemory / listMemory) * 100));
            System.out.println();
        }
    }
    
    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    public void stressTest() {
        System.out.println("=== 压力测试 ===\n");
        
        GeneratorFactory factory = new GeneratorFactory();
        ConcurrentDataGenerator concurrentGen = new ConcurrentDataGenerator(8);
        
        int[] testSizes = {100000, 500000, 1000000};
        
        for (int size : testSizes) {
            System.out.println(String.format("压力测试数据量: %,d", size));
            
            long startTime = System.currentTimeMillis();
            
            List<String> results = concurrentGen.generate(
                factory.createGenerator("complex_object"),
                new GenerationContext(),
                size,
                size / 100  // 动态批次大小
            );
            
            long duration = System.currentTimeMillis() - startTime;
            double rate = size / (duration / 1000.0);
            
            System.out.println(String.format("  耗时: %d ms", duration));
            System.out.println(String.format("  速度: %.2f records/sec", rate));
            System.out.println(String.format("  成功率: %.2f%%", 
                (results.size() / (double)size) * 100));
            System.out.println();
        }
        
        concurrentGen.shutdown();
    }
}
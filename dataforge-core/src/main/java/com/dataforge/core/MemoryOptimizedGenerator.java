package com.dataforge.core;

import com.dataforge.core.GenerationContext;
import com.dataforge.core.DataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 内存优化数据生成器
 * 支持流式生成，避免内存溢出，适合大规模数据生成
 */
public class MemoryOptimizedGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MemoryOptimizedGenerator.class);
    private static final long DEFAULT_MEMORY_THRESHOLD = 64 * 1024 * 1024; // 64MB
    private static final int DEFAULT_BATCH_SIZE = 1000;
    
    private final long memoryThreshold;
    private final int batchSize;

    public MemoryOptimizedGenerator() {
        this(DEFAULT_MEMORY_THRESHOLD, DEFAULT_BATCH_SIZE);
    }

    public MemoryOptimizedGenerator(long memoryThreshold, int batchSize) {
        this.memoryThreshold = memoryThreshold;
        this.batchSize = batchSize;
    }

    /**
     * 流式生成数据，避免内存溢出
     * @param generator 数据生成器
     * @param context 生成上下文
     * @param totalCount 总生成数量
     * @param consumer 数据消费者
     */
    public <T> void generateStreaming(DataGenerator<T> generator, GenerationContext context, 
                                       long totalCount, Consumer<T> consumer) {
        generateStreaming(generator, context, totalCount, consumer, batchSize);
    }

    /**
     * 流式生成数据，避免内存溢出
     * @param generator 数据生成器
     * @param context 生成上下文
     * @param totalCount 总生成数量
     * @param consumer 数据消费者
     * @param batchSize 批次大小
     */
    public <T> void generateStreaming(DataGenerator<T> generator, GenerationContext context, 
                                       long totalCount, Consumer<T> consumer, int batchSize) {
        
        if (totalCount <= 0) {
            return;
        }

        logger.info("Starting memory-optimized streaming generation: {} items", totalCount);
        
        AtomicLong processedCount = new AtomicLong(0);
        AtomicInteger currentBatch = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        Runtime runtime = Runtime.getRuntime();
        
        try {
            for (long i = 0; i < totalCount; i++) {
                T data = generator.generate(context);
                consumer.accept(data);
                
                long current = processedCount.incrementAndGet();
                
                // 内存监控
                if (current % batchSize == 0) {
                    checkMemoryUsage(runtime, current, totalCount);
                    
                    long elapsed = System.currentTimeMillis() - startTime;
                    double rate = current / (elapsed / 1000.0);
                    logger.debug("Progress: {}/{} items generated ({} items/sec)", 
                               current, totalCount, String.format("%.2f", rate));
                }
            }

            long totalTime = System.currentTimeMillis() - startTime;
            double rate = totalCount / (totalTime / 1000.0);
            logger.info("Memory-optimized generation completed: {} items in {}ms ({} items/sec)",
                       totalCount, totalTime, String.format("%.2f", rate));

        } catch (Exception e) {
            logger.error("Error during memory-optimized generation", e);
            throw new RuntimeException("Memory-optimized generation failed", e);
        }
    }

    /**
     * 检查内存使用情况
     * @param runtime 运行时
     * @param current 当前处理数量
     * @param total 总数量
     */
    private void checkMemoryUsage(Runtime runtime, long current, long total) {
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        
        long usedMemory = totalMemory - freeMemory;
        
        if (usedMemory > memoryThreshold) {
            logger.warn("Memory usage high: {}MB used, {}MB free (threshold: {}MB)",
                       usedMemory / (1024 * 1024), 
                       freeMemory / (1024 * 1024),
                       memoryThreshold / (1024 * 1024));
            
            // 触发垃圾回收
            runtime.gc();
            
            freeMemory = runtime.freeMemory();
            totalMemory = runtime.totalMemory();
            usedMemory = totalMemory - freeMemory;
            
            logger.info("After GC: {}MB used, {}MB free", 
                       usedMemory / (1024 * 1024), 
                       freeMemory / (1024 * 1024));
        }
    }

    /**
     * 获取内存使用统计
     * @return 内存统计信息
     */
    public MemoryStats getMemoryStats() {
        Runtime runtime = Runtime.getRuntime();
        return new MemoryStats(
            runtime.totalMemory(),
            runtime.freeMemory(),
            runtime.maxMemory()
        );
    }

    /**
     * 内存统计信息类
     */
    public static class MemoryStats {
        private final long totalMemory;
        private final long freeMemory;
        private final long maxMemory;

        public MemoryStats(long totalMemory, long freeMemory, long maxMemory) {
            this.totalMemory = totalMemory;
            this.freeMemory = freeMemory;
            this.maxMemory = maxMemory;
        }

        public long getTotalMemory() { return totalMemory; }
        public long getFreeMemory() { return freeMemory; }
        public long getMaxMemory() { return maxMemory; }
        public long getUsedMemory() { return totalMemory - freeMemory; }

        @Override
        public String toString() {
            return String.format("MemoryStats{total=%dMB, free=%dMB, used=%dMB, max=%dMB}",
                               totalMemory / (1024 * 1024),
                               freeMemory / (1024 * 1024),
                               (totalMemory - freeMemory) / (1024 * 1024),
                               maxMemory / (1024 * 1024));
        }
    }

    /**
     * 获取推荐的批次大小
     * @param totalCount 总数量
     * @return 推荐的批次大小
     */
    public int getRecommendedBatchSize(long totalCount) {
        MemoryStats stats = getMemoryStats();
        long availableMemory = stats.getFreeMemory();
        
        // 根据可用内存调整批次大小
        if (availableMemory > 512 * 1024 * 1024) { // > 512MB
            return Math.min(10000, (int) Math.min(totalCount, 10000));
        } else if (availableMemory > 256 * 1024 * 1024) { // > 256MB
            return Math.min(5000, (int) Math.min(totalCount, 5000));
        } else if (availableMemory > 128 * 1024 * 1024) { // > 128MB
            return Math.min(1000, (int) Math.min(totalCount, 1000));
        } else {
            return Math.min(100, (int) Math.min(totalCount, 100));
        }
    }

    /**
     * 生成并验证内存使用情况
     * @param generator 数据生成器
     * @param context 生成上下文
     * @param totalCount 总生成数量
     * @param consumer 数据消费者
     */
    public <T> void generateWithValidation(DataGenerator<T> generator, GenerationContext context, 
                                           long totalCount, Consumer<T> consumer) {
        
        int recommendedBatch = getRecommendedBatchSize(totalCount);
        logger.info("Recommended batch size: {} based on memory analysis", recommendedBatch);
        
        generateStreaming(generator, context, totalCount, consumer, recommendedBatch);
    }
}
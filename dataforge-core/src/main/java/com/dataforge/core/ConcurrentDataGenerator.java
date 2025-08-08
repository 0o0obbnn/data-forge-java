package com.dataforge.core;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 并发数据生成器
 * 支持多线程并行生成数据以提高性能
 */
public class ConcurrentDataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentDataGenerator.class);
    private static final int DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_BATCH_SIZE = 1000;

    private final int threadCount;
    private final ExecutorService executorService;
    private final CompletionService<GenerationResult> completionService;

    public ConcurrentDataGenerator() {
        this(DEFAULT_THREAD_COUNT);
    }

    public ConcurrentDataGenerator(int threadCount) {
        this.threadCount = Math.max(1, Math.min(threadCount, DEFAULT_THREAD_COUNT * 2));
        this.executorService = Executors.newFixedThreadPool(this.threadCount);
        this.completionService = new ExecutorCompletionService<>(executorService);
    }

    /**
     * 并发生成数据
     * @param generator 数据生成器
     * @param context 生成上下文
     * @param totalCount 总生成数量
     * @return 生成的数据列表
     */
    public <T> List<T> generate(DataGenerator<T> generator, GenerationContext context, int totalCount) {
        return generate(generator, context, totalCount, DEFAULT_BATCH_SIZE);
    }

    /**
     * 并发生成数据
     * @param generator 数据生成器
     * @param context 生成上下文
     * @param totalCount 总生成数量
     * @param batchSize 每批次大小
     * @return 生成的数据列表
     */
    public <T> List<T> generate(DataGenerator<T> generator, GenerationContext context, int totalCount, int batchSize) {
        if (totalCount <= 0) {
            return new ArrayList<>();
        }

        int actualBatchSize = Math.max(1, Math.min(batchSize, totalCount / threadCount + 1));
        int taskCount = (int) Math.ceil((double) totalCount / actualBatchSize);

        logger.info("Starting concurrent generation: total={}, batch={}, tasks={}, threads={}",
                   totalCount, actualBatchSize, taskCount, threadCount);

        List<Future<GenerationResult>> futures = new ArrayList<>();
        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

        // 提交任务
        for (int i = 0; i < taskCount; i++) {
            final int start = i * actualBatchSize;
            final int end = Math.min(start + actualBatchSize, totalCount);
            final int taskId = i;

            futures.add(completionService.submit(() -> {
                List<Object> batchResults = new ArrayList<>();
                
                try {
                    for (int j = start; j < end; j++) {
                        batchResults.add(generator.generate(context));
                        
                        // 进度报告
                        int current = processedCount.incrementAndGet();
                        if (current % 1000 == 0) {
                            long elapsed = System.currentTimeMillis() - startTime.get();
                            double rate = current / (elapsed / 1000.0);
                            logger.debug("Progress: {}/{} items generated ({} items/sec)", 
                                       current, totalCount, String.format("%.2f", rate));
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error in batch task {}: {}", taskId, e.getMessage(), e);
                    throw e;
                }
                
                return new GenerationResult(batchResults);
            }));
        }

        // 收集结果
        List<T> results = new ArrayList<>(totalCount);
        try {
            for (int i = 0; i < taskCount; i++) {
                Future<GenerationResult> future = completionService.take();
                GenerationResult result = future.get();
                
                @SuppressWarnings("unchecked")
                List<T> batchResults = (List<T>) (List<?>) result.getResults();
                results.addAll(batchResults);
            }

            long totalTime = System.currentTimeMillis() - startTime.get();
            double rate = totalCount / (totalTime / 1000.0);
            logger.info("Concurrent generation completed: {} items in {}ms ({} items/sec)",
                       totalCount, totalTime, String.format("%.2f", rate));

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error during concurrent generation", e);
            throw new RuntimeException("Concurrent generation failed", e);
        }

        return results;
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取当前线程数
     * @return 线程数
     */
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * 获取线程池状态
     * @return 是否已关闭
     */
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    /**
     * 生成结果包装类
     */
    private static class GenerationResult {
        private final List<Object> results;

        public GenerationResult(List<Object> results) {
            this.results = results;
        }

        public List<Object> getResults() {
            return results;
        }
    }
}
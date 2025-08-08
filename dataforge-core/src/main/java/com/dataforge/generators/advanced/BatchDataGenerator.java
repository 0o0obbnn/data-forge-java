package com.dataforge.generators.advanced;

import com.dataforge.core.EnhancedDataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 批量数据生成器
 * 支持大规模数据生成、并行处理、进度监控
 */
public class BatchDataGenerator<T> implements EnhancedDataGenerator<T> {
    
    private final EnhancedDataGenerator<T> baseGenerator;
    private final ExecutorService executorService;
    private final boolean useParallelProcessing;
    private final int batchSize;
    private final ProgressCallback progressCallback;
    
    public BatchDataGenerator(EnhancedDataGenerator<T> baseGenerator) {
        this(baseGenerator, true, 1000, null);
    }
    
    public BatchDataGenerator(EnhancedDataGenerator<T> baseGenerator, 
                             boolean useParallelProcessing, 
                             int batchSize,
                             ProgressCallback progressCallback) {
        this.baseGenerator = baseGenerator;
        this.useParallelProcessing = useParallelProcessing;
        this.batchSize = batchSize;
        this.progressCallback = progressCallback;
        
        if (useParallelProcessing) {
            int processorCount = Runtime.getRuntime().availableProcessors();
            this.executorService = Executors.newFixedThreadPool(processorCount);
        } else {
            this.executorService = null;
        }
    }
    
    @Override
    public T generate(GenerationContext context) {
        return baseGenerator.generate(context);
    }
    
    @Override
    public List<T> generateBatch(GenerationContext context, int count) {
        if (count <= 0) {
            return new ArrayList<>();
        }
        
        long startTime = System.currentTimeMillis();
        List<T> result;
        
        if (useParallelProcessing && count > batchSize && executorService != null) {
            result = generateBatchParallel(context, count);
        } else {
            result = generateBatchSequential(context, count);
        }
        
        long endTime = System.currentTimeMillis();
        
        if (progressCallback != null) {
            progressCallback.onCompleted(count, endTime - startTime);
        }
        
        return result;
    }
    
    /**
     * 顺序批量生成
     */
    private List<T> generateBatchSequential(GenerationContext context, int count) {
        List<T> result = new ArrayList<>(count);
        
        for (int i = 0; i < count; i++) {
            result.add(baseGenerator.generate(context));
            
            if (progressCallback != null && i % 100 == 0) {
                progressCallback.onProgress(i, count);
            }
        }
        
        return result;
    }
    
    /**
     * 并行批量生成
     */
    private List<T> generateBatchParallel(GenerationContext context, int count) {
        List<Future<List<T>>> futures = new ArrayList<>();
        int numBatches = (count + batchSize - 1) / batchSize;
        
        for (int i = 0; i < numBatches; i++) {
            final int batchStart = i * batchSize;
            final int batchEnd = Math.min(batchStart + batchSize, count);
            final int batchCount = batchEnd - batchStart;
            
            Future<List<T>> future = executorService.submit(() -> {
                List<T> batchResult = new ArrayList<>(batchCount);
                for (int j = 0; j < batchCount; j++) {
                    batchResult.add(baseGenerator.generate(context));
                }
                return batchResult;
            });
            
            futures.add(future);
        }
        
        // 收集结果
        List<T> result = new ArrayList<>(count);
        int completedBatches = 0;
        
        for (Future<List<T>> future : futures) {
            try {
                List<T> batchResult = future.get();
                result.addAll(batchResult);
                completedBatches++;
                
                if (progressCallback != null) {
                    int completedItems = completedBatches * batchSize;
                    progressCallback.onProgress(Math.min(completedItems, count), count);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Batch generation failed", e);
            }
        }
        
        return result;
    }
    
    @Override
    public Set<T> generateUniqueSet(GenerationContext context, int count, int maxAttempts) {
        Set<T> result = ConcurrentHashMap.newKeySet();
        int attempts = 0;
        
        if (useParallelProcessing && executorService != null) {
            return generateUniqueSetParallel(context, count, maxAttempts);
        }
        
        while (result.size() < count && attempts < maxAttempts) {
            T item = baseGenerator.generate(context);
            result.add(item);
            attempts++;
            
            if (progressCallback != null && attempts % 1000 == 0) {
                progressCallback.onProgress(result.size(), count);
            }
        }
        
        return result;
    }
    
    /**
     * 并行生成唯一数据集
     */
    private Set<T> generateUniqueSetParallel(GenerationContext context, int count, int maxAttempts) {
        Set<T> result = ConcurrentHashMap.newKeySet();
        int batchAttempts = maxAttempts / 4; // 分批处理
        
        List<Future<Set<T>>> futures = new ArrayList<>();
        
        for (int i = 0; i < 4; i++) {
            Future<Set<T>> future = executorService.submit(() -> {
                Set<T> batchResult = new HashSet<>();
                int attempts = 0;
                
                while (result.size() + batchResult.size() < count && attempts < batchAttempts) {
                    T item = baseGenerator.generate(context);
                    if (!result.contains(item)) {
                        batchResult.add(item);
                    }
                    attempts++;
                }
                
                return batchResult;
            });
            futures.add(future);
        }
        
        for (Future<Set<T>> future : futures) {
            try {
                Set<T> batchResult = future.get();
                result.addAll(batchResult);
                
                if (progressCallback != null) {
                    progressCallback.onProgress(result.size(), count);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Unique set generation failed", e);
            }
        }
        
        return result;
    }
    
    /**
     * 生成大规模数据集
     */
    public DataSet<T> generateLargeDataset(GenerationContext context, int count) {
        long startTime = System.currentTimeMillis();
        
        List<T> data = generateBatch(context, count);
        DataStatistics statistics = getStatistics(data);
        
        long endTime = System.currentTimeMillis();
        long generationTime = endTime - startTime;
        
        return new DataSet<>(data, statistics, generationTime);
    }
    
    /**
     * 分页生成数据
     */
    public PagedResult<T> generatePaged(GenerationContext context, int pageSize, int pageNumber) {
        if (pageSize <= 0 || pageNumber < 0) {
            throw new IllegalArgumentException("Invalid page parameters");
        }
        
        int startIndex = pageNumber * pageSize;
        List<T> pageData = generateBatch(context, pageSize);
        
        return new PagedResult<>(pageData, pageNumber, pageSize, startIndex);
    }
    
    /**
     * 流式生成数据
     */
    public Iterator<T> generateStream(GenerationContext context) {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return true; // 无限流
            }
            
            @Override
            public T next() {
                return baseGenerator.generate(context);
            }
        };
    }
    
    /**
     * 关闭资源
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 进度回调接口
     */
    public interface ProgressCallback {
        void onProgress(int completed, int total);
        void onCompleted(int total, long timeMillis);
    }
    
    /**
     * 数据集类
     */
    public static class DataSet<T> {
        public final List<T> data;
        public final EnhancedDataGenerator.DataStatistics statistics;
        public final long generationTimeMillis;
        
        public DataSet(List<T> data, EnhancedDataGenerator.DataStatistics statistics, long generationTimeMillis) {
            this.data = data;
            this.statistics = statistics;
            this.generationTimeMillis = generationTimeMillis;
        }
        
        public double getGenerationRate() {
            return generationTimeMillis > 0 ? (double) data.size() / generationTimeMillis * 1000 : 0;
        }
        
        @Override
        public String toString() {
            return String.format("DataSet{size=%d, time=%dms, rate=%.2f items/sec}", 
                data.size(), generationTimeMillis, getGenerationRate());
        }
    }
    
    /**
     * 分页结果类
     */
    public static class PagedResult<T> {
        public final List<T> data;
        public final int pageNumber;
        public final int pageSize;
        public final int startIndex;
        
        public PagedResult(List<T> data, int pageNumber, int pageSize, int startIndex) {
            this.data = data;
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.startIndex = startIndex;
        }
        
        public boolean hasData() {
            return data != null && !data.isEmpty();
        }
        
        @Override
        public String toString() {
            return String.format("PagedResult{page=%d, size=%d, items=%d}", 
                pageNumber, pageSize, data != null ? data.size() : 0);
        }
    }
    
    /**
     * 生成性能报告
     */
    public PerformanceReport generatePerformanceReport(GenerationContext context, int[] testSizes) {
        Map<Integer, Long> performanceData = new HashMap<>();
        
        for (int size : testSizes) {
            long startTime = System.currentTimeMillis();
            generateBatch(context, size);
            long endTime = System.currentTimeMillis();
            
            performanceData.put(size, endTime - startTime);
        }
        
        return new PerformanceReport(performanceData, useParallelProcessing, batchSize);
    }
    
    /**
     * 性能报告类
     */
    public static class PerformanceReport {
        public final Map<Integer, Long> performanceData;
        public final boolean parallelProcessing;
        public final int batchSize;
        
        public PerformanceReport(Map<Integer, Long> performanceData, boolean parallelProcessing, int batchSize) {
            this.performanceData = performanceData;
            this.parallelProcessing = parallelProcessing;
            this.batchSize = batchSize;
        }
        
        public double getAverageRate() {
            return performanceData.entrySet().stream()
                .mapToDouble(entry -> (double) entry.getKey() / entry.getValue() * 1000)
                .average()
                .orElse(0.0);
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Performance Report:\n");
            sb.append("Parallel Processing: ").append(parallelProcessing).append("\n");
            sb.append("Batch Size: ").append(batchSize).append("\n");
            sb.append("Average Rate: ").append(String.format("%.2f items/sec", getAverageRate())).append("\n");
            sb.append("Test Results:\n");
            
            performanceData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    double rate = (double) entry.getKey() / entry.getValue() * 1000;
                    sb.append(String.format("  %d items: %dms (%.2f items/sec)\n", 
                        entry.getKey(), entry.getValue(), rate));
                });
            
            return sb.toString();
        }
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("parallel_processing", "batch_size", "progress_callback", "thread_count");
    }
    
    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("useParallelProcessing", useParallelProcessing);
        config.put("batchSize", batchSize);
        config.put("hasProgressCallback", progressCallback != null);
        config.put("baseGenerator", baseGenerator.getClass().getSimpleName());
        return config;
    }
}
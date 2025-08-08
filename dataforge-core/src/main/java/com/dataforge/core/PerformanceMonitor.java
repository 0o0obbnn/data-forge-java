package com.dataforge.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 性能监控器
 * 监控数据生成器的性能指标
 */
public class PerformanceMonitor {
    
    private static final PerformanceMonitor INSTANCE = new PerformanceMonitor();
    
    // 性能指标
    private final Map<String, GeneratorMetrics> generatorMetrics = new ConcurrentHashMap<>();
    private final AtomicLong totalOperations = new AtomicLong(0);
    private final AtomicLong totalTime = new AtomicLong(0);
    private final long startTime = System.currentTimeMillis();
    
    // 监控配置
    private boolean enabled = true;
    private int maxMetricsHistory = 1000;
    
    private PerformanceMonitor() {}
    
    public static PerformanceMonitor getInstance() {
        return INSTANCE;
    }
    
    /**
     * 记录操作开始
     */
    public OperationContext startOperation(String generatorName) {
        if (!enabled) {
            return OperationContext.DISABLED;
        }
        
        return new OperationContext(generatorName, System.nanoTime());
    }
    
    /**
     * 记录操作完成
     */
    public void endOperation(OperationContext context, int itemsGenerated) {
        if (!enabled || context == OperationContext.DISABLED) {
            return;
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - context.startTime;
        
        recordMetrics(context.generatorName, duration, itemsGenerated);
    }
    
    /**
     * 记录指标
     */
    private void recordMetrics(String generatorName, long durationNanos, int itemsGenerated) {
        GeneratorMetrics metrics = generatorMetrics.computeIfAbsent(
            generatorName, k -> new GeneratorMetrics(k)
        );
        
        metrics.recordOperation(durationNanos, itemsGenerated);
        
        totalOperations.incrementAndGet();
        totalTime.addAndGet(durationNanos);
    }
    
    /**
     * 获取生成器性能指标
     */
    public GeneratorMetrics getGeneratorMetrics(String generatorName) {
        return generatorMetrics.get(generatorName);
    }
    
    /**
     * 获取所有性能指标
     */
    public Map<String, GeneratorMetrics> getAllMetrics() {
        return new HashMap<>(generatorMetrics);
    }
    
    /**
     * 获取总体性能报告
     */
    public OverallPerformanceReport getOverallReport() {
        long currentTime = System.currentTimeMillis();
        long uptimeMs = currentTime - startTime;
        
        return new OverallPerformanceReport(
            totalOperations.get(),
            totalTime.get(),
            uptimeMs,
            generatorMetrics.size(),
            calculateTotalThroughput()
        );
    }
    
    /**
     * 计算总体吞吐量
     */
    private double calculateTotalThroughput() {
        long totalItems = generatorMetrics.values().stream()
            .mapToLong(m -> m.getTotalItemsGenerated())
            .sum();
        
        long uptimeMs = System.currentTimeMillis() - startTime;
        return uptimeMs > 0 ? (double) totalItems / uptimeMs * 1000 : 0;
    }
    
    /**
     * 获取性能排行榜
     */
    public List<GeneratorRanking> getPerformanceRanking() {
        return generatorMetrics.values().stream()
            .map(metrics -> new GeneratorRanking(
                metrics.getGeneratorName(),
                metrics.getAverageLatency(),
                metrics.getThroughput(),
                metrics.getTotalOperations()
            ))
            .sorted(Comparator.comparing(GeneratorRanking::getThroughput).reversed())
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 重置所有指标
     */
    public void reset() {
        generatorMetrics.clear();
        totalOperations.set(0);
        totalTime.set(0);
    }
    
    /**
     * 启用/禁用监控
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * 操作上下文
     */
    public static class OperationContext {
        public static final OperationContext DISABLED = new OperationContext("", 0);
        
        final String generatorName;
        final long startTime;
        
        OperationContext(String generatorName, long startTime) {
            this.generatorName = generatorName;
            this.startTime = startTime;
        }
    }
    
    /**
     * 生成器指标类
     */
    public static class GeneratorMetrics {
        private final String generatorName;
        private final LongAdder totalOperations = new LongAdder();
        private final LongAdder totalTime = new LongAdder();
        private final LongAdder totalItems = new LongAdder();
        private final List<Long> recentLatencies = Collections.synchronizedList(new ArrayList<>());
        
        private volatile long minLatency = Long.MAX_VALUE;
        private volatile long maxLatency = Long.MIN_VALUE;
        private final long creationTime = System.currentTimeMillis();
        
        public GeneratorMetrics(String generatorName) {
            this.generatorName = generatorName;
        }
        
        void recordOperation(long durationNanos, int itemsGenerated) {
            totalOperations.increment();
            totalTime.add(durationNanos);
            totalItems.add(itemsGenerated);
            
            // 更新最小最大延迟
            updateLatencyBounds(durationNanos);
            
            // 记录最近的延迟（用于计算百分位数）
            synchronized (recentLatencies) {
                recentLatencies.add(durationNanos);
                if (recentLatencies.size() > 1000) {
                    recentLatencies.remove(0);
                }
            }
        }
        
        private void updateLatencyBounds(long latency) {
            if (latency < minLatency) {
                minLatency = latency;
            }
            if (latency > maxLatency) {
                maxLatency = latency;
            }
        }
        
        public String getGeneratorName() {
            return generatorName;
        }
        
        public long getTotalOperations() {
            return totalOperations.sum();
        }
        
        public long getTotalItemsGenerated() {
            return totalItems.sum();
        }
        
        public double getAverageLatency() {
            long ops = totalOperations.sum();
            return ops > 0 ? (double) totalTime.sum() / ops / 1_000_000 : 0; // 转换为毫秒
        }
        
        public double getThroughput() {
            long elapsedMs = System.currentTimeMillis() - creationTime;
            return elapsedMs > 0 ? (double) totalItems.sum() / elapsedMs * 1000 : 0; // 每秒项目数
        }
        
        public long getMinLatency() {
            return minLatency == Long.MAX_VALUE ? 0 : minLatency / 1_000_000; // 毫秒
        }
        
        public long getMaxLatency() {
            return maxLatency == Long.MIN_VALUE ? 0 : maxLatency / 1_000_000; // 毫秒
        }
        
        public double getPercentile(double percentile) {
            List<Long> latencies;
            synchronized (recentLatencies) {
                latencies = new ArrayList<>(recentLatencies);
            }
            
            if (latencies.isEmpty()) {
                return 0;
            }
            
            Collections.sort(latencies);
            int index = (int) Math.ceil(percentile * latencies.size()) - 1;
            index = Math.max(0, Math.min(index, latencies.size() - 1));
            
            return latencies.get(index) / 1_000_000.0; // 毫秒
        }
        
        @Override
        public String toString() {
            return String.format(
                "GeneratorMetrics{name='%s', operations=%d, avgLatency=%.2fms, " +
                "throughput=%.2f items/sec, p95=%.2fms, p99=%.2fms}",
                generatorName, getTotalOperations(), getAverageLatency(),
                getThroughput(), getPercentile(0.95), getPercentile(0.99)
            );
        }
    }
    
    /**
     * 总体性能报告
     */
    public static class OverallPerformanceReport {
        public final long totalOperations;
        public final long totalTimeNanos;
        public final long uptimeMs;
        public final int activeGenerators;
        public final double overallThroughput;
        
        public OverallPerformanceReport(long totalOperations, long totalTimeNanos, 
                                      long uptimeMs, int activeGenerators, double overallThroughput) {
            this.totalOperations = totalOperations;
            this.totalTimeNanos = totalTimeNanos;
            this.uptimeMs = uptimeMs;
            this.activeGenerators = activeGenerators;
            this.overallThroughput = overallThroughput;
        }
        
        public double getAverageLatency() {
            return totalOperations > 0 ? (double) totalTimeNanos / totalOperations / 1_000_000 : 0;
        }
        
        public double getOperationsPerSecond() {
            return uptimeMs > 0 ? (double) totalOperations / uptimeMs * 1000 : 0;
        }
        
        @Override
        public String toString() {
            return String.format(
                "OverallPerformanceReport{operations=%d, avgLatency=%.2fms, " +
                "opsPerSec=%.2f, throughput=%.2f items/sec, uptime=%dms, generators=%d}",
                totalOperations, getAverageLatency(), getOperationsPerSecond(),
                overallThroughput, uptimeMs, activeGenerators
            );
        }
    }
    
    /**
     * 生成器排名
     */
    public static class GeneratorRanking {
        private final String generatorName;
        private final double averageLatency;
        private final double throughput;
        private final long totalOperations;
        
        public GeneratorRanking(String generatorName, double averageLatency, 
                              double throughput, long totalOperations) {
            this.generatorName = generatorName;
            this.averageLatency = averageLatency;
            this.throughput = throughput;
            this.totalOperations = totalOperations;
        }
        
        public String getGeneratorName() { return generatorName; }
        public double getAverageLatency() { return averageLatency; }
        public double getThroughput() { return throughput; }
        public long getTotalOperations() { return totalOperations; }
        
        @Override
        public String toString() {
            return String.format("%s: %.2f items/sec (%.2fms avg, %d ops)",
                generatorName, throughput, averageLatency, totalOperations);
        }
    }
}
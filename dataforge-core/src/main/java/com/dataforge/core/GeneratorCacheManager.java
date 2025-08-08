package com.dataforge.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据生成器缓存管理器
 * 提供缓存机制以提高生成器性能
 */
public class GeneratorCacheManager {
    
    private static final GeneratorCacheManager INSTANCE = new GeneratorCacheManager();
    
    // 生成器实例缓存
    private final Map<String, DataGenerator<?>> generatorCache = new ConcurrentHashMap<>();
    
    // 生成数据缓存
    private final Map<String, CacheEntry<?>> dataCache = new ConcurrentHashMap<>();
    
    // 缓存统计
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    private final AtomicLong totalGenerations = new AtomicLong(0);
    
    // 缓存配置
    private int maxCacheSize = 10000;
    private long cacheExpirationMs = 300000; // 5分钟
    private boolean enableDataCache = true;
    private boolean enableGeneratorCache = true;
    
    private GeneratorCacheManager() {}
    
    public static GeneratorCacheManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * 获取缓存的生成器实例
     */
    @SuppressWarnings("unchecked")
    public <T> DataGenerator<T> getCachedGenerator(String generatorName) {
        if (!enableGeneratorCache) {
            return null;
        }
        
        DataGenerator<?> generator = generatorCache.get(generatorName);
        if (generator != null) {
            cacheHits.incrementAndGet();
            return (DataGenerator<T>) generator;
        }
        
        cacheMisses.incrementAndGet();
        return null;
    }
    
    /**
     * 缓存生成器实例
     */
    public void cacheGenerator(String generatorName, DataGenerator<?> generator) {
        if (enableGeneratorCache && generator != null) {
            generatorCache.put(generatorName, generator);
        }
    }
    
    /**
     * 获取缓存的数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getCachedData(String key) {
        if (!enableDataCache) {
            return null;
        }
        
        CacheEntry<?> entry = dataCache.get(key);
        if (entry != null && !entry.isExpired()) {
            cacheHits.incrementAndGet();
            return (T) entry.data;
        }
        
        if (entry != null && entry.isExpired()) {
            dataCache.remove(key);
        }
        
        cacheMisses.incrementAndGet();
        return null;
    }
    
    /**
     * 缓存数据
     */
    public <T> void cacheData(String key, T data) {
        if (!enableDataCache || data == null) {
            return;
        }
        
        // 检查缓存大小限制
        if (dataCache.size() >= maxCacheSize) {
            cleanup();
        }
        
        long expirationTime = System.currentTimeMillis() + cacheExpirationMs;
        dataCache.put(key, new CacheEntry<>(data, expirationTime));
    }
    
    /**
     * 生成缓存键
     */
    public String generateCacheKey(String generatorName, GenerationContext context) {
        StringBuilder keyBuilder = new StringBuilder(generatorName);
        
        // 添加上下文参数到键中
        if (context.getSeed() != null) {
            keyBuilder.append("_seed:").append(context.getSeed());
        }
        
        keyBuilder.append("_count:").append(context.getCount());
        
        // 添加参数
        for (Map.Entry<String, Object> param : context.getParameters().entrySet()) {
            keyBuilder.append("_").append(param.getKey()).append(":").append(param.getValue());
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 清理过期缓存
     */
    public void cleanup() {
        long currentTime = System.currentTimeMillis();
        
        dataCache.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
        
        // 如果仍然超过限制，删除最旧的条目
        if (dataCache.size() >= maxCacheSize) {
            List<Map.Entry<String, CacheEntry<?>>> entries = new ArrayList<>(dataCache.entrySet());
            entries.sort(Comparator.comparing(e -> e.getValue().creationTime));
            
            int toRemove = dataCache.size() - maxCacheSize / 2;
            for (int i = 0; i < toRemove && i < entries.size(); i++) {
                dataCache.remove(entries.get(i).getKey());
            }
        }
    }
    
    /**
     * 清空所有缓存
     */
    public void clearAll() {
        dataCache.clear();
        generatorCache.clear();
        cacheHits.set(0);
        cacheMisses.set(0);
        totalGenerations.set(0);
    }
    
    /**
     * 获取缓存统计信息
     */
    public CacheStatistics getStatistics() {
        long hits = cacheHits.get();
        long misses = cacheMisses.get();
        long total = hits + misses;
        double hitRatio = total > 0 ? (double) hits / total : 0.0;
        
        return new CacheStatistics(
            hits, misses, hitRatio,
            generatorCache.size(), dataCache.size(),
            totalGenerations.get()
        );
    }
    
    /**
     * 配置缓存设置
     */
    public void configure(CacheConfiguration config) {
        this.maxCacheSize = config.maxCacheSize;
        this.cacheExpirationMs = config.cacheExpirationMs;
        this.enableDataCache = config.enableDataCache;
        this.enableGeneratorCache = config.enableGeneratorCache;
    }
    
    /**
     * 记录生成操作
     */
    public void recordGeneration() {
        totalGenerations.incrementAndGet();
    }
    
    /**
     * 缓存条目类
     */
    private static class CacheEntry<T> {
        final T data;
        final long creationTime;
        final long expirationTime;
        
        CacheEntry(T data, long expirationTime) {
            this.data = data;
            this.creationTime = System.currentTimeMillis();
            this.expirationTime = expirationTime;
        }
        
        boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }
        
        boolean isExpired(long currentTime) {
            return currentTime > expirationTime;
        }
    }
    
    /**
     * 缓存统计信息类
     */
    public static class CacheStatistics {
        public final long cacheHits;
        public final long cacheMisses;
        public final double hitRatio;
        public final int generatorCacheSize;
        public final int dataCacheSize;
        public final long totalGenerations;
        
        public CacheStatistics(long cacheHits, long cacheMisses, double hitRatio,
                             int generatorCacheSize, int dataCacheSize, long totalGenerations) {
            this.cacheHits = cacheHits;
            this.cacheMisses = cacheMisses;
            this.hitRatio = hitRatio;
            this.generatorCacheSize = generatorCacheSize;
            this.dataCacheSize = dataCacheSize;
            this.totalGenerations = totalGenerations;
        }
        
        @Override
        public String toString() {
            return String.format(
                "CacheStatistics{hits=%d, misses=%d, hitRatio=%.2f%%, " +
                "generatorCache=%d, dataCache=%d, totalGenerations=%d}",
                cacheHits, cacheMisses, hitRatio * 100,
                generatorCacheSize, dataCacheSize, totalGenerations
            );
        }
    }
    
    /**
     * 缓存配置类
     */
    public static class CacheConfiguration {
        public int maxCacheSize = 10000;
        public long cacheExpirationMs = 300000; // 5分钟
        public boolean enableDataCache = true;
        public boolean enableGeneratorCache = true;
        
        public CacheConfiguration() {}
        
        public CacheConfiguration(int maxCacheSize, long cacheExpirationMs, 
                                boolean enableDataCache, boolean enableGeneratorCache) {
            this.maxCacheSize = maxCacheSize;
            this.cacheExpirationMs = cacheExpirationMs;
            this.enableDataCache = enableDataCache;
            this.enableGeneratorCache = enableGeneratorCache;
        }
        
        public static CacheConfiguration disabled() {
            return new CacheConfiguration(0, 0, false, false);
        }
        
        public static CacheConfiguration highPerformance() {
            return new CacheConfiguration(50000, 600000, true, true);
        }
        
        public static CacheConfiguration memoryOptimized() {
            return new CacheConfiguration(1000, 60000, true, true);
        }
    }
}
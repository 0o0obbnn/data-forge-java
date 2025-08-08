package com.dataforge.core;

/**
 * 带缓存的数据生成器包装器
 */
public class CachedDataGenerator<T> implements DataGenerator<T> {
    
    private final String generatorName;
    private final GenerationContext baseContext;
    private final GeneratorCacheManager cacheManager;
    
    public CachedDataGenerator(String generatorName, GenerationContext baseContext) {
        this.generatorName = generatorName;
        this.baseContext = baseContext;
        this.cacheManager = GeneratorCacheManager.getInstance();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public T generate(GenerationContext context) {
        String cacheKey = cacheManager.generateCacheKey(generatorName, context);
        
        // 尝试从缓存获取
        T cachedData = cacheManager.getCachedData(cacheKey);
        if (cachedData != null) {
            return cachedData;
        }
        
        // 缓存未命中，生成新数据
        DataGenerator<T> generator = (DataGenerator<T>) GeneratorFactory.createGenerator(generatorName);
        T result = generator.generate(context);
        
        // 缓存结果
        cacheManager.cacheData(cacheKey, result);
        cacheManager.recordGeneration();
        
        return result;
    }
    
    @Override
    public String getName() {
        return generatorName;
    }
}
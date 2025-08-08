package com.dataforge.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 扩展数据生成器接口
 * 提供批量生成、数据关联、相似数据生成等高级功能
 */
public interface EnhancedDataGenerator<T> extends DataGenerator<T> {
    
    /**
     * 批量生成数据
     * 
     * @param context 生成上下文
     * @param count 生成数量
     * @return 生成的数据列表
     */
    default List<T> generateBatch(GenerationContext context, int count) {
        List<T> result = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(generate(context));
        }
        return result;
    }
    
    /**
     * 生成唯一数据集合
     * 
     * @param context 生成上下文
     * @param count 期望生成数量
     * @param maxAttempts 最大尝试次数
     * @return 唯一数据集合
     */
    default Set<T> generateUniqueSet(GenerationContext context, int count, int maxAttempts) {
        Set<T> result = new java.util.LinkedHashSet<>();
        int attempts = 0;
        
        while (result.size() < count && attempts < maxAttempts) {
            result.add(generate(context));
            attempts++;
        }
        
        return result;
    }
    
    /**
     * 生成相似数据
     * 基于给定的种子数据生成相似的数据
     * 
     * @param context 生成上下文
     * @param seedData 种子数据
     * @param count 生成数量
     * @return 相似数据列表
     */
    default List<T> generateSimilar(GenerationContext context, T seedData, int count) {
        // 默认实现：仅生成普通数据
        return generateBatch(context, count);
    }
    
    /**
     * 根据模板生成数据
     * 
     * @param context 生成上下文
     * @param template 数据模板
     * @param count 生成数量
     * @return 基于模板的数据列表
     */
    default List<T> generateFromTemplate(GenerationContext context, DataTemplate<T> template, int count) {
        List<T> result = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(template.apply(generate(context)));
        }
        return result;
    }
    
    /**
     * 生成关联数据
     * 基于已有数据生成相关联的数据
     * 
     * @param context 生成上下文
     * @param relatedData 关联的已有数据
     * @return 关联数据
     */
    default T generateRelated(GenerationContext context, Map<String, Object> relatedData) {
        // 默认实现：忽略关联数据，生成普通数据
        return generate(context);
    }
    
    /**
     * 生成数据序列
     * 生成有序或有规律的数据序列
     * 
     * @param context 生成上下文
     * @param count 序列长度
     * @param sequenceType 序列类型
     * @return 数据序列
     */
    default List<T> generateSequence(GenerationContext context, int count, SequenceType sequenceType) {
        return generateBatch(context, count);
    }
    
    /**
     * 验证生成的数据
     * 
     * @param data 待验证的数据
     * @return 验证结果
     */
    default ValidationResult validate(T data) {
        return new ValidationResult(true, "数据有效", null);
    }
    
    /**
     * 获取数据统计信息
     * 
     * @param dataList 数据列表
     * @return 统计信息
     */
    default DataStatistics getStatistics(List<T> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return new DataStatistics(0, 0, 0.0);
        }
        
        int totalCount = dataList.size();
        Set<T> uniqueSet = new java.util.HashSet<>(dataList);
        int uniqueCount = uniqueSet.size();
        double uniqueRatio = (double) uniqueCount / totalCount;
        
        return new DataStatistics(totalCount, uniqueCount, uniqueRatio);
    }
    
    /**
     * 获取生成器的配置信息
     * 
     * @return 配置信息映射
     */
    default Map<String, Object> getConfiguration() {
        return new java.util.HashMap<>();
    }
    
    /**
     * 克隆生成器
     * 
     * @return 生成器的副本
     */
    default EnhancedDataGenerator<T> cloneGenerator() {
        // 默认实现，子类可以重写
        throw new UnsupportedOperationException("Clone not supported by " + this.getClass().getSimpleName());
    }
    
    /**
     * 序列类型枚举
     */
    enum SequenceType {
        ASCENDING,      // 递增序列
        DESCENDING,     // 递减序列
        RANDOM,         // 随机序列
        CYCLIC,         // 循环序列
        FIBONACCI,      // 斐波那契序列
        ARITHMETIC,     // 等差序列
        GEOMETRIC       // 等比序列
    }
    
    /**
     * 数据模板接口
     */
    @FunctionalInterface
    interface DataTemplate<T> {
        T apply(T originalData);
    }
    
    /**
     * 验证结果类
     */
    class ValidationResult {
        public final boolean isValid;
        public final String message;
        public final Exception exception;
        
        public ValidationResult(boolean isValid, String message, Exception exception) {
            this.isValid = isValid;
            this.message = message;
            this.exception = exception;
        }
        
        @Override
        public String toString() {
            return String.format("ValidationResult{valid=%s, message='%s'}", isValid, message);
        }
    }
    
    /**
     * 数据统计信息类
     */
    class DataStatistics {
        public final int totalCount;
        public final int uniqueCount;
        public final double uniqueRatio;
        public final long generationTime;
        
        public DataStatistics(int totalCount, int uniqueCount, double uniqueRatio) {
            this(totalCount, uniqueCount, uniqueRatio, System.currentTimeMillis());
        }
        
        public DataStatistics(int totalCount, int uniqueCount, double uniqueRatio, long generationTime) {
            this.totalCount = totalCount;
            this.uniqueCount = uniqueCount;
            this.uniqueRatio = uniqueRatio;
            this.generationTime = generationTime;
        }
        
        @Override
        public String toString() {
            return String.format("DataStatistics{total=%d, unique=%d, ratio=%.2f%%}", 
                totalCount, uniqueCount, uniqueRatio * 100);
        }
    }
}
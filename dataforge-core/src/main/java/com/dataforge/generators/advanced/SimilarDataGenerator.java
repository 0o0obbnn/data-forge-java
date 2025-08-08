package com.dataforge.generators.advanced;

import com.dataforge.core.EnhancedDataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 相似数据生成器
 * 基于种子数据生成相似的数据
 */
public class SimilarDataGenerator<T> implements EnhancedDataGenerator<T> {
    
    private final EnhancedDataGenerator<T> baseGenerator;
    private final SimilarityStrategy<T> similarityStrategy;
    private final double similarityDegree; // 0.0-1.0，越高越相似
    private final Random random = new Random();
    
    public SimilarDataGenerator(EnhancedDataGenerator<T> baseGenerator, 
                               SimilarityStrategy<T> similarityStrategy, 
                               double similarityDegree) {
        this.baseGenerator = baseGenerator;
        this.similarityStrategy = similarityStrategy;
        this.similarityDegree = Math.max(0.0, Math.min(1.0, similarityDegree));
    }
    
    @Override
    public T generate(GenerationContext context) {
        return baseGenerator.generate(context);
    }
    
    @Override
    public List<T> generateSimilar(GenerationContext context, T seedData, int count) {
        List<T> result = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            T similarData = similarityStrategy.generateSimilar(seedData, similarityDegree, context);
            result.add(similarData);
        }
        
        return result;
    }
    
    /**
     * 相似性策略接口
     */
    public interface SimilarityStrategy<T> {
        T generateSimilar(T seedData, double similarityDegree, GenerationContext context);
    }
    
    /**
     * 字符串相似性策略
     */
    public static class StringSimilarityStrategy implements SimilarityStrategy<String> {
        private final Random random = new Random();
        
        @Override
        public String generateSimilar(String seedData, double similarityDegree, GenerationContext context) {
            if (seedData == null || seedData.isEmpty()) {
                return seedData;
            }
            
            StringBuilder result = new StringBuilder(seedData);
            int length = seedData.length();
            
            // 计算要修改的字符数量
            int modificationsCount = (int) Math.ceil(length * (1 - similarityDegree));
            modificationsCount = Math.min(modificationsCount, length);
            
            Set<Integer> modifiedPositions = new HashSet<>();
            
            for (int i = 0; i < modificationsCount; i++) {
                int position;
                do {
                    position = random.nextInt(length);
                } while (modifiedPositions.contains(position));
                
                modifiedPositions.add(position);
                
                char originalChar = seedData.charAt(position);
                char newChar = generateSimilarChar(originalChar);
                result.setCharAt(position, newChar);
            }
            
            return result.toString();
        }
        
        private char generateSimilarChar(char originalChar) {
            if (Character.isDigit(originalChar)) {
                // 数字替换为相邻数字
                int digit = Character.getNumericValue(originalChar);
                int newDigit = Math.max(0, Math.min(9, digit + random.nextInt(3) - 1));
                return Character.forDigit(newDigit, 10);
            } else if (Character.isLetter(originalChar)) {
                // 字母替换为相邻字母
                if (Character.isLowerCase(originalChar)) {
                    int offset = random.nextInt(3) - 1; // -1, 0, 1
                    char newChar = (char) (originalChar + offset);
                    return (newChar >= 'a' && newChar <= 'z') ? newChar : originalChar;
                } else {
                    int offset = random.nextInt(3) - 1;
                    char newChar = (char) (originalChar + offset);
                    return (newChar >= 'A' && newChar <= 'Z') ? newChar : originalChar;
                }
            }
            return originalChar;
        }
    }
    
    /**
     * 数值相似性策略
     */
    public static class NumericSimilarityStrategy implements SimilarityStrategy<Number> {
        private final Random random = new Random();
        
        @Override
        public Number generateSimilar(Number seedData, double similarityDegree, GenerationContext context) {
            if (seedData == null) {
                return 0;
            }
            
            double originalValue = seedData.doubleValue();
            
            // 计算变化范围
            double maxVariation = Math.abs(originalValue) * (1 - similarityDegree);
            if (maxVariation == 0) {
                maxVariation = 1 - similarityDegree;
            }
            
            // 生成变化量
            double variation = (random.nextDouble() * 2 - 1) * maxVariation;
            double newValue = originalValue + variation;
            
            // 根据原始数据类型返回相应类型
            if (seedData instanceof Integer) {
                return (int) Math.round(newValue);
            } else if (seedData instanceof Long) {
                return Math.round(newValue);
            } else if (seedData instanceof Float) {
                return (float) newValue;
            } else {
                return newValue;
            }
        }
    }
    
    /**
     * 集合相似性策略
     */
    public static class CollectionSimilarityStrategy<E> implements SimilarityStrategy<Collection<E>> {
        private final Random random = new Random();
        
        @Override
        public Collection<E> generateSimilar(Collection<E> seedData, double similarityDegree, GenerationContext context) {
            if (seedData == null || seedData.isEmpty()) {
                return new ArrayList<>();
            }
            
            List<E> originalList = new ArrayList<>(seedData);
            List<E> result = new ArrayList<>(originalList);
            
            int originalSize = originalList.size();
            
            // 计算要修改的元素数量
            int modificationsCount = (int) Math.ceil(originalSize * (1 - similarityDegree));
            modificationsCount = Math.min(modificationsCount, originalSize);
            
            // 随机删除一些元素
            for (int i = 0; i < modificationsCount / 2 && !result.isEmpty(); i++) {
                int indexToRemove = random.nextInt(result.size());
                result.remove(indexToRemove);
            }
            
            // 随机重复一些现有元素
            for (int i = 0; i < modificationsCount / 2 && !originalList.isEmpty(); i++) {
                E elementToAdd = originalList.get(random.nextInt(originalList.size()));
                result.add(elementToAdd);
            }
            
            return result;
        }
    }
    
    /**
     * Map相似性策略
     */
    public static class MapSimilarityStrategy<K, V> implements SimilarityStrategy<Map<K, V>> {
        private final Random random = new Random();
        
        @Override
        public Map<K, V> generateSimilar(Map<K, V> seedData, double similarityDegree, GenerationContext context) {
            if (seedData == null || seedData.isEmpty()) {
                return new HashMap<>();
            }
            
            Map<K, V> result = new HashMap<>(seedData);
            List<K> keys = new ArrayList<>(seedData.keySet());
            
            // 计算要修改的键值对数量
            int modificationsCount = (int) Math.ceil(keys.size() * (1 - similarityDegree));
            modificationsCount = Math.min(modificationsCount, keys.size());
            
            Set<K> modifiedKeys = new HashSet<>();
            
            for (int i = 0; i < modificationsCount; i++) {
                K key;
                do {
                    key = keys.get(random.nextInt(keys.size()));
                } while (modifiedKeys.contains(key));
                
                modifiedKeys.add(key);
                
                // 随机选择修改方式：删除或修改值
                if (random.nextBoolean()) {
                    result.remove(key);
                } else {
                    // 这里简单地设置为null，实际应用中可以根据类型生成相似值
                    result.put(key, null);
                }
            }
            
            return result;
        }
    }
    
    /**
     * 创建字符串相似数据生成器
     */
    public static SimilarDataGenerator<String> createStringSimilarGenerator(
            EnhancedDataGenerator<String> baseGenerator, double similarityDegree) {
        return new SimilarDataGenerator<>(baseGenerator, new StringSimilarityStrategy(), similarityDegree);
    }
    
    /**
     * 创建数值相似数据生成器
     */
    public static SimilarDataGenerator<Number> createNumericSimilarGenerator(
            EnhancedDataGenerator<Number> baseGenerator, double similarityDegree) {
        return new SimilarDataGenerator<>(baseGenerator, new NumericSimilarityStrategy(), similarityDegree);
    }
    
    /**
     * 批量生成相似数据
     */
    public List<T> generateSimilarBatch(GenerationContext context, T seedData, int count, double variationRange) {
        List<T> result = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            // 每次生成时稍微调整相似度
            double adjustedSimilarity = similarityDegree + (random.nextDouble() * 2 - 1) * variationRange;
            adjustedSimilarity = Math.max(0.0, Math.min(1.0, adjustedSimilarity));
            
            T similarData = similarityStrategy.generateSimilar(seedData, adjustedSimilarity, context);
            result.add(similarData);
        }
        
        return result;
    }
    
    /**
     * 计算两个数据项的相似度
     */
    public double calculateSimilarity(T data1, T data2) {
        if (data1 == null && data2 == null) {
            return 1.0;
        }
        if (data1 == null || data2 == null) {
            return 0.0;
        }
        
        if (data1 instanceof String && data2 instanceof String) {
            return calculateStringSimilarity((String) data1, (String) data2);
        } else if (data1 instanceof Number && data2 instanceof Number) {
            return calculateNumericSimilarity((Number) data1, (Number) data2);
        } else {
            return data1.equals(data2) ? 1.0 : 0.0;
        }
    }
    
    private double calculateStringSimilarity(String str1, String str2) {
        if (str1.equals(str2)) {
            return 1.0;
        }
        
        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) {
            return 1.0;
        }
        
        int editDistance = calculateEditDistance(str1, str2);
        return 1.0 - (double) editDistance / maxLength;
    }
    
    private int calculateEditDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        
        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        
        return dp[str1.length()][str2.length()];
    }
    
    private double calculateNumericSimilarity(Number num1, Number num2) {
        double val1 = num1.doubleValue();
        double val2 = num2.doubleValue();
        
        if (val1 == val2) {
            return 1.0;
        }
        
        double maxVal = Math.max(Math.abs(val1), Math.abs(val2));
        if (maxVal == 0) {
            return 1.0;
        }
        
        double difference = Math.abs(val1 - val2);
        return Math.max(0.0, 1.0 - difference / maxVal);
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("similarity_degree", "variation_range", "modification_strategy");
    }
    
    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("similarityDegree", similarityDegree);
        config.put("strategyType", similarityStrategy.getClass().getSimpleName());
        config.put("baseGenerator", baseGenerator.getClass().getSimpleName());
        return config;
    }
}
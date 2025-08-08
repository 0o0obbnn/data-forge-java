package com.dataforge.generators.demographic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.*;

/**
 * 婚姻状况生成器
 * 支持不同年龄段的婚姻状况分布权重，符合实际人口统计特征
 */
public class MaritalStatusGenerator implements DataGenerator<String> {
    
    /**
     * 婚姻状况枚举
     */
    public enum MaritalStatus {
        SINGLE("未婚"),
        MARRIED("已婚"),
        DIVORCED("离婚"),
        WIDOWED("丧偶"),
        ANY("任意");
        
        private final String name;
        
        MaritalStatus(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private final MaritalStatus status;
    
    // 不同年龄段的婚姻状况分布权重
    private static final Map<String, Map<MaritalStatus, Double>> AGE_GROUP_WEIGHTS = new HashMap<>();
    
    static {
        // 18-25岁：主要是未婚
        Map<MaritalStatus, Double> young = new HashMap<>();
        young.put(MaritalStatus.SINGLE, 0.85);
        young.put(MaritalStatus.MARRIED, 0.14);
        young.put(MaritalStatus.DIVORCED, 0.01);
        young.put(MaritalStatus.WIDOWED, 0.00);
        AGE_GROUP_WEIGHTS.put("18-25", young);
        
        // 26-35岁：结婚高峰期
        Map<MaritalStatus, Double> adult = new HashMap<>();
        adult.put(MaritalStatus.SINGLE, 0.35);
        adult.put(MaritalStatus.MARRIED, 0.60);
        adult.put(MaritalStatus.DIVORCED, 0.05);
        adult.put(MaritalStatus.WIDOWED, 0.00);
        AGE_GROUP_WEIGHTS.put("26-35", adult);
        
        // 36-50岁：已婚为主
        Map<MaritalStatus, Double> middle = new HashMap<>();
        middle.put(MaritalStatus.SINGLE, 0.15);
        middle.put(MaritalStatus.MARRIED, 0.75);
        middle.put(MaritalStatus.DIVORCED, 0.09);
        middle.put(MaritalStatus.WIDOWED, 0.01);
        AGE_GROUP_WEIGHTS.put("36-50", middle);
        
        // 51-65岁：已婚占主导，离婚比例增加
        Map<MaritalStatus, Double> senior = new HashMap<>();
        senior.put(MaritalStatus.SINGLE, 0.08);
        senior.put(MaritalStatus.MARRIED, 0.78);
        senior.put(MaritalStatus.DIVORCED, 0.12);
        senior.put(MaritalStatus.WIDOWED, 0.02);
        AGE_GROUP_WEIGHTS.put("51-65", senior);
        
        // 65岁以上：丧偶比例增加
        Map<MaritalStatus, Double> elderly = new HashMap<>();
        elderly.put(MaritalStatus.SINGLE, 0.05);
        elderly.put(MaritalStatus.MARRIED, 0.70);
        elderly.put(MaritalStatus.DIVORCED, 0.10);
        elderly.put(MaritalStatus.WIDOWED, 0.15);
        AGE_GROUP_WEIGHTS.put("65+", elderly);
        
        // 默认权重（所有年龄段平均）
        Map<MaritalStatus, Double> defaultWeights = new HashMap<>();
        defaultWeights.put(MaritalStatus.SINGLE, 0.30);
        defaultWeights.put(MaritalStatus.MARRIED, 0.60);
        defaultWeights.put(MaritalStatus.DIVORCED, 0.08);
        defaultWeights.put(MaritalStatus.WIDOWED, 0.02);
        AGE_GROUP_WEIGHTS.put("default", defaultWeights);
    }
    
    public MaritalStatusGenerator() {
        this(MaritalStatus.ANY);
    }
    
    public MaritalStatusGenerator(MaritalStatus status) {
        this.status = status;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        if (status != MaritalStatus.ANY) {
            return status.getName();
        }
        
        // 使用默认权重分布
        Map<MaritalStatus, Double> weights = AGE_GROUP_WEIGHTS.get("default");
        return selectByWeight(weights, random);
    }
    
    /**
     * 根据年龄生成合理的婚姻状况
     * @param age 年龄
     * @param context 生成上下文
     * @return 合适的婚姻状况
     */
    public static String generateForAge(int age, GenerationContext context) {
        Random random = context.getRandom();
        
        if (age < 18) {
            return MaritalStatus.SINGLE.getName(); // 未成年人都是未婚
        }
        
        String ageGroup = getAgeGroup(age);
        Map<MaritalStatus, Double> weights = AGE_GROUP_WEIGHTS.get(ageGroup);
        
        if (weights == null) {
            weights = AGE_GROUP_WEIGHTS.get("default");
        }
        
        return selectByWeight(weights, random);
    }
    
    private static String getAgeGroup(int age) {
        if (age >= 18 && age <= 25) return "18-25";
        if (age >= 26 && age <= 35) return "26-35";
        if (age >= 36 && age <= 50) return "36-50";
        if (age >= 51 && age <= 65) return "51-65";
        if (age > 65) return "65+";
        return "default";
    }
    
    private static String selectByWeight(Map<MaritalStatus, Double> weights, Random random) {
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;
        
        for (Map.Entry<MaritalStatus, Double> entry : weights.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                return entry.getKey().getName();
            }
        }
        
        // 默认返回未婚
        return MaritalStatus.SINGLE.getName();
    }
    
    /**
     * 验证年龄和婚姻状况的合理性
     * @param age 年龄
     * @param maritalStatus 婚姻状况
     * @return 是否合理
     */
    public static boolean isValidForAge(int age, String maritalStatus) {
        if (age < 18 && !maritalStatus.equals(MaritalStatus.SINGLE.getName())) {
            return false; // 未成年人只能是未婚
        }
        
        if (age < 20 && maritalStatus.equals(MaritalStatus.WIDOWED.getName())) {
            return false; // 太年轻不太可能丧偶
        }
        
        if (age < 22 && maritalStatus.equals(MaritalStatus.DIVORCED.getName())) {
            return false; // 太年轻不太可能离婚
        }
        
        return true;
    }
    
    /**
     * 获取指定年龄段的婚姻状况分布
     * @param age 年龄
     * @return 婚姻状况分布权重
     */
    public static Map<String, Double> getMaritalDistributionForAge(int age) {
        String ageGroup = getAgeGroup(age);
        Map<MaritalStatus, Double> weights = AGE_GROUP_WEIGHTS.getOrDefault(ageGroup, AGE_GROUP_WEIGHTS.get("default"));
        
        Map<String, Double> distribution = new HashMap<>();
        for (Map.Entry<MaritalStatus, Double> entry : weights.entrySet()) {
            distribution.put(entry.getKey().getName(), entry.getValue());
        }
        return distribution;
    }
    
    /**
     * 获取所有可用的婚姻状况
     * @return 婚姻状况列表
     */
    public static List<String> getAllMaritalStatuses() {
        return Arrays.asList(
            MaritalStatus.SINGLE.getName(),
            MaritalStatus.MARRIED.getName(),
            MaritalStatus.DIVORCED.getName(),
            MaritalStatus.WIDOWED.getName()
        );
    }
    
    /**
     * 获取所有支持的年龄段
     * @return 年龄段列表
     */
    public static List<String> getSupportedAgeGroups() {
        return Arrays.asList("18-25", "26-35", "36-50", "51-65", "65+");
    }
    
    @Override
    public String getName() {
        return "marital_status";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("status", "age");
    }
}
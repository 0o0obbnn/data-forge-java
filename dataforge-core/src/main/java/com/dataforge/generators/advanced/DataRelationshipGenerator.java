package com.dataforge.generators.advanced;

import com.dataforge.core.EnhancedDataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.*;

/**
 * 数据关联生成器
 * 生成具有关联关系的数据集合
 */
public class DataRelationshipGenerator implements EnhancedDataGenerator<Map<String, Object>> {
    
    private final List<RelationshipRule> relationshipRules;
    private final Map<String, EnhancedDataGenerator<?>> fieldGenerators;
    private final Random random = new Random();
    
    public DataRelationshipGenerator() {
        this.relationshipRules = new ArrayList<>();
        this.fieldGenerators = new HashMap<>();
    }
    
    /**
     * 添加字段生成器
     */
    public DataRelationshipGenerator addFieldGenerator(String fieldName, EnhancedDataGenerator<?> generator) {
        fieldGenerators.put(fieldName, generator);
        return this;
    }
    
    /**
     * 添加关联规则
     */
    public DataRelationshipGenerator addRelationshipRule(RelationshipRule rule) {
        relationshipRules.add(rule);
        return this;
    }
    
    @Override
    public Map<String, Object> generate(GenerationContext context) {
        Map<String, Object> result = new HashMap<>();
        
        // 首先生成基础字段
        for (Map.Entry<String, EnhancedDataGenerator<?>> entry : fieldGenerators.entrySet()) {
            String fieldName = entry.getKey();
            EnhancedDataGenerator<?> generator = entry.getValue();
            result.put(fieldName, generator.generate(context));
        }
        
        // 应用关联规则
        for (RelationshipRule rule : relationshipRules) {
            rule.apply(result, context);
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> generateRelated(GenerationContext context, Map<String, Object> relatedData) {
        Map<String, Object> result = new HashMap<>();
        
        // 使用关联数据影响生成
        for (Map.Entry<String, EnhancedDataGenerator<?>> entry : fieldGenerators.entrySet()) {
            String fieldName = entry.getKey();
            EnhancedDataGenerator<?> generator = entry.getValue();
            
            // 将相关数据添加到上下文中
            GenerationContext enhancedContext = new GenerationContext(context.getCount(), context.getSeed());
            for (Map.Entry<String, Object> relatedEntry : relatedData.entrySet()) {
                enhancedContext.setParameter(relatedEntry.getKey(), relatedEntry.getValue());
            }
            
            result.put(fieldName, generator.generateRelated(enhancedContext, relatedData));
        }
        
        // 应用关联规则
        for (RelationshipRule rule : relationshipRules) {
            rule.apply(result, context);
        }
        
        return result;
    }
    
    /**
     * 生成关联数据集
     */
    public List<Map<String, Object>> generateRelatedDataset(GenerationContext context, int count) {
        List<Map<String, Object>> dataset = new ArrayList<>();
        
        // 生成第一条记录
        if (count > 0) {
            dataset.add(generate(context));
        }
        
        // 基于前一条记录生成后续记录
        for (int i = 1; i < count; i++) {
            Map<String, Object> previousRecord = dataset.get(i - 1);
            Map<String, Object> newRecord = generateRelated(context, previousRecord);
            dataset.add(newRecord);
        }
        
        return dataset;
    }
    
    /**
     * 关联规则接口
     */
    public interface RelationshipRule {
        void apply(Map<String, Object> data, GenerationContext context);
    }
    
    /**
     * 条件关联规则
     * 当满足某个条件时，设置相关字段的值
     */
    public static class ConditionalRule implements RelationshipRule {
        private final String conditionField;
        private final Object conditionValue;
        private final String targetField;
        private final Object targetValue;
        
        public ConditionalRule(String conditionField, Object conditionValue, 
                             String targetField, Object targetValue) {
            this.conditionField = conditionField;
            this.conditionValue = conditionValue;
            this.targetField = targetField;
            this.targetValue = targetValue;
        }
        
        @Override
        public void apply(Map<String, Object> data, GenerationContext context) {
            Object fieldValue = data.get(conditionField);
            if (Objects.equals(fieldValue, conditionValue)) {
                data.put(targetField, targetValue);
            }
        }
    }
    
    /**
     * 范围关联规则
     * 当某个数值字段在指定范围内时，设置相关字段
     */
    public static class RangeRule implements RelationshipRule {
        private final String sourceField;
        private final double minValue;
        private final double maxValue;
        private final String targetField;
        private final Object targetValue;
        
        public RangeRule(String sourceField, double minValue, double maxValue,
                        String targetField, Object targetValue) {
            this.sourceField = sourceField;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.targetField = targetField;
            this.targetValue = targetValue;
        }
        
        @Override
        public void apply(Map<String, Object> data, GenerationContext context) {
            Object fieldValue = data.get(sourceField);
            if (fieldValue instanceof Number) {
                double value = ((Number) fieldValue).doubleValue();
                if (value >= minValue && value <= maxValue) {
                    data.put(targetField, targetValue);
                }
            }
        }
    }
    
    /**
     * 计算关联规则
     * 基于其他字段计算目标字段的值
     */
    public static class CalculationRule implements RelationshipRule {
        private final String targetField;
        private final CalculationFunction function;
        
        public CalculationRule(String targetField, CalculationFunction function) {
            this.targetField = targetField;
            this.function = function;
        }
        
        @Override
        public void apply(Map<String, Object> data, GenerationContext context) {
            Object calculatedValue = function.calculate(data, context);
            data.put(targetField, calculatedValue);
        }
        
        @FunctionalInterface
        public interface CalculationFunction {
            Object calculate(Map<String, Object> data, GenerationContext context);
        }
    }
    
    /**
     * 序列关联规则
     * 生成递增或递减的序列值
     */
    public static class SequenceRule implements RelationshipRule {
        private final String targetField;
        private final SequenceType sequenceType;
        private int currentValue;
        private final int step;
        
        public SequenceRule(String targetField, SequenceType sequenceType, int startValue, int step) {
            this.targetField = targetField;
            this.sequenceType = sequenceType;
            this.currentValue = startValue;
            this.step = step;
        }
        
        @Override
        public void apply(Map<String, Object> data, GenerationContext context) {
            switch (sequenceType) {
                case ASCENDING:
                    data.put(targetField, currentValue);
                    currentValue += step;
                    break;
                case DESCENDING:
                    data.put(targetField, currentValue);
                    currentValue -= step;
                    break;
                default:
                    data.put(targetField, currentValue);
                    break;
            }
        }
    }
    
    /**
     * 创建用户信息关联生成器的工厂方法
     */
    public static DataRelationshipGenerator createUserProfileGenerator() {
        DataRelationshipGenerator generator = new DataRelationshipGenerator();
        
        // 添加基础字段生成器 - 这里需要实际的生成器实例
        // generator.addFieldGenerator("age", new AgeGenerator());
        // generator.addFieldGenerator("gender", new GenderGenerator());
        // generator.addFieldGenerator("email", new EmailGenerator());
        
        // 添加关联规则
        generator.addRelationshipRule(new ConditionalRule("gender", "female", "title", "Ms."));
        generator.addRelationshipRule(new ConditionalRule("gender", "male", "title", "Mr."));
        
        generator.addRelationshipRule(new RangeRule("age", 0, 18, "category", "minor"));
        generator.addRelationshipRule(new RangeRule("age", 18, 65, "category", "adult"));
        generator.addRelationshipRule(new RangeRule("age", 65, 150, "category", "senior"));
        
        // 根据年龄计算出生年份
        generator.addRelationshipRule(new CalculationRule("birthYear", (data, context) -> {
            Object ageObj = data.get("age");
            if (ageObj instanceof Number) {
                int age = ((Number) ageObj).intValue();
                return 2024 - age;
            }
            return 2000;
        }));
        
        return generator;
    }
    
    /**
     * 创建订单数据关联生成器
     */
    public static DataRelationshipGenerator createOrderDataGenerator() {
        DataRelationshipGenerator generator = new DataRelationshipGenerator();
        
        // 添加序列规则生成订单ID
        generator.addRelationshipRule(new SequenceRule("orderId", SequenceType.ASCENDING, 100001, 1));
        
        // 根据商品数量计算总价
        generator.addRelationshipRule(new CalculationRule("totalPrice", (data, context) -> {
            Object quantityObj = data.get("quantity");
            Object priceObj = data.get("unitPrice");
            
            if (quantityObj instanceof Number && priceObj instanceof Number) {
                double quantity = ((Number) quantityObj).doubleValue();
                double price = ((Number) priceObj).doubleValue();
                return Math.round(quantity * price * 100.0) / 100.0; // 保留两位小数
            }
            return 0.0;
        }));
        
        // 根据总价设置订单状态
        generator.addRelationshipRule(new RangeRule("totalPrice", 0, 100, "priority", "normal"));
        generator.addRelationshipRule(new RangeRule("totalPrice", 100, 1000, "priority", "high"));
        generator.addRelationshipRule(new RangeRule("totalPrice", 1000, Double.MAX_VALUE, "priority", "urgent"));
        
        return generator;
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("relationship_rules", "field_generators", "sequence_config");
    }
    
    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("fieldGenerators", fieldGenerators.keySet());
        config.put("relationshipRules", relationshipRules.size());
        return config;
    }
}
package com.dataforge.generators.demographic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * 学历生成器
 * 支持多种学历层次，可配置权重分布和年龄关联验证
 */
public class EducationLevelGenerator implements DataGenerator<String> {
    
    /**
     * 学历层次枚举
     */
    public enum Level {
        PRIMARY("小学"),
        JUNIOR_HIGH("初中"),
        HIGH_SCHOOL("高中"),
        COLLEGE("大专"),
        BACHELOR("本科"),
        MASTER("硕士"),
        PHD("博士"),
        ANY("任意");
        
        private final String name;
        
        Level(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * 分布类型
     */
    public enum Distribution {
        UNIFORM,    // 均匀分布
        WEIGHTED    // 加权分布
    }
    
    private final Level level;
    private final Distribution distribution;
    private final Map<Level, Double> weights;
    
    // 默认权重（符合中国教育现状）
    private static final Map<Level, Double> DEFAULT_WEIGHTS = new HashMap<>();
    static {
        DEFAULT_WEIGHTS.put(Level.PRIMARY, 0.05);      // 小学 5%
        DEFAULT_WEIGHTS.put(Level.JUNIOR_HIGH, 0.15);  // 初中 15%
        DEFAULT_WEIGHTS.put(Level.HIGH_SCHOOL, 0.30);  // 高中 30%
        DEFAULT_WEIGHTS.put(Level.COLLEGE, 0.25);      // 大专 25%
        DEFAULT_WEIGHTS.put(Level.BACHELOR, 0.20);     // 本科 20%
        DEFAULT_WEIGHTS.put(Level.MASTER, 0.04);       // 硕士 4%
        DEFAULT_WEIGHTS.put(Level.PHD, 0.01);          // 博士 1%
    }
    
    public EducationLevelGenerator() {
        this(Level.ANY, Distribution.WEIGHTED, DEFAULT_WEIGHTS);
    }
    
    public EducationLevelGenerator(Level level) {
        this(level, Distribution.WEIGHTED, DEFAULT_WEIGHTS);
    }
    
    public EducationLevelGenerator(Level level, Distribution distribution) {
        this(level, distribution, DEFAULT_WEIGHTS);
    }
    
    public EducationLevelGenerator(Level level, Distribution distribution, Map<Level, Double> weights) {
        this.level = level;
        this.distribution = distribution;
        this.weights = weights != null ? new HashMap<>(weights) : new HashMap<>(DEFAULT_WEIGHTS);
        
        // 验证权重总和
        validateWeights();
    }
    
    private void validateWeights() {
        double sum = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(sum - 1.0) > 0.01) {
            // 如果权重不为1，进行归一化
            weights.replaceAll((k, v) -> v / sum);
        }
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        List<Level> availableLevels = getAvailableLevels();
        
        if (availableLevels.isEmpty()) {
            return Level.HIGH_SCHOOL.getName(); // 默认返回高中
        }
        
        Level selectedLevel;
        if (distribution == Distribution.UNIFORM) {
            selectedLevel = availableLevels.get(random.nextInt(availableLevels.size()));
        } else {
            selectedLevel = selectByWeight(availableLevels, random);
        }
        
        return selectedLevel.getName();
    }
    
    private List<Level> getAvailableLevels() {
        if (level == Level.ANY) {
            return Arrays.asList(Level.PRIMARY, Level.JUNIOR_HIGH, Level.HIGH_SCHOOL, 
                               Level.COLLEGE, Level.BACHELOR, Level.MASTER, Level.PHD);
        } else {
            return Arrays.asList(level);
        }
    }
    
    private Level selectByWeight(List<Level> levels, Random random) {
        double totalWeight = levels.stream()
                .mapToDouble(l -> weights.getOrDefault(l, 0.0))
                .sum();
        
        if (totalWeight <= 0) {
            return levels.get(random.nextInt(levels.size()));
        }
        
        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;
        
        for (Level lvl : levels) {
            cumulativeWeight += weights.getOrDefault(lvl, 0.0);
            if (randomValue <= cumulativeWeight) {
                return lvl;
            }
        }
        
        return levels.get(levels.size() - 1); // 默认返回最后一个
    }
    
    /**
     * 根据年龄验证学历的合理性
     * @param age 年龄
     * @param education 学历
     * @return 是否合理
     */
    public static boolean isValidForAge(int age, String education) {
        if (age < 6) return false; // 6岁以下无学历
        
        switch (education) {
            case "小学":
                return age >= 6;
            case "初中":
                return age >= 12;
            case "高中":
                return age >= 15;
            case "大专":
                return age >= 18;
            case "本科":
                return age >= 20;
            case "硕士":
                return age >= 22;
            case "博士":
                return age >= 25;
            default:
                return true;
        }
    }
    
    /**
     * 根据年龄生成合理的学历
     * @param age 年龄
     * @param context 生成上下文
     * @return 合适的学历
     */
    public static String generateForAge(int age, GenerationContext context) {
        Random random = context.getRandom();
        
        if (age < 6) return "无";
        if (age < 12) return Level.PRIMARY.getName();
        if (age < 15) return random.nextBoolean() ? Level.PRIMARY.getName() : Level.JUNIOR_HIGH.getName();
        if (age < 18) {
            Level[] options = {Level.JUNIOR_HIGH, Level.HIGH_SCHOOL};
            return options[random.nextInt(options.length)].getName();
        }
        if (age < 22) {
            Level[] options = {Level.HIGH_SCHOOL, Level.COLLEGE};
            return options[random.nextInt(options.length)].getName();
        }
        if (age < 25) {
            Level[] options = {Level.HIGH_SCHOOL, Level.COLLEGE, Level.BACHELOR};
            return options[random.nextInt(options.length)].getName();
        }
        if (age < 28) {
            Level[] options = {Level.COLLEGE, Level.BACHELOR, Level.MASTER};
            return options[random.nextInt(options.length)].getName();
        }
        
        // 28岁以上，所有学历都可能
        EducationLevelGenerator generator = new EducationLevelGenerator();
        return generator.generate(context);
    }
    
    /**
     * 获取所有可用的学历层次名称
     * @return 学历层次名称列表
     */
    public static List<String> getAllEducationLevels() {
        return Arrays.asList(
            Level.PRIMARY.getName(),
            Level.JUNIOR_HIGH.getName(),
            Level.HIGH_SCHOOL.getName(),
            Level.COLLEGE.getName(),
            Level.BACHELOR.getName(),
            Level.MASTER.getName(),
            Level.PHD.getName()
        );
    }
    
    @Override
    public String getName() {
        return "education_level";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("levels", "distribution", "weights");
    }
}
package com.dataforge.generators.demographic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.*;

/**
 * 血型生成器
 * 根据不同地区人口的血型分布生成血型，支持ABO血型和Rh因子
 */
public class BloodTypeGenerator implements DataGenerator<String> {
    
    /**
     * ABO血型枚举
     */
    public enum ABO {
        A("A"),
        B("B"),
        AB("AB"),
        O("O"),
        ANY("任意");
        
        private final String type;
        
        ABO(String type) {
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
    }
    
    /**
     * Rh因子枚举
     */
    public enum RhFactor {
        POSITIVE("阳性"),
        NEGATIVE("阴性"),
        ANY("任意");
        
        private final String factor;
        
        RhFactor(String factor) {
            this.factor = factor;
        }
        
        public String getFactor() {
            return factor;
        }
    }
    
    /**
     * 地区枚举（不同地区血型分布不同）
     */
    public enum Region {
        CHINA("中国"),
        ASIA("亚洲"),
        EUROPE("欧洲"),
        AFRICA("非洲"),
        WORLD("世界平均");
        
        private final String name;
        
        Region(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private final ABO aboType;
    private final RhFactor rhFactor;
    private final Region region;
    
    // 不同地区的ABO血型分布权重
    private static final Map<Region, Map<ABO, Double>> ABO_DISTRIBUTIONS = new HashMap<>();
    
    // Rh因子分布权重
    private static final Map<Region, Map<RhFactor, Double>> RH_DISTRIBUTIONS = new HashMap<>();
    
    static {
        initializeDistributions();
    }
    
    private static void initializeDistributions() {
        // 中国血型分布
        Map<ABO, Double> chinaABO = new HashMap<>();
        chinaABO.put(ABO.A, 0.28);      // A型 28%
        chinaABO.put(ABO.B, 0.24);      // B型 24%
        chinaABO.put(ABO.AB, 0.09);     // AB型 9%
        chinaABO.put(ABO.O, 0.39);      // O型 39%
        ABO_DISTRIBUTIONS.put(Region.CHINA, chinaABO);
        
        Map<RhFactor, Double> chinaRh = new HashMap<>();
        chinaRh.put(RhFactor.POSITIVE, 0.995);  // Rh阳性 99.5%
        chinaRh.put(RhFactor.NEGATIVE, 0.005);  // Rh阴性 0.5%
        RH_DISTRIBUTIONS.put(Region.CHINA, chinaRh);
        
        // 亚洲血型分布
        Map<ABO, Double> asiaABO = new HashMap<>();
        asiaABO.put(ABO.A, 0.27);
        asiaABO.put(ABO.B, 0.25);
        asiaABO.put(ABO.AB, 0.08);
        asiaABO.put(ABO.O, 0.40);
        ABO_DISTRIBUTIONS.put(Region.ASIA, asiaABO);
        
        Map<RhFactor, Double> asiaRh = new HashMap<>();
        asiaRh.put(RhFactor.POSITIVE, 0.990);
        asiaRh.put(RhFactor.NEGATIVE, 0.010);
        RH_DISTRIBUTIONS.put(Region.ASIA, asiaRh);
        
        // 欧洲血型分布
        Map<ABO, Double> europeABO = new HashMap<>();
        europeABO.put(ABO.A, 0.42);
        europeABO.put(ABO.B, 0.10);
        europeABO.put(ABO.AB, 0.04);
        europeABO.put(ABO.O, 0.44);
        ABO_DISTRIBUTIONS.put(Region.EUROPE, europeABO);
        
        Map<RhFactor, Double> europeRh = new HashMap<>();
        europeRh.put(RhFactor.POSITIVE, 0.85);
        europeRh.put(RhFactor.NEGATIVE, 0.15);
        RH_DISTRIBUTIONS.put(Region.EUROPE, europeRh);
        
        // 非洲血型分布
        Map<ABO, Double> africaABO = new HashMap<>();
        africaABO.put(ABO.A, 0.27);
        africaABO.put(ABO.B, 0.20);
        africaABO.put(ABO.AB, 0.04);
        africaABO.put(ABO.O, 0.49);
        ABO_DISTRIBUTIONS.put(Region.AFRICA, africaABO);
        
        Map<RhFactor, Double> africaRh = new HashMap<>();
        africaRh.put(RhFactor.POSITIVE, 0.95);
        africaRh.put(RhFactor.NEGATIVE, 0.05);
        RH_DISTRIBUTIONS.put(Region.AFRICA, africaRh);
        
        // 世界平均血型分布
        Map<ABO, Double> worldABO = new HashMap<>();
        worldABO.put(ABO.A, 0.31);
        worldABO.put(ABO.B, 0.16);
        worldABO.put(ABO.AB, 0.06);
        worldABO.put(ABO.O, 0.47);
        ABO_DISTRIBUTIONS.put(Region.WORLD, worldABO);
        
        Map<RhFactor, Double> worldRh = new HashMap<>();
        worldRh.put(RhFactor.POSITIVE, 0.85);
        worldRh.put(RhFactor.NEGATIVE, 0.15);
        RH_DISTRIBUTIONS.put(Region.WORLD, worldRh);
    }
    
    public BloodTypeGenerator() {
        this(ABO.ANY, RhFactor.ANY, Region.CHINA);
    }
    
    public BloodTypeGenerator(Region region) {
        this(ABO.ANY, RhFactor.ANY, region);
    }
    
    public BloodTypeGenerator(ABO aboType, RhFactor rhFactor, Region region) {
        this.aboType = aboType;
        this.rhFactor = rhFactor;
        this.region = region;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // 生成ABO血型
        ABO selectedABO;
        if (aboType != ABO.ANY) {
            selectedABO = aboType;
        } else {
            selectedABO = selectABOByWeight(region, random);
        }
        
        // 生成Rh因子
        RhFactor selectedRh;
        if (rhFactor != RhFactor.ANY) {
            selectedRh = rhFactor;
        } else {
            selectedRh = selectRhByWeight(region, random);
        }
        
        // 组合血型字符串
        return formatBloodType(selectedABO, selectedRh);
    }
    
    private ABO selectABOByWeight(Region region, Random random) {
        Map<ABO, Double> weights = ABO_DISTRIBUTIONS.get(region);
        if (weights == null) {
            weights = ABO_DISTRIBUTIONS.get(Region.WORLD);
        }
        
        double randomValue = random.nextDouble();
        double cumulativeWeight = 0.0;
        
        for (Map.Entry<ABO, Double> entry : weights.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                return entry.getKey();
            }
        }
        
        return ABO.O; // 默认返回O型
    }
    
    private RhFactor selectRhByWeight(Region region, Random random) {
        Map<RhFactor, Double> weights = RH_DISTRIBUTIONS.get(region);
        if (weights == null) {
            weights = RH_DISTRIBUTIONS.get(Region.WORLD);
        }
        
        double randomValue = random.nextDouble();
        double cumulativeWeight = 0.0;
        
        for (Map.Entry<RhFactor, Double> entry : weights.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                return entry.getKey();
            }
        }
        
        return RhFactor.POSITIVE; // 默认返回阳性
    }
    
    private String formatBloodType(ABO abo, RhFactor rh) {
        String rhSymbol = rh == RhFactor.POSITIVE ? "+" : "-";
        return abo.getType() + rhSymbol;
    }
    
    /**
     * 获取指定地区的血型分布统计
     * @param region 地区
     * @return 血型分布统计
     */
    public static Map<String, Double> getBloodTypeDistribution(Region region) {
        Map<String, Double> distribution = new HashMap<>();
        
        Map<ABO, Double> aboWeights = ABO_DISTRIBUTIONS.get(region);
        Map<RhFactor, Double> rhWeights = RH_DISTRIBUTIONS.get(region);
        
        if (aboWeights == null || rhWeights == null) {
            return distribution;
        }
        
        for (Map.Entry<ABO, Double> aboEntry : aboWeights.entrySet()) {
            for (Map.Entry<RhFactor, Double> rhEntry : rhWeights.entrySet()) {
                String bloodType = aboEntry.getKey().getType() + 
                                 (rhEntry.getKey() == RhFactor.POSITIVE ? "+" : "-");
                double probability = aboEntry.getValue() * rhEntry.getValue();
                distribution.put(bloodType, probability);
            }
        }
        
        return distribution;
    }
    
    /**
     * 验证血型格式是否正确
     * @param bloodType 血型字符串
     * @return 是否有效
     */
    public static boolean isValidBloodType(String bloodType) {
        if (bloodType == null || bloodType.length() < 2) {
            return false;
        }
        
        String aboType = bloodType.substring(0, bloodType.length() - 1);
        String rhFactor = bloodType.substring(bloodType.length() - 1);
        
        return (aboType.equals("A") || aboType.equals("B") || 
                aboType.equals("AB") || aboType.equals("O")) &&
               (rhFactor.equals("+") || rhFactor.equals("-"));
    }
    
    /**
     * 获取所有可能的血型
     * @return 血型列表
     */
    public static List<String> getAllBloodTypes() {
        List<String> bloodTypes = new ArrayList<>();
        for (ABO abo : Arrays.asList(ABO.A, ABO.B, ABO.AB, ABO.O)) {
            for (RhFactor rh : Arrays.asList(RhFactor.POSITIVE, RhFactor.NEGATIVE)) {
                String rhSymbol = rh == RhFactor.POSITIVE ? "+" : "-";
                bloodTypes.add(abo.getType() + rhSymbol);
            }
        }
        return bloodTypes;
    }
    
    /**
     * 获取所有支持的地区
     * @return 地区列表
     */
    public static List<String> getSupportedRegions() {
        List<String> regions = new ArrayList<>();
        for (Region region : Region.values()) {
            regions.add(region.name());
        }
        return regions;
    }
    
    /**
     * 根据地区生成血型
     * @param region 地区
     * @param context 生成上下文
     * @return 血型
     */
    public static String generateForRegion(Region region, GenerationContext context) {
        BloodTypeGenerator generator = new BloodTypeGenerator(region);
        return generator.generate(context);
    }
    
    @Override
    public String getName() {
        return "blood_type";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("abo", "rh", "region");
    }
}
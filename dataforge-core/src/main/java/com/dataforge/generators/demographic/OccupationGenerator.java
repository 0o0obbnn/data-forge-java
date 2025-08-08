package com.dataforge.generators.demographic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 职业生成器
 * 支持按行业分类生成职业，可配置自定义职业列表
 */
public class OccupationGenerator implements DataGenerator<String> {
    
    private static final String OCCUPATIONS_FILE = "/config/occupation/occupations.txt";
    
    /**
     * 行业枚举
     */
    public enum Industry {
        IT("IT/互联网"),
        FINANCE("金融"),
        EDUCATION("教育"),
        HEALTHCARE("医疗健康"),
        MANUFACTURING("制造业"),
        GOVERNMENT("政府机关"),
        SERVICES("服务业"),
        AGRICULTURE("农业"),
        CONSTRUCTION("建筑业"),
        TRANSPORTATION("交通运输"),
        ANY("任意行业");
        
        private final String name;
        
        Industry(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private final Industry industry;
    private static final Map<Industry, List<String>> occupationsByIndustry = new HashMap<>();
    
    static {
        loadConfiguration();
    }
    
    public OccupationGenerator() {
        this(Industry.ANY);
    }
    
    public OccupationGenerator(Industry industry) {
        this.industry = industry;
    }
    
    private static void loadConfiguration() {
        // 加载职业配置
        loadOccupationsFromFile();
        
        // 初始化默认职业库
        if (occupationsByIndustry.isEmpty()) {
            initializeDefaultOccupations();
        }
        
        // 合并所有行业的职业
        List<String> allOccupations = new ArrayList<>();
        for (Industry ind : Industry.values()) {
            if (ind != Industry.ANY) {
                List<String> occupations = occupationsByIndustry.get(ind);
                if (occupations != null) {
                    allOccupations.addAll(occupations);
                }
            }
        }
        occupationsByIndustry.put(Industry.ANY, allOccupations);
    }
    
    private static void loadOccupationsFromFile() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        OccupationGenerator.class.getResourceAsStream(OCCUPATIONS_FILE), 
                        StandardCharsets.UTF_8))) {
            
            String line;
            Industry currentIndustry = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // 检查是否是行业标题
                if (line.startsWith("[") && line.endsWith("]")) {
                    String industryName = line.substring(1, line.length() - 1);
                    currentIndustry = findIndustryByName(industryName);
                    continue;
                }
                
                // 添加职业到当前行业
                if (currentIndustry != null) {
                    occupationsByIndustry.computeIfAbsent(currentIndustry, k -> new ArrayList<>()).add(line);
                }
            }
        } catch (IOException | NullPointerException e) {
            // 文件不存在时使用默认配置
        }
    }
    
    private static Industry findIndustryByName(String name) {
        for (Industry industry : Industry.values()) {
            if (industry.getName().contains(name) || name.contains(industry.getName())) {
                return industry;
            }
        }
        return null;
    }
    
    private static void initializeDefaultOccupations() {
        // IT/互联网
        occupationsByIndustry.put(Industry.IT, Arrays.asList(
            "软件工程师", "产品经理", "UI设计师", "数据分析师", "系统架构师", 
            "运维工程师", "测试工程师", "前端开发工程师", "后端开发工程师", 
            "移动端开发工程师", "算法工程师", "人工智能工程师", "网络安全工程师"
        ));
        
        // 金融
        occupationsByIndustry.put(Industry.FINANCE, Arrays.asList(
            "银行经理", "投资顾问", "理财师", "风控专员", "审计师", 
            "会计师", "金融分析师", "保险经纪人", "证券分析师", "信贷专员"
        ));
        
        // 教育
        occupationsByIndustry.put(Industry.EDUCATION, Arrays.asList(
            "小学教师", "中学教师", "大学教授", "教研员", "校长", 
            "培训师", "教育顾问", "心理咨询师", "班主任", "教务管理员"
        ));
        
        // 医疗健康
        occupationsByIndustry.put(Industry.HEALTHCARE, Arrays.asList(
            "医生", "护士", "药剂师", "医学检验师", "放射科医师", 
            "外科医生", "内科医生", "儿科医生", "妇产科医生", "心理医生", "牙医"
        ));
        
        // 制造业
        occupationsByIndustry.put(Industry.MANUFACTURING, Arrays.asList(
            "工程师", "技术员", "生产主管", "质量管理员", "设备维护员", 
            "产品设计师", "工艺工程师", "车间主任", "生产计划员", "采购员"
        ));
        
        // 政府机关
        occupationsByIndustry.put(Industry.GOVERNMENT, Arrays.asList(
            "公务员", "事业单位工作人员", "警察", "消防员", "城管", 
            "税务稽查员", "海关关员", "法官", "检察官", "律师"
        ));
        
        // 服务业
        occupationsByIndustry.put(Industry.SERVICES, Arrays.asList(
            "销售员", "客服代表", "市场专员", "人力资源专员", "行政助理", 
            "餐厅服务员", "导游", "美容师", "理发师", "保洁员", "保安"
        ));
        
        // 农业
        occupationsByIndustry.put(Industry.AGRICULTURE, Arrays.asList(
            "农民", "农业技术员", "兽医", "农产品经纪人", "农机操作员", 
            "种植技术员", "畜牧技术员", "农业科研人员", "农业推广员"
        ));
        
        // 建筑业
        occupationsByIndustry.put(Industry.CONSTRUCTION, Arrays.asList(
            "建筑工程师", "施工员", "监理工程师", "建筑设计师", "项目经理", 
            "电工", "水暖工", "瓦工", "木工", "钢筋工", "架子工"
        ));
        
        // 交通运输
        occupationsByIndustry.put(Industry.TRANSPORTATION, Arrays.asList(
            "司机", "飞行员", "船员", "列车员", "地铁司机", 
            "物流专员", "快递员", "调度员", "交通管理员", "机械师"
        ));
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        List<String> availableOccupations = occupationsByIndustry.get(industry);
        
        if (availableOccupations == null || availableOccupations.isEmpty()) {
            availableOccupations = occupationsByIndustry.get(Industry.ANY);
        }
        
        if (availableOccupations == null || availableOccupations.isEmpty()) {
            return "软件工程师"; // 默认职业
        }
        
        return availableOccupations.get(random.nextInt(availableOccupations.size()));
    }
    
    /**
     * 根据学历生成合适的职业
     * @param education 学历
     * @param context 生成上下文
     * @return 合适的职业
     */
    public static String generateForEducation(String education, GenerationContext context) {
        Random random = context.getRandom();
        
        List<String> suitableOccupations = new ArrayList<>();
        
        switch (education) {
            case "博士":
                suitableOccupations.addAll(Arrays.asList(
                    "大学教授", "科研人员", "高级工程师", "医学专家", "技术总监"
                ));
                break;
            case "硕士":
                suitableOccupations.addAll(Arrays.asList(
                    "高级工程师", "项目经理", "研发工程师", "金融分析师", "医生", "律师"
                ));
                break;
            case "本科":
                suitableOccupations.addAll(Arrays.asList(
                    "软件工程师", "会计师", "教师", "银行经理", "销售经理", "人力资源专员"
                ));
                break;
            case "大专":
                suitableOccupations.addAll(Arrays.asList(
                    "技术员", "护士", "销售员", "客服代表", "行政助理", "会计助理"
                ));
                break;
            case "高中":
                suitableOccupations.addAll(Arrays.asList(
                    "销售员", "服务员", "司机", "工人", "保安", "收银员"
                ));
                break;
            default:
                // 初中及以下
                suitableOccupations.addAll(Arrays.asList(
                    "工人", "服务员", "保洁员", "农民", "司机", "保安"
                ));
                break;
        }
        
        if (suitableOccupations.isEmpty()) {
            return "服务员";
        }
        
        return suitableOccupations.get(random.nextInt(suitableOccupations.size()));
    }
    
    /**
     * 获取指定行业的所有职业
     * @param industry 行业
     * @return 职业列表
     */
    public static List<String> getOccupationsByIndustry(Industry industry) {
        List<String> occupations = occupationsByIndustry.get(industry);
        return occupations != null ? new ArrayList<>(occupations) : new ArrayList<>();
    }
    
    /**
     * 获取所有可用的行业
     * @return 行业列表
     */
    public static List<String> getAvailableIndustries() {
        List<String> industries = new ArrayList<>();
        for (Industry industry : Industry.values()) {
            industries.add(industry.name());
        }
        return industries;
    }
    
    @Override
    public String getName() {
        return "occupation";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("industry", "file");
    }
}
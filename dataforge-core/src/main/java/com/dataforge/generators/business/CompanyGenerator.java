package com.dataforge.generators.business;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 公司名称生成器
 * 支持生成5000个不重复的公司名称
 * 支持配置文件加载行业关键词、公司类型后缀等
 */
public class CompanyGenerator implements DataGenerator<String> {
    
    private static final String CONFIG_PATH = "/config/business/";
    private static final int MAX_UNIQUE_COMPANIES = 5000;
    
    // 行业关键词
    private final List<String> industryKeywords = new ArrayList<>();
    
    // 公司类型后缀
    private final List<String> companyTypes = new ArrayList<>();
    
    // 地区前缀
    private final List<String> regionPrefixes = new ArrayList<>();
    
    // 用于确保唯一性的集合
    private final Set<String> generatedCompanies = ConcurrentHashMap.newKeySet();
    private final AtomicLong uniqueCounter = new AtomicLong(0);
    
    // 默认行业关键词
    private static final String[] DEFAULT_INDUSTRY_KEYWORDS = {
        "科技", "信息", "网络", "软件", "数据", "智能", "电子", "通信", "互联网",
        "金融", "银行", "保险", "证券", "投资", "基金", "财富", "资本", "信托",
        "制造", "工业", "机械", "设备", "材料", "能源", "电力", "化工", "钢铁",
        "医疗", "健康", "生物", "医药", "器械", "护理", "康复", "体检", "美容",
        "教育", "培训", "文化", "艺术", "传媒", "广告", "影视", "出版", "娱乐",
        "房地产", "建筑", "装饰", "物业", "置业", "开发", "建设", "工程",
        "贸易", "商业", "零售", "批发", "电商", "商城", "购物", "百货", "超市",
        "物流", "运输", "快递", "货运", "仓储", "配送", "供应链",
        "餐饮", "食品", "饮料", "餐饮", "美食", "料理", "餐厅", "酒店",
        "旅游", "出行", "航空", "交通", "客运", "租车", "票务", "景区",
        "环保", "新能源", "清洁", "节能", "绿化", "生态", "环境", "治理"
    };
    
    // 默认公司类型后缀
    private static final String[] DEFAULT_COMPANY_TYPES = {
        "有限公司", "股份有限公司", "有限责任公司", "集团公司", "控股公司",
        "科技有限公司", "网络科技有限公司", "信息技术有限公司", "软件有限公司",
        "投资有限公司", "资产管理有限公司", "咨询有限公司", "服务有限公司"
    };
    
    // 默认地区前缀
    private static final String[] DEFAULT_REGION_PREFIXES = {
        "北京", "上海", "广州", "深圳", "杭州", "南京", "成都", "武汉", "西安", "重庆",
        "天津", "苏州", "青岛", "大连", "厦门", "宁波", "无锡", "郑州", "长沙", "福州"
    };
    
    public CompanyGenerator() {
        loadDefaultData();
    }
    
    private void loadDefaultData() {
        // 加载默认数据
        industryKeywords.addAll(Arrays.asList(DEFAULT_INDUSTRY_KEYWORDS));
        companyTypes.addAll(Arrays.asList(DEFAULT_COMPANY_TYPES));
        regionPrefixes.addAll(Arrays.asList(DEFAULT_REGION_PREFIXES));
    }
    
    @Override
    public String generate(GenerationContext context) {
        boolean unique = (Boolean) context.getParameter("unique", true);
        boolean prefixRegion = (Boolean) context.getParameter("prefixRegion", true);
        String industry = (String) context.getParameter("industry", null);
        String type = (String) context.getParameter("type", null);
        
        String company;
        int attempts = 0;
        
        do {
            company = generateCompanyName(context, prefixRegion, industry, type);
            attempts++;
            
            // 如果不需要唯一性或已达到最大尝试次数，返回结果
            if (!unique || attempts > 100) {
                break;
            }
            
        } while (generatedCompanies.contains(company) && generatedCompanies.size() < MAX_UNIQUE_COMPANIES);
        
        if (unique && generatedCompanies.size() < MAX_UNIQUE_COMPANIES) {
            generatedCompanies.add(company);
            uniqueCounter.incrementAndGet();
        }
        
        return company;
    }
    
    private String generateCompanyName(GenerationContext context, boolean prefixRegion, String industry, String type) {
        Random random = context.getRandom();
        
        StringBuilder name = new StringBuilder();
        
        // 添加地区前缀（可选）
        if (prefixRegion && !regionPrefixes.isEmpty() && random.nextBoolean()) {
            name.append(regionPrefixes.get(random.nextInt(regionPrefixes.size()))).append(" ");
        }
        
        // 添加行业关键词
        String keyword;
        if (industry != null && !industry.isEmpty()) {
            keyword = industry;
        } else {
            keyword = industryKeywords.get(random.nextInt(industryKeywords.size()));
        }
        name.append(keyword);
        
        // 有一定概率添加数字后缀以增加唯一性（在公司类型后缀之前）
        if (random.nextDouble() < 0.3) { // 30%概率
            name.append(random.nextInt(100) + 1); // 添加1-100的数字
        }
        
        // 添加公司类型后缀
        String companyType;
        if (type != null && !type.isEmpty()) {
            companyType = type;
        } else {
            companyType = companyTypes.get(random.nextInt(companyTypes.size()));
        }
        name.append(companyType);
        
        return name.toString();
    }
    
    /**
     * 从配置文件加载数据
     * @param configPath 配置文件路径
     */
    public void loadFromConfig(String configPath) {
        try {
            // 加载行业关键词
            loadListFromFile(configPath + "/industry_keywords.txt", industryKeywords);
            
            // 加载公司类型后缀
            loadListFromFile(configPath + "/company_types.txt", companyTypes);
            
            // 加载地区前缀
            loadListFromFile(configPath + "/region_prefixes.txt", regionPrefixes);
            
        } catch (IOException e) {
            // 如果配置文件加载失败，使用默认数据
            System.err.println("Failed to load company configuration, using defaults: " + e.getMessage());
        }
    }
    
    private void loadListFromFile(String fileName, List<String> targetList) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        getClass().getResourceAsStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    targetList.add(line);
                }
            }
        }
    }
    
    public int getGeneratedUniqueCount() {
        return uniqueCounter.intValue();
    }
    
    public int getMaxUniqueCompanies() {
        return MAX_UNIQUE_COMPANIES;
    }
    
    public boolean isFull() {
        return generatedCompanies.size() >= MAX_UNIQUE_COMPANIES;
    }
    
    public void resetUniqueCompanies() {
        generatedCompanies.clear();
        uniqueCounter.set(0);
    }
    
    public Map<String, Integer> getCompanyStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total_industry_keywords", industryKeywords.size());
        stats.put("total_company_types", companyTypes.size());
        stats.put("total_region_prefixes", regionPrefixes.size());
        stats.put("generated_unique", getGeneratedUniqueCount());
        return stats;
    }
    
    @Override
    public String getName() {
        return "company";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("unique", "prefixRegion", "industry", "type");
    }
    
    /**
     * 计算理论最大唯一公司名称数量
     */
    public long calculateMaxUniqueCompanies() {
        // 这是一个估算值，实际唯一性取决于组合的数量和随机性
        long combinations = (long) industryKeywords.size() * companyTypes.size();
        
        // 考虑地区前缀（约50%概率）
        combinations += combinations * regionPrefixes.size() / 2;
        
        // 考虑数字后缀（30%概率，1-100）
        combinations += combinations * 100 * 0.3;
        
        return combinations;
    }
}
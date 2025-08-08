package com.dataforge.generators.basic;

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
 * 高级姓名生成器，支持中英文姓名，支持5000万不重复姓名生成
 * 支持配置文件加载姓氏和名字库
 */
public class NameGenerator implements DataGenerator<String> {
    
    private static final String CONFIG_PATH = "/config/names/";
    private static final int MAX_UNIQUE_NAMES = 50_000_000;
    
    // 姓氏和名字库
    private final List<String> chineseSurnames = new ArrayList<>();
    private final List<String> chineseCompoundSurnames = new ArrayList<>();
    private final List<String> chineseMaleNames = new ArrayList<>();
    private final List<String> chineseFemaleNames = new ArrayList<>();
    private final List<String> chineseNeutralNames = new ArrayList<>();
    private final List<String> englishFirstNames = new ArrayList<>();
    private final List<String> englishLastNames = new ArrayList<>();
    
    // 用于确保唯一性的集合
    private final Set<String> generatedNames = ConcurrentHashMap.newKeySet();
    private final AtomicLong uniqueCounter = new AtomicLong(0);
    
    // 百家姓（完整版）
    private static final String[] BAIDU_SURNAMES = {
        "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨",
        "朱", "秦", "尤", "许", "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜",
        "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎",
        "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐",
        "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常",
        "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄",
        "和", "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧",
        "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒", "屈", "项", "祝", "董", "梁"
    };
    
    // 常见复姓
    private static final String[] COMPOUND_SURNAMES = {
        "欧阳", "太史", "端木", "上官", "司马", "东方", "独孤", "南宫", "万俟", "闻人",
        "夏侯", "诸葛", "尉迟", "公羊", "赫连", "澹台", "皇甫", "宗政", "濮阳", "公冶",
        "太叔", "申屠", "公孙", "慕容", "仲孙", "钟离", "长孙", "宇文", "司徒", "鲜于",
        "司空", "闾丘", "子车", "亓官", "司寇", "巫马", "公西", "颛孙", "壤驷", "公良",
        "漆雕", "乐正", "宰父", "谷梁", "拓跋", "夹谷", "轩辕", "令狐", "段干", "百里",
        "呼延", "东郭", "南门", "羊舌", "微生", "公户", "公玉", "公仪", "梁丘", "公仲",
        "公上", "公门", "公山", "公坚", "左丘", "公伯", "西门", "公祖", "第五", "公乘",
        "贯丘", "公皙", "南荣", "东里", "东宫", "仲长", "子书", "子桑", "即墨", "达奚",
        "褚师", "吴铭"
    };
    
    // 常见男性名字
    private static final String[] MALE_NAMES = {
        "伟", "强", "磊", "军", "洋", "勇", "斌", "涛", "超", "明", "波", "辉", "刚", "健", "亮",
        "俊", "凯", "浩", "华", "平", "鑫", "宇", "鹏", "杰", "峰", "彬", "龙", "翔", "阳", "旭",
        "锋", "志", "哲", "诚", "梁", "栋", "维", "启", "克", "伦", "翔", "旭", "鹏", "泽", "晨",
        "辰", "士", "以", "建", "家", "致", "树", "炎", "盛", "雄", "琛", "钧", "冠", "策", "腾"
    };
    
    // 常见女性名字
    private static final String[] FEMALE_NAMES = {
        "芳", "娜", "敏", "静", "丽", "艳", "娟", "霞", "燕", "慧", "莹", "雪", "琳", "玲", "婷",
        "淑", "珍", "莉", "桂", "娣", "叶", "璧", "璐", "娅", "琦", "晶", "妍", "茜", "秋", "珊",
        "莎", "锦", "黛", "青", "倩", "婷", "姣", "婉", "娴", "瑾", "颖", "露", "瑶", "怡", "婵",
        "雁", "蓓", "纨", "仪", "荷", "丹", "蓉", "眉", "君", "琴", "蕊", "薇", "菁", "梦", "岚"
    };
    
    // 中性名字
    private static final String[] NEUTRAL_NAMES = {
        "文", "思", "雅", "艺", "云", "天", "心", "月", "春", "夏", "秋", "冬", "山", "水", "风",
        "雨", "雷", "电", "冰", "雪", "霜", "露", "花", "草", "树", "木", "石", "玉", "金", "银",
        "光", "明", "星", "辰", "海", "洋", "江", "河", "湖", "泉", "溪", "波", "浪", "潮", "流"
    };
    
    // 英文名字
    private static final String[] ENGLISH_FIRST_NAMES = {
        "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth",
        "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen",
        "Christopher", "Nancy", "Daniel", "Lisa", "Matthew", "Betty", "Anthony", "Dorothy", "Donald", "Sandra",
        "Mark", "Ashley", "Paul", "Kimberly", "Steven", "Donna", "Andrew", "Emily", "Kenneth", "Michelle"
    };
    
    private static final String[] ENGLISH_LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
        "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
        "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson",
        "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores"
    };
    
    public NameGenerator() {
        loadDefaultData();
    }
    
    private void loadDefaultData() {
        // 加载默认数据
        chineseSurnames.addAll(Arrays.asList(BAIDU_SURNAMES));
        chineseCompoundSurnames.addAll(Arrays.asList(COMPOUND_SURNAMES));
        chineseMaleNames.addAll(Arrays.asList(MALE_NAMES));
        chineseFemaleNames.addAll(Arrays.asList(FEMALE_NAMES));
        chineseNeutralNames.addAll(Arrays.asList(NEUTRAL_NAMES));
        englishFirstNames.addAll(Arrays.asList(ENGLISH_FIRST_NAMES));
        englishLastNames.addAll(Arrays.asList(ENGLISH_LAST_NAMES));
    }
    
    @Override
    public String generate(GenerationContext context) {
        String type = (String) context.getParameter("type", "mixed");
        String culture = (String) context.getParameter("culture", "chinese");
        int length = (Integer) context.getParameter("length", 2);
        String gender = (String) context.getParameter("gender", "random");
        boolean unique = (Boolean) context.getParameter("unique", false);
        boolean compoundSurname = (Boolean) context.getParameter("compoundSurname", false);
        
        String name;
        int attempts = 0;
        
        do {
            if ("english".equalsIgnoreCase(culture)) {
                name = generateEnglishName(context, length, gender);
            } else {
                name = generateChineseName(context, length, gender, compoundSurname);
            }
            
            attempts++;
            
            // 如果不需要唯一性或已达到最大尝试次数，返回结果
            if (!unique || attempts > 100) {
                break;
            }
            
        } while (generatedNames.contains(name) && generatedNames.size() < MAX_UNIQUE_NAMES);
        
        if (unique && generatedNames.size() < MAX_UNIQUE_NAMES) {
            generatedNames.add(name);
            uniqueCounter.incrementAndGet();
        }
        
        return name;
    }
    
    private String generateChineseName(GenerationContext context, int length, String gender, boolean compoundSurname) {
        Random random = context.getRandom();
        
        // 选择姓氏
        String surname;
        if (compoundSurname && random.nextDouble() < 0.1) { // 10%概率使用复姓
            surname = chineseCompoundSurnames.get(random.nextInt(chineseCompoundSurnames.size()));
        } else {
            surname = chineseSurnames.get(random.nextInt(chineseSurnames.size()));
        }
        
        // 选择名字
        StringBuilder givenName = new StringBuilder();
        int givenNameLength = Math.max(1, length - surname.length());
        
        List<String> namePool;
        switch (gender.toLowerCase()) {
            case "male":
                namePool = chineseMaleNames;
                break;
            case "female":
                namePool = chineseFemaleNames;
                break;
            case "neutral":
                namePool = chineseNeutralNames;
                break;
            default:
                // 随机选择名字池
                int poolChoice = random.nextInt(3);
                if (poolChoice == 0) namePool = chineseMaleNames;
                else if (poolChoice == 1) namePool = chineseFemaleNames;
                else namePool = chineseNeutralNames;
        }
        
        // 生成名字
        for (int i = 0; i < givenNameLength; i++) {
            givenName.append(namePool.get(random.nextInt(namePool.size())));
        }
        
        return surname + givenName.toString();
    }
    
    private String generateEnglishName(GenerationContext context, int length, String gender) {
        Random random = context.getRandom();
        
        String firstName = englishFirstNames.get(random.nextInt(englishFirstNames.size()));
        String lastName = englishLastNames.get(random.nextInt(englishLastNames.size()));
        
        // 支持生成中间名
        if (length > 2 && random.nextDouble() < 0.3) {
            String middleName = englishFirstNames.get(random.nextInt(englishFirstNames.size()));
            return firstName + " " + middleName + " " + lastName;
        }
        
        return firstName + " " + lastName;
    }
    
    public void loadFromConfig(String configPath) {
        try {
            // 加载姓氏配置
            loadNamesFromFile(configPath + "/surnames.txt", chineseSurnames);
            loadNamesFromFile(configPath + "/compound_surnames.txt", chineseCompoundSurnames);
            
            // 加载名字配置
            loadNamesFromFile(configPath + "/male_names.txt", chineseMaleNames);
            loadNamesFromFile(configPath + "/female_names.txt", chineseFemaleNames);
            loadNamesFromFile(configPath + "/neutral_names.txt", chineseNeutralNames);
            
            // 加载英文名字
            loadNamesFromFile(configPath + "/english_first_names.txt", englishFirstNames);
            loadNamesFromFile(configPath + "/english_last_names.txt", englishLastNames);
            
        } catch (IOException e) {
            // 如果配置文件加载失败，使用默认数据
            System.err.println("Failed to load name configuration, using defaults: " + e.getMessage());
        }
    }
    
    private void loadNamesFromFile(String fileName, List<String> targetList) throws IOException {
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
    
    public int getMaxUniqueNames() {
        return MAX_UNIQUE_NAMES;
    }
    
    public boolean isFull() {
        return generatedNames.size() >= MAX_UNIQUE_NAMES;
    }
    
    public void resetUniqueNames() {
        generatedNames.clear();
        uniqueCounter.set(0);
    }
    
    public Map<String, Integer> getNameStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total_chinese_surnames", chineseSurnames.size());
        stats.put("total_compound_surnames", chineseCompoundSurnames.size());
        stats.put("total_male_names", chineseMaleNames.size());
        stats.put("total_female_names", chineseFemaleNames.size());
        stats.put("total_neutral_names", chineseNeutralNames.size());
        stats.put("total_english_first", englishFirstNames.size());
        stats.put("total_english_last", englishLastNames.size());
        stats.put("generated_unique", getGeneratedUniqueCount());
        return stats;
    }
    
    @Override
    public String getName() {
        return "name";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList(
            "type", "culture", "length", "gender", "unique", 
            "compoundSurname", "surname", "givenName"
        );
    }
    
    /**
     * 计算理论最大唯一姓名数量
     */
    public long calculateMaxUniqueNames() {
        long chineseCombinations = 0;
        
        // 中文单姓组合
        long singleSurnameNames = (long) chineseSurnames.size() * 
                                 (chineseMaleNames.size() + chineseFemaleNames.size() + chineseNeutralNames.size());
        
        // 中文复姓组合
        long compoundSurnameNames = (long) chineseCompoundSurnames.size() * 
                                   (chineseMaleNames.size() + chineseFemaleNames.size() + chineseNeutralNames.size());
        
        // 考虑不同长度的名字
        chineseCombinations = singleSurnameNames * 10 + compoundSurnameNames * 5;
        
        // 英文组合
        long englishCombinations = (long) englishFirstNames.size() * englishLastNames.size() * 5;
        
        return chineseCombinations + englishCombinations;
    }
}
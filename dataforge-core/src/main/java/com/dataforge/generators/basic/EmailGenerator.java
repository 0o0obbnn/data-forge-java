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
 * 高级邮箱生成器，支持5000万不重复邮箱地址
 * 支持配置文件加载邮箱域名和用户名模式
 */
public class EmailGenerator implements DataGenerator<String> {
    
    private static final String CONFIG_PATH = "/config/email/";
    private static final int MAX_UNIQUE_EMAILS = 50_000_000;
    
    // 邮箱域名
    private final List<String> domains = new ArrayList<>();
    private final List<String> popularDomains = new ArrayList<>();
    private final List<String> companyDomains = new ArrayList<>();
    private final List<String> countryDomains = new ArrayList<>();
    
    // 用户名模式
    private final List<String> firstNames = new ArrayList<>();
    private final List<String> lastNames = new ArrayList<>();
    private final List<String> adjectives = new ArrayList<>();
    private final List<String> nouns = new ArrayList<>();
    private final List<String> numbers = new ArrayList<>();
    
    // 用户名分隔符
    private final List<String> separators = Arrays.asList("", ".", "_", "-");
    
    // 用于确保唯一性的集合
    private final Set<String> generatedEmails = ConcurrentHashMap.newKeySet();
    private final AtomicLong uniqueCounter = new AtomicLong(0);
    
    // 默认数据
    private static final String[] DEFAULT_DOMAINS = {
        "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "aol.com",
        "icloud.com", "protonmail.com", "zoho.com", "yandex.com", "mail.com"
    };
    
    private static final String[] DEFAULT_POPULAR_DOMAINS = {
        "163.com", "qq.com", "sina.com", "126.com", "139.com", "189.cn",
        "foxmail.com", "yeah.net", "21cn.com", "sohu.com"
    };
    
    private static final String[] DEFAULT_COMPANY_DOMAINS = {
        "microsoft.com", "google.com", "apple.com", "amazon.com", "facebook.com",
        "netflix.com", "twitter.com", "linkedin.com", "github.com", "stackoverflow.com"
    };
    
    private static final String[] DEFAULT_FIRST_NAMES = {
        "john", "jane", "michael", "sarah", "david", "emma", "chris", "olivia", 
        "matthew", "ava", "james", "sophia", "robert", "isabella", "daniel", "mia",
        "william", "charlotte", "joseph", "amelia", "thomas", "harper", "charles", "evelyn"
    };
    
    private static final String[] DEFAULT_LAST_NAMES = {
        "smith", "johnson", "williams", "brown", "jones", "garcia", "miller", "davis",
        "rodriguez", "martinez", "hernandez", "lopez", "gonzalez", "wilson", "anderson", "thomas"
    };
    
    private static final String[] DEFAULT_ADJECTIVES = {
        "happy", "smart", "cool", "fast", "bright", "swift", "sharp", "quick",
        "lucky", "wise", "strong", "brave", "calm", "wild", "bold", "clever"
    };
    
    private static final String[] DEFAULT_NOUNS = {
        "tiger", "eagle", "phoenix", "dragon", "wolf", "hawk", "lion", "shark",
        "storm", "thunder", "lightning", "shadow", "flame", "ice", "stone", "river"
    };
    
    private static final String[] DEFAULT_NUMBERS = {
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
        "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
        "69", "88", "99", "100", "123", "007", "365", "999", "2023", "2024"
    };
    
    public EmailGenerator() {
        loadDefaultData();
    }
    
    private void loadDefaultData() {
        domains.addAll(Arrays.asList(DEFAULT_DOMAINS));
        popularDomains.addAll(Arrays.asList(DEFAULT_POPULAR_DOMAINS));
        companyDomains.addAll(Arrays.asList(DEFAULT_COMPANY_DOMAINS));
        firstNames.addAll(Arrays.asList(DEFAULT_FIRST_NAMES));
        lastNames.addAll(Arrays.asList(DEFAULT_LAST_NAMES));
        adjectives.addAll(Arrays.asList(DEFAULT_ADJECTIVES));
        nouns.addAll(Arrays.asList(DEFAULT_NOUNS));
        numbers.addAll(Arrays.asList(DEFAULT_NUMBERS));
    }
    
    @Override
    public String generate(GenerationContext context) {
        String type = (String) context.getParameter("type", "mixed");
        String domainType = (String) context.getParameter("domainType", "mixed");
        int length = (Integer) context.getParameter("length", 8);
        boolean unique = (Boolean) context.getParameter("unique", false);
        
        String email;
        int attempts = 0;
        
        do {
            email = generateEmail(context, type, domainType, length);
            attempts++;
            
            if (!unique || attempts > 100) {
                break;
            }
            
        } while (generatedEmails.contains(email) && generatedEmails.size() < MAX_UNIQUE_EMAILS);
        
        if (unique && generatedEmails.size() < MAX_UNIQUE_EMAILS) {
            generatedEmails.add(email);
            uniqueCounter.incrementAndGet();
        }
        
        return email;
    }
    
    private String generateEmail(GenerationContext context, String type, String domainType, int length) {
        Random random = context.getRandom();
        
        // 选择域名
        String domain = selectDomain(random, domainType);
        
        // 生成用户名
        String username;
        switch (type.toLowerCase()) {
            case "namebased":
                username = generateNameBasedUsername(random);
                break;
            case "random":
                username = generateRandomUsername(random, length);
                break;
            case "pattern":
                username = generatePatternUsername(random);
                break;
            default:
                username = random.nextBoolean() ? generateNameBasedUsername(random) : generateRandomUsername(random, length);
        }
        
        return username + "@" + domain;
    }
    
    private String selectDomain(Random random, String domainType) {
        List<String> selectedDomains;
        
        switch (domainType.toLowerCase()) {
            case "popular":
                selectedDomains = popularDomains;
                break;
            case "company":
                selectedDomains = companyDomains;
                break;
            case "custom":
                selectedDomains = domains;
                break;
            default:
                // 混合选择
                selectedDomains = random.nextBoolean() ? domains : (random.nextBoolean() ? popularDomains : companyDomains);
        }
        
        return selectedDomains.get(random.nextInt(selectedDomains.size()));
    }
    
    private String generateNameBasedUsername(Random random) {
        StringBuilder username = new StringBuilder();
        
        // 姓名组合模式
        int pattern = random.nextInt(6);
        switch (pattern) {
            case 0: // firstname.lastname
                username.append(firstNames.get(random.nextInt(firstNames.size())))
                       .append(separators.get(random.nextInt(separators.size())))
                       .append(lastNames.get(random.nextInt(lastNames.size())));
                break;
            case 1: // lastname.firstname
                username.append(lastNames.get(random.nextInt(lastNames.size())))
                       .append(separators.get(random.nextInt(separators.size())))
                       .append(firstNames.get(random.nextInt(firstNames.size())));
                break;
            case 2: // firstname + number
                username.append(firstNames.get(random.nextInt(firstNames.size())))
                       .append(numbers.get(random.nextInt(numbers.size())));
                break;
            case 3: // firstname.lastname + number
                username.append(firstNames.get(random.nextInt(firstNames.size())))
                       .append(separators.get(random.nextInt(separators.size())))
                       .append(lastNames.get(random.nextInt(lastNames.size())))
                       .append(numbers.get(random.nextInt(numbers.size())));
                break;
            case 4: // firstname + lastname
                username.append(firstNames.get(random.nextInt(firstNames.size())))
                       .append(lastNames.get(random.nextInt(lastNames.size())));
                break;
            case 5: // lastname + firstname
                username.append(lastNames.get(random.nextInt(lastNames.size())))
                       .append(firstNames.get(random.nextInt(firstNames.size())));
                break;
        }
        
        return username.toString().toLowerCase();
    }
    
    private String generateRandomUsername(Random random, int length) {
        StringBuilder username = new StringBuilder();
        length = Math.max(5, Math.min(15, length));
        
        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                username.append((char) ('a' + random.nextInt(26)));
            } else {
                username.append(random.nextInt(10));
            }
        }
        
        return username.toString();
    }
    
    private String generatePatternUsername(Random random) {
        StringBuilder username = new StringBuilder();
        
        // 形容词 + 名词 + 数字
        username.append(adjectives.get(random.nextInt(adjectives.size())))
               .append(nouns.get(random.nextInt(nouns.size())))
               .append(numbers.get(random.nextInt(numbers.size())));
        
        return username.toString().toLowerCase();
    }
    
    public void loadFromConfig(String configPath) {
        try {
            loadDataFromFile(configPath + "/domains.txt", domains);
            loadDataFromFile(configPath + "/popular_domains.txt", popularDomains);
            loadDataFromFile(configPath + "/company_domains.txt", companyDomains);
            loadDataFromFile(configPath + "/first_names.txt", firstNames);
            loadDataFromFile(configPath + "/last_names.txt", lastNames);
            loadDataFromFile(configPath + "/adjectives.txt", adjectives);
            loadDataFromFile(configPath + "/nouns.txt", nouns);
            loadDataFromFile(configPath + "/numbers.txt", numbers);
        } catch (IOException e) {
            System.err.println("Failed to load email configuration, using defaults: " + e.getMessage());
        }
    }
    
    private void loadDataFromFile(String fileName, List<String> targetList) throws IOException {
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
    
    public int getMaxUniqueEmails() {
        return MAX_UNIQUE_EMAILS;
    }
    
    public boolean isFull() {
        return generatedEmails.size() >= MAX_UNIQUE_EMAILS;
    }
    
    public void resetUniqueEmails() {
        generatedEmails.clear();
        uniqueCounter.set(0);
    }
    
    public Map<String, Integer> getEmailStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total_domains", domains.size());
        stats.put("total_popular_domains", popularDomains.size());
        stats.put("total_company_domains", companyDomains.size());
        stats.put("total_first_names", firstNames.size());
        stats.put("total_last_names", lastNames.size());
        stats.put("total_adjectives", adjectives.size());
        stats.put("total_nouns", nouns.size());
        stats.put("generated_unique", getGeneratedUniqueCount());
        return stats;
    }
    
    @Override
    public String getName() {
        return "email";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("type", "domainType", "length", "unique");
    }
    
    /**
     * 计算理论最大唯一邮箱数量
     */
    public long calculateMaxUniqueEmails() {
        long usernameCombinations = 0;
        
        // 姓名组合
        long nameBased = (long) firstNames.size() * lastNames.size() * separators.size() * numbers.size();
        
        // 随机组合
        long randomBased = (long) Math.pow(36, 8); // 36^8 for 8-char alphanumeric
        
        // 模式组合
        long patternBased = (long) adjectives.size() * nouns.size() * numbers.size();
        
        usernameCombinations = nameBased + randomBased + patternBased;
        
        // 域名组合
        long domainCombinations = domains.size() + popularDomains.size() + companyDomains.size();
        
        return usernameCombinations * domainCombinations;
    }
}
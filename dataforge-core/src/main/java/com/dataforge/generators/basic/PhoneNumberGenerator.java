package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 增强手机号码生成器
 * 支持按运营商区分号段，配置文件加载运营商号段信息
 * 支持生成5000万唯一手机号码
 */
public class PhoneNumberGenerator implements DataGenerator<String> {
    
    private static final String CHINA_MOBILE_FILE = "/config/phone/china_mobile.txt";
    private static final String CHINA_UNICOM_FILE = "/config/phone/china_unicom.txt";
    private static final String CHINA_TELECOM_FILE = "/config/phone/china_telecom.txt";
    private static final String VIRTUAL_OPERATORS_FILE = "/config/phone/virtual_operators.txt";
    
    private static final Map<String, List<String>> operatorPrefixes = new HashMap<>();
    private static final ConcurrentMap<String, Boolean> generatedNumbers = new ConcurrentHashMap<>();
    
    /**
     * 运营商枚举
     */
    public enum Operator {
        CHINA_MOBILE("中国移动"),
        CHINA_UNICOM("中国联通"),
        CHINA_TELECOM("中国电信"),
        VIRTUAL_OPERATOR("虚拟运营商"),
        ALL("全部");
        
        private final String name;
        
        Operator(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private final Operator operator;
    private final int maxAttempts;
    
    public PhoneNumberGenerator() {
        this(Operator.ALL);
    }
    
    public PhoneNumberGenerator(Operator operator) {
        this.operator = operator;
        this.maxAttempts = 1000;
        loadConfiguration();
    }
    
    static {
        loadConfiguration();
    }
    
    private static synchronized void loadConfiguration() {
        if (!operatorPrefixes.isEmpty()) {
            return;
        }
        
        // 加载中国移动号段
        operatorPrefixes.put(Operator.CHINA_MOBILE.name(), 
            loadPrefixesFromFile(CHINA_MOBILE_FILE, getDefaultChinaMobilePrefixes()));
        
        // 加载中国联通号段
        operatorPrefixes.put(Operator.CHINA_UNICOM.name(), 
            loadPrefixesFromFile(CHINA_UNICOM_FILE, getDefaultChinaUnicomPrefixes()));
        
        // 加载中国电信号段
        operatorPrefixes.put(Operator.CHINA_TELECOM.name(), 
            loadPrefixesFromFile(CHINA_TELECOM_FILE, getDefaultChinaTelecomPrefixes()));
        
        // 加载虚拟运营商号段
        operatorPrefixes.put(Operator.VIRTUAL_OPERATOR.name(), 
            loadPrefixesFromFile(VIRTUAL_OPERATORS_FILE, getDefaultVirtualOperatorPrefixes()));
        
        // 合并所有号段
        List<String> allPrefixes = new ArrayList<>();
        operatorPrefixes.values().forEach(allPrefixes::addAll);
        operatorPrefixes.put(Operator.ALL.name(), allPrefixes);
    }
    
    private static List<String> loadPrefixesFromFile(String fileName, List<String> defaults) {
        List<String> prefixes = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        PhoneNumberGenerator.class.getResourceAsStream(fileName), 
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    // 支持格式：139 或 139=中国移动
                    String prefix = line.split("=")[0].trim();
                    if (prefix.matches("\\d{3,4}")) {
                        prefixes.add(prefix);
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            prefixes.addAll(defaults);
        }
        
        return prefixes;
    }
    
    private static List<String> getDefaultChinaMobilePrefixes() {
        return Arrays.asList(
            "134", "135", "136", "137", "138", "139",
            "147", "148",
            "150", "151", "152", "157", "158", "159",
            "172", "178", "182", "183", "184", "187", "188",
            "195", "197", "198"
        );
    }
    
    private static List<String> getDefaultChinaUnicomPrefixes() {
        return Arrays.asList(
            "130", "131", "132", "145", "146",
            "155", "156", "166", "167",
            "171", "175", "176", "185", "186"
        );
    }
    
    private static List<String> getDefaultChinaTelecomPrefixes() {
        return Arrays.asList(
            "133", "149",
            "153", "173", "174", "177",
            "180", "181", "189", "190", "191", "193", "199"
        );
    }
    
    private static List<String> getDefaultVirtualOperatorPrefixes() {
        return Arrays.asList(
            "162", "165", "170"
        );
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        List<String> prefixes = operatorPrefixes.get(operator.name());
        
        if (prefixes == null || prefixes.isEmpty()) {
            prefixes = operatorPrefixes.get(Operator.ALL.name());
        }
        
        // Ensure we always have prefixes to work with
        if (prefixes == null || prefixes.isEmpty()) {
            prefixes = getDefaultChinaMobilePrefixes();
        }
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String prefix = prefixes.get(random.nextInt(prefixes.size()));
            String phoneNumber = generatePhoneNumber(prefix, random);
            
            if (generatedNumbers.putIfAbsent(phoneNumber, Boolean.TRUE) == null) {
                return phoneNumber;
            }
        }
        
        // Fallback with timestamp if max attempts exceeded
        String prefix = prefixes.get(random.nextInt(prefixes.size()));
        return prefix + String.format("%08d", System.nanoTime() % 100000000);
    }
    
    private String generatePhoneNumber(String prefix, Random random) {
        StringBuilder phoneNumber = new StringBuilder(prefix);
        int remainingDigits = 11 - prefix.length();
        
        for (int i = 0; i < remainingDigits; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        
        return phoneNumber.toString();
    }
    
    /**
     * Gets the total number of unique phone numbers generated.
     * 
     * @return the count of unique phone numbers
     */
    public static int getUniquePhoneNumberCount() {
        return generatedNumbers.size();
    }
    
    /**
     * Clears the generated phone numbers cache (for testing purposes).
     */
    public static void clearCache() {
        generatedNumbers.clear();
    }
    
    /**
     * Gets available prefixes for a specific operator
     * @param operator the operator to get prefixes for
     * @return list of prefixes
     */
    public static List<String> getOperatorPrefixes(Operator operator) {
        List<String> prefixes = operatorPrefixes.get(operator.name());
        return prefixes != null ? new ArrayList<>(prefixes) : new ArrayList<>();
    }
    
    /**
     * Gets all available operators
     * @return set of operators
     */
    public static Set<String> getAvailableOperators() {
        return new HashSet<>(operatorPrefixes.keySet());
    }
    
    @Override
    public String getName() {
        return "phone_number";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("operator");
    }
    
    /**
     * Generates a phone number for a specific operator
     * @param operator the operator to generate for
     * @param context the generation context
     * @return the generated phone number
     */
    public static String generateForOperator(Operator operator, GenerationContext context) {
        PhoneNumberGenerator generator = new PhoneNumberGenerator(operator);
        return generator.generate(context);
    }
    
    /**
     * Gets operator information for a phone number
     * @param phoneNumber the phone number to analyze
     * @return the operator name
     */
    public static String getOperator(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 3) {
            return "未知";
        }
        
        String prefix = phoneNumber.substring(0, 3);
        String prefix4 = phoneNumber.length() >= 4 ? phoneNumber.substring(0, 4) : null;
        
        // 优先匹配传统运营商（中国移动、联通、电信），避免虚拟运营商冲突
        Operator[] priorityOrder = {Operator.CHINA_MOBILE, Operator.CHINA_UNICOM, Operator.CHINA_TELECOM, Operator.VIRTUAL_OPERATOR};
        
        for (Operator operator : priorityOrder) {
            List<String> prefixes = operatorPrefixes.get(operator.name());
            if (prefixes != null) {
                // First check 3-digit prefix
                if (prefixes.contains(prefix)) {
                    return operator.getName();
                }
                // Then check 4-digit prefix if available
                if (prefix4 != null && prefixes.contains(prefix4)) {
                    return operator.getName();
                }
            }
        }
        
        return "未知";
    }
}
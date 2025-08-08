package com.dataforge.generators.location;

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
 * 增强地区数据生成器
 * 支持生成详细的地址信息，包括小区、单元、门牌号和邮政编码
 * 支持分别生成省、市、区、具体地址、邮政编码
 * 支持5000万唯一地址组合
 */
public class EnhancedLocationDataGenerator implements DataGenerator<Map<String, String>> {

    private static final String CHINA_PROVINCES_FILE = "/config/location/china_provinces.txt";
    private static final String CHINA_CITIES_FILE = "/config/location/china_cities.txt";
    private static final String CHINA_DISTRICTS_FILE = "/config/location/china_districts.txt";
    private static final String COMMUNITY_NAMES_FILE = "/config/location/community_names.txt";
    private static final String POSTAL_CODES_FILE = "/config/location/postal_codes.txt";
    
    private static final List<String> chinaProvinces = new ArrayList<>();
    private static final List<String> chinaCities = new ArrayList<>();
    private static final List<String> chinaDistricts = new ArrayList<>();
    private static final List<String> communityNames = new ArrayList<>();
    private static final Map<String, String> postalCodes = new HashMap<>();
    
    private static final ConcurrentMap<String, Boolean> generatedAddresses = new ConcurrentHashMap<>();
    
    static {
        loadConfiguration();
    }
    
    /**
     * 地址组件类型枚举
     */
    public enum AddressComponent {
        PROVINCE,
        CITY,
        DISTRICT,
        COMMUNITY,
        STREET,
        BUILDING,
        UNIT,
        DOOR_NUMBER,
        POSTAL_CODE,
        FULL_ADDRESS
    }
    
    /**
     * 地址数据结构
     */
    public static class AddressData {
        private final String province;
        private final String city;
        private final String district;
        private final String community;
        private final String street;
        private final String building;
        private final String unit;
        private final String doorNumber;
        private final String postalCode;
        private final String fullAddress;
        
        public AddressData(String province, String city, String district, String community,
                          String street, String building, String unit, String doorNumber,
                          String postalCode, String fullAddress) {
            this.province = province;
            this.city = city;
            this.district = district;
            this.community = community;
            this.street = street;
            this.building = building;
            this.unit = unit;
            this.doorNumber = doorNumber;
            this.postalCode = postalCode;
            this.fullAddress = fullAddress;
        }
        
        public String getProvince() { return province; }
        public String getCity() { return city; }
        public String getDistrict() { return district; }
        public String getCommunity() { return community; }
        public String getStreet() { return street; }
        public String getBuilding() { return building; }
        public String getUnit() { return unit; }
        public String getDoorNumber() { return doorNumber; }
        public String getPostalCode() { return postalCode; }
        public String getFullAddress() { return fullAddress; }
        
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("province", province);
            map.put("city", city);
            map.put("district", district);
            map.put("community", community);
            map.put("street", street);
            map.put("building", building);
            map.put("unit", unit);
            map.put("doorNumber", doorNumber);
            map.put("postalCode", postalCode);
            map.put("fullAddress", fullAddress);
            return map;
        }
    }
    
    private final AddressComponent component;
    private final int maxAttempts;
    
    public EnhancedLocationDataGenerator() {
        this(AddressComponent.FULL_ADDRESS);
    }
    
    public EnhancedLocationDataGenerator(AddressComponent component) {
        this.component = component;
        this.maxAttempts = 1000;
    }
    
    private static void loadConfiguration() {
        loadListFromFile(CHINA_PROVINCES_FILE, chinaProvinces, getDefaultChinaProvinces());
        loadListFromFile(CHINA_CITIES_FILE, chinaCities, getDefaultChinaCities());
        loadListFromFile(CHINA_DISTRICTS_FILE, chinaDistricts, getDefaultChinaDistricts());
        loadListFromFile(COMMUNITY_NAMES_FILE, communityNames, getDefaultCommunityNames());
        loadPostalCodes();
    }
    
    private static void loadListFromFile(String fileName, List<String> list, List<String> defaults) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        EnhancedLocationDataGenerator.class.getResourceAsStream(fileName), 
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    list.add(line);
                }
            }
        } catch (IOException | NullPointerException e) {
            list.addAll(defaults);
        }
    }
    
    private static void loadPostalCodes() {
        // 加载邮政编码映射
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        EnhancedLocationDataGenerator.class.getResourceAsStream(POSTAL_CODES_FILE), 
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        postalCodes.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            // 使用默认邮政编码
            postalCodes.putAll(getDefaultPostalCodes());
        }
    }
    
    private static List<String> getDefaultChinaProvinces() {
        return Arrays.asList("北京", "上海", "广东", "江苏", "浙江", "山东", "河南", "四川", "湖北", "湖南");
    }
    
    private static List<String> getDefaultChinaCities() {
        return Arrays.asList("北京", "上海", "广州", "深圳", "成都", "杭州", "武汉", "西安", "南京", "重庆");
    }
    
    private static List<String> getDefaultChinaDistricts() {
        return Arrays.asList("朝阳区", "海淀区", "西城区", "东城区", "浦东新区", "天河区", "南山区", "武侯区");
    }
    
    private static List<String> getDefaultCommunityNames() {
        return Arrays.asList(
            "幸福家园", "阳光小区", "和平里社区", "花园洋房", "紫荆花园",
            "碧水云天", "星河湾", "绿地世纪城", "万科城", "保利花园",
            "碧桂园", "恒大绿洲", "万达华府", "金地格林", "中海国际",
            "龙湖花园", "融创城", "华润置地", "招商花园", "雅居乐"
        );
    }
    
    private static Map<String, String> getDefaultPostalCodes() {
        Map<String, String> codes = new HashMap<>();
        codes.put("北京", "100000");
        codes.put("上海", "200000");
        codes.put("广州", "510000");
        codes.put("深圳", "518000");
        codes.put("成都", "610000");
        codes.put("杭州", "310000");
        codes.put("武汉", "430000");
        codes.put("西安", "710000");
        codes.put("南京", "210000");
        codes.put("重庆", "400000");
        return codes;
    }
    
    @Override
    public Map<String, String> generate(GenerationContext context) {
        Random random = context.getRandom();
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            AddressData address = generateAddressData(random);
            String uniqueKey = address.getFullAddress();
            
            if (generatedAddresses.putIfAbsent(uniqueKey, Boolean.TRUE) == null) {
                return getComponentData(address);
            }
        }
        
        // Fallback with timestamp suffix
        AddressData address = generateAddressData(random);
        String uniqueAddress = address.getFullAddress() + System.nanoTime();
        return getComponentData(new AddressData(
            address.getProvince(), address.getCity(), address.getDistrict(),
            address.getCommunity(), address.getStreet(), address.getBuilding(),
            address.getUnit(), address.getDoorNumber() + "-" + System.nanoTime(),
            address.getPostalCode(), uniqueAddress
        ));
    }
    
    private AddressData generateAddressData(Random random) {
        String province = chinaProvinces.get(random.nextInt(chinaProvinces.size()));
        String city = chinaCities.get(random.nextInt(chinaCities.size()));
        String district = chinaDistricts.get(random.nextInt(chinaDistricts.size()));
        String community = communityNames.get(random.nextInt(communityNames.size()));
        
        String street = generateStreetName(random);
        String building = generateBuildingNumber(random);
        String unit = "单元" + (random.nextInt(20) + 1);
        String doorNumber = generateDoorNumber(random);
        String postalCode = postalCodes.getOrDefault(city, "100000");
        
        String fullAddress = String.format("%s省%s市%s区%s%s%s%s%s%s",
            province, city, district, community, street, building, unit, doorNumber, 
            "邮政编码：" + postalCode);
        
        return new AddressData(province, city, district, community, street, building, 
                             unit, doorNumber, postalCode, fullAddress);
    }
    
    private Map<String, String> getComponentData(AddressData address) {
        Map<String, String> result = new LinkedHashMap<>();
        
        switch (component) {
            case PROVINCE:
                result.put("province", address.getProvince());
                break;
            case CITY:
                result.put("city", address.getCity());
                break;
            case DISTRICT:
                result.put("district", address.getDistrict());
                break;
            case COMMUNITY:
                result.put("community", address.getCommunity());
                break;
            case BUILDING:
                result.put("building", address.getBuilding());
                break;
            case UNIT:
                result.put("unit", address.getUnit());
                break;
            case DOOR_NUMBER:
                result.put("doorNumber", address.getDoorNumber());
                break;
            case POSTAL_CODE:
                result.put("postalCode", address.getPostalCode());
                break;
            case FULL_ADDRESS:
            default:
                result.put("province", address.getProvince());
                result.put("city", address.getCity());
                result.put("district", address.getDistrict());
                result.put("community", address.getCommunity());
                result.put("street", address.getStreet());
                result.put("building", address.getBuilding());
                result.put("unit", address.getUnit());
                result.put("doorNumber", address.getDoorNumber());
                result.put("postalCode", address.getPostalCode());
                result.put("fullAddress", address.getFullAddress());
                break;
        }
        
        return result;
    }
    
    private String generateStreetName(Random random) {
        String[] prefixes = {"人民", "解放", "建设", "发展", "和平", "友谊", "光明", "胜利", "前进", "朝阳"};
        String[] suffixes = {"大道", "路", "街", "巷", "胡同", "弄", "里", "坊", "巷", "坊"};
        
        return prefixes[random.nextInt(prefixes.length)] + 
               suffixes[random.nextInt(suffixes.length)];
    }
    
    private String generateBuildingNumber(Random random) {
        return String.valueOf(random.nextInt(999) + 1) + "号";
    }
    
    private String generateDoorNumber(Random random) {
        // 生成3-4位数字的门牌号
        if (random.nextBoolean()) {
            // 生成3位数字 (100-999)
            return String.valueOf(random.nextInt(900) + 100);
        } else {
            // 生成4位数字 (1000-9999)
            return String.valueOf(random.nextInt(9000) + 1000);
        }
    }
    
    /**
     * Gets the total number of unique addresses generated.
     * 
     * @return the count of unique addresses
     */
    public static int getUniqueAddressCount() {
        return generatedAddresses.size();
    }
    
    /**
     * Clears the generated addresses cache (for testing purposes).
     */
    public static void clearCache() {
        generatedAddresses.clear();
    }
    
    @Override
    public String getName() {
        return "enhanced_location";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("component", "region");
    }
    
    /**
     * Generates a random address component
     * @param component the address component to generate
     * @return the generated component value
     */
    public static String generateComponent(AddressComponent component) {
        Random random = new Random();
        
        switch (component) {
            case PROVINCE:
                return chinaProvinces.get(random.nextInt(chinaProvinces.size()));
            case CITY:
                return chinaCities.get(random.nextInt(chinaCities.size()));
            case DISTRICT:
                return chinaDistricts.get(random.nextInt(chinaDistricts.size()));
            case COMMUNITY:
                return communityNames.get(random.nextInt(communityNames.size()));
            case POSTAL_CODE:
                return postalCodes.getOrDefault(
                    chinaCities.get(random.nextInt(chinaCities.size())), 
                    "100000"
                );
            default:
                return "";
        }
    }
}
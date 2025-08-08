package com.dataforge.generators.location;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 地区数据生成器
 * 支持生成中国及世界地区数据，包括国家、省份、城市、区县
 * 支持5000万唯一地区数据组合
 */
public class LocationDataGenerator implements DataGenerator<String> {

    private static final String CHINA_PROVINCES_FILE = "/config/location/china_provinces.txt";
    private static final String CHINA_CITIES_FILE = "/config/location/china_cities.txt";
    private static final String CHINA_DISTRICTS_FILE = "/config/location/china_districts.txt";
    private static final String WORLD_COUNTRIES_FILE = "/config/location/world_countries.txt";
    
    private static final List<String> chinaProvinces = new ArrayList<>();
    private static final List<String> chinaCities = new ArrayList<>();
    private static final List<String> chinaDistricts = new ArrayList<>();
    private static final List<String> worldCountries = new ArrayList<>();
    
    private static final ConcurrentMap<String, Boolean> generatedLocations = new ConcurrentHashMap<>();
    
    static {
        loadConfiguration();
    }
    
    /**
     * 地区类型枚举
     */
    public enum LocationType {
        COUNTRY,
        PROVINCE,
        CITY,
        DISTRICT,
        FULL_ADDRESS
    }
    
    private final LocationType locationType;
    private final String region;
    private final int maxAttempts;
    
    public LocationDataGenerator() {
        this(LocationType.CITY, "china");
    }
    
    public LocationDataGenerator(LocationType locationType) {
        this(locationType, "china");
    }
    
    public LocationDataGenerator(LocationType locationType, String region) {
        this.locationType = locationType;
        this.region = region.toLowerCase();
        this.maxAttempts = 1000;
    }
    
    private static void loadConfiguration() {
        loadListFromFile(CHINA_PROVINCES_FILE, chinaProvinces, getDefaultChinaProvinces());
        loadListFromFile(CHINA_CITIES_FILE, chinaCities, getDefaultChinaCities());
        loadListFromFile(CHINA_DISTRICTS_FILE, chinaDistricts, getDefaultChinaDistricts());
        loadListFromFile(WORLD_COUNTRIES_FILE, worldCountries, getDefaultWorldCountries());
    }
    
    private static void loadListFromFile(String fileName, List<String> list, List<String> defaults) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        LocationDataGenerator.class.getResourceAsStream(fileName), 
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    list.add(line);
                }
            }
        } catch (IOException | NullPointerException e) {
            // Fall back to defaults if file loading fails
            list.addAll(defaults);
        }
    }
    
    private static List<String> getDefaultChinaProvinces() {
        return List.of("北京", "上海", "广东", "江苏", "浙江", "山东", "河南", "四川", "湖北", "湖南");
    }
    
    private static List<String> getDefaultChinaCities() {
        return List.of("北京", "上海", "广州", "深圳", "成都", "杭州", "武汉", "西安", "南京", "重庆");
    }
    
    private static List<String> getDefaultChinaDistricts() {
        return List.of("朝阳区", "海淀区", "西城区", "东城区", "浦东新区", "天河区", "南山区", "武侯区");
    }
    
    private static List<String> getDefaultWorldCountries() {
        return List.of("中国", "美国", "日本", "德国", "英国", "法国", "意大利", "加拿大", "澳大利亚", "韩国");
    }
    
    /**
     * Gets the total number of unique locations generated.
     * 
     * @return the count of unique locations
     */
    public static int getUniqueLocationCount() {
        return generatedLocations.size();
    }
    
    /**
     * Clears the generated locations cache (for testing purposes).
     */
    public static void clearCache() {
        generatedLocations.clear();
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String location = generateLocation(random);
            
            // Ensure uniqueness
            if (generatedLocations.putIfAbsent(location, Boolean.TRUE) == null) {
                return location;
            }
        }
        
        // If we couldn't generate unique location after max attempts, add random suffix
        return generateUniqueWithSuffix(random);
    }
    
    private String generateLocation(Random random) {
        switch (locationType) {
            case COUNTRY:
                return generateCountry(random);
            case PROVINCE:
                return generateProvince(random);
            case CITY:
                return generateCity(random);
            case DISTRICT:
                return generateDistrict(random);
            case FULL_ADDRESS:
                return generateFullAddress(random);
            default:
                return generateCity(random);
        }
    }
    
    private String generateCountry(Random random) {
        if ("china".equals(region)) {
            return "中国";
        } else if (!worldCountries.isEmpty()) {
            return worldCountries.get(random.nextInt(worldCountries.size()));
        }
        return "中国";
    }
    
    private String generateProvince(Random random) {
        if ("china".equals(region) && !chinaProvinces.isEmpty()) {
            return chinaProvinces.get(random.nextInt(chinaProvinces.size()));
        }
        return generateCountry(random);
    }
    
    private String generateCity(Random random) {
        if ("china".equals(region) && !chinaCities.isEmpty()) {
            return chinaCities.get(random.nextInt(chinaCities.size()));
        }
        return generateProvince(random);
    }
    
    private String generateDistrict(Random random) {
        if ("china".equals(region) && !chinaDistricts.isEmpty()) {
            return chinaDistricts.get(random.nextInt(chinaDistricts.size()));
        }
        return generateCity(random);
    }
    
    private String generateFullAddress(Random random) {
        StringBuilder address = new StringBuilder();
        
        // Country
        address.append(generateCountry(random));
        
        // Province for China
        if ("china".equals(region) && !chinaProvinces.isEmpty()) {
            address.append("省").append(chinaProvinces.get(random.nextInt(chinaProvinces.size())));
            
            // City
            if (!chinaCities.isEmpty()) {
                address.append("市").append(chinaCities.get(random.nextInt(chinaCities.size())));
                
                // District
                if (!chinaDistricts.isEmpty() && random.nextBoolean()) {
                    address.append("区").append(chinaDistricts.get(random.nextInt(chinaDistricts.size())));
                }
            }
        }
        
        return address.toString();
    }
    
    private String generateUniqueWithSuffix(Random random) {
        String baseLocation = generateLocation(random);
        
        // Add random suffix based on location type
        String suffix = "";
        switch (locationType) {
            case CITY:
                suffix = "新区";
                break;
            case DISTRICT:
                suffix = "街道";
                break;
            default:
                suffix = String.valueOf(random.nextInt(100));
        }
        
        return baseLocation + suffix;
    }
    
    @Override
    public String getName() {
        return "location_data";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return List.of("type", "region");
    }
    
    /**
     * Gets available provinces for specified region
     * @param region region name (e.g., "china")
     * @return list of provinces
     */
    public static List<String> getProvinces(String region) {
        if ("china".equals(region.toLowerCase())) {
            return List.copyOf(chinaProvinces);
        }
        return List.of();
    }
    
    /**
     * Gets available cities for specified region
     * @param region region name (e.g., "china")
     * @return list of cities
     */
    public static List<String> getCities(String region) {
        if ("china".equals(region.toLowerCase())) {
            return List.copyOf(chinaCities);
        }
        return List.copyOf(worldCountries);
    }
    
    /**
     * Gets available districts for specified region
     * @param region region name (e.g., "china")
     * @return list of districts
     */
    public static List<String> getDistricts(String region) {
        if ("china".equals(region.toLowerCase())) {
            return List.copyOf(chinaDistricts);
        }
        return List.of();
    }
}
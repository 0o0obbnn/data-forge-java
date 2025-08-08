package com.dataforge.generators.location;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.*;

/**
 * 增强版地址生成器
 * 提供更详细和真实的地址信息，包括街道、门牌号、邮编等
 */
public class EnhancedAddressGenerator implements DataGenerator<Map<String, Object>> {

    private static final List<String> PROVINCES = Arrays.asList(
        "北京市", "上海市", "广东省", "江苏省", "浙江省", "山东省", "河南省", "四川省", "湖北省", "湖南省",
        "福建省", "安徽省", "江西省", "河北省", "山西省", "辽宁省", "吉林省", "黑龙江省", "陕西省", "甘肃省",
        "青海省", "台湾省", "内蒙古自治区", "广西壮族自治区", "西藏自治区", "宁夏回族自治区", "新疆维吾尔自治区",
        "天津市", "重庆市"
    );

    private static final Map<String, List<String>> CITY_MAP = new HashMap<>();
    private static final Map<String, List<String>> DISTRICT_MAP = new HashMap<>();
    private static final List<String> STREET_SUFFIXES = Arrays.asList("路", "街", "大道", "巷", "胡同", "弄");
    private static final List<String> BUILDING_TYPES = Arrays.asList("号", "栋", "单元", "楼", "室", "层");

    static {
        // 初始化城市映射
        CITY_MAP.put("北京市", Arrays.asList("朝阳区", "海淀区", "西城区", "东城区", "丰台区", "石景山区", "通州区", "昌平区"));
        CITY_MAP.put("上海市", Arrays.asList("浦东新区", "徐汇区", "黄浦区", "静安区", "长宁区", "普陀区", "虹口区", "杨浦区"));
        CITY_MAP.put("广东省", Arrays.asList("广州市", "深圳市", "东莞市", "佛山市", "珠海市", "中山市", "惠州市", "汕头市"));
        CITY_MAP.put("江苏省", Arrays.asList("南京市", "苏州市", "无锡市", "常州市", "南通市", "扬州市", "镇江市", "泰州市"));
        CITY_MAP.put("浙江省", Arrays.asList("杭州市", "宁波市", "温州市", "嘉兴市", "湖州市", "绍兴市", "金华市", "台州市"));
        CITY_MAP.put("山东省", Arrays.asList("济南市", "青岛市", "烟台市", "潍坊市", "临沂市", "济宁市", "泰安市", "威海市"));

        // 初始化区县映射
        DISTRICT_MAP.put("广州市", Arrays.asList("天河区", "越秀区", "荔湾区", "海珠区", "白云区", "黄埔区", "番禺区", "花都区"));
        DISTRICT_MAP.put("深圳市", Arrays.asList("南山区", "福田区", "罗湖区", "宝安区", "龙岗区", "盐田区", "龙华区", "坪山区"));
        DISTRICT_MAP.put("杭州市", Arrays.asList("西湖区", "上城区", "下城区", "江干区", "拱墅区", "滨江区", "余杭区", "萧山区"));
        DISTRICT_MAP.put("南京市", Arrays.asList("玄武区", "秦淮区", "建邺区", "鼓楼区", "浦口区", "栖霞区", "雨花台区", "江宁区"));
    }

    private final AddressFormat format;
    private final boolean includePostcode;
    private final boolean includeCoordinates;

    public enum AddressFormat {
        COMPLETE,    // 完整地址
        STRUCTURED,  // 结构化地址
        SIMPLE       // 简单地址
    }

    public EnhancedAddressGenerator() {
        this(AddressFormat.COMPLETE, true, false);
    }

    public EnhancedAddressGenerator(AddressFormat format, boolean includePostcode, boolean includeCoordinates) {
        this.format = format;
        this.includePostcode = includePostcode;
        this.includeCoordinates = includeCoordinates;
    }

    @Override
    public Map<String, Object> generate(GenerationContext context) {
        Random random = context.getRandom();
        
        String province = PROVINCES.get(random.nextInt(PROVINCES.size()));
        List<String> cities = CITY_MAP.get(province);
        if (cities == null || cities.isEmpty()) {
            cities = Arrays.asList("示例市", "测试市");
        }
        
        String city = cities.get(random.nextInt(cities.size()));
        
        List<String> districts = DISTRICT_MAP.get(city);
        if (districts == null || districts.isEmpty()) {
            districts = Arrays.asList("示例区", "测试区", "示范区");
        }
        
        String district = districts.get(random.nextInt(districts.size()));
        
        // 生成街道信息
        String streetName = generateStreetName(random);
        String streetNumber = String.valueOf(random.nextInt(9999) + 1);
        String buildingInfo = generateBuildingInfo(random);
        
        // 生成邮编
        String postcode = includePostcode ? generatePostcode(province, city) : null;
        
        // 生成坐标（模拟）
        Map<String, Double> coordinates = includeCoordinates ? generateCoordinates(province) : null;

        Map<String, Object> address = new LinkedHashMap<>();
        
        switch (format) {
            case COMPLETE:
                address.put("province", province);
                address.put("city", city);
                address.put("district", district);
                address.put("street", streetName);
                address.put("streetNumber", streetNumber);
                address.put("building", buildingInfo);
                address.put("fullAddress", province + city + district + streetName + streetNumber + "号" + buildingInfo);
                if (postcode != null) {
                    address.put("postcode", postcode);
                }
                if (coordinates != null) {
                    address.put("latitude", coordinates.get("latitude"));
                    address.put("longitude", coordinates.get("longitude"));
                }
                break;
                
            case STRUCTURED:
                address.put("province", province);
                address.put("city", city);
                address.put("district", district);
                address.put("addressLine", streetName + streetNumber + "号" + buildingInfo);
                break;
                
            case SIMPLE:
                address.put("address", province + city + district + streetName + streetNumber + "号" + buildingInfo);
                break;
        }
        
        return address;
    }

    private String generateStreetName(Random random) {
        String[] prefixes = {"中山", "人民", "解放", "建设", "和平", "民主", "光明", "文化", "科技", "工业"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = STREET_SUFFIXES.get(random.nextInt(STREET_SUFFIXES.size()));
        return prefix + suffix;
    }

    private String generateBuildingInfo(Random random) {
        int buildingNumber = random.nextInt(50) + 1;
        int unitNumber = random.nextInt(10) + 1;
        int roomNumber = random.nextInt(999) + 1;
        
        String buildingType = BUILDING_TYPES.get(random.nextInt(BUILDING_TYPES.size()));
        
        return buildingNumber + "栋" + unitNumber + "单元" + roomNumber + "室";
    }

    private String generatePostcode(String province, String city) {
        // 根据省份和城市生成合理的邮编前缀
        int provinceCode = PROVINCES.indexOf(province) % 10;
        int cityCode = Math.abs(city.hashCode()) % 100;
        
        String prefix = String.format("%01d%02d", provinceCode, cityCode);
        String suffix = String.format("%03d", new Random().nextInt(1000));
        
        return prefix + "0" + suffix; // 确保是6位邮编
    }

    private Map<String, Double> generateCoordinates(String province) {
        Random random = new Random();
        
        // 根据省份生成合理的坐标范围（模拟中国地区）
        double baseLatitude = 30.0 + random.nextDouble() * 15.0; // 30-45度
        double baseLongitude = 100.0 + random.nextDouble() * 20.0; // 100-120度
        
        // 根据省份微调坐标
        switch (province) {
            case "北京市":
                baseLatitude = 39.9 + (random.nextDouble() - 0.5) * 2.0;
                baseLongitude = 116.4 + (random.nextDouble() - 0.5) * 2.0;
                break;
            case "上海市":
                baseLatitude = 31.2 + (random.nextDouble() - 0.5) * 2.0;
                baseLongitude = 121.5 + (random.nextDouble() - 0.5) * 2.0;
                break;
            case "广东省":
                baseLatitude = 23.0 + (random.nextDouble() - 0.5) * 5.0;
                baseLongitude = 113.0 + (random.nextDouble() - 0.5) * 5.0;
                break;
        }
        
        Map<String, Double> coordinates = new HashMap<>();
        coordinates.put("latitude", Math.round(baseLatitude * 10000.0) / 10000.0);
        coordinates.put("longitude", Math.round(baseLongitude * 10000.0) / 10000.0);
        
        return coordinates;
    }

    @Override
    public String getName() {
        return "enhanced_address";
    }

    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("format", "includePostcode", "includeCoordinates");
    }

    /**
     * 获取所有省份列表
     */
    public static List<String> getProvinces() {
        return new ArrayList<>(PROVINCES);
    }

    /**
     * 获取指定省份的城市列表
     */
    public static List<String> getCities(String province) {
        return CITY_MAP.getOrDefault(province, Arrays.asList("示例市"));
    }

    /**
     * 获取指定城市的区县列表
     */
    public static List<String> getDistricts(String city) {
        return DISTRICT_MAP.getOrDefault(city, Arrays.asList("示例区"));
    }
}
package com.dataforge.generators.location;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * 地址生成器
 * 生成中国标准格式的地址信息
 */
public class AddressGenerator implements DataGenerator<String> {
    
    private static final Random random = new Random();
    
    private final AddressFormat format;
    private final AddressType type;
    private final boolean includePostalCode;
    
    public enum AddressFormat {
        FULL,           // 完整地址：省市区街道门牌号
        PROVINCIAL,     // 省级地址：省市区
        CITY_LEVEL,     // 市级地址：市区街道
        DISTRICT_LEVEL, // 区级地址：区街道门牌号
        STREET_ONLY     // 仅街道门牌号
    }
    
    public enum AddressType {
        RESIDENTIAL,    // 住宅地址
        COMMERCIAL,     // 商业地址
        INDUSTRIAL,     // 工业地址
        MIXED           // 混合类型
    }
    
    // 省份数据
    private static final String[] PROVINCES = {
        "北京市", "天津市", "河北省", "山西省", "内蒙古自治区", "辽宁省", "吉林省", "黑龙江省",
        "上海市", "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省", "河南省",
        "湖北省", "湖南省", "广东省", "广西壮族自治区", "海南省", "重庆市", "四川省", "贵州省",
        "云南省", "西藏自治区", "陕西省", "甘肃省", "青海省", "宁夏回族自治区", "新疆维吾尔自治区"
    };
    
    // 城市数据 (对应各省的主要城市)
    private static final String[][] CITIES = {
        // 北京市
        {"东城区", "西城区", "朝阳区", "丰台区", "石景山区", "海淀区", "门头沟区", "房山区", "通州区", "顺义区"},
        // 天津市
        {"和平区", "河东区", "河西区", "南开区", "河北区", "红桥区", "东丽区", "西青区", "津南区", "北辰区"},
        // 河北省
        {"石家庄市", "唐山市", "秦皇岛市", "邯郸市", "邢台市", "保定市", "张家口市", "承德市", "沧州市", "廊坊市"},
        // 山西省
        {"太原市", "大同市", "阳泉市", "长治市", "晋城市", "朔州市", "晋中市", "运城市", "忻州市", "临汾市"},
        // 其他省份使用通用城市名
        {"市区", "新区", "开发区", "高新区", "经济区", "工业区", "商务区", "文化区", "科技区", "生态区"}
    };
    
    // 区县数据
    private static final String[] DISTRICTS = {
        "中心区", "新城区", "老城区", "开发区", "高新区", "经济区", "工业区", "商务区",
        "文化区", "科技区", "生态区", "港务区", "金融区", "教育区", "医疗区", "旅游区"
    };
    
    // 街道名称
    private static final String[] STREET_NAMES = {
        "人民", "解放", "中山", "建设", "和平", "友谊", "胜利", "光明", "新华", "东风",
        "红旗", "向阳", "朝阳", "阳光", "春风", "民主", "自由", "团结", "进步", "发展",
        "繁荣", "昌盛", "兴旺", "富强", "美好", "幸福", "康宁", "安定", "祥和", "吉祥"
    };
    
    // 街道类型
    private static final String[] STREET_TYPES = {
        "路", "街", "大道", "大街", "巷", "弄", "胡同", "里", "坊"
    };
    
    // 建筑类型 - 住宅
    private static final String[] RESIDENTIAL_BUILDINGS = {
        "小区", "花园", "家园", "公寓", "大厦", "新村", "苑", "园", "居", "庭"
    };
    
    // 建筑类型 - 商业
    private static final String[] COMMERCIAL_BUILDINGS = {
        "商场", "购物中心", "写字楼", "商务大厦", "办公楼", "商业广场", "金融中心", "贸易大厦"
    };
    
    // 建筑类型 - 工业
    private static final String[] INDUSTRIAL_BUILDINGS = {
        "工业园", "科技园", "产业园", "制造基地", "工厂", "车间", "厂房", "物流园"
    };
    
    public AddressGenerator() {
        this(AddressFormat.FULL, AddressType.MIXED, true);
    }
    
    public AddressGenerator(AddressFormat format, AddressType type, boolean includePostalCode) {
        this.format = format;
        this.type = type;
        this.includePostalCode = includePostalCode;
    }
    
    @Override
    public String generate(GenerationContext context) {
        StringBuilder address = new StringBuilder();
        
        // 根据格式生成不同级别的地址
        switch (format) {
            case FULL:
                address.append(generateFullAddress());
                break;
            case PROVINCIAL:
                address.append(generateProvincialAddress());
                break;
            case CITY_LEVEL:
                address.append(generateCityLevelAddress());
                break;
            case DISTRICT_LEVEL:
                address.append(generateDistrictLevelAddress());
                break;
            case STREET_ONLY:
                address.append(generateStreetAddress());
                break;
        }
        
        // 添加邮政编码
        if (includePostalCode) {
            address.append(" ").append(generatePostalCode());
        }
        
        return address.toString();
    }
    
    /**
     * 生成完整地址
     */
    private String generateFullAddress() {
        String province = getRandomProvince();
        String city = getRandomCity(province);
        String district = getRandomDistrict();
        String street = generateStreetName();
        String building = generateBuildingName();
        String number = generateHouseNumber();
        
        return province + city + district + street + building + number;
    }
    
    /**
     * 生成省级地址
     */
    private String generateProvincialAddress() {
        String province = getRandomProvince();
        String city = getRandomCity(province);
        String district = getRandomDistrict();
        
        return province + city + district;
    }
    
    /**
     * 生成市级地址
     */
    private String generateCityLevelAddress() {
        String city = getRandomCity(null);
        String district = getRandomDistrict();
        String street = generateStreetName();
        
        return city + district + street;
    }
    
    /**
     * 生成区级地址
     */
    private String generateDistrictLevelAddress() {
        String district = getRandomDistrict();
        String street = generateStreetName();
        String building = generateBuildingName();
        String number = generateHouseNumber();
        
        return district + street + building + number;
    }
    
    /**
     * 生成街道地址
     */
    private String generateStreetAddress() {
        String street = generateStreetName();
        String building = generateBuildingName();
        String number = generateHouseNumber();
        
        return street + building + number;
    }
    
    /**
     * 获取随机省份
     */
    private String getRandomProvince() {
        return PROVINCES[random.nextInt(PROVINCES.length)];
    }
    
    /**
     * 获取随机城市
     */
    private String getRandomCity(String province) {
        if (province != null) {
            // 根据省份获取对应城市
            int provinceIndex = -1;
            for (int i = 0; i < PROVINCES.length; i++) {
                if (PROVINCES[i].equals(province)) {
                    provinceIndex = i;
                    break;
                }
            }
            
            if (provinceIndex >= 0 && provinceIndex < CITIES.length) {
                String[] cities = CITIES[provinceIndex];
                return cities[random.nextInt(cities.length)];
            }
        }
        
        // 使用通用城市名
        String[] genericCities = CITIES[CITIES.length - 1];
        return genericCities[random.nextInt(genericCities.length)];
    }
    
    /**
     * 获取随机区县
     */
    private String getRandomDistrict() {
        return DISTRICTS[random.nextInt(DISTRICTS.length)];
    }
    
    /**
     * 生成街道名称
     */
    private String generateStreetName() {
        String name = STREET_NAMES[random.nextInt(STREET_NAMES.length)];
        String type = STREET_TYPES[random.nextInt(STREET_TYPES.length)];
        
        // 可能添加方向
        if (random.nextBoolean()) {
            String[] directions = {"东", "西", "南", "北", "中"};
            String direction = directions[random.nextInt(directions.length)];
            return name + direction + type;
        }
        
        return name + type;
    }
    
    /**
     * 生成建筑名称
     */
    private String generateBuildingName() {
        String[] buildingNames;
        
        switch (type) {
            case RESIDENTIAL:
                buildingNames = RESIDENTIAL_BUILDINGS;
                break;
            case COMMERCIAL:
                buildingNames = COMMERCIAL_BUILDINGS;
                break;
            case INDUSTRIAL:
                buildingNames = INDUSTRIAL_BUILDINGS;
                break;
            case MIXED:
            default:
                // 混合类型，随机选择
                switch (random.nextInt(3)) {
                    case 0:
                        buildingNames = RESIDENTIAL_BUILDINGS;
                        break;
                    case 1:
                        buildingNames = COMMERCIAL_BUILDINGS;
                        break;
                    default:
                        buildingNames = INDUSTRIAL_BUILDINGS;
                        break;
                }
                break;
        }
        
        String buildingType = buildingNames[random.nextInt(buildingNames.length)];
        
        // 生成建筑编号或名称
        if (random.nextBoolean()) {
            // 使用数字编号
            return (random.nextInt(999) + 1) + "号" + buildingType;
        } else {
            // 使用名称
            String name = STREET_NAMES[random.nextInt(STREET_NAMES.length)];
            return name + buildingType;
        }
    }
    
    /**
     * 生成门牌号
     */
    private String generateHouseNumber() {
        StringBuilder number = new StringBuilder();
        
        // 楼栋号
        if (random.nextBoolean()) {
            number.append((random.nextInt(20) + 1)).append("栋");
        }
        
        // 单元号
        if (random.nextBoolean()) {
            number.append((random.nextInt(6) + 1)).append("单元");
        }
        
        // 楼层和房间号
        int floor = random.nextInt(30) + 1;
        int room = random.nextInt(20) + 1;
        
        if (floor < 10) {
            number.append("0").append(floor);
        } else {
            number.append(floor);
        }
        
        if (room < 10) {
            number.append("0").append(room);
        } else {
            number.append(room);
        }
        
        number.append("室");
        
        return number.toString();
    }
    
    /**
     * 生成邮政编码
     */
    private String generatePostalCode() {
        // 中国邮政编码格式：6位数字
        StringBuilder code = new StringBuilder();
        
        // 第一位：1-8 (区域码)
        code.append(random.nextInt(8) + 1);
        
        // 第二位：0-9 (省份码)
        code.append(random.nextInt(10));
        
        // 后四位：0000-9999 (地区码)
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }
    
    /**
     * 生成带坐标的地址（扩展功能）
     */
    public String generateAddressWithCoordinates() {
        String address = generate(null);
        
        // 生成模拟坐标 (中国境内大致范围)
        double longitude = 73.0 + random.nextDouble() * (135.0 - 73.0); // 经度范围
        double latitude = 18.0 + random.nextDouble() * (54.0 - 18.0);   // 纬度范围
        
        return String.format("%s (%.6f, %.6f)", address, longitude, latitude);
    }
    
    /**
     * 验证地址格式
     */
    public static boolean isValidChineseAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        
        // 基本检查：包含中文字符
        return address.matches(".*[\\u4e00-\\u9fa5].*");
    }
    
    /**
     * 提取地址中的邮政编码
     */
    public static String extractPostalCode(String address) {
        if (address == null) return null;
        
        // 查找6位数字的邮政编码
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b\\d{6}\\b");
        java.util.regex.Matcher matcher = pattern.matcher(address);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }
}
package com.dataforge.generators.identifiers;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * 车牌号生成器
 * 生成符合中国标准的车牌号码
 */
public class LicensePlateGenerator implements DataGenerator<String> {
    
    private static final Random random = new Random();
    
    private final PlateType plateType;
    private final String specificProvince;
    
    public enum PlateType {
        REGULAR,        // 普通车牌 (蓝牌)
        NEW_ENERGY,     // 新能源车牌 (绿牌)
        TRUCK,          // 货车车牌 (黄牌)
        POLICE,         // 警车车牌 (白牌)
        MILITARY,       // 军车车牌
        CONSULATE,      // 领事馆车牌 (黑牌)
        SPECIAL         // 特殊车牌
    }
    
    // 省份简称及其代码
    private static final String[] PROVINCES = {
        "京", "津", "冀", "晋", "蒙", "辽", "吉", "黑", 
        "沪", "苏", "浙", "皖", "闽", "赣", "鲁", "豫", 
        "鄂", "湘", "粤", "桂", "琼", "渝", "川", "贵", 
        "云", "藏", "陕", "甘", "青", "宁", "新"
    };
    
    // 城市代码 (A-Z, 不包含I和O)
    private static final String[] CITY_CODES = {
        "A", "B", "C", "D", "E", "F", "G", "H", 
        "J", "K", "L", "M", "N", "P", "Q", "R", 
        "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    
    // 数字和字母组合（不包含I和O）
    private static final String[] ALPHANUMERIC = {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", 
        "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", 
        "W", "X", "Y", "Z"
    };
    
    // 仅数字
    private static final String[] NUMBERS = {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };
    
    // 仅字母（不包含I和O）
    private static final String[] LETTERS = {
        "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", 
        "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", 
        "W", "X", "Y", "Z"
    };
    
    public LicensePlateGenerator() {
        this(PlateType.REGULAR, null);
    }
    
    public LicensePlateGenerator(PlateType plateType) {
        this(plateType, null);
    }
    
    public LicensePlateGenerator(PlateType plateType, String specificProvince) {
        this.plateType = plateType;
        this.specificProvince = specificProvince;
    }
    
    @Override
    public String generate(GenerationContext context) {
        switch (plateType) {
            case REGULAR:
                return generateRegularPlate();
            case NEW_ENERGY:
                return generateNewEnergyPlate();
            case TRUCK:
                return generateTruckPlate();
            case POLICE:
                return generatePolicePlate();
            case MILITARY:
                return generateMilitaryPlate();
            case CONSULATE:
                return generateConsulatePlate();
            case SPECIAL:
                return generateSpecialPlate();
            default:
                return generateRegularPlate();
        }
    }
    
    /**
     * 生成普通车牌 (蓝牌)
     * 格式: 省份简称 + 城市代码 + 5位数字字母组合
     * 例如: 京A12345, 沪B1A234
     */
    private String generateRegularPlate() {
        String province = getRandomProvince();
        String cityCode = getRandomCityCode();
        
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            // 第一位通常是数字
            if (i == 0) {
                suffix.append(getRandomNumber());
            } else {
                // 其他位可以是数字或字母
                suffix.append(getRandomAlphanumeric());
            }
        }
        
        return province + cityCode + suffix.toString();
    }
    
    /**
     * 生成新能源车牌 (绿牌)
     * 格式: 省份简称 + 城市代码 + 6位数字字母组合
     * 例如: 京AD12345, 沪AF1A234
     */
    private String generateNewEnergyPlate() {
        String province = getRandomProvince();
        String cityCode = getRandomCityCode();
        
        StringBuilder suffix = new StringBuilder();
        
        // 新能源车牌第三位通常是D或F
        String energyFlag = random.nextBoolean() ? "D" : "F";
        suffix.append(energyFlag);
        
        // 后5位数字字母组合
        for (int i = 0; i < 5; i++) {
            suffix.append(getRandomAlphanumeric());
        }
        
        return province + cityCode + suffix.toString();
    }
    
    /**
     * 生成货车车牌 (黄牌)
     * 格式与普通车牌相同，但通常数字较多
     */
    private String generateTruckPlate() {
        String province = getRandomProvince();
        String cityCode = getRandomCityCode();
        
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            // 货车牌照数字比例更高
            if (random.nextDouble() < 0.7) {
                suffix.append(getRandomNumber());
            } else {
                suffix.append(getRandomLetter());
            }
        }
        
        return province + cityCode + suffix.toString();
    }
    
    /**
     * 生成警车车牌 (白牌)
     * 格式: 省份简称 + 城市代码 + 4位数字 + "警"
     * 例如: 京A1234警
     */
    private String generatePolicePlate() {
        String province = getRandomProvince();
        String cityCode = getRandomCityCode();
        
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            suffix.append(getRandomNumber());
        }
        suffix.append("警");
        
        return province + cityCode + suffix.toString();
    }
    
    /**
     * 生成军车车牌
     * 格式: 军区代码 + 字母 + 5位数字
     * 例如: 军A12345, 空B23456
     */
    private String generateMilitaryPlate() {
        String[] militaryPrefixes = {"军", "海", "空", "火", "武"};
        String prefix = militaryPrefixes[random.nextInt(militaryPrefixes.length)];
        String code = getRandomLetter();
        
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            suffix.append(getRandomNumber());
        }
        
        return prefix + code + suffix.toString();
    }
    
    /**
     * 生成领事馆车牌 (黑牌)
     * 格式: 使 + 3位数字 + 领 或 领 + 3位数字 + 使
     * 例如: 使001领, 领123使
     */
    private String generateConsulatePlate() {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            number.append(getRandomNumber());
        }
        
        if (random.nextBoolean()) {
            return "使" + number.toString() + "领";
        } else {
            return "领" + number.toString() + "使";
        }
    }
    
    /**
     * 生成特殊车牌
     * 包括教练车、试验车等
     */
    private String generateSpecialPlate() {
        String province = getRandomProvince();
        String cityCode = getRandomCityCode();
        
        String[] specialSuffixes = {"学", "试", "临", "挂"};
        String specialSuffix = specialSuffixes[random.nextInt(specialSuffixes.length)];
        
        StringBuilder middle = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            middle.append(getRandomAlphanumeric());
        }
        
        return province + cityCode + middle.toString() + specialSuffix;
    }
    
    /**
     * 获取随机省份
     */
    private String getRandomProvince() {
        if (specificProvince != null && !specificProvince.isEmpty()) {
            return specificProvince;
        }
        return PROVINCES[random.nextInt(PROVINCES.length)];
    }
    
    /**
     * 获取随机城市代码
     */
    private String getRandomCityCode() {
        return CITY_CODES[random.nextInt(CITY_CODES.length)];
    }
    
    /**
     * 获取随机数字字母组合
     */
    private String getRandomAlphanumeric() {
        return ALPHANUMERIC[random.nextInt(ALPHANUMERIC.length)];
    }
    
    /**
     * 获取随机数字
     */
    private String getRandomNumber() {
        return NUMBERS[random.nextInt(NUMBERS.length)];
    }
    
    /**
     * 获取随机字母
     */
    private String getRandomLetter() {
        return LETTERS[random.nextInt(LETTERS.length)];
    }
    
    /**
     * 验证车牌号格式是否正确
     */
    public static boolean isValidLicensePlate(String plate) {
        if (plate == null || plate.isEmpty()) {
            return false;
        }
        
        // 基本长度检查
        if (plate.length() < 7 || plate.length() > 8) {
            return false;
        }
        
        // 检查省份简称
        String province = plate.substring(0, 1);
        boolean validProvince = false;
        for (String p : PROVINCES) {
            if (p.equals(province)) {
                validProvince = true;
                break;
            }
        }
        
        if (!validProvince) {
            // 检查特殊车牌
            return plate.contains("军") || plate.contains("海") || plate.contains("空") || 
                   plate.contains("火") || plate.contains("武") || plate.contains("使") || 
                   plate.contains("领");
        }
        
        return true;
    }
}
package com.dataforge.generators.identifiers;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 增强银行卡号生成器
 * 支持中国主要银行、多种卡类型、完整的BIN规则和验证算法
 */
public class EnhancedBankCardGenerator implements DataGenerator<String> {
    
    private static final Random random = new Random();
    
    private final CardType cardType;
    private final BankIssuer bankIssuer;
    private final CardCategory category;
    private final boolean generateValidCard;
    
    public enum CardType {
        DEBIT_CARD,         // 借记卡
        CREDIT_CARD,        // 信用卡
        PREPAID_CARD,       // 预付费卡 
        CORPORATE_CARD,     // 企业卡
        ANY                 // 任意类型
    }
    
    public enum BankIssuer {
        // 国有银行
        ICBC,               // 工商银行 (622200-622299)
        CCB,                // 建设银行 (436742, 622700-622799)
        ABC,                // 农业银行 (622848, 95595, 103)
        BOC,                // 中国银行 (621660-621679, 621785-621799)
        BOCOM,              // 交通银行 (622258-622259, 622262-622279)
        PSBC,               // 邮储银行 (622150-622159, 622188)
        
        // 股份制银行
        CMB,                // 招商银行 (622580-622589, 622575-622579)
        CITIC,              // 中信银行 (622690-622699, 376968, 376969)
        CEB,                // 光大银行 (622655-622664, 356837-356846)
        CMBC,               // 民生银行 (622622-622633, 415599)
        SPDB,               // 浦发银行 (622521-622523, 404738-404739)
        CIB,                // 兴业银行 (622902-622904, 438588)
        PAB,                // 平安银行 (622155-622157, 622535-622539)
        HXB,                // 华夏银行 (622630-622634, 528708)
        
        // 国际卡组织
        VISA,               // Visa (4)
        MASTERCARD,         // MasterCard (5)
        UNIONPAY,           // 银联 (62)
        AMEX,               // 美国运通 (34, 37)
        JCB,                // JCB (35)
        DISCOVER,           // Discover (6011, 644-649, 65)
        
        ANY                 // 任意发卡行
    }
    
    public enum CardCategory {
        STANDARD,           // 标准卡
        GOLD,              // 金卡
        PLATINUM,          // 白金卡
        DIAMOND,           // 钻石卡
        BLACK,             // 黑卡
        ANY                // 任意等级
    }
    
    // 中国银行BIN码数据
    private static final Map<BankIssuer, BinInfo[]> CHINA_BANK_BINS = new HashMap<>();
    
    // 国际卡组织BIN码数据
    private static final Map<BankIssuer, BinInfo[]> INTERNATIONAL_BINS = new HashMap<>();
    
    static {
        // 初始化中国银行BIN码
        CHINA_BANK_BINS.put(BankIssuer.ICBC, new BinInfo[]{
            new BinInfo("622200", 19, CardType.DEBIT_CARD, "工商银行借记卡"),
            new BinInfo("622202", 19, CardType.CREDIT_CARD, "工商银行信用卡"),
            new BinInfo("627857", 16, CardType.DEBIT_CARD, "工商银行储蓄卡"),
            new BinInfo("427010", 16, CardType.CREDIT_CARD, "工商银行牡丹卡")
        });
        
        CHINA_BANK_BINS.put(BankIssuer.CCB, new BinInfo[]{
            new BinInfo("436742", 19, CardType.DEBIT_CARD, "建设银行储蓄卡"),
            new BinInfo("622700", 19, CardType.DEBIT_CARD, "建设银行龙卡通"),
            new BinInfo("533178", 16, CardType.CREDIT_CARD, "建设银行龙卡"),
            new BinInfo("622280", 19, CardType.CREDIT_CARD, "建设银行信用卡")
        });
        
        CHINA_BANK_BINS.put(BankIssuer.ABC, new BinInfo[]{
            new BinInfo("622848", 19, CardType.DEBIT_CARD, "农业银行金穗卡"),
            new BinInfo("95595", 19, CardType.DEBIT_CARD, "农业银行储蓄卡"),
            new BinInfo("103", 19, CardType.DEBIT_CARD, "农业银行借记卡"),
            new BinInfo("552599", 16, CardType.CREDIT_CARD, "农业银行信用卡")
        });
        
        CHINA_BANK_BINS.put(BankIssuer.BOC, new BinInfo[]{
            new BinInfo("621660", 19, CardType.DEBIT_CARD, "中国银行长城卡"),
            new BinInfo("621785", 19, CardType.DEBIT_CARD, "中国银行借记卡"),
            new BinInfo("376968", 16, CardType.CREDIT_CARD, "中国银行信用卡"),
            new BinInfo("524094", 16, CardType.CREDIT_CARD, "中国银行长城信用卡")
        });
        
        CHINA_BANK_BINS.put(BankIssuer.CMB, new BinInfo[]{
            new BinInfo("622580", 16, CardType.DEBIT_CARD, "招商银行一卡通"),
            new BinInfo("622588", 19, CardType.DEBIT_CARD, "招商银行储蓄卡"),
            new BinInfo("439188", 16, CardType.CREDIT_CARD, "招商银行信用卡"),
            new BinInfo("356885", 16, CardType.CREDIT_CARD, "招商银行金卡")
        });
        
        // 初始化国际卡组织BIN码
        INTERNATIONAL_BINS.put(BankIssuer.VISA, new BinInfo[]{
            new BinInfo("4", 16, CardType.DEBIT_CARD, "Visa借记卡"),
            new BinInfo("4", 16, CardType.CREDIT_CARD, "Visa信用卡"),
            new BinInfo("4", 19, CardType.DEBIT_CARD, "Visa借记卡长号")
        });
        
        INTERNATIONAL_BINS.put(BankIssuer.MASTERCARD, new BinInfo[]{
            new BinInfo("5", 16, CardType.DEBIT_CARD, "MasterCard借记卡"),
            new BinInfo("5", 16, CardType.CREDIT_CARD, "MasterCard信用卡"),
            new BinInfo("2", 16, CardType.DEBIT_CARD, "MasterCard新系列卡")
        });
        
        INTERNATIONAL_BINS.put(BankIssuer.UNIONPAY, new BinInfo[]{
            new BinInfo("62", 16, CardType.DEBIT_CARD, "银联借记卡"),
            new BinInfo("62", 19, CardType.DEBIT_CARD, "银联借记卡长号"),
            new BinInfo("62", 16, CardType.CREDIT_CARD, "银联信用卡"),
            new BinInfo("60", 16, CardType.DEBIT_CARD, "银联标准卡")
        });
        
        INTERNATIONAL_BINS.put(BankIssuer.AMEX, new BinInfo[]{
            new BinInfo("34", 15, CardType.CREDIT_CARD, "美国运通信用卡"),
            new BinInfo("37", 15, CardType.CREDIT_CARD, "美国运通金卡")
        });
        
        INTERNATIONAL_BINS.put(BankIssuer.JCB, new BinInfo[]{
            new BinInfo("35", 16, CardType.CREDIT_CARD, "JCB信用卡")
        });
        
        INTERNATIONAL_BINS.put(BankIssuer.DISCOVER, new BinInfo[]{
            new BinInfo("6011", 16, CardType.CREDIT_CARD, "Discover信用卡"),
            new BinInfo("65", 16, CardType.CREDIT_CARD, "Discover卡")
        });
    }
    
    public EnhancedBankCardGenerator() {
        this(CardType.ANY, BankIssuer.ANY, CardCategory.ANY, true);
    }
    
    public EnhancedBankCardGenerator(CardType cardType, BankIssuer bankIssuer, CardCategory category, boolean generateValidCard) {
        this.cardType = cardType;
        this.bankIssuer = bankIssuer;
        this.category = category;
        this.generateValidCard = generateValidCard;
    }
    
    @Override
    public String generate(GenerationContext context) {
        BinInfo selectedBin = selectBinInfo();
        String cardNumber = generateCardNumber(selectedBin);
        
        return cardNumber;
    }
    
    /**
     * 选择BIN信息
     */
    private BinInfo selectBinInfo() {
        BinInfo[] availableBins;
        
        if (bankIssuer == BankIssuer.ANY) {
            // 随机选择银行
            BankIssuer[] allIssuers = BankIssuer.values();
            BankIssuer randomIssuer;
            do {
                randomIssuer = allIssuers[random.nextInt(allIssuers.length)];
            } while (randomIssuer == BankIssuer.ANY);
            
            availableBins = getBinsForIssuer(randomIssuer);
        } else {
            availableBins = getBinsForIssuer(bankIssuer);
        }
        
        if (availableBins == null || availableBins.length == 0) {
            // 回退到银联卡
            availableBins = INTERNATIONAL_BINS.get(BankIssuer.UNIONPAY);
        }
        
        // 根据卡类型过滤
        if (cardType != CardType.ANY) {
            BinInfo[] filteredBins = filterBinsByType(availableBins, cardType);
            if (filteredBins.length > 0) {
                availableBins = filteredBins;
            }
        }
        
        return availableBins[random.nextInt(availableBins.length)];
    }
    
    /**
     * 获取指定发卡行的BIN信息
     */
    private BinInfo[] getBinsForIssuer(BankIssuer issuer) {
        BinInfo[] bins = CHINA_BANK_BINS.get(issuer);
        if (bins == null) {
            bins = INTERNATIONAL_BINS.get(issuer);
        }
        return bins;
    }
    
    /**
     * 根据卡类型过滤BIN
     */
    private BinInfo[] filterBinsByType(BinInfo[] bins, CardType type) {
        return java.util.Arrays.stream(bins)
            .filter(bin -> bin.cardType == type || bin.cardType == CardType.ANY)
            .toArray(BinInfo[]::new);
    }
    
    /**
     * 生成银行卡号
     */
    private String generateCardNumber(BinInfo binInfo) {
        StringBuilder cardNumber = new StringBuilder(binInfo.binPrefix);
        
        // 生成剩余位数（除了校验位）
        int remainingLength = binInfo.totalLength - binInfo.binPrefix.length() - 1;
        for (int i = 0; i < remainingLength; i++) {
            cardNumber.append(random.nextInt(10));
        }
        
        // 计算并添加校验位
        if (generateValidCard) {
            int checkDigit = calculateLuhnCheckDigit(cardNumber.toString());
            cardNumber.append(checkDigit);
        } else {
            // 生成无效校验位
            int validCheckDigit = calculateLuhnCheckDigit(cardNumber.toString());
            int invalidCheckDigit;
            do {
                invalidCheckDigit = random.nextInt(10);
            } while (invalidCheckDigit == validCheckDigit);
            cardNumber.append(invalidCheckDigit);
        }
        
        return cardNumber.toString();
    }
    
    /**
     * 使用Luhn算法计算校验位
     */
    private int calculateLuhnCheckDigit(String cardNumber) {
        int sum = 0;
        boolean alternate = true;
        
        // 从右到左处理数字
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum * 9) % 10;
    }
    
    /**
     * 验证银行卡号是否符合Luhn算法
     */
    public static boolean validateLuhn(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10) == 0;
    }
    
    /**
     * 识别银行卡类型
     */
    public static CardInfo identifyCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 6) {
            return new CardInfo(BankIssuer.ANY, CardType.ANY, "未知", false);
        }
        
        String prefix = cardNumber.substring(0, 6);
        
        // 检查中国银行
        for (Map.Entry<BankIssuer, BinInfo[]> entry : CHINA_BANK_BINS.entrySet()) {
            for (BinInfo bin : entry.getValue()) {
                if (cardNumber.startsWith(bin.binPrefix)) {
                    boolean valid = validateLuhn(cardNumber);
                    return new CardInfo(entry.getKey(), bin.cardType, bin.description, valid);
                }
            }
        }
        
        // 检查国际卡组织
        for (Map.Entry<BankIssuer, BinInfo[]> entry : INTERNATIONAL_BINS.entrySet()) {
            for (BinInfo bin : entry.getValue()) {
                if (cardNumber.startsWith(bin.binPrefix)) {
                    boolean valid = validateLuhn(cardNumber);
                    return new CardInfo(entry.getKey(), bin.cardType, bin.description, valid);
                }
            }
        }
        
        return new CardInfo(BankIssuer.ANY, CardType.ANY, "未识别", validateLuhn(cardNumber));
    }
    
    /**
     * BIN信息数据类
     */
    private static class BinInfo {
        final String binPrefix;
        final int totalLength;
        final CardType cardType;
        final String description;
        
        BinInfo(String binPrefix, int totalLength, CardType cardType, String description) {
            this.binPrefix = binPrefix;
            this.totalLength = totalLength;
            this.cardType = cardType;
            this.description = description;
        }
    }
    
    /**
     * 银行卡信息类
     */
    public static class CardInfo {
        public final BankIssuer issuer;
        public final CardType cardType;
        public final String description;
        public final boolean isValid;
        
        public CardInfo(BankIssuer issuer, CardType cardType, String description, boolean isValid) {
            this.issuer = issuer;
            this.cardType = cardType;
            this.description = description;
            this.isValid = isValid;
        }
        
        @Override
        public String toString() {
            return String.format("发卡行: %s, 卡类型: %s, 描述: %s, 有效: %s", 
                issuer, cardType, description, isValid ? "是" : "否");
        }
    }
    
    /**
     * 生成格式化的银行卡号（带空格）
     */
    public String generateFormattedCard(GenerationContext context) {
        String cardNumber = generate(context);
        return formatCardNumber(cardNumber);
    }
    
    /**
     * 格式化银行卡号（每4位加一个空格）
     */
    public static String formatCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return cardNumber;
        }
        
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < cardNumber.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(cardNumber.charAt(i));
        }
        
        return formatted.toString();
    }
    
    /**
     * 生成带掩码的银行卡号（隐藏中间位数）
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return cardNumber;
        }
        
        String first4 = cardNumber.substring(0, 4);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String masked = "*".repeat(cardNumber.length() - 8);
        
        return first4 + masked + last4;
    }
}
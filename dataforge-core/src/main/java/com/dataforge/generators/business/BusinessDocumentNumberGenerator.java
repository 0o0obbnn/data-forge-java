package com.dataforge.generators.business;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 业务单据号生成器
 * 支持多种单据类型的编号生成，包括订单号、发票号、合同号等
 */
public class BusinessDocumentNumberGenerator implements DataGenerator<String> {
    
    /**
     * 单据类型枚举
     */
    public enum DocumentType {
        ORDER("订单号", "ORD", 8),
        INVOICE("发票号", "INV", 10),
        CONTRACT("合同号", "CTR", 12),
        RECEIPT("收据号", "RCP", 8),
        PURCHASE("采购单号", "PUR", 10),
        DELIVERY("发货单号", "DEL", 8),
        RETURN("退货单号", "RTN", 8),
        PAYMENT("付款单号", "PAY", 10),
        REFUND("退款单号", "REF", 8),
        QUOTATION("报价单号", "QUO", 10),
        ANY("任意单据", "DOC", 10);
        
        private final String name;
        private final String prefix;
        private final int defaultLength;
        
        DocumentType(String name, String prefix, int defaultLength) {
            this.name = name;
            this.prefix = prefix;
            this.defaultLength = defaultLength;
        }
        
        public String getName() {
            return name;
        }
        
        public String getPrefix() {
            return prefix;
        }
        
        public int getDefaultLength() {
            return defaultLength;
        }
    }
    
    /**
     * 编号格式枚举
     */
    public enum NumberFormat {
        SEQUENTIAL("连续递增"),          // ORD20240807001
        TIMESTAMP("时间戳"),            // ORD1691375520123
        RANDOM("随机数字"),             // ORD85739264
        DATE_SEQUENCE("日期+序列"),     // ORD240807001
        YEAR_MONTH_SEQ("年月+序列");    // ORD2408001
        
        private final String description;
        
        NumberFormat(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private final DocumentType documentType;
    private final NumberFormat numberFormat;
    private final String customPrefix;
    private final int numberLength;
    
    // 用于生成连续递增号码的计数器
    private static final ConcurrentMap<String, Integer> sequenceCounters = new ConcurrentHashMap<>();
    
    // 用于确保唯一性的集合
    private static final ConcurrentMap<String, Boolean> generatedNumbers = new ConcurrentHashMap<>();
    
    public BusinessDocumentNumberGenerator() {
        this(DocumentType.ORDER, NumberFormat.DATE_SEQUENCE, null, 0);
    }
    
    public BusinessDocumentNumberGenerator(DocumentType documentType) {
        this(documentType, NumberFormat.DATE_SEQUENCE, null, 0);
    }
    
    public BusinessDocumentNumberGenerator(DocumentType documentType, NumberFormat numberFormat) {
        this(documentType, numberFormat, null, 0);
    }
    
    public BusinessDocumentNumberGenerator(DocumentType documentType, NumberFormat numberFormat, 
                                         String customPrefix, int numberLength) {
        this.documentType = documentType;
        this.numberFormat = numberFormat;
        this.customPrefix = customPrefix;
        this.numberLength = numberLength > 0 ? numberLength : documentType.getDefaultLength();
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        String prefix = customPrefix != null ? customPrefix : documentType.getPrefix();
        
        for (int attempt = 0; attempt < 1000; attempt++) {
            String documentNumber = generateDocumentNumber(prefix, random);
            
            if (generatedNumbers.putIfAbsent(documentNumber, Boolean.TRUE) == null) {
                return documentNumber;
            }
        }
        
        // 如果尝试1000次仍有重复，添加时间戳后缀确保唯一性
        String fallbackNumber = generateDocumentNumber(prefix, random) + System.nanoTime() % 1000;
        generatedNumbers.put(fallbackNumber, Boolean.TRUE);
        return fallbackNumber;
    }
    
    private String generateDocumentNumber(String prefix, Random random) {
        LocalDate now = LocalDate.now();
        
        switch (numberFormat) {
            case SEQUENTIAL:
                return generateSequentialNumber(prefix, now);
                
            case TIMESTAMP:
                return generateTimestampNumber(prefix);
                
            case RANDOM:
                return generateRandomNumber(prefix, random);
                
            case DATE_SEQUENCE:
                return generateDateSequenceNumber(prefix, now);
                
            case YEAR_MONTH_SEQ:
                return generateYearMonthSequenceNumber(prefix, now);
                
            default:
                return generateDateSequenceNumber(prefix, now);
        }
    }
    
    private String generateSequentialNumber(String prefix, LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = prefix + dateStr;
        
        int sequence = sequenceCounters.compute(key, (k, v) -> (v == null) ? 1 : v + 1);
        
        // 确保序列号有足够的位数
        int seqDigits = Math.max(3, numberLength - prefix.length() - 8);
        String format = "%s%s%0" + seqDigits + "d";
        
        return String.format(format, prefix, dateStr, sequence);
    }
    
    private String generateTimestampNumber(String prefix) {
        long timestamp = System.currentTimeMillis();
        
        // 截取时间戳的后几位以符合长度要求
        int timestampDigits = numberLength - prefix.length();
        if (timestampDigits > 0) {
            String timestampStr = String.valueOf(timestamp);
            if (timestampStr.length() > timestampDigits) {
                timestampStr = timestampStr.substring(timestampStr.length() - timestampDigits);
            }
            return prefix + timestampStr;
        }
        
        return prefix + timestamp;
    }
    
    private String generateRandomNumber(String prefix, Random random) {
        int digits = numberLength - prefix.length();
        if (digits <= 0) {
            digits = 8; // 默认8位随机数
        }
        
        StringBuilder number = new StringBuilder(prefix);
        for (int i = 0; i < digits; i++) {
            number.append(random.nextInt(10));
        }
        
        return number.toString();
    }
    
    private String generateDateSequenceNumber(String prefix, LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyMMdd"));
        String key = prefix + dateStr;
        
        int sequence = sequenceCounters.compute(key, (k, v) -> (v == null) ? 1 : v + 1);
        
        // 序列号位数
        int seqDigits = Math.max(3, numberLength - prefix.length() - 6);
        String format = "%s%s%0" + seqDigits + "d";
        
        return String.format(format, prefix, dateStr, sequence);
    }
    
    private String generateYearMonthSequenceNumber(String prefix, LocalDate date) {
        String yearMonthStr = date.format(DateTimeFormatter.ofPattern("yyMM"));
        String key = prefix + yearMonthStr;
        
        int sequence = sequenceCounters.compute(key, (k, v) -> (v == null) ? 1 : v + 1);
        
        // 序列号位数
        int seqDigits = Math.max(3, numberLength - prefix.length() - 4);
        String format = "%s%s%0" + seqDigits + "d";
        
        return String.format(format, prefix, yearMonthStr, sequence);
    }
    
    /**
     * 验证单据号格式是否有效
     * @param documentNumber 单据号
     * @return 是否有效
     */
    public static boolean isValidDocumentNumber(String documentNumber) {
        if (documentNumber == null || documentNumber.length() < 5) {
            return false;
        }
        
        // 检查是否以已知前缀开头
        for (DocumentType type : DocumentType.values()) {
            if (documentNumber.startsWith(type.getPrefix())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 从单据号中提取单据类型
     * @param documentNumber 单据号
     * @return 单据类型
     */
    public static DocumentType extractDocumentType(String documentNumber) {
        if (documentNumber == null) {
            return null;
        }
        
        for (DocumentType type : DocumentType.values()) {
            if (documentNumber.startsWith(type.getPrefix())) {
                return type;
            }
        }
        
        return null;
    }
    
    /**
     * 获取生成的单据号总数
     * @return 单据号总数
     */
    public static int getGeneratedCount() {
        return generatedNumbers.size();
    }
    
    /**
     * 清除生成的单据号缓存（用于测试）
     */
    public static void clearCache() {
        generatedNumbers.clear();
        sequenceCounters.clear();
    }
    
    /**
     * 获取所有支持的单据类型
     * @return 单据类型列表
     */
    public static List<String> getSupportedDocumentTypes() {
        List<String> types = new ArrayList<>();
        for (DocumentType type : DocumentType.values()) {
            types.add(type.name());
        }
        return types;
    }
    
    /**
     * 获取所有支持的编号格式
     * @return 编号格式列表
     */
    public static List<String> getSupportedNumberFormats() {
        List<String> formats = new ArrayList<>();
        for (NumberFormat format : NumberFormat.values()) {
            formats.add(format.name());
        }
        return formats;
    }
    
    /**
     * 根据单据类型和格式生成单据号
     * @param type 单据类型
     * @param format 编号格式
     * @param context 生成上下文
     * @return 单据号
     */
    public static String generateForTypeAndFormat(DocumentType type, NumberFormat format, GenerationContext context) {
        BusinessDocumentNumberGenerator generator = new BusinessDocumentNumberGenerator(type, format);
        return generator.generate(context);
    }
    
    @Override
    public String getName() {
        return "business_document_number";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("type", "format", "prefix", "length");
    }
}
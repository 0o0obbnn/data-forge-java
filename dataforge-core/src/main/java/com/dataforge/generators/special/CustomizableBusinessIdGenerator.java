package com.dataforge.generators.special;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * 可自定义业务ID生成器
 * 用于生成具有自定义格式的业务标识符
 * 支持固定或可变长度、自定义字符集、前缀、后缀和分隔符
 */
public class CustomizableBusinessIdGenerator implements DataGenerator<String> {
    
    private final Random random;
    
    /**
     * 使用默认设置创建可自定义业务ID生成器
     */
    public CustomizableBusinessIdGenerator() {
        this.random = new Random();
    }
    
    @Override
    public String generate(GenerationContext context) {
        // 获取参数
        Object minLengthObj = context.getParameter("min_length", "8");
        int minLength = Integer.parseInt(minLengthObj.toString());
        
        Object maxLengthObj = context.getParameter("max_length", "12");
        int maxLength = Integer.parseInt(maxLengthObj.toString());
        
        Object prefixObj = context.getParameter("prefix", "");
        String prefix = prefixObj.toString();
        
        Object suffixObj = context.getParameter("suffix", "");
        String suffix = suffixObj.toString();
        
        Object charsObj = context.getParameter("chars", "alphanumeric");
        String chars = charsObj.toString().toLowerCase();
        
        Object separatorObj = context.getParameter("separator", "");
        String separator = separatorObj.toString();
        
        Object separatorIntervalObj = context.getParameter("separator_interval", "0");
        int separatorInterval = Integer.parseInt(separatorIntervalObj.toString());
        
        Object uniqueObj = context.getParameter("unique", false);
        boolean unique = Boolean.parseBoolean(uniqueObj.toString());
        
        // 确定主体部分的长度（不包括前缀和后缀）
        int mainLength = minLength + random.nextInt(Math.max(1, maxLength - minLength + 1));
        if (!prefix.isEmpty()) {
            mainLength = Math.max(0, mainLength - prefix.length());
        }
        if (!suffix.isEmpty()) {
            mainLength = Math.max(0, mainLength - suffix.length());
        }
        
        // 生成主体部分
        StringBuilder mainPart = new StringBuilder();
        String charset = getCharset(chars);
        
        for (int i = 0; i < mainLength; i++) {
            char c = charset.charAt(random.nextInt(charset.length()));
            mainPart.append(c);
            
            // 在指定间隔添加分隔符
            if (separatorInterval > 0 && separator.length() > 0 && 
                (i + 1) % separatorInterval == 0 && i < mainLength - 1) {
                mainPart.append(separator);
            }
        }
        
        // 组合前缀、主体部分和后缀
        StringBuilder result = new StringBuilder();
        if (!prefix.isEmpty()) {
            result.append(prefix);
        }
        result.append(mainPart);
        if (!suffix.isEmpty()) {
            result.append(suffix);
        }
        
        return result.toString();
    }
    
    /**
     * 根据指定类型获取字符集
     * 
     * @param chars 字符集类型
     * @return 字符集字符串
     */
    private String getCharset(String chars) {
        switch (chars) {
            case "numeric":
                return "0123456789";
            case "alpha":
                return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            case "alphanumeric":
            default:
                return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            case "alphanumeric_upper":
                return "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            case "alphanumeric_lower":
                return "abcdefghijklmnopqrstuvwxyz0123456789";
            case "hex":
                return "0123456789ABCDEF";
            case "base64":
                return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        }
    }
    
    @Override
    public String getName() {
        return "customizable_business_id";
    }
    
    @Override
    public java.util.List<String> getSupportedParameters() {
        return java.util.Arrays.asList(
            "min_length", "max_length", "prefix", "suffix", "chars", 
            "separator", "separator_interval", "unique");
    }
}
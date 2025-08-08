package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * 密码生成器，用于生成具有可配置复杂度的密码。
 */
public class PasswordGenerator implements DataGenerator<String> {
    
    public enum Complexity {
        LOW,    // 仅小写字母
        MEDIUM, // 小写和大写字母 + 数字
        HIGH    // 小写和大写字母 + 数字 + 特殊字符
    }
    
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    private final int minLength;
    private final int maxLength;
    private final Complexity complexity;
    
    public PasswordGenerator() {
        this(8, 16, Complexity.MEDIUM);
    }
    
    public PasswordGenerator(int minLength, int maxLength, Complexity complexity) {
        if (minLength <= 0 || maxLength <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        if (minLength > maxLength) {
            throw new IllegalArgumentException("Min length cannot be greater than max length");
        }
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.complexity = complexity;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // 确定密码长度
        int length = minLength + random.nextInt(maxLength - minLength + 1);
        
        // 根据复杂度构建字符集
        StringBuilder charSet = new StringBuilder();
        switch (complexity) {
            case LOW:
                charSet.append(LOWERCASE);
                break;
            case MEDIUM:
                charSet.append(LOWERCASE).append(UPPERCASE).append(DIGITS);
                break;
            case HIGH:
                charSet.append(LOWERCASE).append(UPPERCASE).append(DIGITS).append(SPECIAL_CHARS);
                break;
        }
        
        // 生成密码
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charSet.length());
            password.append(charSet.charAt(index));
        }
        
        return password.toString();
    }
}
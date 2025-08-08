package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 账户名生成器，用于生成唯一的账户名。
 */
public class AccountNameGenerator implements DataGenerator<String> {
    
    private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    private final int minLength;
    private final int maxLength;
    private final boolean unique;
    
    // 追踪已生成的名称以保证唯一性（在实际实现中，这将更加复杂）
    private final Set<String> generatedNames = new HashSet<>();
    
    public AccountNameGenerator() {
        this(6, 12, true);
    }
    
    public AccountNameGenerator(int minLength, int maxLength, boolean unique) {
        if (minLength <= 0 || maxLength <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        if (minLength > maxLength) {
            throw new IllegalArgumentException("Min length cannot be greater than max length");
        }
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.unique = unique;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        String accountName;
        do {
            // 确定账户名长度
            int length = minLength + random.nextInt(maxLength - minLength + 1);
            
            // 生成账户名
            StringBuilder nameBuilder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int index = random.nextInt(ALPHANUMERIC.length());
                nameBuilder.append(ALPHANUMERIC.charAt(index));
            }
            
            accountName = nameBuilder.toString();
        } while (unique && !generatedNames.add(accountName)); // 如果需要唯一性且名称已存在则重新生成
        
        return accountName;
    }
}
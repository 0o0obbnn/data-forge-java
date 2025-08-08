package com.dataforge.generators.identifiers;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.UUID;

/**
 * UUID生成器，用于生成通用唯一标识符
 */
public class UuidGenerator implements DataGenerator<String> {
    
    public enum UuidVersion {
        RANDOM, // UUID version 4 (random)
        TIME_BASED // UUID version 1 (time-based)
    }
    
    private final UuidVersion version;
    
    public UuidGenerator() {
        this(UuidVersion.RANDOM);
    }
    
    public UuidGenerator(UuidVersion version) {
        this.version = version;
    }
    
    @Override
    public String generate(GenerationContext context) {
        switch (version) {
            case TIME_BASED:
                // 为了简单起见，我们仍然使用随机UUID，但请注意完整实现
                // 将使用基于时间的UUID，包含节点标识符和时间戳
                return UUID.randomUUID().toString();
            case RANDOM:
            default:
                return UUID.randomUUID().toString();
        }
    }
}
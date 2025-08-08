package com.dataforge.core;

import java.util.Collections;
import java.util.List;

/**
 * 数据生成器接口。
 * 
 * @param <T> 要生成的数据类型
 */
public interface DataGenerator<T> {
    
    /**
     * 生成单个数据项。
     * 
     * @param context 生成上下文
     * @return 生成的数据项
     */
    T generate(GenerationContext context);
    
    /**
     * 获取此生成器的名称。
     * 
     * @return 生成器名称
     */
    default String getName() {
        return this.getClass().getSimpleName().toLowerCase().replace("generator", "");
    }
    
    /**
     * 获取此生成器支持的参数列表。
     * 
     * @return 支持的参数名称列表
     */
    default List<String> getSupportedParameters() {
        return Collections.emptyList();
    }
}
package com.dataforge.spi;

import com.dataforge.core.DataGenerator;
import java.util.List;

/**
 * SPI接口：DataForge扩展点
 * 用于支持通过ServiceLoader机制动态加载自定义生成器
 */
public interface DataForgeExtension {

    /**
     * 获取扩展的名称
     * @return 扩展名称
     */
    String getName();

    /**
     * 获取扩展的描述
     * @return 扩展描述
     */
    String getDescription();

    /**
     * 获取扩展提供的生成器类
     * @return 生成器类
     */
    List<Class<? extends DataGenerator>> getGeneratorClasses();

    /**
     * 获取扩展的版本
     * @return 版本号
     */
    default String getVersion() {
        return "1.0.0";
    }

    /**
     * 获取扩展的优先级
     * @return 优先级（数值越大优先级越高）
     */
    default int getPriority() {
        return 0;
    }
}
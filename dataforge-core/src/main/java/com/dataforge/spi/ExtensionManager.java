package com.dataforge.spi;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GeneratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 扩展管理器
 * 负责管理通过SPI机制加载的所有扩展
 */
public class ExtensionManager {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionManager.class);
    private static final ExtensionManager instance = new ExtensionManager();
    
    private final Map<String, DataForgeExtension> extensions = new ConcurrentHashMap<>();
    private final Map<String, Class<? extends DataGenerator>> customGenerators = new ConcurrentHashMap<>();
    private boolean initialized = false;

    private ExtensionManager() {
        // 私有构造函数防止实例化
    }

    public static ExtensionManager getInstance() {
        return instance;
    }

    /**
     * 初始化扩展管理器
     * 通过ServiceLoader加载所有可用的扩展
     */
    public synchronized void initialize() {
        if (initialized) {
            return;
        }

        logger.info("Initializing DataForge extension manager...");
        
        ServiceLoader<DataForgeExtension> loader = ServiceLoader.load(DataForgeExtension.class);
        List<DataForgeExtension> extensionList = new ArrayList<>();
        
        for (DataForgeExtension extension : loader) {
            extensionList.add(extension);
        }
        
        // 按优先级排序
        extensionList.sort(Comparator.comparingInt(DataForgeExtension::getPriority).reversed());
        
        // 注册扩展
        for (DataForgeExtension extension : extensionList) {
            try {
                registerExtension(extension);
                logger.info("Registered extension: {} (priority: {})", 
                           extension.getName(), extension.getPriority());
            } catch (Exception e) {
                logger.error("Failed to register extension: " + extension.getName(), e);
            }
        }
        
        initialized = true;
        logger.info("Extension manager initialized with {} extensions", extensionList.size());
    }

    /**
     * 注册扩展
     * @param extension 扩展实例
     */
    public void registerExtension(DataForgeExtension extension) {
        Objects.requireNonNull(extension, "Extension cannot be null");
        Objects.requireNonNull(extension.getName(), "Extension name cannot be null");
        
        extensions.put(extension.getName(), extension);
        
        // 注册扩展提供的生成器
        List<Class<? extends DataGenerator>> generatorClasses = extension.getGeneratorClasses();
        if (generatorClasses != null) {
            for (Class<? extends DataGenerator> generatorClass : generatorClasses) {
                try {
                    DataGenerator<?> generator = generatorClass.getDeclaredConstructor().newInstance();
                    String generatorName = generator.getName();
                    
                    customGenerators.put(generatorName, generatorClass);
                    GeneratorFactory.register(generatorName, generatorClass);
                    
                    logger.debug("Registered custom generator: {} from extension: {}", 
                               generatorName, extension.getName());
                } catch (Exception e) {
                    logger.error("Failed to register generator from extension: " + extension.getName(), e);
                }
            }
        }
    }

    /**
     * 卸载扩展
     * @param extensionName 扩展名称
     */
    public void unregisterExtension(String extensionName) {
        DataForgeExtension extension = extensions.remove(extensionName);
        if (extension != null) {
            // 移除该扩展提供的所有生成器
            List<Class<? extends DataGenerator>> generatorClasses = extension.getGeneratorClasses();
            if (generatorClasses != null) {
                for (Class<? extends DataGenerator> generatorClass : generatorClasses) {
                    try {
                        DataGenerator<?> generator = generatorClass.getDeclaredConstructor().newInstance();
                        String generatorName = generator.getName();
                        
                        customGenerators.remove(generatorName);
                        // 注意：GeneratorFactory 不支持取消注册，这里仅做记录
                        logger.debug("Unregistered custom generator: {} from extension: {}", 
                                   generatorName, extensionName);
                    } catch (Exception e) {
                        logger.error("Failed to unregister generator from extension: " + extensionName, e);
                    }
                }
            }
        }
    }

    /**
     * 获取所有已注册的扩展
     * @return 扩展列表
     */
    public List<DataForgeExtension> getExtensions() {
        return new ArrayList<>(extensions.values());
    }

    /**
     * 获取特定扩展
     * @param name 扩展名称
     * @return 扩展实例，如果不存在返回null
     */
    public DataForgeExtension getExtension(String name) {
        return extensions.get(name);
    }

    /**
     * 获取所有自定义生成器
     * @return 生成器映射
     */
    public Map<String, Class<? extends DataGenerator>> getCustomGenerators() {
        return Collections.unmodifiableMap(customGenerators);
    }

    /**
     * 检查扩展是否已注册
     * @param name 扩展名称
     * @return 是否已注册
     */
    public boolean isExtensionRegistered(String name) {
        return extensions.containsKey(name);
    }

    /**
     * 获取扩展信息摘要
     * @return 扩展信息列表
     */
    public List<ExtensionInfo> getExtensionInfo() {
        return extensions.values().stream()
                .map(ext -> new ExtensionInfo(
                        ext.getName(),
                        ext.getDescription(),
                        ext.getVersion(),
                        ext.getPriority(),
                        ext.getGeneratorClasses() != null ? ext.getGeneratorClasses().size() : 0
                ))
                .collect(Collectors.toList());
    }

    /**
     * 重新加载所有扩展
     */
    public synchronized void reloadExtensions() {
        logger.info("Reloading extensions...");
        
        // 清除现有扩展
        extensions.clear();
        customGenerators.clear();
        
        // 重新初始化
        initialized = false;
        initialize();
    }

    /**
     * 扩展信息类
     */
    public static class ExtensionInfo {
        private final String name;
        private final String description;
        private final String version;
        private final int priority;
        private final int generatorCount;

        public ExtensionInfo(String name, String description, String version, int priority, int generatorCount) {
            this.name = name;
            this.description = description;
            this.version = version;
            this.priority = priority;
            this.generatorCount = generatorCount;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getVersion() { return version; }
        public int getPriority() { return priority; }
        public int getGeneratorCount() { return generatorCount; }

        @Override
        public String toString() {
            return String.format("Extension{name='%s', version='%s', priority=%d, generators=%d}",
                               name, version, priority, generatorCount);
        }
    }
}
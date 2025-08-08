# DataForge 开发指南

## 项目架构

### 核心模块结构
```
dataforge-core/
├── src/main/java/com/dataforge/
│   ├── core/                      # 核心接口和基础类
│   │   ├── DataGenerator.java     # 基础生成器接口
│   │   ├── EnhancedDataGenerator.java  # 增强生成器接口
│   │   ├── GenerationContext.java # 生成上下文
│   │   ├── GeneratorFactory.java  # 生成器工厂
│   │   ├── GeneratorCacheManager.java  # 缓存管理器
│   │   ├── PerformanceMonitor.java     # 性能监控器
│   │   └── *DataGenerator.java    # 包装器生成器
│   ├── generators/                # 生成器实现
│   │   ├── basic/                 # 基础数据生成器
│   │   ├── identifiers/          # 身份标识生成器
│   │   ├── network/              # 网络通信生成器
│   │   ├── numeric/              # 数值类型生成器
│   │   ├── temporal/             # 时间日期生成器
│   │   ├── structured/           # 结构化数据生成器
│   │   ├── enums/                # 枚举类型生成器
│   │   ├── location/             # 地理位置生成器
│   │   ├── security/             # 安全测试生成器
│   │   ├── text/                 # 文本生成器
│   │   ├── special/              # 特殊场景生成器
│   │   ├── media/                # 媒体文件生成器
│   │   ├── business/             # 业务数据生成器
│   │   └── advanced/             # 高级功能生成器
│   └── test/                     # 测试和演示
└── src/test/java/com/dataforge/test/
    ├── P2FeatureDemo.java        # P2功能演示
    └── *Test.java                # 各种测试用例
```

## 设计模式

### 1. 工厂模式 (Factory Pattern)
```java
// 生成器工厂统一管理所有生成器的创建
public class GeneratorFactory {
    private static final Map<String, Class<? extends DataGenerator>> GENERATORS = new HashMap<>();
    
    public static DataGenerator<?> createGenerator(String name) {
        // 支持缓存、监控等增强功能
    }
}
```

### 2. 装饰者模式 (Decorator Pattern)
```java
// 通过包装器为生成器添加额外功能
public class MonitoredDataGenerator<T> implements DataGenerator<T> {
    private final DataGenerator<T> baseGenerator;
    private final String generatorName;
    // 添加性能监控功能
}

public class CachedDataGenerator<T> implements DataGenerator<T> {
    private final String generatorName;
    private final GeneratorCacheManager cacheManager;
    // 添加缓存功能
}
```

### 3. 模板方法模式 (Template Method Pattern)
```java
// EnhancedDataGenerator提供默认实现，子类可重写特定方法
public interface EnhancedDataGenerator<T> extends DataGenerator<T> {
    // 模板方法：批量生成
    default List<T> generateBatch(GenerationContext context, int count) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(generate(context)); // 调用具体实现
        }
        return result;
    }
}
```

### 4. 策略模式 (Strategy Pattern)
```java
// 统计分布生成器使用不同的分布策略
public class StatisticalDistributionGenerator implements EnhancedDataGenerator<Double> {
    @Override
    public Double generate(GenerationContext context) {
        String distributionType = context.getParameter("distribution", "NORMAL");
        switch (distributionType) {
            case "NORMAL": return generateNormal(...);
            case "UNIFORM": return generateUniform(...);
            case "EXPONENTIAL": return generateExponential(...);
            // 更多分布策略
        }
    }
}
```

### 5. 建造者模式 (Builder Pattern)
```java
// 缓存配置使用建造者模式
public static class CacheConfiguration {
    public static CacheConfiguration highPerformance() {
        return new CacheConfiguration()
            .maxSize(10000)
            .ttl(Duration.ofMinutes(30))
            .enableStatistics(true);
    }
}
```

## 扩展开发

### 1. 创建自定义生成器

#### 基础生成器
```java
public class CustomGenerator implements DataGenerator<String> {
    @Override
    public String generate(GenerationContext context) {
        String prefix = context.getParameter("prefix", "CUSTOM");
        return prefix + "-" + UUID.randomUUID().toString();
    }
    
    @Override
    public String getName() {
        return "custom_generator";
    }
}
```

#### 增强生成器
```java
public class CustomEnhancedGenerator implements EnhancedDataGenerator<String> {
    @Override
    public String generate(GenerationContext context) {
        return generateCustomData(context);
    }
    
    @Override
    public List<String> generateBatch(GenerationContext context, int count) {
        // 可以重写以提供更高效的批量生成实现
        return IntStream.range(0, count)
            .mapToObj(i -> generate(context))
            .collect(Collectors.toList());
    }
    
    @Override
    public ValidationResult validate(String data) {
        // 自定义数据验证逻辑
        boolean isValid = data != null && data.startsWith("CUSTOM");
        return new ValidationResult(isValid, 
            isValid ? "数据有效" : "数据格式错误", null);
    }
}
```

### 2. 注册自定义生成器

#### 直接注册
```java
// 在应用启动时注册
GeneratorFactory.register("my_custom", CustomGenerator.class);
```

#### SPI扩展（推荐）
1. 创建扩展类：
```java
public class MyDataForgeExtension implements DataForgeExtension {
    @Override
    public void registerGenerators(GeneratorFactory factory) {
        factory.register("my_custom", CustomGenerator.class);
        factory.register("my_enhanced", CustomEnhancedGenerator.class);
    }
}
```

2. 创建SPI配置文件：
`META-INF/services/com.dataforge.spi.DataForgeExtension`
```
com.example.MyDataForgeExtension
```

### 3. 高级功能集成

#### 添加缓存支持
```java
public class MyCachedGenerator extends CustomGenerator {
    private final GeneratorCacheManager cacheManager;
    
    public MyCachedGenerator() {
        this.cacheManager = GeneratorCacheManager.getInstance();
    }
    
    @Override
    public String generate(GenerationContext context) {
        String cacheKey = cacheManager.generateCacheKey(getName(), context);
        String cached = cacheManager.getCachedData(cacheKey);
        
        if (cached != null) {
            return cached;
        }
        
        String result = super.generate(context);
        cacheManager.cacheData(cacheKey, result);
        return result;
    }
}
```

#### 添加性能监控
```java
public class MyMonitoredGenerator extends CustomGenerator {
    private final PerformanceMonitor monitor;
    
    public MyMonitoredGenerator() {
        this.monitor = PerformanceMonitor.getInstance();
    }
    
    @Override
    public String generate(GenerationContext context) {
        PerformanceMonitor.OperationContext opContext = 
            monitor.startOperation(getName());
        
        try {
            String result = super.generate(context);
            monitor.endOperation(opContext, 1);
            return result;
        } catch (Exception e) {
            monitor.recordError(getName(), e);
            throw e;
        }
    }
}
```

## 性能优化

### 1. 批量生成优化
```java
// 使用BatchDataGenerator进行高效批量生成
public void efficientBatchGeneration() {
    EnhancedDataGenerator<String> baseGenerator = 
        GeneratorFactory.createGenerator("name");
    
    BatchDataGenerator<String> batchGen = new BatchDataGenerator<>(
        baseGenerator, 
        true,    // 启用并行处理
        1000,    // 批次大小
        new ProgressCallback() {
            @Override
            public void onProgress(int completed, int total) {
                System.out.printf("进度: %.1f%%\n", 
                    (double) completed / total * 100);
            }
        }
    );
    
    // 生成大量数据
    List<String> data = batchGen.generateBatch(context, 100000);
    batchGen.shutdown(); // 确保资源清理
}
```

### 2. 缓存配置优化
```java
// 配置高性能缓存
public void configureCaching() {
    GeneratorCacheManager cacheManager = GeneratorCacheManager.getInstance();
    
    GeneratorCacheManager.CacheConfiguration config = 
        GeneratorCacheManager.CacheConfiguration.builder()
            .maxSize(50000)                    // 最大缓存条目
            .ttl(Duration.ofHours(1))         // 缓存过期时间
            .enableStatistics(true)           // 启用统计
            .cleanupInterval(Duration.ofMinutes(10)) // 清理间隔
            .build();
    
    cacheManager.configure(config);
}
```

### 3. 性能监控设置
```java
// 启用性能监控
public void enablePerformanceMonitoring() {
    PerformanceMonitor monitor = PerformanceMonitor.getInstance();
    monitor.setEnabled(true);
    
    // 定期获取性能报告
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(() -> {
        PerformanceMonitor.OverallPerformanceReport report = 
            monitor.getOverallReport();
        
        System.out.println("性能报告: " + report);
        
        // 获取性能排行榜
        List<PerformanceMonitor.GeneratorRanking> rankings = 
            monitor.getPerformanceRanking();
        rankings.stream()
            .limit(5)
            .forEach(ranking -> System.out.println("排行: " + ranking));
            
    }, 0, 30, TimeUnit.SECONDS);
}
```

## 测试最佳实践

### 1. 单元测试
```java
@Test
public void testCustomGenerator() {
    CustomGenerator generator = new CustomGenerator();
    GenerationContext context = new GenerationContext();
    context.setParameter("prefix", "TEST");
    
    String result = generator.generate(context);
    
    assertNotNull(result);
    assertTrue(result.startsWith("TEST-"));
    assertTrue(result.contains("-"));
}
```

### 2. 批量测试
```java
@Test
public void testBatchGeneration() {
    EnhancedDataGenerator<String> generator = new CustomEnhancedGenerator();
    GenerationContext context = new GenerationContext();
    
    List<String> batch = generator.generateBatch(context, 1000);
    
    assertEquals(1000, batch.size());
    
    // 验证唯一性
    Set<String> uniqueItems = new HashSet<>(batch);
    assertTrue(uniqueItems.size() > 900); // 允许少量重复
}
```

### 3. 性能测试
```java
@Test
public void testPerformance() {
    EnhancedDataGenerator<String> generator = new CustomEnhancedGenerator();
    GenerationContext context = new GenerationContext();
    
    long startTime = System.currentTimeMillis();
    List<String> data = generator.generateBatch(context, 10000);
    long endTime = System.currentTimeMillis();
    
    long duration = endTime - startTime;
    double rate = (double) data.size() / duration * 1000; // items/sec
    
    System.out.printf("生成率: %.2f items/sec\n", rate);
    assertTrue("生成速度应该大于1000 items/sec", rate > 1000);
}
```

## 部署指南

### 1. Maven依赖
```xml
<dependencies>
    <dependency>
        <groupId>com.dataforge</groupId>
        <artifactId>dataforge-core</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### 2. 运行时配置
```java
// 应用启动时配置
public class DataForgeConfig {
    @PostConstruct
    public void init() {
        // 配置缓存
        configureCaching();
        
        // 启用性能监控
        enablePerformanceMonitoring();
        
        // 注册自定义生成器
        registerCustomGenerators();
    }
}
```

### 3. 资源管理
```java
// 应用关闭时清理资源
@PreDestroy
public void cleanup() {
    // 关闭批量生成器
    if (batchGenerator != null) {
        batchGenerator.shutdown();
    }
    
    // 清理缓存
    GeneratorCacheManager.getInstance().clearAll();
    
    // 关闭性能监控
    PerformanceMonitor.getInstance().shutdown();
}
```

## 故障排除

### 常见问题

#### 1. 内存溢出
**原因**: 一次性生成过多数据  
**解决**: 使用BatchDataGenerator分批处理
```java
// 错误方式
List<String> data = generator.generateBatch(context, 1000000);

// 正确方式
BatchDataGenerator<String> batchGen = new BatchDataGenerator<>(generator);
List<String> data = batchGen.generateBatch(context, 1000000);
```

#### 2. 性能问题
**原因**: 没有启用缓存或并行处理  
**解决**: 配置缓存和并行处理
```java
// 启用缓存
DataGenerator<?> cachedGen = GeneratorFactory.createCachedGenerator("name", context);

// 启用并行处理
BatchDataGenerator<String> parallelGen = new BatchDataGenerator<>(
    baseGenerator, true, 1000, null);
```

#### 3. 生成器未找到
**原因**: 生成器名称错误或未注册  
**解决**: 检查名称和注册状态
```java
// 检查是否已注册
boolean registered = GeneratorFactory.isRegistered("custom_generator");
if (!registered) {
    GeneratorFactory.register("custom_generator", CustomGenerator.class);
}
```

### 调试技巧

#### 1. 启用详细日志
```java
// 使用性能监控获取详细信息
PerformanceMonitor monitor = PerformanceMonitor.getInstance();
monitor.setEnabled(true);

// 查看生成器统计
Map<String, Integer> stats = GeneratorFactory.getGeneratorStats();
System.out.println("生成器统计: " + stats);
```

#### 2. 缓存分析
```java
// 获取缓存统计
GeneratorCacheManager.CacheStatistics stats = 
    GeneratorCacheManager.getInstance().getStatistics();
    
System.out.println("缓存命中率: " + stats.getHitRate());
System.out.println("缓存大小: " + stats.getCurrentSize());
```

## 贡献指南

### 代码规范
- 使用Java 8+特性
- 遵循Google Java Style Guide
- 所有public方法需要JavaDoc
- 单元测试覆盖率 > 80%

### 提交流程
1. Fork项目
2. 创建特性分支
3. 编写代码和测试
4. 提交Pull Request
5. 代码审查和合并

### 新功能开发
1. 在GitHub Issues中讨论新功能
2. 设计API和实现方案
3. 编写代码和文档
4. 添加测试用例
5. 更新CHANGELOG.md
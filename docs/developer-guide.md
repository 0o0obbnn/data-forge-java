# DataForge 开发者指南

本指南面向希望扩展、定制或贡献 DataForge 框架的开发者。

## 📋 目录

1. [架构概述](#架构概述)
2. [核心组件](#核心组件)
3. [扩展开发](#扩展开发)
4. [性能优化](#性能优化)
5. [测试策略](#测试策略)
6. [部署指南](#部署指南)
7. [最佳实践](#最佳实践)

## 🏗️ 架构概述

DataForge 采用模块化架构设计，主要包含以下核心组件：

```
dataforge-core/
├── src/main/java/com/dataforge/
│   ├── core/              # 核心接口和基础类
│   ├── generators/        # 数据生成器实现
│   │   ├── base/         # 基础类型生成器
│   │   ├── media/        # 媒体文件生成器
│   │   └── security/     # 安全测试生成器
│   ├── config/           # 配置管理
│   ├── spi/              # 扩展机制
│   └── utils/            # 工具类
├── src/main/resources/   # 资源配置
│   └── META-INF/services/# SPI配置
└── pom.xml              # Maven配置
```

### 核心接口

#### DataGenerator<T>
数据生成的核心接口，所有生成器必须实现：

```java
public interface DataGenerator<T> {
    T generate(GenerationContext context);
    String getName();
    List<String> getSupportedParameters();
}
```

#### GenerationContext
提供生成上下文和参数：

```java
public class GenerationContext {
    private final Map<String, Object> parameters;
    private final Random random;
    
    public Object getParameter(String key, Object defaultValue);
    public void setParameter(String key, Object value);
}
```

## 🔧 核心组件详解

### 1. 生成器工厂 (GeneratorFactory)

负责创建和管理所有数据生成器：

```java
public class GeneratorFactory {
    public DataGenerator<T> createGenerator(String type);
    public void registerGenerator(String name, DataGenerator<?> generator);
    public Set<String> getAvailableGenerators();
}
```

### 2. 配置管理 (ConfigurationManager)

YAML/JSON 配置文件的加载和管理：

```java
public class ConfigurationManager {
    public Configuration loadConfiguration(String filePath);
    public void saveConfiguration(Configuration config, String filePath);
    public boolean validateConfiguration(String filePath);
}
```

### 3. 内存优化 (MemoryOptimizedGenerator)

流式数据生成，避免内存溢出：

```java
public class MemoryOptimizedGenerator {
    public <T> void generateStreaming(
        DataGenerator<T> generator,
        GenerationContext context,
        long totalCount,
        Consumer<T> consumer
    );
}
```

### 4. 并发处理 (ConcurrentDataGenerator)

多线程并行数据生成：

```java
public class ConcurrentDataGenerator {
    public <T> List<T> generate(
        DataGenerator<T> generator,
        GenerationContext context,
        int totalCount,
        int batchSize
    );
}
```

## 🚀 扩展开发

### 创建自定义生成器

#### 基础生成器实现

```java
package com.example.generators;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CustomUUIDGenerator implements DataGenerator<String> {
    
    @Override
    public String generate(GenerationContext context) {
        String prefix = (String) context.getParameter("prefix", "ID");
        String suffix = (String) context.getParameter("suffix", "");
        
        String uuid = UUID.randomUUID().toString();
        
        // 支持短UUID格式
        boolean shortFormat = (Boolean) context.getParameter("short", false);
        if (shortFormat) {
            uuid = uuid.substring(0, 8);
        }
        
        return prefix + uuid + suffix;
    }
    
    @Override
    public String getName() {
        return "custom_uuid";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("prefix", "suffix", "short");
    }
}
```

#### 复杂对象生成器

```java
public class UserObjectGenerator implements DataGenerator<Map<String, Object>> {
    
    private final DataGenerator<String> nameGen;
    private final DataGenerator<Integer> ageGen;
    private final DataGenerator<String> emailGen;
    
    public UserObjectGenerator() {
        this.nameGen = new NameGenerator();
        this.ageGen = new AgeGenerator();
        this.emailGen = new EmailGenerator();
    }
    
    @Override
    public Map<String, Object> generate(GenerationContext context) {
        Map<String, Object> user = new HashMap<>();
        
        user.put("name", nameGen.generate(context));
        user.put("age", ageGen.generate(context));
        user.put("email", emailGen.generate(context));
        user.put("createdAt", new Date());
        
        return user;
    }
    
    @Override
    public String getName() {
        return "user_object";
    }
}
```

### SPI 扩展机制

#### 创建扩展类

```java
package com.example.extensions;

import com.dataforge.spi.DataForgeExtension;
import com.dataforge.core.GeneratorFactory;
import com.example.generators.CustomUUIDGenerator;

public class MyDataForgeExtension implements DataForgeExtension {
    
    @Override
    public void registerGenerators(GeneratorFactory factory) {
        // 注册自定义生成器
        factory.registerGenerator("custom_uuid", new CustomUUIDGenerator());
        factory.registerGenerator("user_object", new UserObjectGenerator());
        
        // 注册多个生成器
        registerSecurityGenerators(factory);
        registerBusinessGenerators(factory);
    }
    
    private void registerSecurityGenerators(GeneratorFactory factory) {
        factory.registerGenerator("jwt_token", new JWTGenerator());
        factory.registerGenerator("oauth_code", new OAuthCodeGenerator());
    }
    
    private void registerBusinessGenerators(GeneratorFactory factory) {
        factory.registerGenerator("order_id", new OrderIdGenerator());
        factory.registerGenerator("invoice_number", new InvoiceGenerator());
    }
}
```

#### 配置 SPI

创建文件：`src/main/resources/META-INF/services/com.dataforge.spi.DataForgeExtension`

```
com.example.extensions.MyDataForgeExtension
```

### 媒体文件生成器开发

#### 图片文件头生成器

```java
public class ImageFileHeaderGenerator implements DataGenerator<byte[]> {
    
    private static final Map<String, byte[]> SIGNATURES = Map.of(
        "PNG", new byte[]{(byte)0x89, 'P', 'N', 'G'},
        "JPEG", new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF},
        "GIF", new byte[]{'G', 'I', 'F', '8'}
    );
    
    @Override
    public byte[] generate(GenerationContext context) {
        String format = (String) context.getParameter("format", "PNG");
        return SIGNATURES.getOrDefault(format.toUpperCase(), SIGNATURES.get("PNG"));
    }
    
    @Override
    public String getName() {
        return "image_header";
    }
}
```

#### 文件扩展名生成器

```java
public class FileExtensionGenerator implements DataGenerator<String> {
    
    private static final Map<String, List<String>> EXTENSIONS = Map.of(
        "image", List.of("jpg", "png", "gif", "bmp", "webp"),
        "document", List.of("pdf", "doc", "docx", "txt"),
        "executable", List.of("exe", "msi", "deb", "rpm")
    );
    
    @Override
    public String generate(GenerationContext context) {
        String category = (String) context.getParameter("category", "image");
        boolean includeDangerous = (Boolean) context.getParameter("dangerous", false);
        
        List<String> extensions = new ArrayList<>(EXTENSIONS.get(category));
        
        if (includeDangerous) {
            extensions.addAll(List.of("exe", "bat", "cmd", "scr"));
        }
        
        return extensions.get(random.nextInt(extensions.size()));
    }
}
```

## ⚡ 性能优化

### 内存优化策略

#### 1. 对象池化

```java
public class GeneratorPool {
    private final ObjectPool<StringBuilder> stringBuilderPool;
    
    public GeneratorPool() {
        this.stringBuilderPool = new GenericObjectPool<>(
            new BasePooledObjectFactory<StringBuilder>() {
                @Override
                public StringBuilder create() {
                    return new StringBuilder(256);
                }
            }
        );
    }
    
    public String generateWithPool() {
        StringBuilder sb = stringBuilderPool.borrowObject();
        try {
            // 使用StringBuilder
            return sb.toString();
        } finally {
            sb.setLength(0);
            stringBuilderPool.returnObject(sb);
        }
    }
}
```

#### 2. 缓存机制

```java
public class CachedGenerator implements DataGenerator<String> {
    private final LoadingCache<String, String> cache;
    
    public CachedGenerator() {
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    return generateInternal(key);
                }
            });
    }
    
    @Override
    public String generate(GenerationContext context) {
        String cacheKey = buildCacheKey(context);
        return cache.getUnchecked(cacheKey);
    }
}
```

### 并发优化

#### 1. 线程安全的随机数生成

```java
public class ThreadSafeGenerator implements DataGenerator<Integer> {
    private static final ThreadLocal<Random> THREAD_LOCAL_RANDOM = 
        ThreadLocal.withInitial(Random::new);
    
    @Override
    public Integer generate(GenerationContext context) {
        Random random = THREAD_LOCAL_RANDOM.get();
        int min = (Integer) context.getParameter("min", 0);
        int max = (Integer) context.getParameter("max", 100);
        return random.nextInt(max - min + 1) + min;
    }
}
```

#### 2. 并行流处理

```java
public class ParallelGenerator {
    public List<String> generateParallel(int count) {
        return IntStream.range(0, count)
            .parallel()
            .mapToObj(i -> generator.generate(new GenerationContext()))
            .collect(Collectors.toList());
    }
}
```

## 🧪 测试策略

### 单元测试

#### 生成器测试

```java
@Test
public void testCustomUUIDGenerator() {
    CustomUUIDGenerator generator = new CustomUUIDGenerator();
    GenerationContext context = new GenerationContext();
    
    // 测试基本功能
    String result = generator.generate(context);
    assertNotNull(result);
    assertTrue(result.startsWith("ID"));
    assertEquals(36, result.length()); // UUID标准长度
    
    // 测试参数支持
    context.setParameter("prefix", "TEST");
    context.setParameter("short", true);
    
    String shortResult = generator.generate(context);
    assertTrue(shortResult.startsWith("TEST"));
    assertEquals(12, shortResult.length()); // TEST + 8位短UUID
}
```

#### 性能测试

```java
@Test
public void testMemoryOptimizedGenerator() {
    MemoryOptimizedGenerator generator = new MemoryOptimizedGenerator();
    DataGenerator<String> stringGen = factory.createGenerator("random_string");
    
    AtomicInteger count = new AtomicInteger(0);
    
    long startTime = System.currentTimeMillis();
    generator.generateStreaming(
        stringGen, 
        new GenerationContext(), 
        100000, 
        data -> count.incrementAndGet()
    );
    long duration = System.currentTimeMillis() - startTime;
    
    assertEquals(100000, count.get());
    assertTrue(duration < 5000); // 5秒内完成
}
```

### 集成测试

```java
@Test
public void testSPIExtension() {
    // 测试SPI扩展是否正常工作
    GeneratorFactory factory = new GeneratorFactory();
    
    // 检查自定义生成器是否已注册
    assertTrue(factory.isAvailable("custom_uuid"));
    assertTrue(factory.isAvailable("user_object"));
    
    // 测试生成器功能
    DataGenerator<String> generator = factory.createGenerator("custom_uuid");
    String result = generator.generate(new GenerationContext());
    assertNotNull(result);
}
```

## 🚀 部署指南

### Maven 构建

```xml
<!-- 发布到Maven中央仓库 -->
<distributionManagement>
    <repository>
        <id>ossrh</id>
        <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
    </repository>
</distributionManagement>

<build>
    <plugins>
        <plugin>
            <groupId>org.sonatype.plugins</groupId>
            <artifactId>nexus-staging-maven-plugin</artifactId>
            <version>1.6.8</version>
            <extensions>true</extensions>
            <configuration>
                <serverId>ossrh</serverId>
                <nexusUrl>https://oss.sonatype.org/</nexusUrl>
                <autoReleaseAfterClose>true</autoReleaseAfterClose>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Docker 部署

```dockerfile
FROM openjdk:11-jre-slim

COPY target/dataforge-core-*.jar /app/dataforge.jar
COPY config/ /app/config/

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "dataforge.jar"]
```

### Kubernetes 部署

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: dataforge
spec:
  replicas: 3
  selector:
    matchLabels:
      app: dataforge
  template:
    metadata:
      labels:
        app: dataforge
    spec:
      containers:
      - name: dataforge
        image: dataforge:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
```

## 🎯 最佳实践

### 1. 生成器设计原则

- **单一职责**: 每个生成器只负责一种数据类型
- **可配置**: 提供丰富的参数配置选项
- **高性能**: 避免不必要的对象创建
- **线程安全**: 确保生成器在多线程环境下安全

### 2. 错误处理

```java
@Override
public T generate(GenerationContext context) {
    try {
        // 参数验证
        validateParameters(context);
        
        // 生成逻辑
        return doGenerate(context);
        
    } catch (IllegalArgumentException e) {
        throw new GenerationException("Invalid parameters", e);
    } catch (Exception e) {
        throw new GenerationException("Generation failed", e);
    }
}

private void validateParameters(GenerationContext context) {
    Integer min = (Integer) context.getParameter("min");
    Integer max = (Integer) context.getParameter("max");
    
    if (min != null && max != null && min > max) {
        throw new IllegalArgumentException("min must be less than or equal to max");
    }
}
```

### 3. 性能监控

```java
public class MonitoredGenerator implements DataGenerator<String> {
    private final DataGenerator<String> delegate;
    private final MeterRegistry registry;
    
    public MonitoredGenerator(DataGenerator<String> delegate, MeterRegistry registry) {
        this.delegate = delegate;
        this.registry = registry;
    }
    
    @Override
    public String generate(GenerationContext context) {
        return registry.timer("generator.duration", "type", getName())
            .recordCallable(() -> delegate.generate(context));
    }
}
```

### 4. 文档和示例

为每个生成器提供：
- 详细的JavaDoc文档
- 使用示例代码
- 参数说明
- 性能特性说明

```java
/**
 * 生成符合特定格式的订单号
 * 
 * 示例:
 * {@code
 * GenerationContext context = new GenerationContext();
 * context.setParameter("prefix", "ORD");
 * context.setParameter("dateFormat", "yyyyMMdd");
 * 
 * OrderIdGenerator generator = new OrderIdGenerator();
 * String orderId = generator.generate(context);
 * // 结果: ORD202312150001
 * }
 * 
 * 参数:
 * - prefix: 订单号前缀 (默认: "ORD")
 * - dateFormat: 日期格式 (默认: "yyyyMMdd")
 * - sequenceLength: 序列号长度 (默认: 4)
 * 
 * 性能: 每次生成约0.1ms，无状态，线程安全
 */
public class OrderIdGenerator implements DataGenerator<String> {
    // 实现代码
}
```

## 🔗 相关资源

- [项目GitHub](https://github.com/dataforge/dataforge)
- [用户手册](../README.md)
- [API文档](api-docs.md)
- [性能调优指南](performance-guide.md)
- [示例代码](examples/)

## 📞 支持

如有问题或建议，请通过以下方式联系：
- GitHub Issues
- 邮件: support@dataforge.com
- 社区论坛: https://forum.dataforge.com
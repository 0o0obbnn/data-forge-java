# DataForge - 智能数据生成框架

DataForge 是一个高性能、可扩展的Java数据生成框架，专为测试、演示和开发环境提供灵活的模拟数据生成能力。现已包含74+种数据生成器，支持P0-P2优先级的全面功能实现。

## 🚀 特性

### 核心功能
- **多样化数据生成**: 74+种内置生成器，涵盖基础、业务、安全、网络、媒体等各个领域
- **高性能优化**: 内存优化、并发处理、流式生成、智能缓存机制
- **配置驱动**: YAML/JSON 配置文件支持，模板化配置
- **扩展机制**: Java SPI 扩展支持自定义生成器，增强接口设计
- **企业级**: 支持大规模数据生成场景，批量处理和性能监控

### 支持的生成器类型

#### P0优先级 - 核心生成器 (已实现)
- **基础数据**: 姓名、手机、邮箱、年龄、性别、密码、账户名
- **身份标识**: 身份证号、银行卡号、统一社会信用代码、UUID
- **网络通信**: IP地址、MAC地址、域名、端口号、HTTP头、URL
- **数值类型**: 整数、小数、货币、科学计数法、高精度小数、百分比
- **时间日期**: 日期、时间、时间戳、Cron表达式
- **结构化数据**: JSON对象、XML文档、YAML数据
- **枚举类型**: HTTP状态码、业务状态码
- **地理位置**: 地址生成器、增强地址生成器、车牌号

#### P1优先级 - 增强生成器 (已实现)
- **金融增强**: 增强银行卡生成器 (支持中国各大银行、国际卡组织、Luhn验证)
- **网络设备**: 网络设备生成器 (MAC地址、设备ID、主机名、IEEE OUI支持)
- **统计分析**: 统计分布生成器 (17种分布类型、样本统计、分析功能)

#### P2优先级 - 高级功能 (已实现)
- **扩展接口**: EnhancedDataGenerator接口，支持批量生成、唯一集合、相似数据
- **高级生成器**: 数据关联生成器、相似数据生成器、批量数据生成器
- **性能优化**: 缓存管理器、性能监控器、生成器包装器
- **并行处理**: 多线程批量生成、进度回调、性能报告

### 性能特性
- **并发生成**: 多线程并行处理，支持自定义线程池
- **内存优化**: 流式处理避免内存溢出，智能批次调整
- **智能缓存**: 基于LRU的缓存机制，支持过期清理和统计
- **实时监控**: 生成性能监控、缓存命中率统计、操作时间分析
- **批量处理**: 大规模数据生成，分页处理，进度回调

## 📋 快速开始

### 1. 添加依赖

#### Maven
```xml
<dependency>
    <groupId>com.dataforge</groupId>
    <artifactId>dataforge-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Gradle
```gradle
implementation 'com.dataforge:dataforge-core:1.0.0'
```

### 2. 基本使用

#### 简单生成示例
```java
import com.dataforge.core.*;

// 基础数据生成
DataGenerator<String> nameGen = GeneratorFactory.createGenerator("name");
String name = nameGen.generate(new GenerationContext());

// 网络数据生成
DataGenerator<String> ipGen = GeneratorFactory.createGenerator("ip");
String ipAddress = ipGen.generate(new GenerationContext());

// 身份标识生成
DataGenerator<String> idGen = GeneratorFactory.createGenerator("idcard");
String idCard = idGen.generate(new GenerationContext());
```

#### 增强生成器使用
```java
// 增强银行卡生成器
EnhancedBankCardGenerator bankCardGen = new EnhancedBankCardGenerator();
GenerationContext context = new GenerationContext();
context.setParameter("issuer", "ICBC");
context.setParameter("cardType", "CREDIT_CARD");
String bankCard = bankCardGen.generate(context);

// 统计分布生成器
StatisticalDistributionGenerator statGen = new StatisticalDistributionGenerator();
context.setParameter("distribution", "NORMAL");
context.setParameter("mean", 100.0);
context.setParameter("stdDev", 15.0);
Double normalValue = statGen.generate(context);
```

#### 带参数生成
```java
GenerationContext context = new GenerationContext();
context.setParameter("min", 1);
context.setParameter("max", 100);

DataGenerator<Integer> intGen = factory.createGenerator("random_int");
int randomInt = intGen.generate(context);
```

### 3. 配置文件使用

#### 创建配置文件 (config.yml)
```yaml
generation:
  defaultCount: 1000
  batchSize: 100
  threadCount: 4

generators:
  user:
    type: "json"
    template:
      name: "random_name"
      email: "random_email"
      age: "random_int"
    parameters:
      name:
        minLength: 3
        maxLength: 10
      age:
        min: 18
        max: 65
```

#### 使用配置
```java
ConfigurationManager configManager = new ConfigurationManager("config.yml");
Configuration config = configManager.loadConfiguration();

// 使用配置生成数据
GenerationTask task = config.getTasks().get("user");
List<String> users = task.execute();
```

## 🔧 高级功能

### P2批量数据生成
```java
// 创建批量生成器
EnhancedDataGenerator<String> baseGenerator = GeneratorFactory.createGenerator("name");
BatchDataGenerator<String> batchGen = new BatchDataGenerator<>(baseGenerator, true, 1000, 
    new BatchDataGenerator.ProgressCallback() {
        @Override
        public void onProgress(int completed, int total) {
            System.out.printf("进度: %d/%d\n", completed, total);
        }
        
        @Override
        public void onCompleted(int total, long timeMillis) {
            System.out.printf("完成: %d 项，耗时 %d ms\n", total, timeMillis);
        }
    });

// 生成大量数据
List<String> data = batchGen.generateBatch(new GenerationContext(), 10000);
batchGen.shutdown();
```

### 相似数据生成
```java
// 创建相似数据生成器
SimilarDataGenerator<String> similarGen = SimilarDataGenerator.createStringSimilarGenerator(
    baseGenerator, 0.8 // 80%相似度
);

// 基于种子数据生成相似数据
String seedData = "DataForge2024";
List<String> similarData = similarGen.generateSimilar(context, seedData, 5);

// 计算相似度
for (String data : similarData) {
    double similarity = similarGen.calculateSimilarity(seedData, data);
    System.out.printf("%s -> 相似度: %.2f%%\n", data, similarity * 100);
}
```

### 数据关联生成
```java
// 创建数据关联生成器
DataRelationshipGenerator relGen = new DataRelationshipGenerator();

// 添加字段生成器
relGen.addFieldGenerator("age", new MockAgeGenerator());
relGen.addFieldGenerator("gender", new MockGenderGenerator());

// 添加关联规则
relGen.addRelationshipRule(new DataRelationshipGenerator.ConditionalRule(
    "gender", "female", "title", "Ms."));
relGen.addRelationshipRule(new DataRelationshipGenerator.RangeRule(
    "age", 18, 65, "category", "adult"));

// 生成关联数据
Map<String, Object> userData = relGen.generate(new GenerationContext());
```

### 性能监控和缓存
```java
// 启用性能监控
PerformanceMonitor monitor = PerformanceMonitor.getInstance();
monitor.setEnabled(true);

// 配置缓存
GeneratorCacheManager cacheManager = GeneratorCacheManager.getInstance();
GeneratorCacheManager.CacheConfiguration config = 
    GeneratorCacheManager.CacheConfiguration.highPerformance();
cacheManager.configure(config);

// 生成监控报告
PerformanceMonitor.OverallPerformanceReport report = monitor.getOverallReport();
GeneratorCacheManager.CacheStatistics cacheStats = cacheManager.getStatistics();
```

### 自定义生成器
```java
public class CustomGenerator implements DataGenerator<String> {
    @Override
    public String generate(GenerationContext context) {
        String prefix = (String) context.getParameter("prefix", "CUSTOM");
        return prefix + "-" + UUID.randomUUID().toString();
    }

    @Override
    public String getName() {
        return "custom_uuid";
    }
}

// 注册自定义生成器
factory.registerGenerator("custom_uuid", new CustomGenerator());
```

### SPI 扩展
创建扩展类：
```java
public class MyExtension implements DataForgeExtension {
    @Override
    public void registerGenerators(GeneratorFactory factory) {
        factory.registerGenerator("my_type", new MyDataGenerator());
    }
}
```

在 `META-INF/services/com.dataforge.spi.DataForgeExtension` 中添加：
```
com.example.MyExtension
```

## 📊 性能调优

### 内存配置
```yaml
performance:
  memoryThreshold: 67108864  # 64MB
  batchSize: 1000
  enableGC: true
```

### 并发配置
```yaml
performance:
  threadCount: 8
  queueSize: 10000
  timeout: 30000
```

## 🎯 使用场景

### 测试数据生成
```java
// 生成测试用户数据
DataGenerator<String> userGen = factory.createGenerator("json_user");
List<String> testUsers = IntStream.range(0, 1000)
    .mapToObj(i -> userGen.generate(new GenerationContext()))
    .collect(Collectors.toList());
```

### 性能测试
```java
// 大规模数据生成测试
long startTime = System.currentTimeMillis();
MemoryOptimizedGenerator generator = new MemoryOptimizedGenerator();
generator.generateStreaming(
    factory.createGenerator("large_dataset"),
    new GenerationContext(),
    10000000,
    data -> {
        // 处理生成的数据
    }
);
long duration = System.currentTimeMillis() - startTime;
System.out.println("Generated 10M records in " + duration + "ms");
```

### 演示数据
```java
// 生成演示用的产品数据
DataGenerator<String> productGen = factory.createGenerator("product");
List<String> products = IntStream.range(0, 100)
    .mapToObj(i -> {
        GenerationContext ctx = new GenerationContext();
        ctx.setParameter("category", "electronics");
        return productGen.generate(ctx);
    })
    .collect(Collectors.toList());
```

## 📚 生成器参考

### 内置生成器列表 (74+种)

#### 基础数据生成器
| 生成器名称 | 描述 | 参数示例 |
|-----------|------|----------|
| `name` | 中文姓名 | gender, region |
| `phone` | 手机号码 | carrier, region |
| `email` | 电子邮箱 | domain, usernameLength |
| `age` | 年龄 | min, max |
| `gender` | 性别 | culture |
| `password` | 密码 | length, complexity |
| `account` | 账户名 | minLength, maxLength |

#### 身份标识生成器
| 生成器名称 | 描述 | 参数示例 |
|-----------|------|----------|
| `idcard` | 身份证号码 | region, age |
| `bankcard` | 银行卡号 | type, bank |
| `enhanced_bank_card` | 增强银行卡 | issuer, cardType, validate |
| `uscc` | 统一社会信用代码 | region, type |
| `uuid` | UUID | version (v1, v4) |
| `enhanced_uuid` | 增强UUID | namespace, format |
| `custom_id` | 自定义ID | pattern, prefix |
| `license_plate` | 车牌号 | region, type |

#### 网络通信生成器
| 生成器名称 | 描述 | 参数示例 |
|-----------|------|----------|
| `ip` | IP地址 | version (v4, v6), subnet |
| `mac` | MAC地址 | vendor, format |
| `domain` | 域名 | tld, subdomain |
| `port` | 端口号 | range, wellKnown |
| `header` | HTTP头 | type, custom |
| `url` | URL | protocol, domain, path |
| `network_device` | 网络设备 | vendor, deviceType |

#### 数值类型生成器
| 生成器名称 | 描述 | 参数示例 |
|-----------|------|----------|
| `integer` | 整数 | min, max |
| `decimal` | 小数 | min, max, precision |
| `currency` | 货币 | currency, format |
| `negative` | 负数 | range, type |
| `scientific` | 科学计数法 | exponentRange, mantissa |
| `high_precision_decimal` | 高精度小数 | precision, scale |
| `percentage_rate` | 百分比 | min, max, format |
| `measurement_unit` | 计量单位 | unit, range |
| `statistical_distribution` | 统计分布 | distribution, parameters |

#### 时间日期生成器
| 生成器名称 | 描述 | 参数示例 |
|-----------|------|----------|
| `date` | 日期 | startDate, endDate, format |
| `time` | 时间 | format, timezone |
| `timestamp` | 时间戳 | unit, range |
| `cron` | Cron表达式 | fields, complexity |

#### 结构化数据生成器
| 生成器名称 | 描述 | 参数示例 |
|-----------|------|----------|
| `json_object` | JSON对象 | template, depth, fields |
| `xml_document` | XML文档 | schema, elements, attributes |
| `yaml_data` | YAML数据 | structure, depth |

#### 枚举类型生成器
| 生成器名称 | 描述 | 支持的值 |
|-----------|------|----------|
| `http_status_code` | HTTP状态码 | 1xx-5xx状态码 |
| `business_status_code` | 业务状态码 | 成功、失败、处理中等 |

#### 地理位置生成器
| 生成器名称 | 描述 | 参数示例 |
|-----------|------|----------|
| `location` | 位置数据 | country, city, coordinates |
| `enhanced_location` | 增强位置 | precision, format |
| `address` | 地址 | region, detailed |
| `enhanced_address` | 增强地址 | components, validation |

#### 安全测试生成器
| 生成器名称 | 描述 | 攻击类型 |
|-----------|------|----------|
| `sqlinjection` | SQL注入载荷 | Union, Boolean, Time-based |
| `xss` | XSS攻击脚本 | Reflected, Stored, DOM |
| `commandinjection` | 命令注入 | Shell, PowerShell, Batch |
| `pathtraversal` | 路径遍历 | ../, ..\, URL编码 |
| `binary_base64` | Base64二进制 | 编码格式, 数据类型 |

#### 文本生成器
| 生成器名称 | 描述 | 特性 |
|-----------|------|------|
| `longtext` | 长文本 | 段落、章节、字数控制 |
| `richtext` | 富文本 | HTML、Markdown、格式化 |
| `unicode_boundary` | Unicode边界字符 | 边界测试、编码问题 |
| `multilingual_text` | 多语言文本 | 中英日韩、混合语言 |
| `special_char` | 特殊字符 | 符号、控制字符、表情 |

#### 特殊场景生成器
| 生成器名称 | 描述 | 用途 |
|-----------|------|------|
| `emptynull` | 空值/NULL | 边界测试 |
| `boundary` | 边界极值 | 极限测试 |
| `invalid_exception` | 异常数据 | 错误处理测试 |
| `customizable_business_id` | 可定制业务ID | 业务规则测试 |
| `duplicate_data` | 重复数据 | 去重测试 |
| `sorted_data` | 排序数据 | 排序算法测试 |
| `concurrent_contention_data` | 并发竞争数据 | 并发测试 |

#### 媒体文件生成器
| 生成器名称 | 描述 | 支持的格式 |
|-----------|------|------------|
| `image_header` | 图片文件头 | PNG, JPEG, GIF, BMP, TIFF, WEBP |
| `file_extension` | 文件扩展名 | 所有常见格式，危险扩展名 |
| `file_size` | 文件大小 | 单位支持 (B, KB, MB, GB, TB) |
| `image_dimensions` | 图片尺寸 | 支持自定义宽高比例 |
| `simulated_media_file` | 模拟媒体文件 | 完整文件数据生成 |

## 🔍 故障排除

### 常见问题

#### 内存溢出
```java
// 使用内存优化生成器
MemoryOptimizedGenerator generator = new MemoryOptimizedGenerator(32 * 1024 * 1024, 500);
generator.generateStreaming(...);
```

#### 生成速度慢
```java
// 使用并发生成
ConcurrentDataGenerator generator = new ConcurrentDataGenerator(8);
List<String> results = generator.generate(...);
generator.shutdown();
```

#### 配置加载失败
```java
// 检查配置文件格式
ConfigurationManager configManager = new ConfigurationManager();
configManager.validateConfiguration("config.yml");
```

## 📖 示例项目

### 运行P2功能演示
```bash
# 编译项目
mvn clean compile

# 运行P2高级功能演示
mvn exec:java -Dexec.mainClass="com.dataforge.test.P2FeatureDemo"
```

### 查看示例代码
查看 `src/test/java/com/dataforge/test/` 目录获取完整示例：
- `P2FeatureDemo.java` - P2高级功能演示（批量生成、相似数据、数据关联、性能监控、缓存）
- `BasicGeneratorTest.java` - 基础生成器测试
- `EnhancedGeneratorTest.java` - 增强生成器测试
- `StatisticalDistributionTest.java` - 统计分布测试

### 功能模块示例

#### 1. 批量数据生成示例
```java
// 参考 P2FeatureDemo.demonstrateBatchGeneration()
BatchDataGenerator<String> batchGen = new BatchDataGenerator<>(baseGenerator, true, 50, callback);
List<String> data = batchGen.generateBatch(context, 500);
PerformanceReport report = batchGen.generatePerformanceReport(context, new int[]{100, 500, 1000});
```

#### 2. 相似数据生成示例
```java
// 参考 P2FeatureDemo.demonstrateSimilarDataGeneration()
SimilarDataGenerator<String> similarGen = SimilarDataGenerator.createStringSimilarGenerator(baseGenerator, 0.8);
List<String> similarData = similarGen.generateSimilar(context, "DataForge2024", 5);
```

#### 3. 数据关联生成示例
```java
// 参考 P2FeatureDemo.demonstrateDataRelationship()
DataRelationshipGenerator generator = new DataRelationshipGenerator();
generator.addRelationshipRule(new ConditionalRule("gender", "female", "title", "Ms."));
Map<String, Object> userData = generator.generate(context);
```

#### 4. 统计分布生成示例
```java
// 正态分布
StatisticalDistributionGenerator statGen = new StatisticalDistributionGenerator();
context.setParameter("distribution", "NORMAL");
context.setParameter("mean", 100.0);
context.setParameter("stdDev", 15.0);
Double normalValue = statGen.generate(context);

// 获取分析报告
SampleStatistics stats = statGen.getLastSampleStatistics();
```

## 🤝 贡献

欢迎贡献！请查看 [开发者指南](docs/developer-guide.md) 了解如何参与项目开发。

## 📄 许可证

MIT License - 查看 [LICENSE](LICENSE) 文件了解详情。
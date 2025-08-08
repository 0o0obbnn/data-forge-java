# DataForge 配置模板使用指南

本目录包含了 DataForge 的各种配置模板，用于演示和快速启动不同场景的数据生成任务。

## 模板文件说明

### 1. `basic-user-data.yaml` - 基础用户数据
**用途**: 生成基本的用户信息，包括姓名、邮箱、电话、年龄等
**输出格式**: JSON
**数据量**: 100条记录
**使用场景**: 
- 用户注册测试
- 基础功能测试
- 数据库初始化

**使用方法**:
```bash
java -jar dataforge-cli.jar --config basic-user-data.yaml
```

### 2. `enterprise-data.json` - 企业数据
**用途**: 生成企业相关信息，包括公司名称、统一社会信用代码、联系方式等
**输出格式**: CSV
**数据量**: 50条记录
**使用场景**:
- 企业管理系统测试
- B2B应用测试
- 工商数据验证

**使用方法**:
```bash
java -jar dataforge-cli.jar --config enterprise-data.json
```

### 3. `security-testing.yaml` - 安全测试数据
**用途**: 生成各种安全测试载荷，包括SQL注入、XSS、路径穿越等
**输出格式**: JSON
**数据量**: 70条记录（多种类型）
**使用场景**:
- 安全渗透测试
- 防护系统验证
- 安全工具测试

**使用方法**:
```bash
java -jar dataforge-cli.jar --config security-testing.yaml
```

### 4. `multilingual-text.json` - 多语言文本数据
**用途**: 生成多语言和特殊字符数据，用于国际化测试
**输出格式**: JSON
**数据量**: 130条记录（多种类型）
**使用场景**:
- 国际化(i18n)测试
- 字符编码测试
- 多语言UI测试

**使用方法**:
```bash
java -jar dataforge-cli.jar --config multilingual-text.json
```

### 5. `comprehensive-demo.yaml` - 综合功能演示
**用途**: 展示DataForge所有核心功能的综合配置
**输出格式**: JSON
**数据量**: 1600+条记录（多种类型）
**使用场景**:
- 功能演示
- 性能测试
- 全面功能验证

**使用方法**:
```bash
java -jar dataforge-cli.jar --config comprehensive-demo.yaml
```

## 配置文件结构说明

### 基本结构
```yaml
name: "配置名称"
description: "配置描述"
outputFormat: "json|csv|xml"
outputFile: "输出文件路径"
prettyPrint: true

globalParameters:
  # 全局参数，会传递给所有任务
  locale: "zh_CN"
  seed: 12345

tasks:
  - name: "字段名称"
    type: "生成器类型"
    count: 数据量
    parameters:
      # 任务特定参数
    nestedTasks:
      # 嵌套任务，用于生成关联数据
```

### 支持的生成器类型
详见 `dataforge-cli.jar --list-generators` 命令输出。

主要类别包括：
- **基础信息类**: name, phone, email, age, gender 等
- **标识类**: uuid, idcard, bankcard, uscc 等
- **网络设备类**: ip, mac, domain, port, url 等
- **数值计量类**: integer, decimal, currency 等
- **时间日历类**: date, time, timestamp, cron 等
- **安全注入测试类**: sqlinjection, xss, commandinjection 等
- **文本多语言类**: longtext, multilingual_text, unicode_boundary 等
- **媒体文件类**: image_header, file_extension, file_size 等
- **特殊场景数据**: emptynull, boundary, invalid_exception 等
- **位置数据类**: location, enhanced_location, enhanced_address 等

### 参数配置
不同的生成器支持不同的参数：

#### 通用参数
- `locale`: 地区设置 (zh_CN, en_US等)
- `seed`: 随机种子，用于可重现的生成
- `count`: 生成数量

#### 生成器特定参数
每个生成器都有自己的特定参数，例如：
- `age`: min, max (年龄范围)
- `phone`: type (mobile|landline)
- `email`: domain (邮箱域名类型)
- `currency`: currency_code, min_value, max_value, precision

## 自定义配置

你可以基于这些模板创建自己的配置文件：

1. 复制一个最接近需求的模板
2. 修改任务配置和参数
3. 调整输出格式和文件路径
4. 运行并验证结果

## 配置验证

在运行前可以验证配置文件格式：
```bash
java -jar dataforge-cli.jar --validate-config your-config.yaml
```

## 批量执行

可以批量执行多个配置文件：
```bash
# 执行所有模板
for config in *.yaml *.json; do
    echo "执行配置: $config"
    java -jar dataforge-cli.jar --config "$config"
done
```

## 性能建议

1. **大数据量生成**: 使用 `--parallel` 参数启用并行处理
2. **内存优化**: 对于超大数据集，考虑分批生成
3. **输出优化**: CSV格式通常比JSON更节省空间和内存
4. **缓存复用**: 使用固定的seed值确保结果可重现

## 故障排除

1. **配置文件格式错误**: 检查YAML/JSON语法
2. **生成器不存在**: 使用 `--list-generators` 查看支持的生成器
3. **参数无效**: 查看具体生成器的参数文档
4. **输出文件权限**: 确保有写入目标文件的权限

更多详细信息请参考 DataForge 项目文档。
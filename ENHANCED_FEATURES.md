# DataForge 增强功能文档

## 概述

DataForge 现已完成高优先级功能开发，包括增强版地址、企业信息、UUID生成器，完整的JSON输出格式支持，以及基础数据验证功能。

## 🎯 已完成的高优先级功能

### ✅ 1. 高频使用的基础数据类型

#### 增强版地址生成器 (`enhanced_address`)
- **功能**: 生成包含省份、城市、区县、街道、门牌号、邮编、坐标的完整地址
- **格式支持**: COMPLETE（完整）、STRUCTURED（结构化）、SIMPLE（简单）
- **参数**:
  - `format`: 地址格式 (COMPLETE/STRUCTURED/SIMPLE)
  - `includePostcode`: 是否包含邮编 (true/false)
  - `includeCoordinates`: 是否包含经纬度坐标 (true/false)

**示例**:
```bash
java -jar dataforge-cli.jar address:enhanced_address 100 addresses.json
```

**输出示例**:
```json
{
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "street": "科技路",
  "streetNumber": "123",
  "building": "1栋2单元301室",
  "fullAddress": "广东省深圳市南山区科技路123号1栋2单元301室",
  "postcode": "518000",
  "latitude": 22.5431,
  "longitude": 114.0579
}
```

#### 企业信息生成器 (`company`)
- **功能**: 生成真实的企业名称，支持行业、地区、公司类型配置
- **唯一性**: 支持5000个不重复的公司名称
- **参数**:
  - `unique`: 是否保证唯一性 (true/false)
  - `prefixRegion`: 是否添加地区前缀 (true/false)
  - `industry`: 指定行业关键词
  - `type`: 指定公司类型

**示例**:
```bash
java -jar dataforge-cli.jar company_name:company 100 companies.json
```

#### 增强版UUID生成器 (`enhanced_uuid`)
- **功能**: 支持多种UUID版本和格式
- **版本支持**: V1-V5 (时间戳、随机、基于名字等)
- **格式支持**: 标准、大写、无连字符、带大括号、URN格式
- **验证**: 内置格式验证
- **参数**:
  - `version`: UUID版本 (V1_TIME_BASED, V2_DCE_SECURITY, V3_NAME_BASED_MD5, V4_RANDOM, V5_NAME_BASED_SHA1)
  - `format`: 输出格式 (STANDARD, UPPERCASE, WITHOUT_DASHES, BRACED, URN)
  - `includeValidation`: 是否包含验证信息
  - `includeTimestamp`: 是否包含时间戳

**示例**:
```bash
java -jar dataforge-cli.jar uuid:enhanced_uuid 10 uuids.json
```

### ✅ 2. 完整的JSON输出格式支持

#### JSON输出功能
- **格式**: 标准JSON数组格式
- **转义**: 自动处理特殊字符转义
- **结构**: 每行一个JSON对象
- **文件支持**: 直接输出到文件

**使用示例**:
```bash
# 基本使用
java -jar dataforge-cli.jar name:name,email:email,phone:phone 100 users.json

# 复杂数据
java -jar dataforge-cli.jar address:enhanced_address,company:company,uuid:enhanced_uuid 50 complex.json
```

### ✅ 3. 基础数据验证功能

#### 数据验证器 (`DataValidator`)
- **邮箱格式**: RFC标准邮箱验证
- **手机号**: 中国手机号格式验证
- **身份证号**: 18位身份证号格式和校验码验证
- **URL**: 标准URL格式验证
- **IP地址**: IPv4地址格式验证
- **UUID**: 标准UUID格式验证
- **统一社会信用代码**: 18位信用代码验证
- **银行卡号**: 银行卡号格式和Luhn算法验证
- **企业名称**: 基本格式验证
- **地址**: 基本格式验证

**验证功能**:
```java
// 单个验证
boolean isValid = DataValidator.isValidEmail("test@example.com");

// 批量验证
Map<String, Object> data = new HashMap<>();
data.put("email", "test@example.com");
data.put("phone", "13800138000");
ValidationResult result = DataValidator.validateDataIntegrity(data);
```

### ✅ 4. 配置文件支持

#### YAML/JSON配置支持
- **格式**: 支持YAML和JSON配置文件
- **功能**: 定义复杂的数据生成任务
- **参数**: 全局参数、任务参数、嵌套任务
- **输出**: 指定输出格式和文件

**配置示例** (config.yaml):
```yaml
name: 企业信息生成任务
description: 生成包含地址、企业信息、UUID的完整数据
outputFormat: json
outputFile: enterprise_data.json
prettyPrint: true
globalParameters:
  locale: zh_CN
tasks:
  - name: company_info
    type: company
    count: 100
    parameters:
      unique: true
      prefixRegion: true
  - name: company_address
    type: enhanced_address
    count: 100
    parameters:
      format: COMPLETE
      includePostcode: true
  - name: company_uuid
    type: enhanced_uuid
    count: 100
    parameters:
      version: V4_RANDOM
      format: STANDARD
```

### ✅ 5. 扩展常用标识类数据生成器

#### 自定义ID生成器 (`custom_id`)
- **ID类型**: 订单号、流水号、批次号、发票号、追踪号、会员号
- **日期格式**: 支持多种日期格式嵌入
- **序列号**: 可配置的序列号长度
- **随机后缀**: 可配置的随机字符后缀
- **格式**: 完全可自定义的格式

**参数**:
- `idType`: ID类型
- `prefix`: 前缀
- `dateFormat`: 日期格式
- `sequenceLength`: 序列号长度
- `randomSuffix`: 是否包含随机后缀
- `suffixLength`: 后缀长度
- `separator`: 分隔符

**示例**:
```bash
# 订单号格式: ORD-20240804-000001-ABCD
java -jar dataforge-cli.jar order_id:custom_id 1000 orders.json

# 会员号格式: MBR202408040001
java -jar dataforge-cli.jar member_id:custom_id 500 members.json
```

## 🚀 使用示例

### 命令行使用

#### 基础示例
```bash
# 生成100个企业信息
java -jar dataforge-cli.jar company_name:company,uscc:uscc,email:email,phone:phone 100 companies.json

# 生成50个详细地址
java -jar dataforge-cli.jar address:enhanced_address 50 addresses.json

# 生成1000个订单号
java -jar dataforge-cli.jar order_id:custom_id 1000 orders.json

# 生成复杂用户信息
java -jar dataforge-cli.jar \
  name:name,email:email,phone:phone,company:company,address:enhanced_address,uuid:enhanced_uuid \
  200 \
  users.json
```

#### 配置文件使用
```bash
# 使用配置文件生成数据
java -cp dataforge-cli.jar com.dataforge.config.ConfigurationManager config.yaml

# 验证配置文件
java -cp dataforge-core.jar com.dataforge.config.ConfigurationManager config.yaml --validate
```

### 编程使用

#### Java API 示例
```java
// 创建生成器
DataGenerator<Map<String, Object>> addressGen = 
    new EnhancedAddressGenerator(EnhancedAddressGenerator.AddressFormat.COMPLETE, true, true);

// 生成数据
GenerationContext context = new GenerationContext(100);
Map<String, Object> address = addressGen.generate(context);

// 验证数据
boolean isValid = DataValidator.isValidAddress(address.get("fullAddress").toString());

// 批量生成
List<Map<String, Object>> addresses = new ArrayList<>();
for (int i = 0; i < 100; i++) {
    addresses.add(addressGen.generate(context));
}
```

### 数据验证示例

#### 实时验证
```java
// 验证单个字段
String email = "test@example.com";
if (DataValidator.isValidEmail(email)) {
    System.out.println("邮箱格式正确");
}

// 验证完整数据
Map<String, Object> userData = new HashMap<>();
userData.put("email", "test@example.com");
userData.put("phone", "13800138000");
userData.put("idcard", "110105199003078888");

DataValidator.ValidationResult result = DataValidator.validateDataIntegrity(userData);
if (result.isValid()) {
    System.out.println("数据验证通过");
} else {
    System.out.println("验证失败: " + result.getErrorMessage());
}
```

## 📊 支持的生成器列表

### 基础数据类型
- `name`: 姓名生成器
- `phone`: 手机号生成器
- `email`: 邮箱生成器
- `company`: 公司名称生成器
- `enhanced_address`: 增强版地址生成器

### 标识类数据
- `uuid`: UUID生成器
- `enhanced_uuid`: 增强版UUID生成器
- `custom_id`: 自定义ID生成器
- `idcard`: 身份证号生成器
- `uscc`: 统一社会信用代码生成器
- `bankcard`: 银行卡号生成器

### 其他类型
- 网络相关: `ip`, `mac`, `domain`, `url`, `port`
- 数值相关: `integer`, `decimal`, `currency`, `percentage`
- 时间相关: `date`, `time`, `timestamp`, `cron`
- 文本相关: `longtext`, `richtext`, `multilingual_text`
- 媒体相关: `image_header`, `file_extension`, `file_size`

## 🔧 配置示例

### 完整配置文件
```yaml
# 企业数据生成配置
name: 企业数据生成任务
description: 生成包含完整信息的企业测试数据
outputFormat: json
outputFile: enterprise_data.json
prettyPrint: true

# 全局参数
globalParameters:
  locale: zh_CN
  timezone: Asia/Shanghai

# 数据生成任务
tasks:
  - name: enterprise
    type: company
    count: 500
    parameters:
      unique: true
      prefixRegion: true
      
  - name: address
    type: enhanced_address
    count: 500
    parameters:
      format: COMPLETE
      includePostcode: true
      includeCoordinates: true
      
  - name: business_id
    type: custom_id
    count: 500
    parameters:
      idType: ORDER_NUMBER
      prefix: ENT
      dateFormat: YYYYMMDD
      sequenceLength: 6
      randomSuffix: true
      suffixLength: 4
      separator: "-"
```

## 🎉 总结

DataForge 现已完成所有高优先级功能开发，提供了：

1. **完整的地址生成功能** - 支持省份、城市、区县、街道、邮编、坐标
2. **企业信息生成** - 支持5000个不重复的真实企业名称
3. **增强版UUID** - 支持多种版本和格式的UUID生成
4. **自定义ID** - 支持订单号、流水号等各种业务ID格式
5. **完整JSON输出** - 支持标准JSON格式输出
6. **数据验证** - 提供全面的数据格式验证功能
7. **配置文件支持** - 支持YAML/JSON复杂任务配置

这些功能可以覆盖99%的常见数据生成需求，为测试、开发、演示提供高质量的数据支持。
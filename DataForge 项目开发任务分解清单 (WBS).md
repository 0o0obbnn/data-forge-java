##  **DataForge 项目开发任务分解清单 (WBS)**

### 阶段一：核心框架与基础数据生成 (MVP)

**目标：** 搭建 `DataForge` 的核心骨架，实现命令行接口（CLI）的基础功能，并完成最常用、最核心的基础信息类数据生成器。此阶段完成后，将能通过CLI生成指定数量的基础用户数据。

- **模块与功能点细化：**
    
    - **项目初始化与构建系统：**
        
        - 创建 Maven 项目骨架，定义 `pom.xml`。
            
        - 引入核心依赖：`spring-boot-starter-parent` (使用Spring Boot), `commons-cli` (CLI解析), `slf4j-api` 和 `logback-classic` (日志)。
            
        - 定义基础包结构（例如 `com.dataforge.generator`, `com.dataforge.cli`, `com.dataforge.output`）。
            
        - 配置单元测试框架（JUnit 5）。
            
    - **CLI 解析模块：**
        
        - 使用 `org.apache.commons.cli` 定义命令行选项 (`Options`)。
            
        - 实现 `CommandLineParser` 解析用户输入。
            
        - 集成 `HelpFormatter` 提供 `--help` 和 `--version` 功能。
            
        - 设计初步的命令结构，例如：`java -jar dataforge.jar --type name --count 10 --output-format csv --output-file names.csv`。
            
        - 实现基础的参数校验（例如 `--count` 必须是正整数）。
            
    - **数据输出模块（基础）：**
        
        - **`StdoutOutputWriter`：** 实现将生成的数据直接打印到控制台。
            
        - **`CsvFileOutputWriter`：**
            
            - 创建文件并处理文件写入流。
                
            - 实现 CSV 格式化逻辑，包含字段分隔符（逗号）、字符串引号处理、换行符处理。
                
            - 支持写入标题行。
                
    - **数据类型定义与生成引擎（核心）：**
        
        - 定义泛型接口 `DataGenerator<T>`，包含 `T generate(GenerationContext context)` 方法。
            
        - 设计 `GenerationContext` 类，封装生成数量、通用参数（例如 `locale`）、随机数生成器实例等。
            
        - 实现 `GeneratorFactory` 类，负责根据数据类型名称（例如 "name", "phone"）返回对应的 `DataGenerator` 实例，使用 `Map` 存储映射关系。
            
    - **基础信息类数据生成器：**
        
        - **`NameGenerator` (姓名)：**
            
            - 加载内置的中文姓氏库（含频率）和常用名字库，以及英文名/姓库。
                
            - 实现基于频率加权的随机选择逻辑。
                
            - 支持 `--name.gender` 参数，根据性别倾向性调整名字组合。
                
        - **`PhoneNumberGenerator` (手机号码)：**
            
            - 定义中国大陆主要运营商号段（例如 13x, 15x, 18x）。
                
            - 随机选择前缀，并生成剩余数字以确保 11 位长度。
                
            - 实现 `--phone.valid` 参数，用于生成符合或不符合基本号段规则的号码。
                
        - **`EmailGenerator` (邮箱)：**
            
            - 生成随机的字母数字组合作为用户名。
                
            - 从内置常用公共域名库（例如 `qq.com`, `163.com`, `gmail.com`）中随机选择域名。
                
            - 实现基础的邮箱格式校验。
                
        - **`AgeGenerator` (年龄)：**
            
            - 生成指定 `--age.min` 和 `--age.max` 范围内的随机整数。
                
        - **`GenderGenerator` (性别)：**
            
            - 实现随机选择“男”、“女”、“其他”的逻辑。
                
            - 支持 `--gender.male_ratio` 等参数调整生成比例。
                
        - **`PasswordGenerator` (密码)：**
            
            - 根据 `--password.length` 范围和 `--password.complexity` (LOW/MEDIUM/HIGH) 规则生成随机字符串。
                
            - 包含数字、大小写字母、特殊字符的组合逻辑。
                
        - **`AccountNameGenerator` (账号名)：**
            
            - 根据 `--accountname.length` 范围和 `--accountname.chars` 规则生成随机字母数字组合。
                
            - 实现 `--accountname.unique` 参数，确保在批次生成中的唯一性。
                
- **优先级：** 高
    
- **相对工作量：** 中等
    

### 阶段二：关键标识与结构化数据

**目标：** 扩展 `DataForge` 的核心能力，实现业务中常用的标识类数据生成，并支持灵活的结构化数据（JSON/CSV）生成，为更复杂的测试场景打下基础。

- **模块与功能点细化：**
    
    - **标识类数据生成器：**
        
        - **`UUIDGenerator` (全局唯一 ID)：**
            
            - 实现 UUID 版本4 (`UUID.randomUUID().toString()`)。
                
            - 集成或实现 ULID (Universally Unique Lexicographically Sortable Identifier) 的生成逻辑，必要时引入第三方库。
                
        - **`IdCardNumberGenerator` (身份证号码)：**
            
            - 加载最新的中国行政区划代码库（精确到区县）。
                
            - 实现出生日期生成逻辑，精确处理闰年和月份天数。
                
            - 实现顺序码生成，确保倒数第二位与性别逻辑一致。
                
            - **核心：实现中国居民身份证号码的 GB 11643-1999 校验位算法。**
                
            - 支持 `--idcard.region`、`--idcard.birth_date_range`、`--idcard.gender` 等参数。
                
        - **`BankCardNumberGenerator` (银行卡号/信用卡号)：**
            
            - 维护一个常用 BIN 码库（银行识别码），包含卡组织和卡号长度信息。
                
            - 根据 BIN 码生成卡号前缀，并随机生成中间数字。
                
            - **核心：实现 Luhn 算法（Mod 10 算法）计算校验位，确保生成的卡号有效。**
                
            - 支持 `--bankcard.type`、`--bankcard.issuer`、`--bankcard.bank` 等参数。
                
        - **`USCCGenerator` (统一社会信用代码)：**
            
            - 实现登记管理部门码、机构类别码、行政区划码和主体标识码的生成逻辑。
                
            - **核心：实现 GB32100-2015 标准中定义的复杂校验算法。**
                
            - 支持 `--uscc.region` 参数。
                
    - **结构化数据生成模块：**
        
        - **`JsonGenerator` (JSON 对象/数组)：**
            
            - 引入 JSON 库（例如 Jackson 或 Gson）。
                
            - 设计简化的 JSON Schema 或模板配置，允许用户定义字段名、数据类型（string, int, boolean, array, object）、嵌套层级。
                
            - 实现递归生成逻辑，填充不同数据类型的随机值。
                
            - 支持 `--json.pretty_print`。
                
        - **`CsvGenerator` (CSV/TSV 增强)：**
            
            - 重构 `CsvFileOutputWriter`，使其能接收列定义（字段名和对应的数据生成器）。
                
            - 实现 `--csv.delimiter`、`--csv.include_header`、`--csv.quote_mode` 等参数。
                
            - 确保字段值中的分隔符和引号能正确转义。
                
    - **数据校验模块（集成）：**
        
        - 将身份证、银行卡、USCC 等复杂校验逻辑作为独立方法或内部逻辑集成到各自的 `DataGenerator` 实现中。
            
        - 在生成过程中，当 `--valid false` 时，故意生成校验失败的数据。
            
        - 在输出时提供校验结果的反馈（例如，在日志中标记哪些数据无效）。
            
    - **配置管理增强：**
        
        - 引入 YAML/JSON 解析库（例如 SnakeYAML 或 Jackson Databind）。
            
        - 设计配置类，通过文件加载复杂的数据生成任务，例如定义一个包含多个字段（姓名、身份证、邮箱）的记录模板。
            
        - 实现配置属性到 `DataGenerator` 参数的映射。
            
- **优先级：** 高
    
- **相对工作量：** 较大
    

### 阶段三：高级文本与网络/设备数据

**目标：** 丰富文本生成类型，支持富文本和多语言场景，并涵盖网络通信和设备相关的关键数据，满足更广泛的测试需求。

- **模块与功能点细化：**
    
    - **文本/多语言类数据生成器：**
        
        - **`LongTextGenerator` (长文本)：**
            
            - 准备中文常用词汇库和英文 Lorem Ipsum 片段。
                
            - 实现按字符数或段落数生成文本，随机插入标点和换行符。
                
        - **`RichTextGenerator` (富文本/HTML、Markdown 片段)：**
            
            - 实现生成随机 HTML 标签（`<b>`, `<i>`, `<a>`, `<img>`）或 Markdown 元素（标题、列表、链接）。
                
            - **实现简单的 XSS payload 生成（例如 `<script>alert(1)</script>`），并支持编码选项。**
                
        - **`UnicodeBoundaryCharGenerator` (Unicode 边界字符)：**
            
            - 收集并定义常见的 Unicode 边界字符集，包含高低位代理对、控制字符（`\u0000`）、Emoji、组合字符、零宽字符。
                
            - 实现将这些字符随机插入到生成的文本中。
                
        - **`MultilingualTextGenerator` (多语言示例)：**
            
            - 准备不同语言（例如阿拉伯文、日文、韩文、希腊文）的文本片段或字符范围。
                
            - 实现单语生成和多语言混排生成，支持混排比例配置。
                
        - **`SpecialCharGenerator` (特殊字符)：**
            
            - 定义常见的特殊字符集（例如 `!@#$%^&*`）。
                
            - 实现将这些字符随机插入到生成的字符串中。
                
    - **网络/设备类数据生成器：**
        
        - **`IpAddressGenerator` (IP 地址)：**
            
            - 实现 IPv4 地址生成（`xxx.xxx.xxx.xxx`），处理 0-255 范围。
                
            - 定义并实现公网 IP 和私网 IP 范围的生成逻辑。
                
            - 实现 IPv6 地址生成（`xxxx:xxxx:...`），考虑缩写形式。
                
            - 支持 `--ip.cidr` 参数，在指定网段内生成 IP。
                
        - **`MacAddressGenerator` (MAC 地址)：**
            
            - 生成 6 组两位十六进制数。
                
            - 支持冒号或连字符分隔符。
                
            - 研究并集成常用 OUI（Organizationally Unique Identifier）前缀，模拟特定厂商设备。
                
        - **`UrlGenerator` (URL/URI)：**
            
            - 组合协议（HTTP/HTTPS）、随机域名、随机路径、随机查询参数和可选锚点。
                
            - 实现 URL 编码处理。
                
        - **`DomainNameGenerator` (域名)：**
            
            - 生成随机的二级域名部分。
                
            - 从内置常用顶级域名（TLD）列表（例如 `.com`, `.org`, `.cn`）中随机选择。
                
        - **`HttpHeaderGenerator` (HTTP 头)：**
            
            - 定义常见的 HTTP 请求头名称（例如 `User-Agent`, `Cookie`, `Referer`, `Authorization`）。
                
            - 为每个头生成随机值或从预设列表中选择（例如 User-Agent 列表）。
                
        - **`SessionIdTokenGenerator` (Session ID/Token)：**
            
            - 生成指定长度的随机字母数字字符串。
                
            - **实现模拟 JWT (JSON Web Token) 结构（`header.payload.signature`），其中 signature 部分为占位符。**
                
        - **`PortNumberGenerator` (端口号)：**
            
            - 生成 0-65535 范围内的随机整数。
                
            - 支持 `--port.common` 参数，优先生成常用服务端口（例如 80, 443, 22）。
                
- **优先级：** 中等偏高
    
- **相对工作量：** 较大
    

### 阶段四：数值、时间与媒体模拟数据

**目标：** 完善数值和时间相关的数据生成能力，并提供媒体文件（非实际内容）的模拟生成，覆盖更多业务场景。

- **模块与功能点细化：**
    
    - **数值/计量类数据生成器：**
        
        - **`DecimalGenerator` (小数)：** 生成指定范围和精度的小数，支持正负数。
            
        - **`IntegerGenerator` (整数)：** 生成指定范围的整数，支持正负数。
            
        - **`NegativeNumberGenerator` (负数)：** 专门生成指定范围的负整数或负小数。
            
        - **`CurrencyGenerator` (币种)：** 加载 ISO 4217 货币代码列表，随机选择或指定常用货币。
            
        - **`ScientificNotationGenerator` (科学计数法)：** 生成符合 `X.YZe+N` 格式的字符串，控制尾数和指数范围及精度。
            
        - **`HighPrecisionDecimalGenerator` (高精度小数)：** 生成具有指定小数位数（例如 4-8 位）的浮点数，使用 `BigDecimal`。
            
        - **`PercentageRateGenerator` (百分比、利率、汇率)：** 生成浮点数，可配置是否包含 `%` 符号，控制数值范围和精度。
            
        - **`MeasurementUnitCombinationGenerator` (度量单位组合)：**
            
            - 定义常见的度量类型（长度、重量、体积等）及其单位列表。
                
            - 组合数值（由 `DecimalGenerator` 或 `IntegerGenerator` 生成）和随机选择的单位。
                
    - **时间/日历类数据生成器：**
        
        - **`DateGenerator` (日期)：**
            
            - 生成指定 `--date.start` 和 `--date.end` 范围内的随机日期。
                
            - 支持多种日期格式化字符串（例如 `yyyy-MM-dd` 等）。
                
            - **实现日期有效性校验（例如 2 月 30 日为非法日期）。**
                
        - **`TimeGenerator` (时间)：**
            
            - 生成指定 `--time.start` 和 `--time.end` 范围内的随机时间。
                
            - 支持多种时间格式化字符串，包含毫秒。
                
            - **实现时间有效性校验（例如 25:00:00 为非法时间）。**
                
        - **`TimestampGenerator` (时间戳)：**
            
            - 生成 Unix 秒或毫秒时间戳。
                
            - 根据指定日期时间范围计算对应的时间戳。
                
        - **`CronExpressionGenerator` (Cron 表达式)：**
            
            - 实现 Cron 表达式的各个字段（秒、分、时、日、月、周）的随机生成逻辑。
                
            - 支持 `*`, `?`, `-`, `,`, `/` 等特殊字符的组合。
                
            - **实现 Cron 表达式的有效性校验（例如逻辑冲突）。**
                
    - **媒体/二进制及文件相关类（模拟）：**
        
        - **`ImageFileHeaderGenerator` (图像文件头)：**
            
            - 定义常见图像格式（PNG, JPEG, GIF）的“魔数”字节序列。
                
            - 生成这些字节序列，并支持生成篡改过的文件头用于异常测试。
                
        - **`FileExtensionGenerator` (文件扩展名)：**
            
            - 维护常用文件扩展名库（例如 `.jpg`, `.pdf`）。
                
            - 支持生成合法扩展名或非法/危险扩展名（例如 `.exe`, `.php`）。
                
        - **`FileSizeGenerator` (文件大小)：**
            
            - 生成指定范围内的文件大小数值，支持 KB, MB, GB 单位。
                
        - **`ImageDimensionsGenerator` (图片尺寸)：**
            
            - 生成宽度和高度像素值，支持指定范围。
                
            - 实现可选的宽高比保持逻辑。
                
        - **`SimulatedMediaFileGenerator` (模拟的音频/视频/压缩包/PDF/Office文档片段)：**
            
            - 对于每种文件类型（音频、视频、压缩包、PDF、Office文档），仅生成其文件头和少量随机二进制数据。
                
            - 不生成实际可播放/可打开的内容，仅模拟其文件结构特征。
                
            - 支持模拟损坏的文件或包含特定“特征”（例如宏脚本签名）。
                
- **优先级：** 中等
    
- **相对工作量：** 较大
    

### 阶段五：安全注入与高级特殊场景

**目标：** 实现安全测试相关的攻击 payload 生成，并完善所有特殊场景数据类型，提供更全面的测试数据覆盖能力。

- **模块与功能点细化：**
    
    - **安全/注入测试数据生成器：**
        
        - **`SqlInjectionPayloadGenerator` (SQL 注入 payload)：**
            
            - 定义常见的 SQL 注入类型（基于错误、联合查询、盲注、时间盲注、堆叠查询）。
                
            - 为不同数据库类型（MySQL, PostgreSQL, SQL Server, Oracle）生成特定语法的 payload。
                
            - 支持 URL 编码或 Base64 编码。
                
        - **`XssAttackScriptGenerator` (XSS 攻击脚本)：**
            
            - 定义常见的反射型、存储型、DOM 型 XSS payload。
                
            - 支持不同 HTML 标签和事件处理函数。
                
            - 实现 URL 编码、HTML 实体编码、Base64 编码等绕过方式。
                
        - **`CommandInjectionGenerator` (命令注入)：**
            
            - 定义常见的命令注入 payload（例如 `; rm -rf /`, `| ls`）。
                
            - 支持 Linux/Unix 和 Windows 操作系统特定的命令分隔符和命令。
                
        - **`PathTraversalGenerator` (路径穿越)：**
            
            - 定义常见的路径穿越 payload（例如 `../../etc/passwd`）。
                
            - 支持 Windows (`..\..\`) 和 Unix (`../../`) 风格。
                
            - 支持 URL 编码。
                
        - **`BinaryBase64DataGenerator` (二进制/Base64 编码数据)：**
            
            - 生成指定长度的随机二进制数据。
                
            - 实现 Base64 编码功能。
                
            - 支持生成包含非法 Base64 字符的编码数据。
                
    - **特殊场景数据（通用）生成器：**
        
        - **`EmptyNullValueGenerator` (空值/Null值)：**
            
            - 为任何数据类型生成空字符串、`null` 值、空数组 `[]`、空对象 `{}`。
                
            - 支持配置生成频率和指定字段。
                
        - **`BoundaryExtremeValueGenerator` (边界值/极端值)：**
            
            - 针对数值型：生成最小值、最大值、`min+1`、`max-1`、零、负零。
                
            - 针对字符串型：空字符串、单字符、最大长度、`max_length-1`、`max_length+1`。
                
            - 针对日期时间型：闰年边界、世纪末、极端过去/未来日期。
                
            - 支持配置生成频率和指定字段。
                
        - **`InvalidExceptionDataGenerator` (非法/异常数据)：**
            
            - 根据数据类型生成格式错误（例如手机号位数不对）、超出范围（例如年龄为负）、超长（超过字段限制）、包含非法字符、类型不匹配的数据。
                
            - 支持配置生成频率和指定字段。
                
        - **`CustomizableBusinessIdGenerator` (可自定义长度格式的业务编号)：**
            
            - 生成由字母、数字、特殊字符组成的自定义格式编号。
                
            - 支持固定长度或长度范围、字符集、前缀、后缀、分隔符及其间隔。
                
            - 支持生成批次内的唯一性。
                
        - **`DuplicateDataGenerator` (重复数据)：**
            
            - 在生成批次中，故意生成指定字段或整条记录的重复数据。
                
            - 支持配置重复频率或重复次数。
                
        - **`SortedDataGenerator` (排序数据)：**
            
            - 在生成数据后，对指定字段进行升序、降序或逆序排序。
                
            - 支持数值、日期时间、字符串类型排序。
                
        - **`ConcurrentContentionDataGenerator` (并发/竞争数据)：**
            
            - 模拟多线程并发生成数据，引入微小的时间戳抖动或 ID 偏移量。
                
            - 主要通过控制生成器实例的调用上下文来模拟并发场景。
                
    - **扩展机制模块（完善）：**
        
        - 实现 Java 的 ServiceLoader (SPI) 机制，允许用户通过 JAR 包形式动态加载自定义的 `DataGenerator` 实现。
            
        - 提供清晰的接口和抽象类，指导用户如何编写和注册自定义生成器。
            
    - **配置管理模块（完善）：**
        
        - 支持更复杂的配置模板，允许定义多条记录、嵌套结构和字段间的关联规则。
            
        - 实现常用配置的保存和加载功能，方便用户复用。
            
- **优先级：** 中等
    
- **相对工作量：** 较大
    

### 阶段六：性能优化、测试与文档

**目标：** 对整个工具进行性能调优，确保在大规模数据生成场景下的高效运行，并产出高质量的用户和开发者文档，提升工具的可用性和可维护性。

- **模块与功能点细化：**
    
    - **性能优化：**
        
        - **内存管理：** 针对大数据量生成，优化数据结构和流式写入，避免内存溢出。
            
        - **并发生成：** 利用 Java 并发工具（`ExecutorService`, `Future`）优化多线程生成效率。
            
        - **算法效率：** 评估并优化各 `DataGenerator` 内部的生成算法，特别是涉及大量数据加载或复杂计算的（例如身份证、银行卡校验）。
            
        - **资源加载：** 优化外部数据（例如姓氏库、行政区划代码）的加载策略，集成缓存机制。
            
    - **测试用例完善：**
        
        - **单元测试 (JUnit 5/Mockito)：** 为所有 `DataGenerator` 实现编写全面的单元测试，覆盖正常生成、边界条件和异常情况。
            
        - **集成测试：** 编写测试用例验证 CLI 解析、数据生成与输出的端到端流程。
            
        - **性能测试：** 使用 JMH (Java Microbenchmark Harness) 对关键生成器进行性能基准测试。
            
        - **回归测试：** 确保新功能不影响现有功能。
            
    - **文档编写：**
        
        - **用户手册：** 详细的 CLI 命令参考、参数说明、配置文件示例、常见用例场景、故障排除指南。
            
        - **开发者指南：** 深入解释架构设计、模块职责、如何添加新的数据类型生成器、扩展机制的使用。
            
        - **API 文档 (Javadoc)：** 为所有公共类、接口和方法编写规范的 Javadoc 注释。
            
        - **README.md：** 更新项目简介、快速启动指南、核心特性。
            
- **优先级：** 高（贯穿整个项目生命周期，但在此阶段进行集中收尾和提升）
    
- **相对工作量：** 中等
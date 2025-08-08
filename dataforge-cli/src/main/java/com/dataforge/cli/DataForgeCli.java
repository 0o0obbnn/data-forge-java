package com.dataforge.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Help.Visibility;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.core.GeneratorFactory;
import com.dataforge.output.CsvOutputWriter;
import com.dataforge.output.JsonOutputWriter;
import com.dataforge.output.XmlOutputWriter;
import com.dataforge.output.SqlInsertOutputWriter;
import com.dataforge.output.DatabaseOutputWriter;
import com.dataforge.config.ConfigurationManager;
import com.dataforge.config.ConfigurationManager.DataForgeConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * DataForge CLI 应用主入口
 * 使用 PicoCLI 框架实现完整的命令行参数解析和配置文件支持
 */
@Command(
    name = "dataforge",
    description = "DataForge - 强大灵活的测试数据生成工具",
    version = "DataForge 1.0.0-SNAPSHOT",
    mixinStandardHelpOptions = true,
    headerHeading = "DataForge - 测试数据生成工具%n%n",
    descriptionHeading = "%n描述:%n",
    parameterListHeading = "%n参数:%n",
    optionListHeading = "%n选项:%n",
    commandListHeading = "%n命令:%n",
    footerHeading = "%n示例:%n",
    footer = {
        "  生成基础数据:",
        "    dataforge -f name,email,age -c 100 -o output.csv",
        "",
        "  使用配置文件:",
        "    dataforge --config config.yaml",
        "", 
        "  指定字段类型:",
        "    dataforge -f 'name:name,contact:email,years:age' -c 50",
        "",
        "  生成JSON格式:",
        "    dataforge -f name,phone,idcard -c 1000 -o data.json",
        "",
        "  生成XML格式:",
        "    dataforge -f name,email,age -c 100 -o users.xml",
        "",
        "  生成SQL INSERT语句:",
        "    dataforge -f name,email,phone -c 1000 -o insert.sql --table users --db-type mysql",
        "",
        "  直接写入数据库:",
        "    dataforge -f name,email,age -c 100 --jdbc-url jdbc:mysql://localhost:3306/test --db-username user --db-password pass --table users"
    }
)
public class DataForgeCli implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(DataForgeCli.class);

    @Parameters(
        index = "0..*",
        arity = "0..*",
        description = "字段定义 (格式: field1:type1,field2:type2 或简写 field1,field2)",
        hidden = true
    )
    private List<String> positionalArgs;

    @Option(
        names = {"-f", "--fields"},
        description = "字段定义 (格式: field1:type1,field2:type2 或 field1,field2)",
        required = false
    )
    private String fields;

    @Option(
        names = {"-c", "--count"},
        description = "生成记录数量 (默认: 10)",
        defaultValue = "10"
    )
    private int count;

    @Option(
        names = {"-o", "--output"},
        description = "输出文件路径 (支持 .csv, .json 格式，未指定则输出到控制台)"
    )
    private File outputFile;

    @Option(
        names = {"--config"},
        description = "配置文件路径 (支持 .yaml, .json 格式)"
    )
    private File configFile;

    @Option(
        names = {"--format"},
        description = "输出格式 (csv, json, xml, sql)，会根据输出文件扩展名自动判断"
    )
    private String outputFormat;

    @Option(
        names = {"--list-generators"},
        description = "显示所有可用的数据生成器"
    )
    private boolean listGenerators;

    @Option(
        names = {"--validate"},
        description = "是否启用数据校验 (默认: true)",
        defaultValue = "true"
    )
    private boolean enableValidation;

    @Option(
        names = {"-v", "--verbose"},
        description = "启用详细输出模式"
    )
    private boolean verbose;

    @Option(
        names = {"--parallel"},
        description = "启用并行生成模式 (默认: false)",
        defaultValue = "false"
    )
    private boolean parallel;

    @Option(
        names = {"--seed"},
        description = "随机种子 (用于可重现的数据生成)"
    )
    private Long seed;

    @Option(
        names = {"--table"},
        description = "SQL输出的目标表名 (用于SQL格式输出)"
    )
    private String tableName;

    @Option(
        names = {"--batch-size"},
        description = "批处理大小 (默认: 100，用于SQL和数据库输出)",
        defaultValue = "100"
    )
    private int batchSize;

    @Option(
        names = {"--db-type"},
        description = "数据库类型 (mysql, postgresql, oracle, sqlserver)，用于SQL格式输出",
        defaultValue = "mysql"
    )
    private String databaseType;

    @Option(
        names = {"--jdbc-url"},
        description = "JDBC连接URL (用于数据库直接写入)"
    )
    private String jdbcUrl;

    @Option(
        names = {"--db-username"},
        description = "数据库用户名 (用于数据库直接写入)"
    )
    private String dbUsername;

    @Option(
        names = {"--db-password"},
        description = "数据库密码 (用于数据库直接写入)"
    )
    private String dbPassword;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new DataForgeCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        try {
            // 设置日志级别
            if (verbose) {
                System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
            }

            logger.info("DataForge CLI 启动，版本: 1.0.0-SNAPSHOT");

            // 显示生成器列表
            if (listGenerators) {
                printAvailableGenerators();
                return 0;
            }

            // 解析配置
            DataForgeConfig config = parseConfiguration();
            if (config == null) {
                System.err.println("错误: 缺少必要的配置信息");
                return 1;
            }

            // 执行数据生成
            return executeGeneration(config);

        } catch (Exception e) {
            logger.error("执行失败: {}", e.getMessage(), e);
            System.err.println("错误: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    private void printAvailableGenerators() {
        System.out.println("\n=== DataForge 可用数据生成器 ===");
        System.out.println();
        System.out.println("基础信息类 (Basic Information):");
        System.out.println("  name           - 姓名生成器");
        System.out.println("  phone          - 手机号生成器");
        System.out.println("  email          - 邮箱生成器"); 
        System.out.println("  age            - 年龄生成器");
        System.out.println("  gender         - 性别生成器");
        System.out.println("  password       - 密码生成器");
        System.out.println("  account        - 账号名生成器");
        System.out.println();
        
        System.out.println("标识类 (Identifiers):");
        System.out.println("  uuid           - UUID生成器");
        System.out.println("  idcard         - 身份证号生成器");
        System.out.println("  bankcard       - 银行卡号生成器");
        System.out.println("  uscc           - 统一社会信用代码生成器");
        System.out.println();
        
        System.out.println("网络设备类 (Network / Device):");
        System.out.println("  ip             - IP地址生成器");
        System.out.println("  mac            - MAC地址生成器");
        System.out.println("  domain         - 域名生成器");
        System.out.println("  port           - 端口号生成器");
        System.out.println("  header         - HTTP头生成器");
        System.out.println("  url            - URL生成器");
        System.out.println();
        
        System.out.println("数值计量类 (Numeric / Measurement):");
        System.out.println("  integer        - 整数生成器");
        System.out.println("  decimal        - 小数生成器");
        System.out.println("  currency       - 币种生成器");
        System.out.println("  negative       - 负数生成器");
        System.out.println("  scientific     - 科学计数法生成器");
        System.out.println();
        
        System.out.println("时间日历类 (Time / Calendar):");
        System.out.println("  date           - 日期生成器");
        System.out.println("  time           - 时间生成器");
        System.out.println("  timestamp      - 时间戳生成器");
        System.out.println("  cron           - Cron表达式生成器");
        System.out.println();
        
        System.out.println("安全注入测试类 (Security / Injection Test):");
        System.out.println("  sqlinjection   - SQL注入Payload生成器");
        System.out.println("  xss            - XSS攻击脚本生成器");
        System.out.println("  commandinjection - 命令注入生成器");
        System.out.println("  pathtraversal  - 路径穿越生成器");
        System.out.println();
        
        System.out.println("文本多语言类 (Text / Multilingual):");
        System.out.println("  longtext       - 长文本生成器");
        System.out.println("  richtext       - 富文本生成器");
        System.out.println("  unicode_boundary - Unicode边界字符生成器");
        System.out.println("  multilingual_text - 多语言示例生成器");
        System.out.println("  special_char   - 特殊字符生成器");
        System.out.println();
        
        System.out.println("媒体文件类 (Media / Binary):");
        System.out.println("  image_header   - 图像文件头生成器");
        System.out.println("  file_extension - 文件扩展名生成器");
        System.out.println("  file_size      - 文件大小生成器");
        System.out.println("  image_dimensions - 图片尺寸生成器");
        System.out.println("  simulated_media_file - 模拟媒体文件生成器");
        System.out.println();
        
        System.out.println("特殊场景数据 (Special Scenario):");
        System.out.println("  emptynull      - 空值/Null值生成器");
        System.out.println("  boundary       - 边界值/极端值生成器");
        System.out.println("  invalid_exception - 非法/异常数据生成器");
        System.out.println("  customizable_business_id - 可自定义业务编号生成器");
        System.out.println("  duplicate_data - 重复数据生成器");
        System.out.println("  sorted_data    - 排序数据生成器");
        System.out.println();
        
        System.out.println("位置数据类 (Location Data):");
        System.out.println("  location       - 位置数据生成器");
        System.out.println("  enhanced_location - 增强位置数据生成器");
        System.out.println("  enhanced_address - 增强地址生成器");
        System.out.println();
    }

    private DataForgeConfig parseConfiguration() throws Exception {
        DataForgeConfig config = new DataForgeConfig();

        // 从配置文件加载
        if (configFile != null) {
            if (!configFile.exists() || !configFile.isFile()) {
                throw new IllegalArgumentException("配置文件不存在或不是有效文件: " + configFile.getPath());
            }
            
            logger.info("从配置文件加载配置: {}", configFile.getPath());
            ConfigurationManager configManager = new ConfigurationManager();
            config = (DataForgeConfig) configManager.loadFromFile(configFile);
        }

        // 命令行参数覆盖配置文件
        if (fields != null || (positionalArgs != null && !positionalArgs.isEmpty())) {
            String fieldStr = fields != null ? fields : String.join(",", positionalArgs);
            config.setFields(parseFields(fieldStr));
        }

        if (count > 0) {
            config.setCount(count);
        }

        if (outputFile != null) {
            config.setOutputFile(outputFile.getPath());
        }

        if (outputFormat != null) {
            config.setOutputFormat(outputFormat);
        }

        config.setEnableValidation(enableValidation);
        config.setParallel(parallel);
        config.setVerbose(verbose);
        
        if (seed != null) {
            config.setSeed(seed);
        }

        // 验证配置
        if (config.getFields() == null || config.getFields().isEmpty()) {
            return null;
        }

        return config;
    }

    private Map<String, String> parseFields(String fieldStr) {
        Map<String, String> fieldMap = new LinkedHashMap<>();
        String[] fieldDefs = fieldStr.split(",");
        
        for (String fieldDef : fieldDefs) {
            fieldDef = fieldDef.trim();
            String[] parts = fieldDef.split(":");
            String fieldName = parts[0].trim();
            String generatorType = parts.length > 1 ? parts[1].trim() : fieldName;
            
            fieldMap.put(fieldName, generatorType);
        }
        
        return fieldMap;
    }

    private int executeGeneration(DataForgeConfig config) throws Exception {
        logger.info("开始数据生成，记录数: {}, 字段: {}", config.getCount(), config.getFields().keySet());

        // 创建生成器映射
        Map<String, DataGenerator<?>> generators = new LinkedHashMap<>();
        for (Map.Entry<String, String> field : config.getFields().entrySet()) {
            String fieldName = field.getKey();
            String generatorType = field.getValue();
            
            try {
                DataGenerator<?> generator = GeneratorFactory.createGenerator(generatorType);
                generators.put(fieldName, generator);
                logger.debug("已创建生成器: {} -> {}", fieldName, generatorType);
            } catch (IllegalArgumentException e) {
                System.err.println("错误: 未知的生成器类型: " + generatorType);
                System.err.println("使用 --list-generators 查看所有可用生成器");
                return 1;
            }
        }

        // 创建生成上下文
        GenerationContext context = new GenerationContext(config.getCount());
        if (config.getSeed() != null) {
            context.setSeed(config.getSeed());
        }

        List<String> fieldNames = new ArrayList<>(config.getFields().keySet());

        // 执行数据生成和输出
        if (jdbcUrl != null && !jdbcUrl.isEmpty()) {
            // 数据库直接写入模式
            return executeDatabaseWrite(config, fieldNames, generators, context);
        } else if (config.getOutputFile() != null && !config.getOutputFile().isEmpty()) {
            // 输出到文件
            String outputPath = config.getOutputFile();
            String format = determineOutputFormat(outputPath, config.getOutputFormat());
            
            logger.info("输出到文件: {}, 格式: {}", outputPath, format);
            
            switch (format.toLowerCase()) {
                case "json":
                    JsonOutputWriter jsonWriter = new JsonOutputWriter(outputPath, fieldNames, generators);
                    jsonWriter.write(context);
                    break;
                case "xml":
                    XmlOutputWriter xmlWriter = new XmlOutputWriter(outputPath, fieldNames, generators);
                    xmlWriter.write(context);
                    break;
                case "sql":
                    String table = tableName != null ? tableName : "generated_data";
                    SqlInsertOutputWriter sqlWriter = new SqlInsertOutputWriter(outputPath, fieldNames, generators, table, batchSize, databaseType);
                    sqlWriter.write(context);
                    break;
                case "csv":
                default:
                    CsvOutputWriter csvWriter = new CsvOutputWriter(outputPath, fieldNames, generators);
                    csvWriter.write(context);
                    break;
            }
            
            System.out.println("✓ 成功生成 " + config.getCount() + " 条记录，已保存到: " + outputPath);
        } else {
            // 输出到控制台
            logger.info("输出到控制台");
            outputToConsole(fieldNames, generators, context);
        }

        logger.info("数据生成完成");
        return 0;
    }

    private String determineOutputFormat(String outputPath, String explicitFormat) {
        if (explicitFormat != null && !explicitFormat.isEmpty()) {
            return explicitFormat;
        }
        
        String lowerPath = outputPath.toLowerCase();
        if (lowerPath.endsWith(".json")) {
            return "json";
        } else if (lowerPath.endsWith(".xml")) {
            return "xml";
        } else if (lowerPath.endsWith(".sql")) {
            return "sql";
        }
        
        return "csv"; // 默认格式
    }

    private int executeDatabaseWrite(DataForgeConfig config, List<String> fieldNames, 
                                   Map<String, DataGenerator<?>> generators, GenerationContext context) {
        try {
            String table = tableName != null ? tableName : "generated_data";
            logger.info("直接写入数据库: {}, 表: {}", jdbcUrl, table);
            
            DatabaseOutputWriter dbWriter = new DatabaseOutputWriter(
                jdbcUrl, dbUsername, dbPassword, table, fieldNames, generators, batchSize);
            
            // 测试连接
            if (!dbWriter.testConnection()) {
                System.err.println("错误: 无法连接到数据库");
                return 1;
            }
            
            // 检查表是否存在
            if (!dbWriter.tableExists()) {
                System.err.println("警告: 表 " + table + " 不存在，请确保表已创建");
                dbWriter.printTableStructure();
            }
            
            dbWriter.write(context);
            logger.info("数据库写入完成");
            return 0;
            
        } catch (Exception e) {
            logger.error("数据库写入失败: {}", e.getMessage(), e);
            System.err.println("错误: 数据库写入失败 - " + e.getMessage());
            return 1;
        }
    }

    private void outputToConsole(List<String> fieldNames, Map<String, DataGenerator<?>> generators, GenerationContext context) {
        System.out.println("\n=== DataForge 生成结果 (" + context.getCount() + " 条记录) ===");
        System.out.println();
        
        // 输出表头
        System.out.println(String.join(",", fieldNames));
        
        // 输出数据行
        for (int i = 0; i < context.getCount(); i++) {
            GenerationContext rowContext = new GenerationContext(1);
            if (context.getSeed() != null) {
                rowContext.setSeed(context.getSeed() + i); // 为每行使用不同的种子
            }
            
            StringBuilder row = new StringBuilder();
            
            for (int j = 0; j < fieldNames.size(); j++) {
                String fieldName = fieldNames.get(j);
                DataGenerator<?> generator = generators.get(fieldName);
                Object value = generator.generate(rowContext);
                
                // CSV格式处理
                String valueStr = value != null ? value.toString() : "";
                if (valueStr.contains(",") || valueStr.contains("\"") || valueStr.contains("\n")) {
                    valueStr = "\"" + valueStr.replace("\"", "\"\"") + "\"";
                }
                
                row.append(valueStr);
                
                if (j < fieldNames.size() - 1) {
                    row.append(",");
                }
            }
            
            System.out.println(row.toString());
        }
        
        System.out.println();
    }
}
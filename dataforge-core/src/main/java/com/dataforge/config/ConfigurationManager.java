package com.dataforge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.core.GeneratorFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 配置管理器
 * 支持YAML和JSON格式的配置文件，用于定义复杂的数据生成任务
 */
public class ConfigurationManager {

    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * 任务配置类
     */
    public static class GenerationTask {
        private String name;
        private String type;
        private int count = 1;
        private Map<String, Object> parameters = new HashMap<>();
        private List<GenerationTask> nestedTasks = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        
        public List<GenerationTask> getNestedTasks() { return nestedTasks; }
        public void setNestedTasks(List<GenerationTask> nestedTasks) { this.nestedTasks = nestedTasks; }
    }

    /**
     * 配置文件类
     */
    public static class Configuration {
        private String name;
        private String description;
        private String outputFormat = "json";
        private String outputFile;
        private boolean prettyPrint = true;
        private List<GenerationTask> tasks = new ArrayList<>();
        private Map<String, Object> globalParameters = new HashMap<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getOutputFormat() { return outputFormat; }
        public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
        
        public String getOutputFile() { return outputFile; }
        public void setOutputFile(String outputFile) { this.outputFile = outputFile; }
        
        public boolean isPrettyPrint() { return prettyPrint; }
        public void setPrettyPrint(boolean prettyPrint) { this.prettyPrint = prettyPrint; }
        
        public List<GenerationTask> getTasks() { return tasks; }
        public void setTasks(List<GenerationTask> tasks) { this.tasks = tasks; }
        
        public Map<String, Object> getGlobalParameters() { return globalParameters; }
        public void setGlobalParameters(Map<String, Object> globalParameters) { this.globalParameters = globalParameters; }
    }

    /**
     * 从文件加载配置
     * @param filePath 配置文件路径
     * @return 配置对象
     * @throws IOException 如果读取文件失败
     */
    public static Configuration loadConfiguration(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Configuration file not found: " + filePath);
        }

        String fileName = file.getName().toLowerCase();
        ObjectMapper mapper;
        
        if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            mapper = yamlMapper;
        } else if (fileName.endsWith(".json")) {
            mapper = jsonMapper;
        } else {
            throw new IOException("Unsupported configuration file format: " + fileName);
        }

        return mapper.readValue(file, Configuration.class);
    }

    /**
     * 保存配置到文件
     * @param config 配置对象
     * @param filePath 目标文件路径
     * @throws IOException 如果写入文件失败
     */
    public static void saveConfiguration(Configuration config, String filePath) throws IOException {
        File file = new File(filePath);
        String fileName = file.getName().toLowerCase();
        ObjectMapper mapper;
        
        if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            mapper = yamlMapper;
        } else if (fileName.endsWith(".json")) {
            mapper = jsonMapper;
        } else {
            throw new IOException("Unsupported configuration file format: " + fileName);
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, config);
    }

    /**
     * 根据配置执行任务
     * @param config 配置对象
     * @return 生成的数据列表
     */
    public static List<Map<String, Object>> executeConfiguration(Configuration config) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (GenerationTask task : config.getTasks()) {
            results.addAll(executeTask(task, config.getGlobalParameters()));
        }
        
        return results;
    }

    private static List<Map<String, Object>> executeTask(GenerationTask task, Map<String, Object> globalParameters) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        GenerationContext context = new GenerationContext(task.getCount());
        
        // Set parameters from global and task-specific parameters
        Map<String, Object> mergedParams = new HashMap<>(globalParameters);
        mergedParams.putAll(task.getParameters());
        
        for (Map.Entry<String, Object> entry : mergedParams.entrySet()) {
            context.setParameter(entry.getKey(), entry.getValue());
        }
        
        DataGenerator<?> generator = GeneratorFactory.createGenerator(task.getType());
        
        for (int i = 0; i < task.getCount(); i++) {
            Map<String, Object> record = new HashMap<>();
            
            if (task.getName() != null) {
                record.put(task.getName(), generator.generate(context));
            }
            
            // 处理嵌套任务
            for (GenerationTask nestedTask : task.getNestedTasks()) {
                List<Map<String, Object>> nestedResults = executeTask(nestedTask, mergedParams);
                if (!nestedResults.isEmpty()) {
                    record.putAll(nestedResults.get(0));
                }
            }
            
            results.add(record);
        }
        
        return results;
    }

    /**
     * 创建示例配置
     * @return 示例配置对象
     */
    public static Configuration createExampleConfiguration() {
        Configuration config = new Configuration();
        config.setName("User Data Generation");
        config.setDescription("Generate sample user data for testing");
        config.setOutputFormat("json");
        config.setOutputFile("users.json");
        config.setPrettyPrint(true);
        
        // 全局参数
        config.getGlobalParameters().put("locale", "zh_CN");
        
        // 用户任务
        GenerationTask userTask = new GenerationTask();
        userTask.setName("user");
        userTask.setType("name");
        userTask.setCount(100);
        userTask.getParameters().put("gender", "male");
        
        // 嵌套任务：邮箱
        GenerationTask emailTask = new GenerationTask();
        emailTask.setName("email");
        emailTask.setType("email");
        emailTask.setCount(1);
        userTask.getNestedTasks().add(emailTask);
        
        // 嵌套任务：电话
        GenerationTask phoneTask = new GenerationTask();
        phoneTask.setName("phone");
        phoneTask.setType("phone");
        phoneTask.setCount(1);
        userTask.getNestedTasks().add(phoneTask);
        
        config.getTasks().add(userTask);
        
        return config;
    }

    /**
     * 验证配置格式
     * @param filePath 配置文件路径
     * @return 验证结果
     */
    public static ValidationResult validateConfiguration(String filePath) {
        try {
            Configuration config = loadConfiguration(filePath);
            return new ValidationResult(true, "Configuration is valid");
        } catch (Exception e) {
            return new ValidationResult(false, "Configuration validation failed: " + e.getMessage());
        }
    }

    /**
     * CLI适配器方法 - 为CLI兼容性提供实例方法
     * @param configFile 配置文件
     * @return CLI配置对象
     * @throws Exception 如果加载失败
     */
    public Object loadFromFile(File configFile) throws Exception {
        Configuration config = loadConfiguration(configFile.getPath());
        
        // 将Configuration转换为CLI需要的DataForgeConfig格式
        DataForgeConfig cliConfig = new DataForgeConfig();
        
        // 提取第一个任务的字段信息作为CLI配置
        if (!config.getTasks().isEmpty()) {
            Map<String, String> fields = new LinkedHashMap<>();
            
            for (GenerationTask task : config.getTasks()) {
                if (task.getName() != null && task.getType() != null) {
                    fields.put(task.getName(), task.getType());
                }
                
                // 处理嵌套任务
                for (GenerationTask nestedTask : task.getNestedTasks()) {
                    if (nestedTask.getName() != null && nestedTask.getType() != null) {
                        fields.put(nestedTask.getName(), nestedTask.getType());
                    }
                }
            }
            
            cliConfig.setFields(fields);
            
            // 设置数量（使用第一个任务的数量）
            if (!config.getTasks().isEmpty()) {
                cliConfig.setCount(config.getTasks().get(0).getCount());
            }
        }
        
        // 设置输出配置
        if (config.getOutputFile() != null) {
            cliConfig.setOutputFile(config.getOutputFile());
        }
        
        if (config.getOutputFormat() != null) {
            cliConfig.setOutputFormat(config.getOutputFormat());
        }
        
        return cliConfig;
    }
    
    /**
     * CLI配置数据类 - 简化的配置类用于CLI集成
     */
    public static class DataForgeConfig {
        private Map<String, String> fields = new LinkedHashMap<>();
        private int count = 10;
        private String outputFile;
        private String outputFormat;
        private boolean enableValidation = true;
        private boolean parallel = false;
        private boolean verbose = false;
        private Long seed;

        // Getters and Setters
        public Map<String, String> getFields() { return fields; }
        public void setFields(Map<String, String> fields) { this.fields = fields; }
        
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        
        public String getOutputFile() { return outputFile; }
        public void setOutputFile(String outputFile) { this.outputFile = outputFile; }
        
        public String getOutputFormat() { return outputFormat; }
        public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
        
        public boolean isEnableValidation() { return enableValidation; }
        public void setEnableValidation(boolean enableValidation) { this.enableValidation = enableValidation; }
        
        public boolean isParallel() { return parallel; }
        public void setParallel(boolean parallel) { this.parallel = parallel; }
        
        public boolean isVerbose() { return verbose; }
        public void setVerbose(boolean verbose) { this.verbose = verbose; }
        
        public Long getSeed() { return seed; }
        public void setSeed(Long seed) { this.seed = seed; }
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}
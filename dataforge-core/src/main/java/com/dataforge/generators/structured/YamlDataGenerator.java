package com.dataforge.generators.structured;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.*;

/**
 * YAML数据生成器
 * 生成各种格式的YAML文档
 */
public class YamlDataGenerator implements DataGenerator<String> {
    
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private static final Random random = new Random();
    
    private final YamlDocumentType documentType;
    private final boolean includeComments;
    
    public enum YamlDocumentType {
        SIMPLE,             // 简单YAML
        CONFIGURATION,      // 配置文件格式
        DATA_STRUCTURE,     // 数据结构格式
        KUBERNETES_MANIFEST // Kubernetes清单格式
    }
    
    public YamlDataGenerator() {
        this(YamlDocumentType.SIMPLE, false);
    }
    
    public YamlDataGenerator(YamlDocumentType documentType, boolean includeComments) {
        this.documentType = documentType;
        this.includeComments = includeComments;
    }
    
    @Override
    public String generate(GenerationContext context) {
        try {
            Map<String, Object> data = generateYamlData();
            String yaml = yamlMapper.writeValueAsString(data);
            
            if (includeComments) {
                yaml = addComments(yaml);
            }
            
            return yaml;
            
        } catch (Exception e) {
            return generateFallbackYaml();
        }
    }
    
    /**
     * 根据文档类型生成YAML数据
     */
    private Map<String, Object> generateYamlData() {
        switch (documentType) {
            case SIMPLE:
                return generateSimpleYaml();
            case CONFIGURATION:
                return generateConfigurationYaml();
            case DATA_STRUCTURE:
                return generateDataStructureYaml();
            case KUBERNETES_MANIFEST:
                return generateKubernetesManifest();
            default:
                return generateSimpleYaml();
        }
    }
    
    /**
     * 生成简单YAML
     */
    private Map<String, Object> generateSimpleYaml() {
        Map<String, Object> yaml = new LinkedHashMap<>();
        
        yaml.put("name", generateRandomName());
        yaml.put("version", "1." + random.nextInt(10) + "." + random.nextInt(10));
        yaml.put("active", random.nextBoolean());
        yaml.put("priority", random.nextInt(10) + 1);
        yaml.put("tags", Arrays.asList("tag1", "tag2", "tag3"));
        
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("created", generateRandomTimestamp());
        metadata.put("author", "DataForge Generator");
        metadata.put("description", "Generated YAML document for testing");
        yaml.put("metadata", metadata);
        
        return yaml;
    }
    
    /**
     * 生成配置文件YAML
     */
    private Map<String, Object> generateConfigurationYaml() {
        Map<String, Object> config = new LinkedHashMap<>();
        
        // 应用配置
        Map<String, Object> app = new LinkedHashMap<>();
        app.put("name", "test-application");
        app.put("version", "2.0.0");
        app.put("debug", random.nextBoolean());
        app.put("port", 8080 + random.nextInt(1000));
        config.put("application", app);
        
        // 数据库配置
        Map<String, Object> database = new LinkedHashMap<>();
        database.put("host", "localhost");
        database.put("port", 3306 + random.nextInt(1000));
        database.put("name", "testdb_" + random.nextInt(100));
        database.put("username", "user_" + random.nextInt(1000));
        database.put("pool_size", random.nextInt(50) + 10);
        config.put("database", database);
        
        // 缓存配置
        Map<String, Object> cache = new LinkedHashMap<>();
        cache.put("type", "redis");
        cache.put("ttl", random.nextInt(3600) + 300);
        cache.put("max_entries", random.nextInt(10000) + 1000);
        config.put("cache", cache);
        
        // 日志配置
        Map<String, Object> logging = new LinkedHashMap<>();
        logging.put("level", getRandomLogLevel());
        logging.put("file", "/var/log/app.log");
        logging.put("max_size", "100MB");
        logging.put("rotate", true);
        config.put("logging", logging);
        
        // 功能开关
        Map<String, Object> features = new LinkedHashMap<>();
        features.put("user_registration", random.nextBoolean());
        features.put("email_notifications", random.nextBoolean());
        features.put("analytics", random.nextBoolean());
        features.put("maintenance_mode", false);
        config.put("features", features);
        
        return config;
    }
    
    /**
     * 生成数据结构YAML
     */
    private Map<String, Object> generateDataStructureYaml() {
        Map<String, Object> data = new LinkedHashMap<>();
        
        data.put("schema_version", "1.0");
        data.put("generated_at", generateRandomTimestamp());
        
        // 用户列表
        List<Map<String, Object>> users = new ArrayList<>();
        int userCount = random.nextInt(5) + 3;
        for (int i = 0; i < userCount; i++) {
            Map<String, Object> user = new LinkedHashMap<>();
            user.put("id", i + 1);
            user.put("username", "user_" + (i + 1));
            user.put("email", "user" + (i + 1) + "@example.com");
            user.put("active", random.nextBoolean());
            user.put("created_at", generateRandomTimestamp());
            
            // 用户权限
            List<String> permissions = new ArrayList<>();
            permissions.add("read");
            if (random.nextBoolean()) permissions.add("write");
            if (random.nextBoolean()) permissions.add("admin");
            user.put("permissions", permissions);
            
            users.add(user);
        }
        data.put("users", users);
        
        // 系统配置
        Map<String, Object> system = new LinkedHashMap<>();
        system.put("max_users", random.nextInt(1000) + 100);
        system.put("session_timeout", random.nextInt(3600) + 600);
        system.put("backup_enabled", random.nextBoolean());
        
        Map<String, Object> limits = new LinkedHashMap<>();
        limits.put("memory", "512MB");
        limits.put("cpu", "2 cores");
        limits.put("storage", "10GB");
        system.put("resource_limits", limits);
        
        data.put("system", system);
        
        return data;
    }
    
    /**
     * 生成Kubernetes清单YAML
     */
    private Map<String, Object> generateKubernetesManifest() {
        Map<String, Object> manifest = new LinkedHashMap<>();
        
        manifest.put("apiVersion", "apps/v1");
        manifest.put("kind", "Deployment");
        
        // Metadata
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("name", "test-app-deployment");
        metadata.put("namespace", "default");
        
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put("app", "test-app");
        labels.put("version", "v1.0.0");
        metadata.put("labels", labels);
        
        manifest.put("metadata", metadata);
        
        // Spec
        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("replicas", random.nextInt(5) + 1);
        
        Map<String, Object> selector = new LinkedHashMap<>();
        Map<String, String> matchLabels = new LinkedHashMap<>();
        matchLabels.put("app", "test-app");
        selector.put("matchLabels", matchLabels);
        spec.put("selector", selector);
        
        // Template
        Map<String, Object> template = new LinkedHashMap<>();
        Map<String, Object> templateMetadata = new LinkedHashMap<>();
        templateMetadata.put("labels", matchLabels);
        template.put("metadata", templateMetadata);
        
        Map<String, Object> templateSpec = new LinkedHashMap<>();
        List<Map<String, Object>> containers = new ArrayList<>();
        
        Map<String, Object> container = new LinkedHashMap<>();
        container.put("name", "test-app");
        container.put("image", "test-app:1.0.0");
        
        List<Map<String, Object>> ports = new ArrayList<>();
        Map<String, Object> port = new LinkedHashMap<>();
        port.put("containerPort", 8080);
        ports.add(port);
        container.put("ports", ports);
        
        Map<String, Object> resources = new LinkedHashMap<>();
        Map<String, String> requests = new LinkedHashMap<>();
        requests.put("memory", "256Mi");
        requests.put("cpu", "250m");
        Map<String, String> limits = new LinkedHashMap<>();
        limits.put("memory", "512Mi");
        limits.put("cpu", "500m");
        resources.put("requests", requests);
        resources.put("limits", limits);
        container.put("resources", resources);
        
        containers.add(container);
        templateSpec.put("containers", containers);
        template.put("spec", templateSpec);
        
        spec.put("template", template);
        manifest.put("spec", spec);
        
        return manifest;
    }
    
    /**
     * 添加注释到YAML
     */
    private String addComments(String yaml) {
        StringBuilder commented = new StringBuilder();
        commented.append("# Generated by DataForge YAML Generator\n");
        commented.append("# Generated at: ").append(new Date()).append("\n");
        commented.append("# Document type: ").append(documentType).append("\n\n");
        commented.append(yaml);
        return commented.toString();
    }
    
    /**
     * 生成随机名称
     */
    private String generateRandomName() {
        String[] names = {"sample", "test", "demo", "example", "prototype", "mock"};
        return names[random.nextInt(names.length)] + "_" + random.nextInt(1000);
    }
    
    /**
     * 生成随机时间戳
     */
    private String generateRandomTimestamp() {
        return java.time.LocalDateTime.now().minusDays(random.nextInt(30)).toString();
    }
    
    /**
     * 获取随机日志级别
     */
    private String getRandomLogLevel() {
        String[] levels = {"DEBUG", "INFO", "WARN", "ERROR"};
        return levels[random.nextInt(levels.length)];
    }
    
    /**
     * 生成回退YAML
     */
    private String generateFallbackYaml() {
        return String.format("name: fallback_%d\ntype: error\ntimestamp: %s\n", 
            random.nextInt(10000), new Date().toString());
    }
}
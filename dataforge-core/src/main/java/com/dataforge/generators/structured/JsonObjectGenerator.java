package com.dataforge.generators.structured;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.*;

/**
 * JSON对象/数组生成器
 * 根据配置生成各种复杂度的JSON数据结构
 */
public class JsonObjectGenerator implements DataGenerator<String> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();
    
    private final JsonStructureType structureType;
    private final int maxDepth;
    private final int maxArraySize;
    private final boolean includeNullValues;
    
    public enum JsonStructureType {
        SIMPLE_OBJECT,      // 简单对象
        NESTED_OBJECT,      // 嵌套对象
        ARRAY,              // 数组
        MIXED               // 混合结构
    }
    
    public JsonObjectGenerator() {
        this(JsonStructureType.SIMPLE_OBJECT, 3, 5, false);
    }
    
    public JsonObjectGenerator(JsonStructureType structureType, int maxDepth, int maxArraySize, boolean includeNullValues) {
        this.structureType = structureType;
        this.maxDepth = maxDepth;
        this.maxArraySize = maxArraySize;
        this.includeNullValues = includeNullValues;
    }
    
    @Override
    public String generate(GenerationContext context) {
        try {
            switch (structureType) {
                case SIMPLE_OBJECT:
                    return generateSimpleObject().toString();
                case NESTED_OBJECT:
                    return generateNestedObject(0).toString();
                case ARRAY:
                    return generateArray(0).toString();
                case MIXED:
                default:
                    return generateMixedStructure(0).toString();
            }
        } catch (Exception e) {
            return generateFallbackJson();
        }
    }
    
    /**
     * 生成简单JSON对象
     */
    private ObjectNode generateSimpleObject() {
        ObjectNode obj = objectMapper.createObjectNode();
        
        // 基础字段类型
        obj.put("id", random.nextInt(10000));
        obj.put("name", generateRandomString());
        obj.put("active", random.nextBoolean());
        obj.put("score", Math.round(random.nextDouble() * 100 * 100.0) / 100.0);
        obj.put("created_at", generateRandomTimestamp());
        
        if (includeNullValues && random.nextBoolean()) {
            obj.putNull("optional_field");
        }
        
        return obj;
    }
    
    /**
     * 生成嵌套JSON对象
     */
    private ObjectNode generateNestedObject(int currentDepth) {
        ObjectNode obj = objectMapper.createObjectNode();
        
        // 基础字段
        obj.put("id", random.nextInt(10000));
        obj.put("type", getRandomType());
        
        // 嵌套对象
        if (currentDepth < maxDepth) {
            ObjectNode nested = objectMapper.createObjectNode();
            nested.put("property", generateRandomString());
            nested.put("value", random.nextInt(1000));
            nested.put("enabled", random.nextBoolean());
            
            if (currentDepth < maxDepth - 1 && random.nextBoolean()) {
                nested.set("sub_object", generateNestedObject(currentDepth + 1));
            }
            
            obj.set("details", nested);
        }
        
        // 简单数组
        ArrayNode tags = objectMapper.createArrayNode();
        int tagCount = random.nextInt(5) + 1;
        for (int i = 0; i < tagCount; i++) {
            tags.add("tag_" + (i + 1));
        }
        obj.set("tags", tags);
        
        return obj;
    }
    
    /**
     * 生成JSON数组
     */
    private ArrayNode generateArray(int currentDepth) {
        ArrayNode array = objectMapper.createArrayNode();
        int size = random.nextInt(maxArraySize) + 1;
        
        for (int i = 0; i < size; i++) {
            switch (random.nextInt(4)) {
                case 0: // 字符串
                    array.add(generateRandomString());
                    break;
                case 1: // 数字
                    array.add(random.nextInt(1000));
                    break;
                case 2: // 布尔值
                    array.add(random.nextBoolean());
                    break;
                case 3: // 对象
                    if (currentDepth < maxDepth) {
                        array.add(generateSimpleObject());
                    } else {
                        array.add("item_" + i);
                    }
                    break;
            }
        }
        
        return array;
    }
    
    /**
     * 生成混合结构JSON
     */
    private ObjectNode generateMixedStructure(int currentDepth) {
        ObjectNode obj = objectMapper.createObjectNode();
        
        // 基础信息
        obj.put("id", UUID.randomUUID().toString());
        obj.put("timestamp", generateRandomTimestamp());
        obj.put("status", getRandomStatus());
        
        // 用户信息对象
        ObjectNode user = objectMapper.createObjectNode();
        user.put("username", generateRandomUsername());
        user.put("email", generateRandomEmail());
        user.put("age", random.nextInt(80) + 18);
        obj.set("user", user);
        
        // 设置数组
        ArrayNode settings = objectMapper.createArrayNode();
        String[] settingKeys = {"notifications", "privacy", "security", "preferences"};
        for (String key : settingKeys) {
            ObjectNode setting = objectMapper.createObjectNode();
            setting.put("key", key);
            setting.put("value", random.nextBoolean());
            setting.put("last_modified", generateRandomTimestamp());
            settings.add(setting);
        }
        obj.set("settings", settings);
        
        // 元数据
        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.put("version", "1." + random.nextInt(10) + "." + random.nextInt(10));
        metadata.put("source", "dataforge");
        ArrayNode features = objectMapper.createArrayNode();
        features.add("feature_a");
        features.add("feature_b");
        if (random.nextBoolean()) {
            features.add("feature_c");
        }
        metadata.set("features", features);
        obj.set("metadata", metadata);
        
        return obj;
    }
    
    /**
     * 生成随机字符串
     */
    private String generateRandomString() {
        String[] words = {"data", "test", "sample", "example", "demo", "value", "item", "object", "element"};
        return words[random.nextInt(words.length)] + "_" + random.nextInt(1000);
    }
    
    /**
     * 生成随机类型
     */
    private String getRandomType() {
        String[] types = {"user", "product", "order", "payment", "notification", "system", "config"};
        return types[random.nextInt(types.length)];
    }
    
    /**
     * 生成随机状态
     */
    private String getRandomStatus() {
        String[] statuses = {"active", "inactive", "pending", "completed", "failed", "processing"};
        return statuses[random.nextInt(statuses.length)];
    }
    
    /**
     * 生成随机用户名
     */
    private String generateRandomUsername() {
        String[] prefixes = {"user", "test", "demo", "sample"};
        return prefixes[random.nextInt(prefixes.length)] + random.nextInt(10000);
    }
    
    /**
     * 生成随机邮箱
     */
    private String generateRandomEmail() {
        String[] domains = {"example.com", "test.org", "sample.net", "demo.io"};
        return generateRandomUsername() + "@" + domains[random.nextInt(domains.length)];
    }
    
    /**
     * 生成随机时间戳
     */
    private String generateRandomTimestamp() {
        long now = System.currentTimeMillis();
        long randomTime = now - random.nextInt(86400 * 30) * 1000L; // 30天内随机时间
        return new Date(randomTime).toString();
    }
    
    /**
     * 生成回退JSON（当出现异常时）
     */
    private String generateFallbackJson() {
        return String.format("{\"id\":%d,\"type\":\"fallback\",\"timestamp\":\"%s\"}", 
            random.nextInt(10000), new Date().toString());
    }
}
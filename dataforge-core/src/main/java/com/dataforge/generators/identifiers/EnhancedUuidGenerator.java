package com.dataforge.generators.identifiers;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.validation.DataValidator;

import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 增强版UUID生成器
 * 支持多种UUID版本和格式，包含验证功能
 */
public class EnhancedUuidGenerator implements DataGenerator<Map<String, Object>> {

    public enum UuidVersion {
        V1_TIME_BASED("时间戳+MAC地址"),
        V2_DCE_SECURITY("DCE安全"),
        V3_NAME_BASED_MD5("基于名字的MD5散列"),
        V4_RANDOM("随机数"),
        V5_NAME_BASED_SHA1("基于名字的SHA-1散列");

        private final String description;

        UuidVersion(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum UuidFormat {
        STANDARD("标准格式"),
        UPPERCASE("大写格式"),
        WITHOUT_DASHES("无连字符"),
        BRACED("带大括号"),
        URN("URN格式");

        private final String description;

        UuidFormat(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final UuidVersion version;
    private final UuidFormat format;
    private final boolean includeValidation;
    private final boolean includeTimestamp;

    public EnhancedUuidGenerator() {
        this(UuidVersion.V4_RANDOM, UuidFormat.STANDARD, true, false);
    }

    public EnhancedUuidGenerator(UuidVersion version, UuidFormat format, boolean includeValidation, boolean includeTimestamp) {
        this.version = version;
        this.format = format;
        this.includeValidation = includeValidation;
        this.includeTimestamp = includeTimestamp;
    }

    @Override
    public Map<String, Object> generate(GenerationContext context) {
        UUID uuid = generateUuid(context);
        
        Map<String, Object> result = new HashMap<>();
        result.put("uuid", formatUuid(uuid, format));
        result.put("version", version.name());
        result.put("format", format.name());
        
        if (includeValidation) {
            result.put("valid", DataValidator.isValidUuid(uuid.toString()));
        }
        
        if (includeTimestamp && (version == UuidVersion.V1_TIME_BASED)) {
            result.put("timestamp", extractTimestamp(uuid));
        }
        
        // 添加不同格式的UUID
        result.put("standard", uuid.toString());
        result.put("uppercase", uuid.toString().toUpperCase());
        result.put("no_dashes", uuid.toString().replace("-", ""));
        result.put("braced", "{" + uuid.toString() + "}");
        result.put("urn", "urn:uuid:" + uuid.toString());
        
        return result;
    }

    private UUID generateUuid(GenerationContext context) {
        switch (version) {
            case V1_TIME_BASED:
                // 模拟时间戳UUID，实际应用中需要使用专用库
                return UUID.randomUUID();
            case V4_RANDOM:
                return UUID.randomUUID();
            case V3_NAME_BASED_MD5:
                // 基于名字的MD5散列
                String namespace = "example-namespace";
                String name = "example-name-" + context.getRandom().nextInt(10000);
                return UUID.nameUUIDFromBytes((namespace + name).getBytes());
            case V5_NAME_BASED_SHA1:
                // 基于名字的SHA-1散列（使用V3作为近似）
                String shaNamespace = "example-sha-namespace";
                String shaName = "example-sha-name-" + context.getRandom().nextInt(10000);
                return UUID.nameUUIDFromBytes((shaNamespace + shaName).getBytes());
            case V2_DCE_SECURITY:
                // DCE安全UUID（使用V4作为近似）
                return UUID.randomUUID();
            default:
                return UUID.randomUUID();
        }
    }

    private String formatUuid(UUID uuid, UuidFormat format) {
        String base = uuid.toString();
        
        switch (format) {
            case STANDARD:
                return base;
            case UPPERCASE:
                return base.toUpperCase();
            case WITHOUT_DASHES:
                return base.replace("-", "");
            case BRACED:
                return "{" + base + "}";
            case URN:
                return "urn:uuid:" + base;
            default:
                return base;
        }
    }

    private long extractTimestamp(UUID uuid) {
        // 对于V1 UUID，提取时间戳
        if (version == UuidVersion.V1_TIME_BASED) {
            return uuid.timestamp();
        }
        return 0L;
    }

    /**
     * 批量生成UUID
     */
    public List<Map<String, Object>> generateBatch(GenerationContext context, int count) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            results.add(generate(context));
        }
        
        return results;
    }

    /**
     * 验证UUID格式
     */
    public boolean validateUuid(String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取UUID信息
     */
    public Map<String, Object> getUuidInfo(String uuidString) {
        Map<String, Object> info = new HashMap<>();
        
        try {
            UUID uuid = UUID.fromString(uuidString);
            info.put("valid", true);
            info.put("version", getUuidVersion(uuid));
            info.put("variant", getUuidVariant(uuid));
            
            if (getUuidVersion(uuid) == 1) {
                info.put("timestamp", uuid.timestamp());
            }
        } catch (IllegalArgumentException e) {
            info.put("valid", false);
            info.put("error", e.getMessage());
        }
        
        return info;
    }

    private int getUuidVersion(UUID uuid) {
        return uuid.version();
    }

    private int getUuidVariant(UUID uuid) {
        return uuid.variant();
    }

    @Override
    public String getName() {
        return "enhanced_uuid";
    }

    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("version", "format", "includeValidation", "includeTimestamp");
    }

    /**
     * 获取所有支持的UUID版本
     */
    public static List<String> getSupportedVersions() {
        return Arrays.asList(
            UuidVersion.V1_TIME_BASED.name(),
            UuidVersion.V2_DCE_SECURITY.name(),
            UuidVersion.V3_NAME_BASED_MD5.name(),
            UuidVersion.V4_RANDOM.name(),
            UuidVersion.V5_NAME_BASED_SHA1.name()
        );
    }

    /**
     * 获取所有支持的UUID格式
     */
    public static List<String> getSupportedFormats() {
        return Arrays.asList(
            UuidFormat.STANDARD.name(),
            UuidFormat.UPPERCASE.name(),
            UuidFormat.WITHOUT_DASHES.name(),
            UuidFormat.BRACED.name(),
            UuidFormat.URN.name()
        );
    }
}
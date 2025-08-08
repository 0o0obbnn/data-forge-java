package com.dataforge.core;

import com.dataforge.generators.basic.*;
import com.dataforge.generators.business.CompanyGenerator;
import com.dataforge.generators.identifiers.UuidGenerator;
import com.dataforge.generators.network.IpAddressGenerator;
import com.dataforge.generators.network.MacAddressGenerator;
import com.dataforge.generators.network.DomainNameGenerator;
import com.dataforge.generators.network.PortNumberGenerator;
import com.dataforge.generators.network.HttpHeaderGenerator;
import com.dataforge.generators.network.UrlGenerator;
import com.dataforge.generators.numeric.*;
import com.dataforge.generators.temporal.*;
import com.dataforge.generators.security.SqlInjectionPayloadGenerator;
import com.dataforge.generators.security.XssAttackScriptGenerator;
import com.dataforge.generators.security.CommandInjectionGenerator;
import com.dataforge.generators.security.PathTraversalGenerator;
import com.dataforge.generators.security.SessionIdTokenGenerator;
import com.dataforge.generators.security.BinaryBase64DataGenerator;
import com.dataforge.generators.special.EmptyNullValueGenerator;
import com.dataforge.generators.special.BoundaryExtremeValueGenerator;
import com.dataforge.generators.special.InvalidExceptionDataGenerator;
import com.dataforge.generators.special.CustomizableBusinessIdGenerator;
import com.dataforge.generators.special.DuplicateDataGenerator;
import com.dataforge.generators.special.SortedDataGenerator;
import com.dataforge.generators.special.ConcurrentContentionDataGenerator;
import com.dataforge.generators.text.LongTextGenerator;
import com.dataforge.generators.text.RichTextGenerator;
import com.dataforge.generators.text.UnicodeBoundaryCharGenerator;
import com.dataforge.generators.text.MultilingualTextGenerator;
import com.dataforge.generators.text.SpecialCharGenerator;
import com.dataforge.generators.media.ImageFileHeaderGenerator;
import com.dataforge.generators.media.FileExtensionGenerator;
import com.dataforge.generators.media.FileSizeGenerator;
import com.dataforge.generators.media.ImageDimensionsGenerator;
import com.dataforge.generators.media.SimulatedMediaFileGenerator;
import com.dataforge.generators.location.LocationDataGenerator;
import com.dataforge.generators.location.EnhancedLocationDataGenerator;
import com.dataforge.generators.location.EnhancedAddressGenerator;
import com.dataforge.generators.identifiers.EnhancedUuidGenerator;
import com.dataforge.generators.identifiers.CustomIdGenerator;
import com.dataforge.generators.identifiers.LicensePlateGenerator;
import com.dataforge.generators.identifiers.EnhancedBankCardGenerator;
import com.dataforge.generators.identifiers.OrganizationCodeGenerator;
import com.dataforge.generators.identifiers.LeiCodeGenerator;
import com.dataforge.generators.identifiers.ProductCodeGenerator;
import com.dataforge.generators.location.AddressGenerator;
import com.dataforge.generators.network.NetworkDeviceGenerator;
import com.dataforge.generators.advanced.DataRelationshipGenerator;
import com.dataforge.generators.advanced.SimilarDataGenerator;
import com.dataforge.generators.advanced.BatchDataGenerator;
import com.dataforge.generators.structured.JsonObjectGenerator;
import com.dataforge.generators.structured.XmlDocumentGenerator;
import com.dataforge.generators.structured.YamlDataGenerator;
import com.dataforge.generators.enums.HttpStatusCodeGenerator;
import com.dataforge.generators.enums.BusinessStatusCodeGenerator;
import com.dataforge.generators.communication.VerificationCodeGenerator;
import com.dataforge.generators.communication.LandlinePhoneGenerator;
import com.dataforge.generators.communication.FilePathGenerator;
import com.dataforge.generators.communication.MimeTypeGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * 数据生成器工厂
 * 用于创建各种数据生成器的工厂类
 */
public class GeneratorFactory {
    
    private static final Map<String, Class<? extends DataGenerator>> GENERATORS = new HashMap<>();
    
    static {
        // 注册内置生成器
        register("name", NameGenerator.class);
        register("phone", PhoneNumberGenerator.class);
        register("email", EmailGenerator.class);
        register("age", AgeGenerator.class);
        register("gender", GenderGenerator.class);
        register("password", PasswordGenerator.class);
        register("account", AccountNameGenerator.class);
        register("uuid", UuidGenerator.class);
        register("ip", IpAddressGenerator.class);
        register("mac", MacAddressGenerator.class);
        register("domain", DomainNameGenerator.class);
        register("port", PortNumberGenerator.class);
        register("header", HttpHeaderGenerator.class);
        register("url", UrlGenerator.class);
        register("idcard", IdCardNumberGenerator.class);
        register("bankcard", BankCardNumberGenerator.class);
        register("uscc", UnifiedSocialCreditCodeGenerator.class);
        register("company", CompanyGenerator.class);
        register("occupation", OccupationGenerator.class);
        register("education", EducationLevelGenerator.class);
        register("marital_status", MaritalStatusGenerator.class);
        register("blood_type", BloodTypeGenerator.class);
        register("zodiac_sign", ZodiacSignGenerator.class);
        register("ethnicity", EthnicityGenerator.class);
        register("religion", ReligionGenerator.class);
        
        // Register numeric generators
        register("integer", IntegerGenerator.class);
        register("decimal", DecimalGenerator.class);
        register("currency", CurrencyGenerator.class);
        register("negative", NegativeNumberGenerator.class);
        register("scientific", ScientificNotationGenerator.class);
        register("high_precision_decimal", HighPrecisionDecimalGenerator.class);
        register("percentage_rate", PercentageRateGenerator.class);
        register("measurement_unit", MeasurementUnitCombinationGenerator.class);
        
        // Register temporal generators
        register("date", DateGenerator.class);
        register("time", TimeGenerator.class);
        register("timestamp", TimestampGenerator.class);
        register("cron", CronExpressionGenerator.class);
        
        // Register security generators
        register("sqlinjection", SqlInjectionPayloadGenerator.class);
        register("xss", XssAttackScriptGenerator.class);
        register("commandinjection", CommandInjectionGenerator.class);
        register("pathtraversal", PathTraversalGenerator.class);
        // Register text generators
        register("longtext", LongTextGenerator.class);
        register("richtext", RichTextGenerator.class);
        register("unicode_boundary", UnicodeBoundaryCharGenerator.class);
        register("multilingual_text", MultilingualTextGenerator.class);
        register("special_char", SpecialCharGenerator.class);
        register("binary_base64", BinaryBase64DataGenerator.class);
        
        // Register special scenario generators
        register("emptynull", EmptyNullValueGenerator.class);
        register("boundary", BoundaryExtremeValueGenerator.class);
        register("invalid_exception", InvalidExceptionDataGenerator.class);
        register("customizable_business_id", CustomizableBusinessIdGenerator.class);
        register("duplicate_data", DuplicateDataGenerator.class);
        register("sorted_data", SortedDataGenerator.class);
        register("concurrent_contention_data", ConcurrentContentionDataGenerator.class);
        
        // Register media generators
        register("image_header", ImageFileHeaderGenerator.class);
        register("file_extension", FileExtensionGenerator.class);
        register("file_size", FileSizeGenerator.class);
        register("image_dimensions", ImageDimensionsGenerator.class);
        register("simulated_media_file", SimulatedMediaFileGenerator.class);
        
        // Register location generators
        register("location", LocationDataGenerator.class);
        register("enhanced_location", EnhancedLocationDataGenerator.class);
        register("enhanced_address", EnhancedAddressGenerator.class);
        
        // Register enhanced identifier generators
        register("enhanced_uuid", EnhancedUuidGenerator.class);
        register("custom_id", CustomIdGenerator.class);
        register("license_plate", LicensePlateGenerator.class);
        register("enhanced_bank_card", EnhancedBankCardGenerator.class);
        register("address", AddressGenerator.class);
        register("org_code", OrganizationCodeGenerator.class);
        register("lei", LeiCodeGenerator.class);
        register("product_code", ProductCodeGenerator.class);
        
        // Register network device generators
        register("network_device", NetworkDeviceGenerator.class);
        
        // Register statistical distribution generators
        register("statistical_distribution", StatisticalDistributionGenerator.class);
        
        // Register advanced generators
        register("data_relationship", DataRelationshipGenerator.class);
        
        // Register structured data generators
        register("json_object", JsonObjectGenerator.class);
        register("xml_document", XmlDocumentGenerator.class);
        register("yaml_data", YamlDataGenerator.class);
        
        // Register enum generators
        register("http_status_code", HttpStatusCodeGenerator.class);
        register("business_status_code", BusinessStatusCodeGenerator.class);
        register("verification_code", VerificationCodeGenerator.class);
        register("landline_phone", LandlinePhoneGenerator.class);
        register("file_path", FilePathGenerator.class);
        register("mime_type", MimeTypeGenerator.class);
    }
    
    /**
     * 注册数据生成器
     * 
     * @param name 生成器名称
     * @param generatorClass 生成器类
     */
    public static void register(String name, Class<? extends DataGenerator> generatorClass) {
        GENERATORS.put(name, generatorClass);
    }
    
    /**
     * 检查生成器是否已注册
     * 
     * @param name 生成器名称
     * @return 如果已注册返回true，否则返回false
     */
    public static boolean isRegistered(String name) {
        return GENERATORS.containsKey(name);
    }
    
    /**
     * 根据名称创建数据生成器实例（带缓存和性能监控）
     * 
     * @param name 生成器名称
     * @return 数据生成器实例
     * @throws IllegalArgumentException 如果找不到生成器
     */
    @SuppressWarnings("unchecked")
    public static DataGenerator<?> createGenerator(String name) {
        // 尝试从缓存获取
        GeneratorCacheManager cacheManager = GeneratorCacheManager.getInstance();
        DataGenerator<?> cachedGenerator = cacheManager.getCachedGenerator(name);
        if (cachedGenerator != null) {
            return cachedGenerator;
        }
        
        Class<? extends DataGenerator> generatorClass = GENERATORS.get(name);
        if (generatorClass == null) {
            throw new IllegalArgumentException("Unknown generator: " + name);
        }
        
        try {
            DataGenerator<?> generator = generatorClass.getDeclaredConstructor().newInstance();
            
            // 缓存生成器实例
            cacheManager.cacheGenerator(name, generator);
            
            return generator;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create generator: " + name, e);
        }
    }
    
    /**
     * 创建带性能监控的生成器
     */
    public static DataGenerator<?> createMonitoredGenerator(String name) {
        DataGenerator<?> baseGenerator = createGenerator(name);
        return new MonitoredDataGenerator<>(baseGenerator, name);
    }
    
    /**
     * 创建带缓存的生成器
     */
    public static DataGenerator<?> createCachedGenerator(String name, GenerationContext context) {
        return new CachedDataGenerator<>(name, context);
    }
    
    /**
     * 获取所有已注册的生成器名称
     */
    public static Set<String> getRegisteredGeneratorNames() {
        return new HashSet<>(GENERATORS.keySet());
    }
    
    /**
     * 获取生成器数量统计
     */
    public static Map<String, Integer> getGeneratorStats() {
        Map<String, Integer> stats = new HashMap<>();
        
        // 按包名分组统计
        for (Class<? extends DataGenerator> generatorClass : GENERATORS.values()) {
            String packageName = generatorClass.getPackage().getName();
            String category = packageName.substring(packageName.lastIndexOf('.') + 1);
            stats.merge(category, 1, Integer::sum);
        }
        
        return stats;
    }
}
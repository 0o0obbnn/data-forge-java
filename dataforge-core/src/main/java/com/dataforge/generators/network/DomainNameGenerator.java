package com.dataforge.generators.network;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Domain name generator for generating random domain names with configuration support.
 * Supports up to 50 million unique domain names.
 */
public class DomainNameGenerator implements DataGenerator<String> {
    
    private static final String TLD_CONFIG_FILE = "/config/domain/tlds.txt";
    private static final String PREFIX_CONFIG_FILE = "/config/domain/domain_prefixes.txt";
    private static final String SUBDOMAIN_CONFIG_FILE = "/config/domain/subdomains.txt";
    
    private static final ConcurrentMap<String, Boolean> generatedDomains = new ConcurrentHashMap<>();
    private static final List<String> topLevelDomains = new ArrayList<>();
    private static final List<String> domainPrefixes = new ArrayList<>();
    private static final List<String> subdomainPrefixes = new ArrayList<>();
    
    private final boolean includeSubdomain;
    private final int maxAttempts;
    
    static {
        loadConfiguration();
    }
    
    /**
     * Creates a domain name generator with default settings.
     */
    public DomainNameGenerator() {
        this(false);
    }
    
    /**
     * Creates a domain name generator.
     * 
     * @param includeSubdomain whether to include a subdomain prefix
     */
    public DomainNameGenerator(boolean includeSubdomain) {
        this.includeSubdomain = includeSubdomain;
        this.maxAttempts = 1000; // Maximum attempts to generate unique domain
    }
    
    private static void loadConfiguration() {
        loadListFromFile(TLD_CONFIG_FILE, topLevelDomains, getDefaultTLDs());
        loadListFromFile(PREFIX_CONFIG_FILE, domainPrefixes, getDefaultPrefixes());
        loadListFromFile(SUBDOMAIN_CONFIG_FILE, subdomainPrefixes, getDefaultSubdomains());
    }
    
    private static void loadListFromFile(String fileName, List<String> list, List<String> defaults) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        DomainNameGenerator.class.getResourceAsStream(fileName), 
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    list.add(line);
                }
            }
        } catch (IOException | NullPointerException e) {
            // Fall back to defaults if file loading fails
            list.addAll(defaults);
        }
    }
    
    private static List<String> getDefaultTLDs() {
        return List.of("com", "org", "net", "edu", "gov", "mil", "int", "cn", "uk", "de", "io");
    }
    
    private static List<String> getDefaultPrefixes() {
        return List.of("my", "the", "best", "super", "great", "new", "tech", "web", "net", "e");
    }
    
    private static List<String> getDefaultSubdomains() {
        return List.of("www", "mail", "ftp", "blog", "shop", "api", "dev", "test", "cdn", "static");
    }
    
    /**
     * Gets the total number of unique domains generated.
     * 
     * @return the count of unique domains
     */
    public static int getUniqueDomainCount() {
        return generatedDomains.size();
    }
    
    /**
     * Clears the generated domains cache (for testing purposes).
     */
    public static void clearCache() {
        generatedDomains.clear();
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String domain = generateDomain(random);
            
            // Ensure uniqueness
            if (generatedDomains.putIfAbsent(domain, Boolean.TRUE) == null) {
                return domain;
            }
        }
        
        // If we couldn't generate unique domain after max attempts, add random suffix
        return generateUniqueWithSuffix(random);
    }
    
    private String generateDomain(Random random) {
        StringBuilder domain = new StringBuilder();
        
        // Add optional subdomain
        if (includeSubdomain && !subdomainPrefixes.isEmpty() && random.nextBoolean()) {
            domain.append(subdomainPrefixes.get(random.nextInt(subdomainPrefixes.size())))
                  .append(".");
        }
        
        // Generate main domain name
        String mainDomain = generateMainDomainName(random);
        domain.append(mainDomain);
        
        // Add TLD
        if (!topLevelDomains.isEmpty()) {
            domain.append(".").append(topLevelDomains.get(random.nextInt(topLevelDomains.size())));
        }
        
        return domain.toString();
    }
    
    private String generateMainDomainName(Random random) {
        StringBuilder name = new StringBuilder();
        
        // Use prefix from config or generate random
        if (!domainPrefixes.isEmpty() && random.nextBoolean()) {
            name.append(domainPrefixes.get(random.nextInt(domainPrefixes.size())));
        }
        
        // Add random suffix
        int suffixLength = 3 + random.nextInt(8); // 3-10 characters
        for (int i = 0; i < suffixLength; i++) {
            if (random.nextBoolean()) {
                name.append((char) ('a' + random.nextInt(26)));
            } else {
                name.append((char) ('0' + random.nextInt(10)));
            }
        }
        
        return name.toString();
    }
    
    private String generateUniqueWithSuffix(Random random) {
        String baseDomain = generateDomain(random);
        
        // Add random suffix to ensure uniqueness
        String suffix = "";
        for (int i = 0; i < 3; i++) {
            suffix += (char) ('a' + random.nextInt(26));
        }
        
        int hyphenIndex = baseDomain.lastIndexOf('.');
        if (hyphenIndex > 0) {
            return baseDomain.substring(0, hyphenIndex) + suffix + baseDomain.substring(hyphenIndex);
        }
        
        return baseDomain + suffix;
    }
}
package com.dataforge.generators.network;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * URL generator for generating web URLs.
 */
public class UrlGenerator implements DataGenerator<String> {
    
    private static final String[] PROTOCOLS = {"http", "https"};
    private static final String[] DOMAINS = {
        "example.com", "test.com", "demo.org", "sample.net", "website.io",
        "application.dev", "service.local", "platform.cloud"
    };
    private static final String[] PATHS = {
        "/home", "/about", "/contact", "/products", "/services",
        "/blog", "/news", "/api", "/v1", "/v2", "/user", "/admin"
    };
    private static final String[] PARAM_NAMES = {
        "id", "name", "category", "type", "page", "limit", "offset", "sort"
    };
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Select protocol
        String protocol = PROTOCOLS[random.nextInt(PROTOCOLS.length)];
        
        // Select domain
        String domain = DOMAINS[random.nextInt(DOMAINS.length)];
        
        // Build URL
        StringBuilder url = new StringBuilder(protocol);
        url.append("://");
        url.append(domain);
        
        // Add path (70% chance)
        if (random.nextDouble() < 0.7) {
            url.append(PATHS[random.nextInt(PATHS.length)]);
        }
        
        // Add query parameters (50% chance)
        if (random.nextDouble() < 0.5) {
            url.append("?");
            int paramCount = 1 + random.nextInt(3); // 1-3 parameters
            
            for (int i = 0; i < paramCount; i++) {
                if (i > 0) {
                    url.append("&");
                }
                
                String paramName = PARAM_NAMES[random.nextInt(PARAM_NAMES.length)];
                url.append(paramName).append("=");
                
                // Generate a simple value for the parameter
                if ("id".equals(paramName)) {
                    url.append(1 + random.nextInt(1000));
                } else if ("page".equals(paramName) || "limit".equals(paramName) || "offset".equals(paramName)) {
                    url.append(1 + random.nextInt(50));
                } else if ("sort".equals(paramName)) {
                    url.append(random.nextBoolean() ? "asc" : "desc");
                } else {
                    // For other parameters, generate a random string
                    int length = 3 + random.nextInt(8);
                    for (int j = 0; j < length; j++) {
                        url.append((char) ('a' + random.nextInt(26)));
                    }
                }
            }
        }
        
        return url.toString();
    }
}
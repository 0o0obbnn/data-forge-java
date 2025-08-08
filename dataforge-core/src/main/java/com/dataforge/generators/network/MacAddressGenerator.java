package com.dataforge.generators.network;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * MAC address generator for generating network device identifiers.
 */
public class MacAddressGenerator implements DataGenerator<String> {
    
    // Common OUI prefixes for major vendors (simplified list)
    private static final String[] OUI_PREFIXES = {
        "00:00:00", // Reserved
        "00:0A:95", // Apple
        "00:1A:11", // Google
        "00:50:56", // VMware
        "00:0C:29", // VMware
        "00:16:3E", // Xen
        "08:00:27", // PCS Systemtechnik GmbH (VirtualBox)
        "02:42:AC", // Docker
        "52:54:00", // QEMU/KVM
        "AC:DE:48"  // Private
    };
    
    private final boolean useRealOui;
    private final String separator;
    
    /**
     * Creates a MAC address generator with default settings.
     */
    public MacAddressGenerator() {
        this(true, ":");
    }
    
    /**
     * Creates a MAC address generator.
     * 
     * @param useRealOui whether to use real OUI prefixes or generate random ones
     * @param separator the separator character (":" or "-")
     */
    public MacAddressGenerator(boolean useRealOui, String separator) {
        this.useRealOui = useRealOui;
        this.separator = separator;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        StringBuilder macAddress = new StringBuilder();
        
        if (useRealOui && random.nextBoolean()) {
            // Use a real OUI prefix
            String oui = OUI_PREFIXES[random.nextInt(OUI_PREFIXES.length)];
            macAddress.append(oui);
        } else {
            // Generate random OUI
            for (int i = 0; i < 3; i++) {
                if (i > 0) {
                    macAddress.append(separator);
                }
                macAddress.append(String.format("%02X", random.nextInt(256)));
            }
        }
        
        // Generate the remaining 3 octets
        for (int i = 0; i < 3; i++) {
            macAddress.append(separator);
            macAddress.append(String.format("%02X", random.nextInt(256)));
        }
        
        return macAddress.toString();
    }
}
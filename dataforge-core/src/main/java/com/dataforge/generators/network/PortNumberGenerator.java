package com.dataforge.generators.network;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Port number generator for generating network port numbers.
 */
public class PortNumberGenerator implements DataGenerator<Integer> {
    
    // Common service ports
    public static final int[] COMMON_PORTS = {
        21, 22, 23, 25, 53, 80, 110, 143, 443, 993, 995, // Standard services
        1433, 1521, 3306, 5432, 6379, 27017, // Database ports
        8080, 8443, 9000, 9090 // Common application ports
    };
    
    private final boolean preferCommonPorts;
    
    /**
     * Creates a port number generator with default settings.
     */
    public PortNumberGenerator() {
        this(false);
    }
    
    /**
     * Creates a port number generator.
     * 
     * @param preferCommonPorts whether to prefer generating common service ports
     */
    public PortNumberGenerator(boolean preferCommonPorts) {
        this.preferCommonPorts = preferCommonPorts;
    }
    
    @Override
    public Integer generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Generate a port number (0-65535)
        if (preferCommonPorts && random.nextInt(3) == 0) { // 33% chance to pick a common port
            return COMMON_PORTS[random.nextInt(COMMON_PORTS.length)];
        } else {
            return random.nextInt(65536); // 0-65535 inclusive
        }
    }
}
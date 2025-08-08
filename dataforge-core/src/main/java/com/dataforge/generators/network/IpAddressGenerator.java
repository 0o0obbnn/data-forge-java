package com.dataforge.generators.network;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * IP address generator for generating IPv4 addresses.
 */
public class IpAddressGenerator implements DataGenerator<String> {
    
    public enum IpType {
        PUBLIC,  // Public IP addresses
        PRIVATE, // Private IP addresses (RFC 1918)
        ANY      // Any IP address
    }
    
    private final IpType type;
    
    public IpAddressGenerator() {
        this(IpType.ANY);
    }
    
    public IpAddressGenerator(IpType type) {
        this.type = type;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        switch (type) {
            case PUBLIC:
                return generatePublicIp(random);
            case PRIVATE:
                return generatePrivateIp(random);
            case ANY:
            default:
                // 20% chance of generating a private IP, 80% chance of public
                return random.nextDouble() < 0.2 ? generatePrivateIp(random) : generatePublicIp(random);
        }
    }
    
    private String generatePublicIp(Random random) {
        // Generate a public IP address (not in private ranges)
        int firstOctet;
        do {
            firstOctet = random.nextInt(256);
        } while (firstOctet == 0 || firstOctet == 10 || firstOctet == 127 || 
                 (firstOctet >= 172 && firstOctet <= 172) || 
                 (firstOctet >= 192 && firstOctet <= 192));
        
        // Special handling for 172.x.x.x and 192.168.x.x ranges
        if (firstOctet == 172) {
            // 172.16.0.0 to 172.31.255.255
            int secondOctet;
            do {
                secondOctet = random.nextInt(256);
            } while (secondOctet >= 16 && secondOctet <= 31);
            
            return firstOctet + "." + secondOctet + "." + 
                   random.nextInt(256) + "." + random.nextInt(256);
        } else if (firstOctet == 192) {
            // Not 192.168.x.x
            int secondOctet;
            do {
                secondOctet = random.nextInt(256);
            } while (secondOctet == 168);
            
            return firstOctet + "." + secondOctet + "." + 
                   random.nextInt(256) + "." + random.nextInt(256);
        } else {
            // Regular public IP
            return firstOctet + "." + random.nextInt(256) + "." + 
                   random.nextInt(256) + "." + random.nextInt(256);
        }
    }
    
    private String generatePrivateIp(Random random) {
        // Randomly select one of the private IP ranges:
        // 10.0.0.0 to 10.255.255.255
        // 172.16.0.0 to 172.31.255.255
        // 192.168.0.0 to 192.168.255.255
        int range = random.nextInt(3);
        
        switch (range) {
            case 0: // 10.x.x.x
                return "10." + random.nextInt(256) + "." + 
                       random.nextInt(256) + "." + random.nextInt(256);
            case 1: // 172.16.x.x to 172.31.x.x
                return "172." + (16 + random.nextInt(16)) + "." + 
                       random.nextInt(256) + "." + random.nextInt(256);
            case 2: // 192.168.x.x
            default:
                return "192.168." + random.nextInt(256) + "." + random.nextInt(256);
        }
    }
}
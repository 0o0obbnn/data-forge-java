package com.dataforge.generators.network;

import com.dataforge.core.GenerationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class MacAddressGeneratorTest {
    
    @Test
    public void testGenerateMacAddress() {
        MacAddressGenerator generator = new MacAddressGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Generate several MAC addresses
        Set<String> generatedMacs = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String mac = generator.generate(context);
            Assert.assertNotNull(mac);
            Assert.assertTrue(mac.length() > 0);
            
            // Check format (6 octets separated by colons)
            String[] parts = mac.split(":");
            Assert.assertEquals(parts.length, 6);
            
            // Check that each part is a valid hex value
            for (String part : parts) {
                Assert.assertTrue(part.matches("[0-9A-F]{2}"));
            }
            
            // Check for uniqueness
            Assert.assertTrue(generatedMacs.add(mac), "Duplicate MAC address generated: " + mac);
        }
    }
    
    @Test
    public void testGenerateMacAddressWithDashSeparator() {
        MacAddressGenerator generator = new MacAddressGenerator(false, "-"); // Don't use real OUI to ensure dash separator
        GenerationContext context = new GenerationContext(1);
        
        String mac = generator.generate(context);
        Assert.assertNotNull(mac);
        Assert.assertTrue(mac.contains("-"));
        Assert.assertFalse(mac.contains(":")); // Ensure no colons are present
        
        // Check format (6 octets separated by dashes)
        String[] parts = mac.split("-");
        Assert.assertEquals(parts.length, 6);
        
        // Check that each part is a valid hex value
        for (String part : parts) {
            Assert.assertTrue(part.matches("[0-9A-F]{2}"));
        }
    }
}
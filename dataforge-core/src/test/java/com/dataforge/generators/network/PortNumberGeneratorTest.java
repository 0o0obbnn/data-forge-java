package com.dataforge.generators.network;

import com.dataforge.core.GenerationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PortNumberGeneratorTest {
    
    @Test
    public void testGeneratePortNumber() {
        PortNumberGenerator generator = new PortNumberGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Generate several port numbers
        for (int i = 0; i < 100; i++) {
            Integer port = generator.generate(context);
            Assert.assertNotNull(port);
            
            // Check that port is in valid range (0-65535)
            Assert.assertTrue(port >= 0 && port <= 65535);
        }
    }
    
    @Test
    public void testGeneratePortNumberWithCommonPorts() {
        PortNumberGenerator generator = new PortNumberGenerator(true);
        GenerationContext context = new GenerationContext(1);
        
        // Generate several port numbers
        int commonPortCount = 0;
        for (int i = 0; i < 100; i++) {
            Integer port = generator.generate(context);
            Assert.assertNotNull(port);
            
            // Check that port is in valid range (0-65535)
            Assert.assertTrue(port >= 0 && port <= 65535);
            
            // Count common ports
            for (int commonPort : PortNumberGenerator.COMMON_PORTS) {
                if (port == commonPort) {
                    commonPortCount++;
                    break;
                }
            }
        }
        
        // At least some should be common ports
        Assert.assertTrue(commonPortCount > 0);
    }
}
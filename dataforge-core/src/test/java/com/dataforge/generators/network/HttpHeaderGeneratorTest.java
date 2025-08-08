package com.dataforge.generators.network;

import com.dataforge.core.GenerationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class HttpHeaderGeneratorTest {
    
    @Test
    public void testGenerateHttpHeader() {
        HttpHeaderGenerator generator = new HttpHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Generate several HTTP headers
        for (int i = 0; i < 100; i++) {
            String header = generator.generate(context);
            Assert.assertNotNull(header);
            Assert.assertTrue(header.length() > 0);
            
            // Check that it contains a colon separator
            Assert.assertTrue(header.contains(": "));
            
            // Split into name and value
            String[] parts = header.split(": ", 2);
            Assert.assertEquals(parts.length, 2);
            Assert.assertTrue(parts[0].length() > 0);
            Assert.assertTrue(parts[1].length() > 0);
        }
    }
    
    @Test
    public void testGenerateHttpHeaderWithSpecificHeaders() {
        HttpHeaderGenerator generator = new HttpHeaderGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Generate headers and check for specific ones
        boolean foundUserAgent = false;
        boolean foundContentType = false;
        boolean foundContentLength = false;
        
        for (int i = 0; i < 200; i++) {
            String header = generator.generate(context);
            Assert.assertNotNull(header);
            Assert.assertTrue(header.length() > 0);
            
            if (header.startsWith("User-Agent: ")) {
                foundUserAgent = true;
            } else if (header.startsWith("Content-Type: ")) {
                foundContentType = true;
            } else if (header.startsWith("Content-Length: ")) {
                foundContentLength = true;
                // Check that Content-Length value is a number
                String[] parts = header.split(": ", 2);
                Assert.assertTrue(parts[1].matches("\\d+"));
            }
        }
        
        // Assert that we found at least some of the specific headers
        Assert.assertTrue(foundUserAgent);
        Assert.assertTrue(foundContentType);
        Assert.assertTrue(foundContentLength);
    }
}
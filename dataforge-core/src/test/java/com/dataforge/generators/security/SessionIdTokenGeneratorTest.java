package com.dataforge.generators.security;

import com.dataforge.core.GenerationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Base64;

public class SessionIdTokenGeneratorTest {
    
    @Test
    public void testGenerateSessionId() {
        SessionIdTokenGenerator generator = new SessionIdTokenGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Generate several session IDs
        for (int i = 0; i < 100; i++) {
            String sessionId = generator.generate(context);
            Assert.assertNotNull(sessionId);
            Assert.assertTrue(sessionId.length() > 0);
            
            // Default length should be 32
            Assert.assertEquals(sessionId.length(), 32);
            
            // Should contain only alphanumeric characters
            Assert.assertTrue(sessionId.matches("[A-Za-z0-9]+"));
        }
    }
    
    @Test
    public void testGenerateSessionIdWithCustomLength() {
        SessionIdTokenGenerator generator = new SessionIdTokenGenerator(64, false);
        GenerationContext context = new GenerationContext(1);
        
        // Generate several session IDs with custom length
        for (int i = 0; i < 50; i++) {
            String sessionId = generator.generate(context);
            Assert.assertNotNull(sessionId);
            Assert.assertTrue(sessionId.length() > 0);
            
            // Length should be 64
            Assert.assertEquals(sessionId.length(), 64);
            
            // Should contain only alphanumeric characters
            Assert.assertTrue(sessionId.matches("[A-Za-z0-9]+"));
        }
    }
    
    @Test
    public void testGenerateJwtToken() {
        SessionIdTokenGenerator generator = new SessionIdTokenGenerator(32, true);
        GenerationContext context = new GenerationContext(1);
        
        // Generate several JWT-like tokens
        for (int i = 0; i < 50; i++) {
            String jwtToken = generator.generate(context);
            Assert.assertNotNull(jwtToken);
            Assert.assertTrue(jwtToken.length() > 0);
            
            // Should have three parts separated by dots
            String[] parts = jwtToken.split("\\.");
            Assert.assertEquals(parts.length, 3);
            
            // Each part should be base64 encoded
            for (String part : parts) {
                try {
                    Base64.getDecoder().decode(part);
                } catch (IllegalArgumentException e) {
                    Assert.fail("Part is not valid Base64: " + part);
                }
            }
        }
    }
}

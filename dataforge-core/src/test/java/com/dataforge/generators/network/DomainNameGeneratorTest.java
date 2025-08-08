package com.dataforge.generators.network;

import com.dataforge.core.GenerationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class DomainNameGeneratorTest {
    
    @Test
    public void testGenerateDomainName() {
        DomainNameGenerator generator = new DomainNameGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Generate several domain names
        Set<String> generatedDomains = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String domain = generator.generate(context);
            Assert.assertNotNull(domain);
            Assert.assertTrue(domain.length() > 0);
            
            // Check that it contains a TLD
            Assert.assertTrue(domain.contains("."));
            
            // Check that it doesn't start or end with a hyphen
            Assert.assertFalse(domain.startsWith("-"));
            Assert.assertFalse(domain.endsWith("-"));
            
            // Check that it doesn't contain consecutive hyphens
            Assert.assertFalse(domain.contains("--"));
            
            // Check for valid characters (alphanumeric and hyphens, with at least one dot)
            Assert.assertTrue(domain.matches("[a-z0-9-]+\\.[a-z0-9-.]+"));
            
            // Check for uniqueness
            Assert.assertTrue(generatedDomains.add(domain), "Duplicate domain generated: " + domain);
        }
    }
    
    @Test
    public void testGenerateDomainNameWithSubdomain() {
        DomainNameGenerator generator = new DomainNameGenerator(true);
        GenerationContext context = new GenerationContext(1);
        
        // Generate several domain names with subdomains
        for (int i = 0; i < 50; i++) {
            String domain = generator.generate(context);
            Assert.assertNotNull(domain);
            Assert.assertTrue(domain.length() > 0);
            
            // Check that it contains at least one dot
            Assert.assertTrue(domain.contains("."));
            
            // It might have multiple dots if it has a subdomain
            String[] parts = domain.split("\\.");
            Assert.assertTrue(parts.length >= 2);
            
            // Check that it doesn't start or end with a hyphen
            Assert.assertFalse(domain.startsWith("-"));
            Assert.assertFalse(domain.endsWith("-"));
            
            // Check that it doesn't contain consecutive hyphens
            Assert.assertFalse(domain.contains("--"));
        }
    }
}
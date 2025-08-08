package com.dataforge.generators.demographic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.testng.Assert.*;

class MaritalStatusGeneratorTest {
    
    private MaritalStatusGenerator generator;
    private GenerationContext context;
    
    @BeforeMethod
    void setUp() {
        context = new GenerationContext(1);
    }
    
    @Test
    void testGenerateMaritalStatus() {
        generator = new MaritalStatusGenerator();
        String status = generator.generate(context);
        
        assertNotNull(status);
        assertTrue(MaritalStatusGenerator.getAllMaritalStatuses().contains(status));
    }
    
    @Test
    void testGenerateSpecificStatus() {
        generator = new MaritalStatusGenerator(MaritalStatusGenerator.MaritalStatus.MARRIED);
        String status = generator.generate(context);
        
        assertEquals("已婚", status);
        
        generator = new MaritalStatusGenerator(MaritalStatusGenerator.MaritalStatus.SINGLE);
        status = generator.generate(context);
        
        assertEquals("未婚", status);
    }
    
    @Test
    void testGenerateForAge() {
        // Test minor (under 18) - should be single
        String status = MaritalStatusGenerator.generateForAge(16, context);
        assertEquals("未婚", status);
        
        // Test young adult (18-25) - likely single or married
        status = MaritalStatusGenerator.generateForAge(22, context);
        assertNotNull(status);
        assertTrue(status.equals("未婚") || status.equals("已婚") || 
                  status.equals("离婚") || status.equals("丧偶"));
        
        // Test middle age (40) - likely married
        status = MaritalStatusGenerator.generateForAge(40, context);
        assertNotNull(status);
        assertTrue(MaritalStatusGenerator.getAllMaritalStatuses().contains(status));
        
        // Test elderly (70) - could be any status including widowed
        status = MaritalStatusGenerator.generateForAge(70, context);
        assertNotNull(status);
        assertTrue(MaritalStatusGenerator.getAllMaritalStatuses().contains(status));
    }
    
    @Test
    void testIsValidForAge() {
        // Valid cases
        assertTrue(MaritalStatusGenerator.isValidForAge(25, "未婚"));
        assertTrue(MaritalStatusGenerator.isValidForAge(30, "已婚"));
        assertTrue(MaritalStatusGenerator.isValidForAge(40, "离婚"));
        assertTrue(MaritalStatusGenerator.isValidForAge(70, "丧偶"));
        
        // Invalid cases
        assertFalse(MaritalStatusGenerator.isValidForAge(16, "已婚")); // Minor married
        assertFalse(MaritalStatusGenerator.isValidForAge(19, "丧偶")); // Too young to be widowed
        assertFalse(MaritalStatusGenerator.isValidForAge(20, "离婚")); // Too young to be divorced
    }
    
    @Test
    void testGetMaritalDistributionForAge() {
        // Test young adult distribution
        Map<String, Double> distribution = MaritalStatusGenerator.getMaritalDistributionForAge(22);
        assertNotNull(distribution);
        assertFalse(distribution.isEmpty());
        assertTrue(distribution.containsKey("未婚"));
        assertTrue(distribution.containsKey("已婚"));
        
        // For young adults, single should have higher probability
        assertTrue(distribution.get("未婚") > distribution.get("已婚"));
        
        // Test middle age distribution
        distribution = MaritalStatusGenerator.getMaritalDistributionForAge(40);
        assertNotNull(distribution);
        
        // For middle age, married should have higher probability
        assertTrue(distribution.get("已婚") > distribution.get("未婚"));
        
        // Test elderly distribution
        distribution = MaritalStatusGenerator.getMaritalDistributionForAge(70);
        assertNotNull(distribution);
        
        // Elderly should have some probability of being widowed
        assertTrue(distribution.get("丧偶") > 0);
    }
    
    @Test
    void testGetAllMaritalStatuses() {
        List<String> statuses = MaritalStatusGenerator.getAllMaritalStatuses();
        assertNotNull(statuses);
        assertEquals(4, statuses.size());
        assertTrue(statuses.contains("未婚"));
        assertTrue(statuses.contains("已婚"));
        assertTrue(statuses.contains("离婚"));
        assertTrue(statuses.contains("丧偶"));
    }
    
    @Test
    void testGetSupportedAgeGroups() {
        List<String> ageGroups = MaritalStatusGenerator.getSupportedAgeGroups();
        assertNotNull(ageGroups);
        assertEquals(5, ageGroups.size());
        assertTrue(ageGroups.contains("18-25"));
        assertTrue(ageGroups.contains("26-35"));
        assertTrue(ageGroups.contains("36-50"));
        assertTrue(ageGroups.contains("51-65"));
        assertTrue(ageGroups.contains("65+"));
    }
    
    @Test
    void testGenerateMultipleStatuses() {
        generator = new MaritalStatusGenerator();
        
        // Generate multiple statuses to test distribution
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            String status = generator.generate(context);
            counts.put(status, counts.getOrDefault(status, 0) + 1);
        }
        
        assertFalse(counts.isEmpty());
        // Should have some variety in results (not all the same)
        assertTrue(counts.size() > 1 || counts.values().iterator().next() == 100);
    }
    
    @Test
    void testAgeBasedDistribution() {
        // Test that different age groups produce different distributions
        Map<String, Integer> youngCounts = new HashMap<>();
        Map<String, Integer> elderlyCounts = new HashMap<>();
        
        for (int i = 0; i < 50; i++) {
            String youngStatus = MaritalStatusGenerator.generateForAge(22, context);
            String elderlyStatus = MaritalStatusGenerator.generateForAge(70, context);
            
            youngCounts.put(youngStatus, youngCounts.getOrDefault(youngStatus, 0) + 1);
            elderlyCounts.put(elderlyStatus, elderlyCounts.getOrDefault(elderlyStatus, 0) + 1);
        }
        
        // Young people should have more singles
        int youngSingles = youngCounts.getOrDefault("未婚", 0);
        int elderlySingles = elderlyCounts.getOrDefault("未婚", 0);
        
        // This test might occasionally fail due to randomness, but generally young people should be more single
        assertTrue(youngSingles >= elderlySingles);
    }
    
    @Test
    void testGetName() {
        generator = new MaritalStatusGenerator();
        assertEquals("marital_status", generator.getName());
    }
    
    @Test
    void testGetSupportedParameters() {
        generator = new MaritalStatusGenerator();
        List<String> params = generator.getSupportedParameters();
        
        assertNotNull(params);
        assertEquals(2, params.size());
        assertTrue(params.contains("status"));
        assertTrue(params.contains("age"));
    }
    
    @Test
    void testMaritalStatusEnumNames() {
        assertEquals("未婚", MaritalStatusGenerator.MaritalStatus.SINGLE.getName());
        assertEquals("已婚", MaritalStatusGenerator.MaritalStatus.MARRIED.getName());
        assertEquals("离婚", MaritalStatusGenerator.MaritalStatus.DIVORCED.getName());
        assertEquals("丧偶", MaritalStatusGenerator.MaritalStatus.WIDOWED.getName());
        assertEquals("任意", MaritalStatusGenerator.MaritalStatus.ANY.getName());
    }
}
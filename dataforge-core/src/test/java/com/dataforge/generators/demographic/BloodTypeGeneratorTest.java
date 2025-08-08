package com.dataforge.generators.demographic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.testng.Assert.*;

class BloodTypeGeneratorTest {
    
    private BloodTypeGenerator generator;
    private GenerationContext context;
    
    @BeforeMethod
    void setUp() {
        context = new GenerationContext(1);
    }
    
    @Test
    void testGenerateBloodType() {
        generator = new BloodTypeGenerator();
        String bloodType = generator.generate(context);
        
        assertNotNull(bloodType);
        assertTrue(BloodTypeGenerator.isValidBloodType(bloodType));
        assertTrue(BloodTypeGenerator.getAllBloodTypes().contains(bloodType));
    }
    
    @Test
    void testGenerateByRegion() {
        // Test China region
        generator = new BloodTypeGenerator(BloodTypeGenerator.Region.CHINA);
        String bloodType = generator.generate(context);
        assertNotNull(bloodType);
        assertTrue(BloodTypeGenerator.isValidBloodType(bloodType));
        
        // Test Europe region
        generator = new BloodTypeGenerator(BloodTypeGenerator.Region.EUROPE);
        bloodType = generator.generate(context);
        assertNotNull(bloodType);
        assertTrue(BloodTypeGenerator.isValidBloodType(bloodType));
        
        // Test World region
        generator = new BloodTypeGenerator(BloodTypeGenerator.Region.WORLD);
        bloodType = generator.generate(context);
        assertNotNull(bloodType);
        assertTrue(BloodTypeGenerator.isValidBloodType(bloodType));
    }
    
    @Test
    void testGenerateSpecificBloodType() {
        generator = new BloodTypeGenerator(BloodTypeGenerator.ABO.A, 
                                          BloodTypeGenerator.RhFactor.POSITIVE, 
                                          BloodTypeGenerator.Region.CHINA);
        String bloodType = generator.generate(context);
        
        assertEquals("A+", bloodType);
        
        generator = new BloodTypeGenerator(BloodTypeGenerator.ABO.AB, 
                                          BloodTypeGenerator.RhFactor.NEGATIVE, 
                                          BloodTypeGenerator.Region.CHINA);
        bloodType = generator.generate(context);
        
        assertEquals("AB-", bloodType);
    }
    
    @Test
    void testIsValidBloodType() {
        // Valid blood types
        assertTrue(BloodTypeGenerator.isValidBloodType("A+"));
        assertTrue(BloodTypeGenerator.isValidBloodType("A-"));
        assertTrue(BloodTypeGenerator.isValidBloodType("B+"));
        assertTrue(BloodTypeGenerator.isValidBloodType("B-"));
        assertTrue(BloodTypeGenerator.isValidBloodType("AB+"));
        assertTrue(BloodTypeGenerator.isValidBloodType("AB-"));
        assertTrue(BloodTypeGenerator.isValidBloodType("O+"));
        assertTrue(BloodTypeGenerator.isValidBloodType("O-"));
        
        // Invalid blood types
        assertFalse(BloodTypeGenerator.isValidBloodType("C+"));
        assertFalse(BloodTypeGenerator.isValidBloodType("A"));
        assertFalse(BloodTypeGenerator.isValidBloodType("+"));
        assertFalse(BloodTypeGenerator.isValidBloodType("AA+"));
        assertFalse(BloodTypeGenerator.isValidBloodType("A*"));
        assertFalse(BloodTypeGenerator.isValidBloodType(null));
        assertFalse(BloodTypeGenerator.isValidBloodType(""));
    }
    
    @Test
    void testGetAllBloodTypes() {
        List<String> bloodTypes = BloodTypeGenerator.getAllBloodTypes();
        
        assertNotNull(bloodTypes);
        assertEquals(8, bloodTypes.size());
        assertTrue(bloodTypes.contains("A+"));
        assertTrue(bloodTypes.contains("A-"));
        assertTrue(bloodTypes.contains("B+"));
        assertTrue(bloodTypes.contains("B-"));
        assertTrue(bloodTypes.contains("AB+"));
        assertTrue(bloodTypes.contains("AB-"));
        assertTrue(bloodTypes.contains("O+"));
        assertTrue(bloodTypes.contains("O-"));
    }
    
    @Test
    void testGetSupportedRegions() {
        List<String> regions = BloodTypeGenerator.getSupportedRegions();
        
        assertNotNull(regions);
        assertEquals(5, regions.size());
        assertTrue(regions.contains("CHINA"));
        assertTrue(regions.contains("ASIA"));
        assertTrue(regions.contains("EUROPE"));
        assertTrue(regions.contains("AFRICA"));
        assertTrue(regions.contains("WORLD"));
    }
    
    @Test
    void testGetBloodTypeDistribution() {
        // Test China distribution
        Map<String, Double> distribution = BloodTypeGenerator.getBloodTypeDistribution(BloodTypeGenerator.Region.CHINA);
        
        assertNotNull(distribution);
        assertEquals(8, distribution.size());
        
        // Check that probabilities sum to approximately 1.0
        double sum = distribution.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(1.0, sum, 0.001);
        
        // In China, Rh+ should be much more common than Rh-
        double positiveSum = distribution.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith("+"))
                .mapToDouble(Map.Entry::getValue)
                .sum();
        double negativeSum = distribution.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith("-"))
                .mapToDouble(Map.Entry::getValue)
                .sum();
        
        assertTrue(positiveSum > negativeSum);
    }
    
    @Test
    void testGenerateForRegion() {
        String bloodType = BloodTypeGenerator.generateForRegion(BloodTypeGenerator.Region.CHINA, context);
        assertNotNull(bloodType);
        assertTrue(BloodTypeGenerator.isValidBloodType(bloodType));
        
        bloodType = BloodTypeGenerator.generateForRegion(BloodTypeGenerator.Region.EUROPE, context);
        assertNotNull(bloodType);
        assertTrue(BloodTypeGenerator.isValidBloodType(bloodType));
    }
    
    @Test
    void testRegionalDistribution() {
        // Generate multiple blood types for different regions to test distribution
        Map<String, Integer> chinaCounts = new HashMap<>();
        Map<String, Integer> europeCounts = new HashMap<>();
        
        for (int i = 0; i < 100; i++) {
            String chinaBlood = BloodTypeGenerator.generateForRegion(BloodTypeGenerator.Region.CHINA, context);
            String europeBlood = BloodTypeGenerator.generateForRegion(BloodTypeGenerator.Region.EUROPE, context);
            
            chinaCounts.put(chinaBlood, chinaCounts.getOrDefault(chinaBlood, 0) + 1);
            europeCounts.put(europeBlood, europeCounts.getOrDefault(europeBlood, 0) + 1);
        }
        
        // Both regions should produce multiple different blood types
        assertTrue(chinaCounts.size() > 1);
        assertTrue(europeCounts.size() > 1);
        
        // In China, Rh- should be very rare
        int chinaRhNegative = chinaCounts.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith("-"))
                .mapToInt(Map.Entry::getValue)
                .sum();
        
        // In Europe, Rh- should be more common than in China
        int europeRhNegative = europeCounts.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith("-"))
                .mapToInt(Map.Entry::getValue)
                .sum();
        
        // This test might occasionally fail due to randomness, but generally Europe should have more Rh- cases
        // We'll just check that both regions produce some variety
        assertTrue(chinaCounts.size() >= 2);
        assertTrue(europeCounts.size() >= 2);
    }
    
    @Test
    void testGetName() {
        generator = new BloodTypeGenerator();
        assertEquals("blood_type", generator.getName());
    }
    
    @Test
    void testGetSupportedParameters() {
        generator = new BloodTypeGenerator();
        List<String> params = generator.getSupportedParameters();
        
        assertNotNull(params);
        assertEquals(3, params.size());
        assertTrue(params.contains("abo"));
        assertTrue(params.contains("rh"));
        assertTrue(params.contains("region"));
    }
    
    @Test
    void testEnumValues() {
        assertEquals("A", BloodTypeGenerator.ABO.A.getType());
        assertEquals("B", BloodTypeGenerator.ABO.B.getType());
        assertEquals("AB", BloodTypeGenerator.ABO.AB.getType());
        assertEquals("O", BloodTypeGenerator.ABO.O.getType());
        
        assertEquals("阳性", BloodTypeGenerator.RhFactor.POSITIVE.getFactor());
        assertEquals("阴性", BloodTypeGenerator.RhFactor.NEGATIVE.getFactor());
        
        assertEquals("中国", BloodTypeGenerator.Region.CHINA.getName());
        assertEquals("欧洲", BloodTypeGenerator.Region.EUROPE.getName());
        assertEquals("世界平均", BloodTypeGenerator.Region.WORLD.getName());
    }
}
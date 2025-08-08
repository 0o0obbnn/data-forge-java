package com.dataforge.generators.demographic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

class EducationLevelGeneratorTest {
    
    private EducationLevelGenerator generator;
    private GenerationContext context;
    
    @BeforeMethod
    void setUp() {
        context = new GenerationContext(1);
    }
    
    @Test
    void testGenerateEducationLevel() {
        generator = new EducationLevelGenerator();
        String education = generator.generate(context);
        
        assertNotNull(education);
        assertTrue(EducationLevelGenerator.getAllEducationLevels().contains(education));
    }
    
    @Test
    void testGenerateSpecificLevel() {
        generator = new EducationLevelGenerator(EducationLevelGenerator.Level.BACHELOR);
        String education = generator.generate(context);
        
        assertEquals("本科", education);
    }
    
    @Test
    void testUniformDistribution() {
        generator = new EducationLevelGenerator(EducationLevelGenerator.Level.ANY, 
                                                EducationLevelGenerator.Distribution.UNIFORM);
        
        // Generate multiple samples to test distribution
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            String education = generator.generate(context);
            counts.put(education, counts.getOrDefault(education, 0) + 1);
        }
        
        assertFalse(counts.isEmpty());
        assertTrue(counts.size() > 1); // Should have multiple different levels
    }
    
    @Test
    void testWeightedDistribution() {
        Map<EducationLevelGenerator.Level, Double> weights = new HashMap<>();
        weights.put(EducationLevelGenerator.Level.HIGH_SCHOOL, 0.8);
        weights.put(EducationLevelGenerator.Level.BACHELOR, 0.2);
        
        generator = new EducationLevelGenerator(EducationLevelGenerator.Level.ANY, 
                                                EducationLevelGenerator.Distribution.WEIGHTED, 
                                                weights);
        
        // Generate multiple samples
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            String education = generator.generate(context);
            counts.put(education, counts.getOrDefault(education, 0) + 1);
        }
        
        // High school should be more frequent than bachelor
        assertTrue(counts.getOrDefault("高中", 0) > counts.getOrDefault("本科", 0));
    }
    
    @Test
    void testValidateAge() {
        assertTrue(EducationLevelGenerator.isValidForAge(25, "本科"));
        assertTrue(EducationLevelGenerator.isValidForAge(30, "博士"));
        assertFalse(EducationLevelGenerator.isValidForAge(15, "本科"));
        assertFalse(EducationLevelGenerator.isValidForAge(5, "小学"));
        assertTrue(EducationLevelGenerator.isValidForAge(6, "小学"));
    }
    
    @Test
    void testGenerateForAge() {
        // Test age 10 - should be primary or junior high
        String education = EducationLevelGenerator.generateForAge(10, context);
        assertTrue(education.equals("小学") || education.equals("初中"));
        
        // Test age 20 - should not be PhD
        education = EducationLevelGenerator.generateForAge(20, context);
        assertNotEquals("博士", education);
        
        // Test age 30 - can be any level
        education = EducationLevelGenerator.generateForAge(30, context);
        assertNotNull(education);
        assertTrue(EducationLevelGenerator.getAllEducationLevels().contains(education));
    }
    
    @Test
    void testGetAllEducationLevels() {
        List<String> levels = EducationLevelGenerator.getAllEducationLevels();
        assertNotNull(levels);
        assertEquals(7, levels.size());
        assertTrue(levels.contains("小学"));
        assertTrue(levels.contains("初中"));
        assertTrue(levels.contains("高中"));
        assertTrue(levels.contains("大专"));
        assertTrue(levels.contains("本科"));
        assertTrue(levels.contains("硕士"));
        assertTrue(levels.contains("博士"));
    }
    
    @Test
    void testGetName() {
        generator = new EducationLevelGenerator();
        assertEquals("education_level", generator.getName());
    }
    
    @Test
    void testGetSupportedParameters() {
        generator = new EducationLevelGenerator();
        List<String> params = generator.getSupportedParameters();
        
        assertNotNull(params);
        assertEquals(3, params.size());
        assertTrue(params.contains("levels"));
        assertTrue(params.contains("distribution"));
        assertTrue(params.contains("weights"));
    }
    
    @Test
    void testInvalidWeights() {
        // Test with weights that don't sum to 1.0
        Map<EducationLevelGenerator.Level, Double> weights = new HashMap<>();
        weights.put(EducationLevelGenerator.Level.HIGH_SCHOOL, 0.3);
        weights.put(EducationLevelGenerator.Level.BACHELOR, 0.2);
        
        generator = new EducationLevelGenerator(EducationLevelGenerator.Level.ANY, 
                                                EducationLevelGenerator.Distribution.WEIGHTED, 
                                                weights);
        
        // Should still work (weights are normalized internally)
        String education = generator.generate(context);
        assertNotNull(education);
    }
}
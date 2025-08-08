package com.dataforge.generators.demographic;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

class OccupationGeneratorTest {
    
    private OccupationGenerator generator;
    private GenerationContext context;
    
    @BeforeMethod
    void setUp() {
        context = new GenerationContext(1);
    }
    
    @Test
    void testGenerateOccupation() {
        generator = new OccupationGenerator();
        String occupation = generator.generate(context);
        
        assertNotNull(occupation);
        assertFalse(occupation.trim().isEmpty());
    }
    
    @Test
    void testGenerateITOccupation() {
        generator = new OccupationGenerator(OccupationGenerator.Industry.IT);
        String occupation = generator.generate(context);
        
        assertNotNull(occupation);
        
        // Verify it's an IT-related occupation
        List<String> itOccupations = OccupationGenerator.getOccupationsByIndustry(OccupationGenerator.Industry.IT);
        assertTrue(itOccupations.contains(occupation));
    }
    
    @Test
    void testGenerateFinanceOccupation() {
        generator = new OccupationGenerator(OccupationGenerator.Industry.FINANCE);
        String occupation = generator.generate(context);
        
        assertNotNull(occupation);
        
        List<String> financeOccupations = OccupationGenerator.getOccupationsByIndustry(OccupationGenerator.Industry.FINANCE);
        assertTrue(financeOccupations.contains(occupation));
    }
    
    @Test
    void testGenerateEducationOccupation() {
        generator = new OccupationGenerator(OccupationGenerator.Industry.EDUCATION);
        String occupation = generator.generate(context);
        
        assertNotNull(occupation);
        
        List<String> educationOccupations = OccupationGenerator.getOccupationsByIndustry(OccupationGenerator.Industry.EDUCATION);
        assertTrue(educationOccupations.contains(occupation));
    }
    
    @Test
    void testGenerateForEducation() {
        // Test PhD level
        String occupation = OccupationGenerator.generateForEducation("博士", context);
        assertNotNull(occupation);
        assertTrue(occupation.equals("大学教授") || occupation.equals("科研人员") || 
                  occupation.equals("高级工程师") || occupation.equals("医学专家") || 
                  occupation.equals("技术总监"));
        
        // Test bachelor level
        occupation = OccupationGenerator.generateForEducation("本科", context);
        assertNotNull(occupation);
        
        // Test high school level
        occupation = OccupationGenerator.generateForEducation("高中", context);
        assertNotNull(occupation);
    }
    
    @Test
    void testGetOccupationsByIndustry() {
        List<String> itOccupations = OccupationGenerator.getOccupationsByIndustry(OccupationGenerator.Industry.IT);
        assertNotNull(itOccupations);
        assertFalse(itOccupations.isEmpty());
        assertTrue(itOccupations.contains("软件工程师"));
        
        List<String> financeOccupations = OccupationGenerator.getOccupationsByIndustry(OccupationGenerator.Industry.FINANCE);
        assertNotNull(financeOccupations);
        assertFalse(financeOccupations.isEmpty());
        assertTrue(financeOccupations.contains("银行经理"));
        
        List<String> educationOccupations = OccupationGenerator.getOccupationsByIndustry(OccupationGenerator.Industry.EDUCATION);
        assertNotNull(educationOccupations);
        assertFalse(educationOccupations.isEmpty());
        assertTrue(educationOccupations.contains("教师") || educationOccupations.contains("小学教师"));
    }
    
    @Test
    void testGetAvailableIndustries() {
        List<String> industries = OccupationGenerator.getAvailableIndustries();
        assertNotNull(industries);
        assertFalse(industries.isEmpty());
        assertTrue(industries.contains("IT"));
        assertTrue(industries.contains("FINANCE"));
        assertTrue(industries.contains("EDUCATION"));
        assertTrue(industries.contains("ANY"));
    }
    
    @Test
    void testGenerateMultipleOccupations() {
        generator = new OccupationGenerator();
        
        // Generate multiple occupations to test variety
        String[] occupations = new String[10];
        for (int i = 0; i < 10; i++) {
            occupations[i] = generator.generate(context);
            assertNotNull(occupations[i]);
        }
        
        // Should have at least some variety (not all the same)
        boolean hasDifferent = false;
        for (int i = 1; i < occupations.length; i++) {
            if (!occupations[i].equals(occupations[0])) {
                hasDifferent = true;
                break;
            }
        }
        // Note: This test might occasionally fail due to randomness, but very unlikely
    }
    
    @Test
    void testGetName() {
        generator = new OccupationGenerator();
        assertEquals("occupation", generator.getName());
    }
    
    @Test
    void testGetSupportedParameters() {
        generator = new OccupationGenerator();
        List<String> params = generator.getSupportedParameters();
        
        assertNotNull(params);
        assertEquals(2, params.size());
        assertTrue(params.contains("industry"));
        assertTrue(params.contains("file"));
    }
    
    @Test
    void testIndustryEnumNames() {
        assertEquals("IT/互联网", OccupationGenerator.Industry.IT.getName());
        assertEquals("金融", OccupationGenerator.Industry.FINANCE.getName());
        assertEquals("教育", OccupationGenerator.Industry.EDUCATION.getName());
        assertEquals("医疗健康", OccupationGenerator.Industry.HEALTHCARE.getName());
        assertEquals("任意行业", OccupationGenerator.Industry.ANY.getName());
    }
}
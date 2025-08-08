package com.dataforge.generators.location;

import com.dataforge.core.GenerationContext;
import com.dataforge.generators.location.EnhancedLocationDataGenerator.AddressComponent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

class EnhancedLocationDataGeneratorTest {
    
    private EnhancedLocationDataGenerator generator;
    private GenerationContext context;
    
    @BeforeMethod
    void setUp() {
        EnhancedLocationDataGenerator.clearCache();
        context = new GenerationContext(1);
    }
    
    @Test
    void testGenerateFullAddress() {
        generator = new EnhancedLocationDataGenerator(AddressComponent.FULL_ADDRESS);
        Map<String, String> result = generator.generate(context);
        
        assertNotNull(result);
        assertTrue(result.containsKey("province"));
        assertTrue(result.containsKey("city"));
        assertTrue(result.containsKey("district"));
        assertTrue(result.containsKey("community"));
        assertTrue(result.containsKey("street"));
        assertTrue(result.containsKey("building"));
        assertTrue(result.containsKey("unit"));
        assertTrue(result.containsKey("doorNumber"));
        assertTrue(result.containsKey("postalCode"));
        assertTrue(result.containsKey("fullAddress"));
        
        assertNotNull(result.get("province"));
        assertNotNull(result.get("city"));
        assertNotNull(result.get("postalCode"));
        assertEquals(6, result.get("postalCode").length());
    }
    
    @Test
    void testGenerateProvinceOnly() {
        generator = new EnhancedLocationDataGenerator(AddressComponent.PROVINCE);
        Map<String, String> result = generator.generate(context);
        
        assertNotNull(result);
        assertTrue(result.containsKey("province"));
        assertEquals(1, result.size());
        assertFalse(result.get("province").isEmpty());
    }
    
    @Test
    void testGenerateCityOnly() {
        generator = new EnhancedLocationDataGenerator(AddressComponent.CITY);
        Map<String, String> result = generator.generate(context);
        
        assertNotNull(result);
        assertTrue(result.containsKey("city"));
        assertEquals(1, result.size());
        assertFalse(result.get("city").isEmpty());
    }
    
    @Test
    void testGenerateDistrictOnly() {
        generator = new EnhancedLocationDataGenerator(AddressComponent.DISTRICT);
        Map<String, String> result = generator.generate(context);
        
        assertNotNull(result);
        assertTrue(result.containsKey("district"));
        assertEquals(1, result.size());
        assertFalse(result.get("district").isEmpty());
    }
    
    @Test
    void testGeneratePostalCodeOnly() {
        generator = new EnhancedLocationDataGenerator(AddressComponent.POSTAL_CODE);
        Map<String, String> result = generator.generate(context);
        
        assertNotNull(result);
        assertTrue(result.containsKey("postalCode"));
        assertEquals(1, result.size());
        assertEquals(6, result.get("postalCode").length());
        assertTrue(result.get("postalCode").matches("\\d{6}"));
    }
    
    @Test
    void testGenerateDoorNumber() {
        generator = new EnhancedLocationDataGenerator(AddressComponent.DOOR_NUMBER);
        Map<String, String> result = generator.generate(context);
        
        assertNotNull(result);
        assertTrue(result.containsKey("doorNumber"));
        assertEquals(1, result.size());
        assertTrue(result.get("doorNumber").matches("\\d{3,4}"));
    }
    
    @Test
    void testGenerateUnit() {
        generator = new EnhancedLocationDataGenerator(AddressComponent.UNIT);
        Map<String, String> result = generator.generate(context);
        
        assertNotNull(result);
        assertTrue(result.containsKey("unit"));
        assertEquals(1, result.size());
        assertTrue(result.get("unit").startsWith("单元"));
    }
    
    @Test
    void testGenerateCommunity() {
        generator = new EnhancedLocationDataGenerator(AddressComponent.COMMUNITY);
        Map<String, String> result = generator.generate(context);
        
        assertNotNull(result);
        assertTrue(result.containsKey("community"));
        assertEquals(1, result.size());
        assertFalse(result.get("community").isEmpty());
    }
    
    @Test
    void testUniqueness() {
        generator = new EnhancedLocationDataGenerator(AddressComponent.FULL_ADDRESS);
        
        // Generate multiple addresses and check uniqueness
        String address1 = generator.generate(context).get("fullAddress");
        String address2 = generator.generate(context).get("fullAddress");
        String address3 = generator.generate(context).get("fullAddress");
        
        assertNotEquals(address1, address2);
        assertNotEquals(address2, address3);
        assertNotEquals(address1, address3);
    }
    
    @Test
    void testComponentGeneration() {
        String province = EnhancedLocationDataGenerator.generateComponent(AddressComponent.PROVINCE);
        assertNotNull(province);
        assertFalse(province.isEmpty());
        
        String city = EnhancedLocationDataGenerator.generateComponent(AddressComponent.CITY);
        assertNotNull(city);
        assertFalse(city.isEmpty());
        
        String postalCode = EnhancedLocationDataGenerator.generateComponent(AddressComponent.POSTAL_CODE);
        assertNotNull(postalCode);
        assertEquals(6, postalCode.length());
    }
    
    @Test
    void testAllComponentsPresent() {
        generator = new EnhancedLocationDataGenerator(AddressComponent.FULL_ADDRESS);
        Map<String, String> result = generator.generate(context);
        
        // Verify all components are present in full address
        String fullAddress = result.get("fullAddress");
        assertTrue(fullAddress.contains(result.get("province")));
        assertTrue(fullAddress.contains(result.get("city")));
        assertTrue(fullAddress.contains(result.get("district")));
        assertTrue(fullAddress.contains(result.get("community")));
        assertTrue(fullAddress.contains(result.get("postalCode")));
    }
}
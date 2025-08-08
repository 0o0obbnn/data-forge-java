package com.dataforge.generators.business;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import static org.testng.Assert.*;

class BusinessDocumentNumberGeneratorTest {
    
    private BusinessDocumentNumberGenerator generator;
    private GenerationContext context;
    
    @BeforeMethod
    void setUp() {
        BusinessDocumentNumberGenerator.clearCache();
        context = new GenerationContext(1);
    }
    
    @Test
    void testGenerateDocumentNumber() {
        generator = new BusinessDocumentNumberGenerator();
        String docNumber = generator.generate(context);
        
        assertNotNull(docNumber);
        assertTrue(BusinessDocumentNumberGenerator.isValidDocumentNumber(docNumber));
        assertTrue(docNumber.startsWith("ORD")); // Default is ORDER
    }
    
    @Test
    void testGenerateSpecificDocumentType() {
        generator = new BusinessDocumentNumberGenerator(BusinessDocumentNumberGenerator.DocumentType.INVOICE);
        String docNumber = generator.generate(context);
        
        assertNotNull(docNumber);
        assertTrue(docNumber.startsWith("INV"));
        assertEquals(BusinessDocumentNumberGenerator.DocumentType.INVOICE, 
                    BusinessDocumentNumberGenerator.extractDocumentType(docNumber));
        
        generator = new BusinessDocumentNumberGenerator(BusinessDocumentNumberGenerator.DocumentType.CONTRACT);
        docNumber = generator.generate(context);
        
        assertNotNull(docNumber);
        assertTrue(docNumber.startsWith("CTR"));
    }
    
    @Test
    void testDifferentNumberFormats() {
        // Test DATE_SEQUENCE format
        generator = new BusinessDocumentNumberGenerator(
            BusinessDocumentNumberGenerator.DocumentType.ORDER,
            BusinessDocumentNumberGenerator.NumberFormat.DATE_SEQUENCE
        );
        String docNumber1 = generator.generate(context);
        
        // Test RANDOM format
        generator = new BusinessDocumentNumberGenerator(
            BusinessDocumentNumberGenerator.DocumentType.ORDER,
            BusinessDocumentNumberGenerator.NumberFormat.RANDOM
        );
        String docNumber2 = generator.generate(context);
        
        // Test TIMESTAMP format
        generator = new BusinessDocumentNumberGenerator(
            BusinessDocumentNumberGenerator.DocumentType.ORDER,
            BusinessDocumentNumberGenerator.NumberFormat.TIMESTAMP
        );
        String docNumber3 = generator.generate(context);
        
        assertNotNull(docNumber1);
        assertNotNull(docNumber2);
        assertNotNull(docNumber3);
        
        // All should start with ORD
        assertTrue(docNumber1.startsWith("ORD"));
        assertTrue(docNumber2.startsWith("ORD"));
        assertTrue(docNumber3.startsWith("ORD"));
        
        // They should be different
        assertNotEquals(docNumber1, docNumber2);
        assertNotEquals(docNumber2, docNumber3);
    }
    
    @Test
    void testSequentialNumbers() {
        generator = new BusinessDocumentNumberGenerator(
            BusinessDocumentNumberGenerator.DocumentType.ORDER,
            BusinessDocumentNumberGenerator.NumberFormat.SEQUENTIAL
        );
        
        String docNumber1 = generator.generate(context);
        String docNumber2 = generator.generate(context);
        String docNumber3 = generator.generate(context);
        
        assertNotNull(docNumber1);
        assertNotNull(docNumber2);
        assertNotNull(docNumber3);
        
        // All should be different
        assertNotEquals(docNumber1, docNumber2);
        assertNotEquals(docNumber2, docNumber3);
        assertNotEquals(docNumber1, docNumber3);
    }
    
    @Test
    void testCustomPrefixAndLength() {
        generator = new BusinessDocumentNumberGenerator(
            BusinessDocumentNumberGenerator.DocumentType.ORDER,
            BusinessDocumentNumberGenerator.NumberFormat.RANDOM,
            "CUSTOM", 15
        );
        
        String docNumber = generator.generate(context);
        
        assertNotNull(docNumber);
        assertTrue(docNumber.startsWith("CUSTOM"));
        assertEquals(15, docNumber.length());
    }
    
    @Test
    void testUniqueness() {
        generator = new BusinessDocumentNumberGenerator();
        Set<String> generatedNumbers = new HashSet<>();
        
        // Generate 100 numbers and check uniqueness
        for (int i = 0; i < 100; i++) {
            String docNumber = generator.generate(context);
            assertTrue(generatedNumbers.add(docNumber), "Duplicate number generated: " + docNumber);
        }
        
        assertEquals(100, generatedNumbers.size());
    }
    
    @Test
    void testIsValidDocumentNumber() {
        // Valid document numbers
        assertTrue(BusinessDocumentNumberGenerator.isValidDocumentNumber("ORD20240807001"));
        assertTrue(BusinessDocumentNumberGenerator.isValidDocumentNumber("INV2024080712345"));
        assertTrue(BusinessDocumentNumberGenerator.isValidDocumentNumber("CTR240807001234"));
        
        // Invalid document numbers
        assertFalse(BusinessDocumentNumberGenerator.isValidDocumentNumber(null));
        assertFalse(BusinessDocumentNumberGenerator.isValidDocumentNumber(""));
        assertFalse(BusinessDocumentNumberGenerator.isValidDocumentNumber("ABC"));
        assertFalse(BusinessDocumentNumberGenerator.isValidDocumentNumber("XYZ123456789"));
    }
    
    @Test
    void testExtractDocumentType() {
        assertEquals(BusinessDocumentNumberGenerator.DocumentType.ORDER, 
                    BusinessDocumentNumberGenerator.extractDocumentType("ORD20240807001"));
        assertEquals(BusinessDocumentNumberGenerator.DocumentType.INVOICE, 
                    BusinessDocumentNumberGenerator.extractDocumentType("INV2024080712345"));
        assertEquals(BusinessDocumentNumberGenerator.DocumentType.CONTRACT, 
                    BusinessDocumentNumberGenerator.extractDocumentType("CTR240807001234"));
        
        assertNull(BusinessDocumentNumberGenerator.extractDocumentType(null));
        assertNull(BusinessDocumentNumberGenerator.extractDocumentType("XYZ123456789"));
    }
    
    @Test
    void testGetGeneratedCount() {
        assertEquals(0, BusinessDocumentNumberGenerator.getGeneratedCount());
        
        generator = new BusinessDocumentNumberGenerator();
        generator.generate(context);
        generator.generate(context);
        
        assertEquals(2, BusinessDocumentNumberGenerator.getGeneratedCount());
        
        BusinessDocumentNumberGenerator.clearCache();
        assertEquals(0, BusinessDocumentNumberGenerator.getGeneratedCount());
    }
    
    @Test
    void testGetSupportedDocumentTypes() {
        List<String> types = BusinessDocumentNumberGenerator.getSupportedDocumentTypes();
        
        assertNotNull(types);
        assertFalse(types.isEmpty());
        assertTrue(types.contains("ORDER"));
        assertTrue(types.contains("INVOICE"));
        assertTrue(types.contains("CONTRACT"));
        assertTrue(types.contains("ANY"));
    }
    
    @Test
    void testGetSupportedNumberFormats() {
        List<String> formats = BusinessDocumentNumberGenerator.getSupportedNumberFormats();
        
        assertNotNull(formats);
        assertFalse(formats.isEmpty());
        assertTrue(formats.contains("SEQUENTIAL"));
        assertTrue(formats.contains("TIMESTAMP"));
        assertTrue(formats.contains("RANDOM"));
        assertTrue(formats.contains("DATE_SEQUENCE"));
        assertTrue(formats.contains("YEAR_MONTH_SEQ"));
    }
    
    @Test
    void testGenerateForTypeAndFormat() {
        String docNumber = BusinessDocumentNumberGenerator.generateForTypeAndFormat(
            BusinessDocumentNumberGenerator.DocumentType.PAYMENT,
            BusinessDocumentNumberGenerator.NumberFormat.RANDOM,
            context
        );
        
        assertNotNull(docNumber);
        assertTrue(docNumber.startsWith("PAY"));
        assertTrue(BusinessDocumentNumberGenerator.isValidDocumentNumber(docNumber));
    }
    
    @Test
    void testDifferentDocumentTypes() {
        // Test all document types
        BusinessDocumentNumberGenerator.DocumentType[] types = {
            BusinessDocumentNumberGenerator.DocumentType.ORDER,
            BusinessDocumentNumberGenerator.DocumentType.INVOICE,
            BusinessDocumentNumberGenerator.DocumentType.CONTRACT,
            BusinessDocumentNumberGenerator.DocumentType.RECEIPT,
            BusinessDocumentNumberGenerator.DocumentType.PURCHASE,
            BusinessDocumentNumberGenerator.DocumentType.DELIVERY,
            BusinessDocumentNumberGenerator.DocumentType.RETURN,
            BusinessDocumentNumberGenerator.DocumentType.PAYMENT,
            BusinessDocumentNumberGenerator.DocumentType.REFUND,
            BusinessDocumentNumberGenerator.DocumentType.QUOTATION
        };
        
        for (BusinessDocumentNumberGenerator.DocumentType type : types) {
            generator = new BusinessDocumentNumberGenerator(type);
            String docNumber = generator.generate(context);
            
            assertNotNull(docNumber);
            assertTrue(docNumber.startsWith(type.getPrefix()));
            assertEquals(type, BusinessDocumentNumberGenerator.extractDocumentType(docNumber));
        }
    }
    
    @Test
    void testGetName() {
        generator = new BusinessDocumentNumberGenerator();
        assertEquals("business_document_number", generator.getName());
    }
    
    @Test
    void testGetSupportedParameters() {
        generator = new BusinessDocumentNumberGenerator();
        List<String> params = generator.getSupportedParameters();
        
        assertNotNull(params);
        assertEquals(4, params.size());
        assertTrue(params.contains("type"));
        assertTrue(params.contains("format"));
        assertTrue(params.contains("prefix"));
        assertTrue(params.contains("length"));
    }
    
    @Test
    void testDocumentTypeProperties() {
        assertEquals("订单号", BusinessDocumentNumberGenerator.DocumentType.ORDER.getName());
        assertEquals("ORD", BusinessDocumentNumberGenerator.DocumentType.ORDER.getPrefix());
        assertEquals(8, BusinessDocumentNumberGenerator.DocumentType.ORDER.getDefaultLength());
        
        assertEquals("发票号", BusinessDocumentNumberGenerator.DocumentType.INVOICE.getName());
        assertEquals("INV", BusinessDocumentNumberGenerator.DocumentType.INVOICE.getPrefix());
        assertEquals(10, BusinessDocumentNumberGenerator.DocumentType.INVOICE.getDefaultLength());
    }
    
    @Test
    void testNumberFormatDescriptions() {
        assertEquals("连续递增", BusinessDocumentNumberGenerator.NumberFormat.SEQUENTIAL.getDescription());
        assertEquals("时间戳", BusinessDocumentNumberGenerator.NumberFormat.TIMESTAMP.getDescription());
        assertEquals("随机数字", BusinessDocumentNumberGenerator.NumberFormat.RANDOM.getDescription());
    }
}
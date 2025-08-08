package com.dataforge.generators.basic;

import com.dataforge.core.GenerationContext;
import com.dataforge.generators.basic.PhoneNumberGenerator.Operator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

class PhoneNumberGeneratorTest {
    
    private PhoneNumberGenerator generator;
    private GenerationContext context;
    
    @BeforeMethod
    void setUp() {
        PhoneNumberGenerator.clearCache();
        context = new GenerationContext(1);
    }
    
    @Test
    void testGeneratePhoneNumber() {
        generator = new PhoneNumberGenerator();
        String phoneNumber = generator.generate(context);
        
        assertNotNull(phoneNumber);
        assertEquals(11, phoneNumber.length());
        assertTrue(phoneNumber.matches("\\d{11}"));
    }
    
    @Test
    void testGenerateChinaMobileNumber() {
        generator = new PhoneNumberGenerator(Operator.CHINA_MOBILE);
        String phoneNumber = generator.generate(context);
        
        assertNotNull(phoneNumber);
        assertEquals(11, phoneNumber.length());
        
        String prefix = phoneNumber.substring(0, 3);
        List<String> mobilePrefixes = PhoneNumberGenerator.getOperatorPrefixes(Operator.CHINA_MOBILE);
        assertTrue(mobilePrefixes.contains(prefix));
    }
    
    @Test
    void testGenerateChinaUnicomNumber() {
        generator = new PhoneNumberGenerator(Operator.CHINA_UNICOM);
        String phoneNumber = generator.generate(context);
        
        assertNotNull(phoneNumber);
        assertEquals(11, phoneNumber.length());
        
        String prefix = phoneNumber.substring(0, 3);
        List<String> unicomPrefixes = PhoneNumberGenerator.getOperatorPrefixes(Operator.CHINA_UNICOM);
        assertTrue(unicomPrefixes.contains(prefix));
    }
    
    @Test
    void testGenerateChinaTelecomNumber() {
        generator = new PhoneNumberGenerator(Operator.CHINA_TELECOM);
        String phoneNumber = generator.generate(context);
        
        assertNotNull(phoneNumber);
        assertEquals(11, phoneNumber.length());
        
        String prefix = phoneNumber.substring(0, 3);
        List<String> telecomPrefixes = PhoneNumberGenerator.getOperatorPrefixes(Operator.CHINA_TELECOM);
        assertTrue(telecomPrefixes.contains(prefix));
    }
    
    @Test
    void testGenerateVirtualOperatorNumber() {
        generator = new PhoneNumberGenerator(Operator.VIRTUAL_OPERATOR);
        String phoneNumber = generator.generate(context);
        
        assertNotNull(phoneNumber);
        assertEquals(11, phoneNumber.length());
        
        String prefix3 = phoneNumber.substring(0, 3);
        String prefix4 = phoneNumber.substring(0, 4);
        List<String> virtualPrefixes = PhoneNumberGenerator.getOperatorPrefixes(Operator.VIRTUAL_OPERATOR);
        
        assertTrue(virtualPrefixes.contains(prefix3) || virtualPrefixes.contains(prefix4));
    }
    
    @Test
    void testUniqueness() {
        generator = new PhoneNumberGenerator();
        
        String phone1 = generator.generate(context);
        String phone2 = generator.generate(context);
        String phone3 = generator.generate(context);
        
        assertNotEquals(phone1, phone2);
        assertNotEquals(phone2, phone3);
        assertNotEquals(phone1, phone3);
    }
    
    @Test
    void testGetOperator() {
        String mobileNumber = PhoneNumberGenerator.generateForOperator(Operator.CHINA_MOBILE, context);
        String operator = PhoneNumberGenerator.getOperator(mobileNumber);
        assertEquals("中国移动", operator);
        
        String unicomNumber = PhoneNumberGenerator.generateForOperator(Operator.CHINA_UNICOM, context);
        operator = PhoneNumberGenerator.getOperator(unicomNumber);
        assertEquals("中国联通", operator);
        
        String telecomNumber = PhoneNumberGenerator.generateForOperator(Operator.CHINA_TELECOM, context);
        operator = PhoneNumberGenerator.getOperator(telecomNumber);
        assertEquals("中国电信", operator);
    }
    
    @Test
    void testGetOperatorPrefixes() {
        List<String> mobilePrefixes = PhoneNumberGenerator.getOperatorPrefixes(Operator.CHINA_MOBILE);
        assertFalse(mobilePrefixes.isEmpty());
        assertTrue(mobilePrefixes.contains("139"));
        assertTrue(mobilePrefixes.contains("188"));
        
        List<String> unicomPrefixes = PhoneNumberGenerator.getOperatorPrefixes(Operator.CHINA_UNICOM);
        assertFalse(unicomPrefixes.isEmpty());
        assertTrue(unicomPrefixes.contains("130"));
        assertTrue(unicomPrefixes.contains("186"));
        
        List<String> telecomPrefixes = PhoneNumberGenerator.getOperatorPrefixes(Operator.CHINA_TELECOM);
        assertFalse(telecomPrefixes.isEmpty());
        assertTrue(telecomPrefixes.contains("133"));
        assertTrue(telecomPrefixes.contains("189"));
    }
    
    @Test
    void testGetAvailableOperators() {
        Set<String> operators = PhoneNumberGenerator.getAvailableOperators();
        assertFalse(operators.isEmpty());
        assertTrue(operators.contains("CHINA_MOBILE"));
        assertTrue(operators.contains("CHINA_UNICOM"));
        assertTrue(operators.contains("CHINA_TELECOM"));
        assertTrue(operators.contains("VIRTUAL_OPERATOR"));
        assertTrue(operators.contains("ALL"));
    }
    
    @Test
    void testGetOperatorUnknown() {
        String unknown = PhoneNumberGenerator.getOperator("99912345678");
        assertEquals("未知", unknown);
    }
    
    @Test
    void testGetOperatorNull() {
        String unknown = PhoneNumberGenerator.getOperator(null);
        assertEquals("未知", unknown);
    }
    
    @Test
    void testGetOperatorShortNumber() {
        String unknown = PhoneNumberGenerator.getOperator("123");
        assertEquals("未知", unknown);
    }
    
    @Test
    void testGenerateWithContext() {
        String phone = PhoneNumberGenerator.generateForOperator(Operator.CHINA_MOBILE, context);
        assertNotNull(phone);
        assertEquals(11, phone.length());
        assertTrue(phone.startsWith("1"));
    }
    
    @Test
    void testUniquePhoneNumberCount() {
        generator = new PhoneNumberGenerator();
        PhoneNumberGenerator.clearCache();
        assertEquals(0, PhoneNumberGenerator.getUniquePhoneNumberCount());
        
        for (int i = 0; i < 100; i++) {
            generator.generate(context);
        }
        
        assertTrue(PhoneNumberGenerator.getUniquePhoneNumberCount() > 0);
    }
    
    @Test
    void testClearCache() {
        generator = new PhoneNumberGenerator();
        generator.generate(context);
        assertTrue(PhoneNumberGenerator.getUniquePhoneNumberCount() > 0);
        
        PhoneNumberGenerator.clearCache();
        assertEquals(0, PhoneNumberGenerator.getUniquePhoneNumberCount());
    }
}
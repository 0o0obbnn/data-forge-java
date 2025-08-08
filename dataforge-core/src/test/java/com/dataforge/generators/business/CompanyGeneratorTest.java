package com.dataforge.generators.business;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

public class CompanyGeneratorTest {
    
    @Test
    public void testGenerateCompany() {
        CompanyGenerator generator = new CompanyGenerator();
        GenerationContext context = new GenerationContext(1);
        
        String company = generator.generate(context);
        assertNotNull(company);
        assertFalse(company.isEmpty());
        System.out.println("Generated company: " + company);
    }
    
    @Test
    public void testGenerateUniqueCompanies() {
        CompanyGenerator generator = new CompanyGenerator();
        GenerationContext context = new GenerationContext(1);
        Set<String> companies = new HashSet<>();
        
        // 生成100个公司名称并检查唯一性
        for (int i = 0; i < 100; i++) {
            String company = generator.generate(context);
            assertTrue(companies.add(company), "Duplicate company name generated: " + company);
        }
        
        assertEquals(companies.size(), 100);
        System.out.println("Generated 100 unique companies successfully");
    }
    
    @Test
    public void testGenerate5000UniqueCompanies() {
        CompanyGenerator generator = new CompanyGenerator();
        generator.resetUniqueCompanies(); // 重置以确保从零开始
        GenerationContext context = new GenerationContext(1);
        Set<String> companies = new HashSet<>();
        
        // 生成5000个公司名称并检查唯一性
        for (int i = 0; i < 5000; i++) {
            String company = generator.generate(context);
            assertTrue(companies.add(company), "Duplicate company name generated: " + company);
        }
        
        assertEquals(companies.size(), 5000);
        System.out.println("Generated 5000 unique companies successfully");
    }
    
    @Test
    public void testCompanyStatistics() {
        CompanyGenerator generator = new CompanyGenerator();
        var stats = generator.getCompanyStatistics();
        
        assertTrue(stats.containsKey("total_industry_keywords"));
        assertTrue(stats.containsKey("total_company_types"));
        assertTrue(stats.containsKey("total_region_prefixes"));
        assertTrue(stats.get("total_industry_keywords") > 0);
        assertTrue(stats.get("total_company_types") > 0);
        assertTrue(stats.get("total_region_prefixes") > 0);
        
        System.out.println("Company statistics: " + stats);
    }
    
    @Test
    public void testGenerateCompanyWithParameters() {
        CompanyGenerator generator = new CompanyGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // 测试指定行业
        context.setParameter("industry", "科技");
        String techCompany = generator.generate(context);
        assertTrue(techCompany.contains("科技"), "Company should contain '科技': " + techCompany);
        System.out.println("Tech company: " + techCompany);
        
        // 测试指定类型
        context.setParameter("type", "有限公司");
        String ltdCompany = generator.generate(context);
        assertTrue(ltdCompany.endsWith("有限公司"), "Company should end with '有限公司': " + ltdCompany);
        System.out.println("Ltd company: " + ltdCompany);
        
        // 测试不加地区前缀
        context.setParameter("prefixRegion", false);
        String noPrefixCompany = generator.generate(context);
        // 检查是否不以地区名开头（简单检查）
        assertFalse(noPrefixCompany.startsWith("北京 ") && noPrefixCompany.startsWith("上海 "));
        System.out.println("No prefix company: " + noPrefixCompany);
    }
}
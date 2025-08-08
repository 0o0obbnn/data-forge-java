package com.dataforge.examples;

import com.dataforge.core.GenerationContext;
import com.dataforge.generators.business.CompanyGenerator;

import java.util.HashSet;
import java.util.Set;

/**
 * 公司名称生成器使用示例
 */
public class CompanyGeneratorExample {
    
    public static void main(String[] args) {
        // 创建公司名称生成器
        CompanyGenerator generator = new CompanyGenerator();
        
        // 创建生成上下文
        GenerationContext context = new GenerationContext(1);
        
        System.out.println("=== 公司名称生成器示例 ===\n");
        
        // 1. 生成默认公司名称
        System.out.println("1. 生成默认公司名称:");
        for (int i = 0; i < 5; i++) {
            String company = generator.generate(context);
            System.out.println("   " + company);
        }
        
        // 2. 生成指定行业的公司名称
        System.out.println("\n2. 生成科技行业公司名称:");
        context.setParameter("industry", "科技");
        for (int i = 0; i < 5; i++) {
            String company = generator.generate(context);
            System.out.println("   " + company);
        }
        
        // 3. 生成指定类型的公司名称
        System.out.println("\n3. 生成有限公司:");
        context.setParameter("industry", null); // 清除行业参数
        context.setParameter("type", "有限公司");
        for (int i = 0; i < 5; i++) {
            String company = generator.generate(context);
            System.out.println("   " + company);
        }
        
        // 4. 生成不带地区前缀的公司名称
        System.out.println("\n4. 生成不带地区前缀的公司名称:");
        context.setParameter("type", null); // 清除类型参数
        context.setParameter("prefixRegion", false);
        for (int i = 0; i < 5; i++) {
            String company = generator.generate(context);
            System.out.println("   " + company);
        }
        
        // 5. 生成5000个唯一公司名称
        System.out.println("\n5. 生成5000个唯一公司名称测试:");
        generator.resetUniqueCompanies(); // 重置唯一性计数器
        GenerationContext uniqueContext = new GenerationContext(1);
        uniqueContext.setParameter("unique", true);
        
        Set<String> uniqueCompanies = new HashSet<>();
        int duplicateCount = 0;
        
        for (int i = 0; i < 5000; i++) {
            String company = generator.generate(uniqueContext);
            if (!uniqueCompanies.add(company)) {
                duplicateCount++;
            }
        }
        
        System.out.println("   生成了 " + uniqueCompanies.size() + " 个唯一公司名称");
        System.out.println("   重复数量: " + duplicateCount);
        System.out.println("   成功生成5000个唯一公司名称: " + (uniqueCompanies.size() == 5000));
        
        // 6. 显示统计信息
        System.out.println("\n6. 生成器统计信息:");
        var stats = generator.getCompanyStatistics();
        stats.forEach((key, value) -> System.out.println("   " + key + ": " + value));
    }
}
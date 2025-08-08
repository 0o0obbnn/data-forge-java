package com.dataforge.test;

import com.dataforge.core.GeneratorFactory;
import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

public class P1GeneratorTest {
    public static void main(String[] args) {
        GenerationContext context = new GenerationContext(100);
        
        System.out.println("=== P1优先级生成器测试 ===\n");
        
        // Test Enhanced Bank Card Generator
        System.out.println("1. 增强银行卡号生成器:");
        try {
            DataGenerator<?> bankCardGen = GeneratorFactory.createGenerator("enhanced_bank_card");
            for (int i = 0; i < 5; i++) {
                System.out.println("   " + bankCardGen.generate(context));
            }
        } catch (Exception e) {
            System.err.println("   错误: " + e.getMessage());
        }
        
        System.out.println();
        
        // Test Network Device Generator  
        System.out.println("2. 网络设备生成器:");
        try {
            DataGenerator<?> networkGen = GeneratorFactory.createGenerator("network_device");
            for (int i = 0; i < 5; i++) {
                System.out.println("   " + networkGen.generate(context));
            }
        } catch (Exception e) {
            System.err.println("   错误: " + e.getMessage());
        }
        
        System.out.println();
        
        // Test Statistical Distribution Generator
        System.out.println("3. 统计分布生成器:");
        try {
            DataGenerator<?> statsGen = GeneratorFactory.createGenerator("statistical_distribution");
            for (int i = 0; i < 5; i++) {
                System.out.println("   " + statsGen.generate(context));
            }
        } catch (Exception e) {
            System.err.println("   错误: " + e.getMessage());
        }
        
        System.out.println();
        
        // Test Generator Registration
        System.out.println("4. 生成器注册状态:");
        String[] p1Generators = {"enhanced_bank_card", "network_device", "statistical_distribution"};
        
        for (String name : p1Generators) {
            boolean isRegistered = GeneratorFactory.isRegistered(name);
            System.out.println("   " + name + ": " + (isRegistered ? "✓ 已注册" : "✗ 未注册"));
        }
        
        System.out.println("\n=== P1测试完成 ===");
    }
}
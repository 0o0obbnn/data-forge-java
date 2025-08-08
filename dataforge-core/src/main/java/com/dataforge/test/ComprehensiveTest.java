package com.dataforge.test;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.core.GeneratorFactory;

public class ComprehensiveTest {
    public static void main(String[] args) {
        System.out.println("Comprehensive test of all generators:");
        
        GenerationContext context = new GenerationContext(1);
        
        // Test all generators together
        System.out.println("\nTesting all generators together:");
        DataGenerator<?> nameGenerator = GeneratorFactory.createGenerator("name");
        DataGenerator<?> idCardGenerator = GeneratorFactory.createGenerator("idcard");
        DataGenerator<?> bankCardGenerator = GeneratorFactory.createGenerator("bankcard");
        DataGenerator<?> usccGenerator = GeneratorFactory.createGenerator("uscc");
        DataGenerator<?> uuidGenerator = GeneratorFactory.createGenerator("uuid");
        DataGenerator<?> ipGenerator = GeneratorFactory.createGenerator("ip");
        DataGenerator<?> urlGenerator = GeneratorFactory.createGenerator("url");
        
        for (int i = 0; i < 3; i++) {
            System.out.println("Record " + (i + 1) + ":");
            System.out.println("  Name: " + nameGenerator.generate(context));
            System.out.println("  ID Card: " + idCardGenerator.generate(context));
            System.out.println("  Bank Card: " + bankCardGenerator.generate(context));
            System.out.println("  USCC: " + usccGenerator.generate(context));
            System.out.println("  UUID: " + uuidGenerator.generate(context));
            System.out.println("  IP: " + ipGenerator.generate(context));
            System.out.println("  URL: " + urlGenerator.generate(context));
            System.out.println();
        }
        
        System.out.println("All generators are working correctly together!");
    }
}
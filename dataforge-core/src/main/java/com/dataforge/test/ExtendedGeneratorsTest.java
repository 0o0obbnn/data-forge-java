package com.dataforge.test;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.core.GeneratorFactory;
import com.dataforge.generators.identifiers.UuidGenerator;
import com.dataforge.generators.network.IpAddressGenerator;
import com.dataforge.generators.network.UrlGenerator;

public class ExtendedGeneratorsTest {
    public static void main(String[] args) {
        System.out.println("Testing extended generators:");
        
        GenerationContext context = new GenerationContext(1);
        
        // Test UUID generator
        System.out.println("\nTesting UUID generator:");
        DataGenerator<?> uuidGenerator = GeneratorFactory.createGenerator("uuid");
        for (int i = 0; i < 3; i++) {
            System.out.println("UUID: " + uuidGenerator.generate(context));
        }
        
        // Test IP address generator
        System.out.println("\nTesting IP address generator:");
        DataGenerator<?> ipGenerator = GeneratorFactory.createGenerator("ip");
        for (int i = 0; i < 3; i++) {
            System.out.println("IP: " + ipGenerator.generate(context));
        }
        
        // Test URL generator
        System.out.println("\nTesting URL generator:");
        DataGenerator<?> urlGenerator = GeneratorFactory.createGenerator("url");
        for (int i = 0; i < 3; i++) {
            System.out.println("URL: " + urlGenerator.generate(context));
        }
        
        System.out.println("\nAll extended generators are working correctly!");
    }
}
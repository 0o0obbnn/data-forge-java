package com.dataforge.test;

import com.dataforge.core.GeneratorFactory;
import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

public class GeneratorTest {
    public static void main(String[] args) {
        GenerationContext context = new GenerationContext(100);
        
        // Test JSON Object Generator
        System.out.println("=== JSON Object Generator ===");
        try {
            DataGenerator<?> jsonGen = GeneratorFactory.createGenerator("json_object");
            for (int i = 0; i < 2; i++) {
                System.out.println(jsonGen.generate(context));
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("JSON Generator error: " + e.getMessage());
        }
        
        // Test License Plate Generator
        System.out.println("=== License Plate Generator ===");
        try {
            DataGenerator<?> plateGen = GeneratorFactory.createGenerator("license_plate");
            for (int i = 0; i < 5; i++) {
                System.out.println(plateGen.generate(context));
            }
        } catch (Exception e) {
            System.err.println("License Plate Generator error: " + e.getMessage());
        }
        
        System.out.println();
        
        // Test Address Generator
        System.out.println("=== Address Generator ===");
        try {
            DataGenerator<?> addressGen = GeneratorFactory.createGenerator("address");
            for (int i = 0; i < 3; i++) {
                System.out.println(addressGen.generate(context));
            }
        } catch (Exception e) {
            System.err.println("Address Generator error: " + e.getMessage());
        }
        
        System.out.println();
        
        // Test HTTP Status Code Generator
        System.out.println("=== HTTP Status Code Generator ===");
        try {
            DataGenerator<?> httpGen = GeneratorFactory.createGenerator("http_status_code");
            for (int i = 0; i < 5; i++) {
                System.out.println(httpGen.generate(context));
            }
        } catch (Exception e) {
            System.err.println("HTTP Status Generator error: " + e.getMessage());
        }
        
        System.out.println();
        
        // Test Business Status Code Generator
        System.out.println("=== Business Status Code Generator ===");
        try {
            DataGenerator<?> businessGen = GeneratorFactory.createGenerator("business_status_code");
            for (int i = 0; i < 5; i++) {
                System.out.println(businessGen.generate(context));
            }
        } catch (Exception e) {
            System.err.println("Business Status Generator error: " + e.getMessage());
        }
    }
}
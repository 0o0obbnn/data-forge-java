package com.dataforge.test;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.core.GeneratorFactory;
import com.dataforge.generators.basic.*;

public class TestApp {
    public static void main(String[] args) {
        // Test individual generators
        System.out.println("Testing individual generators:");
        
        GenerationContext context = new GenerationContext(1);
        
        NameGenerator nameGen = new NameGenerator();
        System.out.println("Name: " + nameGen.generate(context));
        
        PhoneNumberGenerator phoneGen = new PhoneNumberGenerator();
        System.out.println("Phone: " + phoneGen.generate(context));
        
        EmailGenerator emailGen = new EmailGenerator();
        System.out.println("Email: " + emailGen.generate(context));
        
        AgeGenerator ageGen = new AgeGenerator();
        System.out.println("Age: " + ageGen.generate(context));
        
        GenderGenerator genderGen = new GenderGenerator();
        System.out.println("Gender: " + genderGen.generate(context));
        
        PasswordGenerator passwordGen = new PasswordGenerator();
        System.out.println("Password: " + passwordGen.generate(context));
        
        AccountNameGenerator accountGen = new AccountNameGenerator();
        System.out.println("Account: " + accountGen.generate(context));
        
        // Test factory
        System.out.println("\nTesting factory:");
        DataGenerator<?> nameGenerator = GeneratorFactory.createGenerator("name");
        System.out.println("Name from factory: " + nameGenerator.generate(context));
        
        DataGenerator<?> phoneGenerator = GeneratorFactory.createGenerator("phone");
        System.out.println("Phone from factory: " + phoneGenerator.generate(context));
    }
}
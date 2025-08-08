package com.dataforge.generators.text;

import com.dataforge.core.GenerationContext;

public class DebugLongTextGenerator {
    public static void main(String[] args) {
        LongTextGenerator generator = new LongTextGenerator(50, 150, false);
        GenerationContext context = new GenerationContext(1);
        
        for (int i = 0; i < 10; i++) {
            String text = generator.generate(context);
            System.out.println("Length: " + text.length() + ", Text: " + text);
        }
    }
}
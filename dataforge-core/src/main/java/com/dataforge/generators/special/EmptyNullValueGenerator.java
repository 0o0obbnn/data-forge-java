package com.dataforge.generators.special;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Empty/Null value generator for generating empty strings, null values, empty arrays, and empty objects.
 */
public class EmptyNullValueGenerator implements DataGenerator<String> {
    
    // Types of empty/null values to generate
    public enum ValueType {
        EMPTY_STRING,  // ""
        NULL,          // null
        EMPTY_ARRAY,   // []
        EMPTY_OBJECT   // {}
    }
    
    private final ValueType valueType;
    
    public EmptyNullValueGenerator() {
        this(ValueType.EMPTY_STRING);
    }
    
    public EmptyNullValueGenerator(ValueType valueType) {
        this.valueType = valueType;
    }
    
    @Override
    public String generate(GenerationContext context) {
        switch (valueType) {
            case EMPTY_STRING:
                return "";
            case NULL:
                return null;
            case EMPTY_ARRAY:
                return "[]";
            case EMPTY_OBJECT:
                return "{}";
            default:
                return "";
        }
    }
}
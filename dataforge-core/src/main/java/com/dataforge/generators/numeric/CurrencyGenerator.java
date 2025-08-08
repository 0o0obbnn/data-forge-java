package com.dataforge.generators.numeric;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Currency generator for generating currency codes.
 */
public class CurrencyGenerator implements DataGenerator<String> {
    
    // Common currency codes based on ISO 4217
    private static final String[] CURRENCY_CODES = {
        "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "SEK", "NZD",
        "MXN", "SGD", "HKD", "NOK", "KRW", "TRY", "RUB", "INR", "BRL", "ZAR"
    };
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        return CURRENCY_CODES[random.nextInt(CURRENCY_CODES.length)];
    }
}
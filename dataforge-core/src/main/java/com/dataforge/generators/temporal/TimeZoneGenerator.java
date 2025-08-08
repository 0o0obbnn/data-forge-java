package com.dataforge.generators.temporal;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generates a random IANA time zone identifier.
 */
public class TimeZoneGenerator implements DataGenerator<String> {

    private static final List<String> TIME_ZONES = Arrays.asList(
            "UTC", "GMT", "America/New_York", "America/Chicago", "America/Denver", "America/Los_Angeles",
            "Europe/London", "Europe/Paris", "Europe/Berlin", "Asia/Tokyo", "Asia/Shanghai", "Asia/Dubai",
            "Australia/Sydney"
    );

    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        return TIME_ZONES.get(random.nextInt(TIME_ZONES.size()));
    }
}

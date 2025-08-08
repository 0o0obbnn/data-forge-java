package com.dataforge.generators.communication;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates landline phone numbers.
 */
public class LandlinePhoneGenerator implements DataGenerator<String> {

    private final Random random = new Random();
    private static final String[] AREA_CODES = {"010", "021", "022", "023", "024", "025", "027", "028"};

    @Override
    public String generate(GenerationContext context) {
        String areaCode = (String) context.getParameter("area_code", AREA_CODES[random.nextInt(AREA_CODES.length)]);
        int numberLength = (int) context.getParameter("number_length", 8);
        int extensionLength = (int) context.getParameter("extension_length", 0);

        StringBuilder phoneNumber = new StringBuilder(areaCode);
        phoneNumber.append("-");
        for (int i = 0; i < numberLength; i++) {
            phoneNumber.append(random.nextInt(10));
        }

        if (extensionLength > 0) {
            phoneNumber.append("-");
            for (int i = 0; i < extensionLength; i++) {
                phoneNumber.append(random.nextInt(10));
            }
        }

        return phoneNumber.toString();
    }
}

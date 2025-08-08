package com.dataforge.generators.media;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generates a random MIME type from a list of common types.
 */
public class MimeTypeGenerator implements DataGenerator<String> {

    private static final List<String> MIME_TYPES = Arrays.asList(
            "application/json", "application/xml", "application/pdf", "application/zip",
            "text/plain", "text/html", "text/css", "text/javascript",
            "image/jpeg", "image/png", "image/gif", "image/svg+xml",
            "audio/mpeg", "audio/wav", "video/mp4", "video/webm"
    );

    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        return MIME_TYPES.get(random.nextInt(MIME_TYPES.size()));
    }
}

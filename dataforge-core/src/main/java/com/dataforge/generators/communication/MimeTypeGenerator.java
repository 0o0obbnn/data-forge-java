package com.dataforge.generators.communication;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates MIME types.
 */
public class MimeTypeGenerator implements DataGenerator<String> {

    private final Random random = new Random();
    public static final String[] MIME_TYPES = {
        "application/json", "application/xml", "application/pdf", "application/zip",
        "text/plain", "text/html", "text/css", "text/javascript",
        "image/jpeg", "image/png", "image/gif", "image/svg+xml",
        "audio/mpeg", "audio/ogg", "video/mp4", "video/webm"
    };

    @Override
    public String generate(GenerationContext context) {
        return MIME_TYPES[random.nextInt(MIME_TYPES.length)];
    }
}

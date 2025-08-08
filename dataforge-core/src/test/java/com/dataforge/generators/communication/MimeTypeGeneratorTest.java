package com.dataforge.generators.communication;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.Arrays;

/**
 * Unit tests for the MimeTypeGenerator class.
 */
public class MimeTypeGeneratorTest {

    private final MimeTypeGenerator generator = new MimeTypeGenerator();
    private final GenerationContext context = new GenerationContext(1);

    @Test
    public void testGenerate() {
        String mimeType = generator.generate(context);
        assertNotNull(mimeType);
        assertTrue(Arrays.asList(MimeTypeGenerator.MIME_TYPES).contains(mimeType));
    }
}

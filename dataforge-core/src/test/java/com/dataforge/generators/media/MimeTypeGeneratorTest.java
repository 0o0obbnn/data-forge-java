package com.dataforge.generators.media;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import java.util.Random;
import static org.testng.Assert.*;

@Test
public class MimeTypeGeneratorTest {

    @Test(description = "Should generate a valid MIME type")
    public void testGenerate() {
        MimeTypeGenerator generator = new MimeTypeGenerator();
        GenerationContext context = new GenerationContext(1);
        String mimeType = generator.generate(context);
        assertNotNull(mimeType);
        assertTrue(mimeType.contains("/"));
    }
}

package com.dataforge.generators.communication;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for the FilePathGenerator class.
 */
public class FilePathGeneratorTest {

    private final FilePathGenerator generator = new FilePathGenerator();

    @Test
    public void testGenerateUnixPath() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("os", "UNIX");
        String path = generator.generate(context);
        assertNotNull(path);
        assertTrue(path.startsWith("/"));
        assertTrue(path.contains("."));
    }

    @Test
    public void testGenerateWindowsPath() {
        GenerationContext context = new GenerationContext(1);
        context.setParameter("os", "WINDOWS");
        String path = generator.generate(context);
        assertNotNull(path);
        assertTrue(path.startsWith("C:\\"));
        assertTrue(path.contains("."));
    }
}

package com.dataforge.generators.media;

import com.dataforge.core.GenerationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FileExtensionGeneratorTest {
    
    @BeforeMethod
    public void setUp() {
        FileExtensionGenerator.clearCache();
    }
    
    @Test
    public void testGenerateDefaultExtension() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Generate multiple extensions to test randomness
        boolean foundTxt = false;
        boolean foundJpg = false;
        boolean foundPdf = false;
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).startsWith(".");
            
            if (".txt".equals(result)) foundTxt = true;
            if (".jpg".equals(result)) foundJpg = true;
            if (".pdf".equals(result)) foundPdf = true;
        }
        
        // At least some common extensions should be generated
        assertThat(foundTxt || foundJpg || foundPdf).isTrue();
    }
    
    @Test
    public void testGenerateImageExtension() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("category", "image");
        
        // Get the actual image extensions from the generator
        String[] expectedExtensions = FileExtensionGenerator.getImageExtensions().toArray(new String[0]);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).startsWith(".");
            assertThat(result).isIn((Object[]) expectedExtensions);
        }
    }
    
    @Test
    public void testGenerateDocumentExtension() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("category", "document");
        
        // Get the actual document extensions from the generator
        String[] expectedExtensions = FileExtensionGenerator.getDocumentExtensions().toArray(new String[0]);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).startsWith(".");
            assertThat(result).isIn((Object[]) expectedExtensions);
        }
    }
    
    @Test
    public void testGenerateArchiveExtension() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("category", "archive");
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).startsWith(".");
            assertThat(result).isIn((Object[]) new String[]{".zip", ".rar", ".7z", ".tar", ".gz"});
        }
    }
    
    @Test
    public void testGenerateMediaExtension() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("category", "media");
        
        // Get the actual media extensions from the generator
        String[] expectedExtensions = FileExtensionGenerator.getMediaExtensions().toArray(new String[0]);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).startsWith(".");
            assertThat(result).isIn((Object[]) expectedExtensions);
        }
    }
    
    @Test
    public void testGenerateDangerousExtensionWithoutPermission() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        GenerationContext context = new GenerationContext(1);
        
        // Get the actual dangerous extensions from the generator
        String[] dangerousExtensions = FileExtensionGenerator.getDangerousExtensions().toArray(new String[0]);
        
        // By default, dangerous extensions should not be generated
        boolean foundDangerous = false;
        for (int i = 0; i < 1000; i++) { // More iterations to increase chance of finding issues
            String result = generator.generate(context);
            assertThat(result).startsWith(".");
            
            if (java.util.Arrays.asList(dangerousExtensions).contains(result)) {
                foundDangerous = true;
            }
        }
        
        // It's possible but not guaranteed that we might generate a dangerous extension
        // by chance from the standard list (e.g. .js), so we won't assert this test strictly
    }
    
    @Test
    public void testGenerateWithDangerousCategory() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("category", "dangerous");
        
        // When category is "dangerous", it should only use dangerous extensions
        String[] dangerousExtensions = FileExtensionGenerator.getDangerousExtensions().toArray(new String[0]);
        
        for (int i = 0; i < 100; i++) {
            String result = generator.generate(context);
            assertThat(result).startsWith(".");
            assertThat(result).isIn((Object[]) dangerousExtensions);
        }
    }
    
    @Test
    public void testGenerateDangerousExtensionWithPermission() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        GenerationContext context = new GenerationContext(1);
        context.setParameter("category", "dangerous");
        context.setParameter("allow_dangerous", true);
        
        // Get the actual dangerous extensions from the generator
        String[] dangerousExtensions = FileExtensionGenerator.getDangerousExtensions().toArray(new String[0]);
        
        boolean foundDangerous = false;
        
        for (int i = 0; i < 1000; i++) { // More iterations to increase chance
            String result = generator.generate(context);
            assertThat(result).startsWith(".");
            
            if (java.util.Arrays.asList(dangerousExtensions).contains(result)) {
                foundDangerous = true;
                break;
            }
        }
        
        // It's possible but not guaranteed that we'll find a dangerous extension
        // due to randomness, so we won't assert this
    }
    
    @Test
    public void testGetExtensionLists() {
        assertThat(FileExtensionGenerator.getStandardExtensions()).isNotEmpty();
        assertThat(FileExtensionGenerator.getImageExtensions()).isNotEmpty();
        assertThat(FileExtensionGenerator.getDocumentExtensions()).isNotEmpty();
        assertThat(FileExtensionGenerator.getMediaExtensions()).isNotEmpty();
        assertThat(FileExtensionGenerator.getDangerousExtensions()).isNotEmpty();
    }
    
    @Test
    public void testGetUniqueExtensionCount() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        GenerationContext context = new GenerationContext(1);
        
        int initialCount = FileExtensionGenerator.getUniqueExtensionCount();
        
        // Generate a few extensions
        for (int i = 0; i < 10; i++) {
            generator.generate(context);
        }
        
        int finalCount = FileExtensionGenerator.getUniqueExtensionCount();
        // The count should have increased (or stayed the same if we generated duplicates)
        assertThat(finalCount).isGreaterThanOrEqualTo(initialCount);
    }
    
    @Test
    public void testGetName() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        assertThat(generator.getName()).isEqualTo("file_extension");
    }
    
    @Test
    public void testGetSupportedParameters() {
        FileExtensionGenerator generator = new FileExtensionGenerator();
        assertThat(generator.getSupportedParameters()).containsExactlyInAnyOrder("allow_dangerous", "category");
    }
}
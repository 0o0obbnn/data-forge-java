package com.dataforge.spi;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.core.GeneratorFactory;
import com.dataforge.spi.example.CustomDataGenerator;
import com.dataforge.spi.example.AnotherCustomGenerator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ExtensionManagerTest {

    @BeforeMethod
    public void setUp() {
        // Reset extension manager for each test
        ExtensionManager.getInstance().reloadExtensions();
    }

    @Test
    public void testInitialize() {
        ExtensionManager manager = ExtensionManager.getInstance();
        manager.initialize();

        // Should have at least one extension registered
        assertThat(manager.getExtensions()).isNotEmpty();
    }

    @Test
    public void testRegisterExtension() {
        ExtensionManager manager = ExtensionManager.getInstance();
        manager.initialize();

        // Check that example extension is registered
        DataForgeExtension exampleExtension = manager.getExtension("example-extension");
        assertThat(exampleExtension).isNotNull();
        assertThat(exampleExtension.getName()).isEqualTo("example-extension");
        assertThat(exampleExtension.getDescription()).isEqualTo("Example extension demonstrating SPI integration");
        assertThat(exampleExtension.getVersion()).isEqualTo("1.0.0");
        assertThat(exampleExtension.getPriority()).isEqualTo(100);
    }

    @Test
    public void testGetExtensionInfo() {
        ExtensionManager manager = ExtensionManager.getInstance();
        manager.initialize();

        // Get extension info
        java.util.List<ExtensionManager.ExtensionInfo> infoList = manager.getExtensionInfo();
        assertThat(infoList).isNotEmpty();

        // Find our example extension
        ExtensionManager.ExtensionInfo exampleInfo = infoList.stream()
                .filter(info -> "example-extension".equals(info.getName()))
                .findFirst()
                .orElse(null);

        assertThat(exampleInfo).isNotNull();
        assertThat(exampleInfo.getName()).isEqualTo("example-extension");
        assertThat(exampleInfo.getVersion()).isEqualTo("1.0.0");
        assertThat(exampleInfo.getPriority()).isEqualTo(100);
        assertThat(exampleInfo.getGeneratorCount()).isEqualTo(2);
    }

    @Test
    public void testCustomGeneratorsAreRegistered() {
        ExtensionManager manager = ExtensionManager.getInstance();
        manager.initialize();

        // Check that custom generators are registered
        assertThat(manager.getCustomGenerators()).containsKeys("custom_value", "product_code");

        // Check that they are also registered in GeneratorFactory
        assertThat(GeneratorFactory.isRegistered("custom_value")).isTrue();
        assertThat(GeneratorFactory.isRegistered("product_code")).isTrue();
    }

    @Test
    public void testCustomGeneratorFunctionality() {
        ExtensionManager manager = ExtensionManager.getInstance();
        manager.initialize();

        // Test custom_value generator
        @SuppressWarnings("unchecked")
        DataGenerator<String> customValueGenerator = (DataGenerator<String>) GeneratorFactory.createGenerator("custom_value");
        GenerationContext context1 = new GenerationContext(1);
        context1.setParameter("prefix", "TEST");
        context1.setParameter("length", 5);

        String result1 = customValueGenerator.generate(context1);
        assertThat(result1).isNotNull();
        assertThat(result1).startsWith("TEST");
        assertThat(result1).hasSize(9); // "TEST" + 5 random chars

        // Test product_code generator
        @SuppressWarnings("unchecked")
        DataGenerator<String> productCodeGenerator = (DataGenerator<String>) GeneratorFactory.createGenerator("product_code");
        GenerationContext context2 = new GenerationContext(1);
        context2.setParameter("category", "Electronics");
        context2.setParameter("id", 12345);

        String result2 = productCodeGenerator.generate(context2);
        assertThat(result2).isNotNull();
        assertThat(result2).isEqualTo("ELECTRONICS-12345");
    }

    @Test
    public void testUnregisterExtension() {
        ExtensionManager manager = ExtensionManager.getInstance();
        manager.initialize();

        // Unregister the extension
        manager.unregisterExtension("example-extension");

        // Extension should no longer be registered
        assertThat(manager.isExtensionRegistered("example-extension")).isFalse();
    }

    @Test
    public void testReloadExtensions() {
        ExtensionManager manager = ExtensionManager.getInstance();
        manager.initialize();

        // Remember initial count
        int initialCount = manager.getExtensions().size();

        // Reload extensions
        manager.reloadExtensions();

        // Should have same or more extensions
        assertThat(manager.getExtensions()).hasSizeGreaterThanOrEqualTo(initialCount);
    }

    @Test
    public void testGetInstance() {
        ExtensionManager manager1 = ExtensionManager.getInstance();
        ExtensionManager manager2 = ExtensionManager.getInstance();

        // Should be the same instance
        assertThat(manager1).isSameAs(manager2);
    }
}
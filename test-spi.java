import com.dataforge.spi.ExtensionManager;
import com.dataforge.core.DataGenerator;
import java.util.List;

public class TestSpi {
    public static void main(String[] args) {
        try {
            ExtensionManager manager = new ExtensionManager();
            List<DataGenerator<?>> generators = manager.getAllGenerators();
            
            System.out.println("SPI Extension Test Results:");
            System.out.println("Total generators loaded: " + generators.size());
            
            for (DataGenerator<?> generator : generators) {
                System.out.println("  - " + generator.getName() + 
                                 " (supports: " + generator.getSupportedParameters() + ")");
            }
            
            // Test specific generators
            String[] testGenerators = {"name", "enhanced_location", "phone", "file_extension"};
            for (String genName : testGenerators) {
                DataGenerator<?> gen = manager.getGenerator(genName);
                if (gen != null) {
                    System.out.println("✅ " + genName + " generator loaded successfully");
                } else {
                    System.out.println("❌ " + genName + " generator not found");
                }
            }
            
        } catch (Exception e) {
            System.err.println("SPI test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
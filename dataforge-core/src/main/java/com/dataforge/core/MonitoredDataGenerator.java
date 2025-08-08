package com.dataforge.core;

/**
 * 带性能监控的数据生成器包装器
 */
public class MonitoredDataGenerator<T> implements DataGenerator<T> {
    
    private final DataGenerator<T> baseGenerator;
    private final String generatorName;
    private final PerformanceMonitor monitor;
    
    public MonitoredDataGenerator(DataGenerator<T> baseGenerator, String generatorName) {
        this.baseGenerator = baseGenerator;
        this.generatorName = generatorName;
        this.monitor = PerformanceMonitor.getInstance();
    }
    
    @Override
    public T generate(GenerationContext context) {
        PerformanceMonitor.OperationContext operationContext = monitor.startOperation(generatorName);
        
        try {
            T result = baseGenerator.generate(context);
            monitor.endOperation(operationContext, 1);
            return result;
        } catch (Exception e) {
            monitor.endOperation(operationContext, 0);
            throw e;
        }
    }
    
    @Override
    public String getName() {
        return baseGenerator.getName();
    }
    
    @Override
    public java.util.List<String> getSupportedParameters() {
        return baseGenerator.getSupportedParameters();
    }
    
    public DataGenerator<T> getBaseGenerator() {
        return baseGenerator;
    }
}
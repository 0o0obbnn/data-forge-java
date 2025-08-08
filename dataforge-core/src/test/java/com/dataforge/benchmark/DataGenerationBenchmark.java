package com.dataforge.benchmark;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;
import com.dataforge.generators.basic.NameGenerator;
import com.dataforge.examples.CachedNameGenerator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * JMH benchmark for comparing cached vs non-cached data generation performance.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
public class DataGenerationBenchmark {
    
    private DataGenerator<String> nameGenerator;
    private CachedNameGenerator cachedNameGenerator;
    private GenerationContext context;
    
    @Setup
    public void setup() {
        nameGenerator = new NameGenerator();
        cachedNameGenerator = new CachedNameGenerator();
        context = new GenerationContext(1);
    }
    
    @Benchmark
    public void testNonCachedGeneration(Blackhole blackhole) {
        String result = nameGenerator.generate(context);
        blackhole.consume(result);
    }
    
    @Benchmark
    public void testCachedGeneration(Blackhole blackhole) {
        String result = cachedNameGenerator.generate(context);
        blackhole.consume(result);
    }
}
package com.dataforge.generators.numeric;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * 统计分布数值生成器
 * 生成符合各种统计分布的数值数据
 */
public class StatisticalDistributionGenerator implements DataGenerator<Double> {
    
    private static final Random random = new Random();
    
    private final DistributionType distributionType;
    private final double parameter1;
    private final double parameter2;
    private final int precision;
    
    public enum DistributionType {
        NORMAL,             // 正态分布 (均值, 标准差)
        UNIFORM,            // 均匀分布 (最小值, 最大值)
        EXPONENTIAL,        // 指数分布 (λ)
        POISSON,            // 泊松分布 (λ) - 返回整数
        GAMMA,              // 伽马分布 (形状参数α, 尺度参数β)
        BETA,               // 贝塔分布 (α, β)
        WEIBULL,            // 威布尔分布 (形状参数k, 尺度参数λ)
        LOGNORMAL,          // 对数正态分布 (均值μ, 标准差σ)
        CHI_SQUARE,         // 卡方分布 (自由度)
        STUDENT_T,          // t分布 (自由度)
        F_DISTRIBUTION,     // F分布 (自由度1, 自由度2)
        BINOMIAL,           // 二项分布 (试验次数n, 成功概率p)
        GEOMETRIC,          // 几何分布 (成功概率p)
        NEGATIVE_BINOMIAL,  // 负二项分布 (成功次数r, 成功概率p)
        PARETO,             // 帕累托分布 (尺度参数xm, 形状参数α)
        CAUCHY,             // 柯西分布 (位置参数x0, 尺度参数γ)
        LAPLACE             // 拉普拉斯分布 (位置参数μ, 尺度参数b)
    }
    
    public StatisticalDistributionGenerator() {
        this(DistributionType.NORMAL, 0.0, 1.0, 4);
    }
    
    public StatisticalDistributionGenerator(DistributionType distributionType, double parameter1, double parameter2) {
        this(distributionType, parameter1, parameter2, 4);
    }
    
    public StatisticalDistributionGenerator(DistributionType distributionType, double parameter1, double parameter2, int precision) {
        this.distributionType = distributionType;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.precision = precision;
    }
    
    @Override
    public Double generate(GenerationContext context) {
        double value = generateDistributionValue();
        return roundToPrecision(value, precision);
    }
    
    /**
     * 根据分布类型生成数值
     */
    private double generateDistributionValue() {
        switch (distributionType) {
            case NORMAL:
                return generateNormal(parameter1, parameter2);
            case UNIFORM:
                return generateUniform(parameter1, parameter2);
            case EXPONENTIAL:
                return generateExponential(parameter1);
            case POISSON:
                return generatePoisson(parameter1);
            case GAMMA:
                return generateGamma(parameter1, parameter2);
            case BETA:
                return generateBeta(parameter1, parameter2);
            case WEIBULL:
                return generateWeibull(parameter1, parameter2);
            case LOGNORMAL:
                return generateLogNormal(parameter1, parameter2);
            case CHI_SQUARE:
                return generateChiSquare(parameter1);
            case STUDENT_T:
                return generateStudentT(parameter1);
            case F_DISTRIBUTION:
                return generateFDistribution(parameter1, parameter2);
            case BINOMIAL:
                return generateBinomial((int)parameter1, parameter2);
            case GEOMETRIC:
                return generateGeometric(parameter1);
            case NEGATIVE_BINOMIAL:
                return generateNegativeBinomial((int)parameter1, parameter2);
            case PARETO:
                return generatePareto(parameter1, parameter2);
            case CAUCHY:
                return generateCauchy(parameter1, parameter2);
            case LAPLACE:
                return generateLaplace(parameter1, parameter2);
            default:
                return generateNormal(0.0, 1.0);
        }
    }
    
    /**
     * 正态分布 N(μ, σ²)
     */
    private double generateNormal(double mean, double stdDev) {
        return random.nextGaussian() * stdDev + mean;
    }
    
    /**
     * 均匀分布 U(a, b)
     */
    private double generateUniform(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
    
    /**
     * 指数分布 Exp(λ)
     */
    private double generateExponential(double lambda) {
        return -Math.log(1 - random.nextDouble()) / lambda;
    }
    
    /**
     * 泊松分布 Pois(λ)
     */
    private double generatePoisson(double lambda) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;
        
        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);
        
        return k - 1;
    }
    
    /**
     * 伽马分布 Gamma(α, β)
     */
    private double generateGamma(double alpha, double beta) {
        // 使用Marsaglia and Tsang方法
        if (alpha < 1) {
            return generateGamma(alpha + 1, beta) * Math.pow(random.nextDouble(), 1.0 / alpha);
        }
        
        double d = alpha - 1.0 / 3.0;
        double c = 1.0 / Math.sqrt(9.0 * d);
        
        while (true) {
            double x = random.nextGaussian();
            double v = Math.pow(1 + c * x, 3);
            
            if (v > 0) {
                double u = random.nextDouble();
                if (u < 1 - 0.0331 * Math.pow(x, 4) || 
                    Math.log(u) < 0.5 * x * x + d * (1 - v + Math.log(v))) {
                    return d * v * beta;
                }
            }
        }
    }
    
    /**
     * 贝塔分布 Beta(α, β)
     */
    private double generateBeta(double alpha, double beta) {
        double x = generateGamma(alpha, 1.0);
        double y = generateGamma(beta, 1.0);
        return x / (x + y);
    }
    
    /**
     * 威布尔分布 Weibull(k, λ)
     */
    private double generateWeibull(double k, double lambda) {
        return lambda * Math.pow(-Math.log(1 - random.nextDouble()), 1.0 / k);
    }
    
    /**
     * 对数正态分布 LogNormal(μ, σ)
     */
    private double generateLogNormal(double mu, double sigma) {
        return Math.exp(generateNormal(mu, sigma));
    }
    
    /**
     * 卡方分布 χ²(ν)
     */
    private double generateChiSquare(double degreesOfFreedom) {
        return generateGamma(degreesOfFreedom / 2.0, 2.0);
    }
    
    /**
     * t分布 t(ν)
     */
    private double generateStudentT(double degreesOfFreedom) {
        double z = random.nextGaussian();
        double chiSquare = generateChiSquare(degreesOfFreedom);
        return z / Math.sqrt(chiSquare / degreesOfFreedom);
    }
    
    /**
     * F分布 F(ν₁, ν₂)
     */
    private double generateFDistribution(double df1, double df2) {
        double chiSquare1 = generateChiSquare(df1);
        double chiSquare2 = generateChiSquare(df2);
        return (chiSquare1 / df1) / (chiSquare2 / df2);
    }
    
    /**
     * 二项分布 Binomial(n, p)
     */
    private double generateBinomial(int n, double p) {
        int successes = 0;
        for (int i = 0; i < n; i++) {
            if (random.nextDouble() < p) {
                successes++;
            }
        }
        return successes;
    }
    
    /**
     * 几何分布 Geometric(p)
     */
    private double generateGeometric(double p) {
        return Math.ceil(Math.log(1 - random.nextDouble()) / Math.log(1 - p));
    }
    
    /**
     * 负二项分布 NegativeBinomial(r, p)
     */
    private double generateNegativeBinomial(int r, double p) {
        double gamma = generateGamma(r, (1 - p) / p);
        return generatePoisson(gamma);
    }
    
    /**
     * 帕累托分布 Pareto(xm, α)
     */
    private double generatePareto(double xm, double alpha) {
        return xm / Math.pow(1 - random.nextDouble(), 1.0 / alpha);
    }
    
    /**
     * 柯西分布 Cauchy(x₀, γ)
     */
    private double generateCauchy(double x0, double gamma) {
        return x0 + gamma * Math.tan(Math.PI * (random.nextDouble() - 0.5));
    }
    
    /**
     * 拉普拉斯分布 Laplace(μ, b)
     */
    private double generateLaplace(double mu, double b) {
        double u = random.nextDouble() - 0.5;
        return mu - b * Math.signum(u) * Math.log(1 - 2 * Math.abs(u));
    }
    
    /**
     * 四舍五入到指定精度
     */
    private double roundToPrecision(double value, int precision) {
        double factor = Math.pow(10, precision);
        return Math.round(value * factor) / factor;
    }
    
    /**
     * 生成分布样本数组
     */
    public double[] generateSample(int sampleSize) {
        double[] sample = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            sample[i] = generateDistributionValue();
        }
        return sample;
    }
    
    /**
     * 计算样本统计信息
     */
    public static SampleStatistics calculateStatistics(double[] sample) {
        if (sample == null || sample.length == 0) {
            return new SampleStatistics(0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        }
        
        int n = sample.length;
        
        // 计算均值
        double sum = 0;
        for (double value : sample) {
            sum += value;
        }
        double mean = sum / n;
        
        // 计算方差和标准差
        double sumSquaredDifferences = 0;
        for (double value : sample) {
            sumSquaredDifferences += Math.pow(value - mean, 2);
        }
        double variance = sumSquaredDifferences / (n - 1);
        double stdDev = Math.sqrt(variance);
        
        // 计算最小值和最大值
        double min = sample[0];
        double max = sample[0];
        for (double value : sample) {
            if (value < min) min = value;
            if (value > max) max = value;
        }
        
        // 计算中位数
        double[] sortedSample = sample.clone();
        java.util.Arrays.sort(sortedSample);
        double median;
        if (n % 2 == 0) {
            median = (sortedSample[n/2 - 1] + sortedSample[n/2]) / 2.0;
        } else {
            median = sortedSample[n/2];
        }
        
        // 计算偏度和峰度
        double skewness = calculateSkewness(sample, mean, stdDev);
        double kurtosis = calculateKurtosis(sample, mean, stdDev);
        
        return new SampleStatistics(n, mean, variance, stdDev, min, max, median, skewness, kurtosis);
    }
    
    /**
     * 计算偏度
     */
    private static double calculateSkewness(double[] sample, double mean, double stdDev) {
        if (stdDev == 0) return 0;
        
        double sum = 0;
        for (double value : sample) {
            sum += Math.pow((value - mean) / stdDev, 3);
        }
        return sum / sample.length;
    }
    
    /**
     * 计算峰度
     */
    private static double calculateKurtosis(double[] sample, double mean, double stdDev) {
        if (stdDev == 0) return 0;
        
        double sum = 0;
        for (double value : sample) {
            sum += Math.pow((value - mean) / stdDev, 4);
        }
        return (sum / sample.length) - 3; // 减去3得到超峰度
    }
    
    /**
     * 样本统计信息类
     */
    public static class SampleStatistics {
        public final int sampleSize;
        public final double mean;
        public final double variance;
        public final double standardDeviation;
        public final double minimum;
        public final double maximum;
        public final double median;
        public final double skewness;
        public final double kurtosis;
        
        public SampleStatistics(int sampleSize, double mean, double variance, double standardDeviation,
                              double minimum, double maximum, double median, double skewness, double kurtosis) {
            this.sampleSize = sampleSize;
            this.mean = mean;
            this.variance = variance;
            this.standardDeviation = standardDeviation;
            this.minimum = minimum;
            this.maximum = maximum;
            this.median = median;
            this.skewness = skewness;
            this.kurtosis = kurtosis;
        }
        
        @Override
        public String toString() {
            return String.format(
                "样本统计信息:\\n" +
                "样本大小: %d\\n" +
                "均值: %.4f\\n" +
                "方差: %.4f\\n" +
                "标准差: %.4f\\n" +
                "最小值: %.4f\\n" +
                "最大值: %.4f\\n" +
                "中位数: %.4f\\n" +
                "偏度: %.4f\\n" +
                "峰度: %.4f",
                sampleSize, mean, variance, standardDeviation,
                minimum, maximum, median, skewness, kurtosis
            );
        }
    }
    
    /**
     * 生成带统计信息的报告
     */
    public String generateStatisticalReport(int sampleSize) {
        double[] sample = generateSample(sampleSize);
        SampleStatistics stats = calculateStatistics(sample);
        
        StringBuilder report = new StringBuilder();
        report.append("分布类型: ").append(distributionType).append("\\n");
        report.append("参数1: ").append(parameter1).append("\\n");
        report.append("参数2: ").append(parameter2).append("\\n");
        report.append("\\n");
        report.append(stats.toString());
        
        return report.toString();
    }
    
    /**
     * 获取分布的理论统计信息
     */
    public String getTheoreticalStatistics() {
        StringBuilder info = new StringBuilder();
        info.append("分布: ").append(distributionType).append("\\n");
        
        switch (distributionType) {
            case NORMAL:
                info.append("理论均值: ").append(parameter1).append("\\n");
                info.append("理论标准差: ").append(parameter2).append("\\n");
                break;
            case UNIFORM:
                double uniformMean = (parameter1 + parameter2) / 2.0;
                double uniformVar = Math.pow(parameter2 - parameter1, 2) / 12.0;
                info.append("理论均值: ").append(uniformMean).append("\\n");
                info.append("理论方差: ").append(uniformVar).append("\\n");
                break;
            case EXPONENTIAL:
                info.append("理论均值: ").append(1.0 / parameter1).append("\\n");
                info.append("理论方差: ").append(1.0 / (parameter1 * parameter1)).append("\\n");
                break;
            case POISSON:
                info.append("理论均值: ").append(parameter1).append("\\n");
                info.append("理论方差: ").append(parameter1).append("\\n");
                break;
            default:
                info.append("参数1: ").append(parameter1).append("\\n");
                info.append("参数2: ").append(parameter2).append("\\n");
                break;
        }
        
        return info.toString();
    }
}
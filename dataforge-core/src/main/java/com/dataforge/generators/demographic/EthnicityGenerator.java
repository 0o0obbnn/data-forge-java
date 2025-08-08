package com.dataforge.generators.demographic;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generates a Chinese ethnicity from the 56 officially recognized ethnic groups.
 */
public class EthnicityGenerator implements DataGenerator<String> {

    private static final List<String> ETHNICITIES = Arrays.asList(
            "汉族", "壮族", "满族", "回族", "苗族", "维吾尔族", "土家族", "彝族", "蒙古族", "藏族",
            "布依族", "侗族", "瑶族", "朝鲜族", "白族", "哈尼族", "哈萨克族", "黎族", "傣族", "畲族",
            "傈僳族", "仡佬族", "东乡族", "高山族", "拉祜族", "水族", "佤族", "纳西族", "羌族", "土族",
            "仫佬族", "锡伯族", "柯尔克孜族", "达斡尔族", "景颇族", "毛南族", "撒拉族", "布朗族", "塔吉克族", "阿昌族",
            "普米族", "鄂温克族", "怒族", "京族", "基诺族", "德昂族", "保安族", "俄罗斯族", "裕固族", "乌孜别克族",
            "门巴族", "鄂伦春族", "独龙族", "塔塔尔族", "赫哲族", "珞巴族"
    );

    private final double hanRatio;

    /**
     * Creates a generator with a default 92% probability of generating "汉族" (Han).
     */
    public EthnicityGenerator() {
        this(0.92);
    }

    /**
     * Creates a generator with a specific probability for generating "汉族" (Han).
     *
     * @param hanRatio The probability (0.0 to 1.0) of generating "汉族".
     */
    public EthnicityGenerator(double hanRatio) {
        if (hanRatio < 0.0 || hanRatio > 1.0) {
            throw new IllegalArgumentException("Han ratio must be between 0.0 and 1.0.");
        }
        this.hanRatio = hanRatio;
    }

    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        if (random.nextDouble() < hanRatio) {
            return "汉族";
        } else {
            // Exclude Han from the random selection of minorities
            return ETHNICITIES.get(1 + random.nextInt(ETHNICITIES.size() - 1));
        }
    }
}

package com.dataforge.generators.basic;

import com.dataforge.core.DataGenerator;
import java.util.Random;

import com.dataforge.core.GenerationContext;

/**
 * Generates random occupation/position titles.
 */
public class OccupationGenerator implements DataGenerator<String> {

    public static final String[] OCCUPATIONS = {
        "软件工程师", "项目经理", "产品经理", "数据分析师", "UI/UX设计师",
        "教师", "医生", "律师", "会计师", "市场专员",
        "销售经理", "人力资源经理", "行政助理", "客服代表", "运营总监",
        "土木工程师", "机械工程师", "电气工程师", "建筑师", "顾问"
    };

    private final Random random = new Random();

    /**
     * Generates a random occupation from a predefined list.
     * @param context The generation context (not used by this generator).
     * @return A randomly selected occupation as a String.
     */
    @Override
    public String generate(GenerationContext context) {
        return OCCUPATIONS[random.nextInt(OCCUPATIONS.length)];
    }
}

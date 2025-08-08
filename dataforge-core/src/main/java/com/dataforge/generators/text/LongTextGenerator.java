package com.dataforge.generators.text;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Long text generator for generating paragraphs of text with configurable length.
 */
public class LongTextGenerator implements DataGenerator<String> {
    
    // Sample Chinese text fragments
    private static final List<String> CHINESE_FRAGMENTS = Arrays.asList(
        "这是一个示例文本片段，用于生成长文本数据。",
        "在自然语言处理和机器学习领域，文本数据是重要的资源。",
        "数据生成工具可以帮助开发者和测试人员创建大量的测试数据。",
        "通过使用不同的文本片段，我们可以模拟真实世界的文本内容。",
        "长文本生成器可以根据需要生成指定长度的文本内容。",
        "文本可以包含各种标点符号和特殊字符，以增加真实性。",
        "在实际应用中，文本数据可能来自不同的来源和领域。",
        "为了提高生成效率，我们可以使用预定义的文本片段库。",
        "文本生成器应该支持多种语言和字符集。",
        "通过随机组合文本片段，可以创建丰富多样的文本内容。"
    );
    
    // Sample English text fragments (Lorem Ipsum style)
    private static final List<String> ENGLISH_FRAGMENTS = Arrays.asList(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.",
        "Duis aute irure dolor in reprehenderit in voluptate velit esse.",
        "Excepteur sint occaecat cupidatat non proident, sunt in culpa.",
        "Sed ut perspiciatis unde omnis iste natus error sit voluptatem.",
        "Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit.",
        "Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet.",
        "Ut enim ad minima veniam, quis nostrum exercitationem ullam.",
        "Quis autem vel eum iure reprehenderit qui in ea voluptate velit."
    );
    
    // Common punctuation marks
    private static final List<String> PUNCTUATION = Arrays.asList(
        ".", ".", ".", "!", "?", ";", ","
    );
    
    private final int minLength;
    private final int maxLength;
    private final boolean mixLanguages;
    
    /**
     * Creates a long text generator with default settings (100-500 characters).
     */
    public LongTextGenerator() {
        this(100, 500, false);
    }
    
    /**
     * Creates a long text generator.
     * 
     * @param minLength minimum length of the generated text
     * @param maxLength maximum length of the generated text
     * @param mixLanguages whether to mix Chinese and English text
     */
    public LongTextGenerator(int minLength, int maxLength, boolean mixLanguages) {
        // Validate parameters
        if (minLength <= 0) {
            throw new IllegalArgumentException("minLength must be positive");
        }
        if (maxLength < minLength) {
            throw new IllegalArgumentException("maxLength must be >= minLength");
        }
        
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.mixLanguages = mixLanguages;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Determine the target length
        int targetLength = minLength + random.nextInt(maxLength - minLength + 1);
        
        StringBuilder text = new StringBuilder();
        
        // Select the language fragments to use
        List<String> fragments = ENGLISH_FRAGMENTS;
        if (mixLanguages || random.nextBoolean()) {
            fragments = CHINESE_FRAGMENTS;
        }
        
        // Generate text by randomly selecting and combining fragments
        while (text.length() < targetLength) {
            // Add a fragment
            String fragment = fragments.get(random.nextInt(fragments.size()));
            
            // If adding this fragment would exceed the target length significantly, 
            // trim it to fit better
            if (text.length() + fragment.length() > targetLength + 50) {
                int charsNeeded = targetLength - text.length();
                if (charsNeeded > 10) { // Only trim if we need more than 10 chars
                    fragment = fragment.substring(0, Math.min(fragment.length(), charsNeeded));
                } else {
                    // If we're close to target, break the loop
                    break;
                }
            }
            
            text.append(fragment);
            
            // Randomly add punctuation and spaces
            if (random.nextInt(3) == 0) { // 33% chance
                text.append(PUNCTUATION.get(random.nextInt(PUNCTUATION.size())));
                text.append(" ");
            }
            
            // Occasionally add a line break for paragraph structure
            if (random.nextInt(5) == 0 && text.length() > 50) { // 20% chance after 50 chars
                text.append("\n");
            }
        }
        
        // Ensure minimum length is met
        while (text.length() < minLength) {
            String fragment = fragments.get(random.nextInt(fragments.size()));
            text.append(fragment);
        }
        
        // Make sure we have enough content
        if (text.length() < targetLength) {
            // Add more fragments until we reach target length
            while (text.length() < targetLength) {
                String fragment = fragments.get(random.nextInt(fragments.size()));
                if (text.length() + fragment.length() <= targetLength) {
                    text.append(fragment);
                } else {
                    // Add a substring of the fragment to reach exactly target length
                    int needed = targetLength - text.length();
                    if (needed > 0) {
                        text.append(fragment.substring(0, Math.min(needed, fragment.length())));
                    }
                    break;
                }
            }
        }
        
        // Trim to exact target length if necessary
        if (text.length() > targetLength) {
            // Try to trim at a word boundary
            String result = text.substring(0, targetLength);
            int lastSpace = result.lastIndexOf(' ');
            if (lastSpace > targetLength - 20 && lastSpace > 0) { // If close to the end
                result = result.substring(0, lastSpace);
            }
            return result;
        }
        
        return text.toString();
    }
    
    @Override
    public String getName() {
        return "longtext";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("min_length", "max_length", "mix_languages");
    }
}
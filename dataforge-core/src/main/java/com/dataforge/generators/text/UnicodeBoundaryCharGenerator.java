package com.dataforge.generators.text;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Unicode boundary character generator for generating text with special Unicode characters.
 * Useful for testing Unicode handling and internationalization support.
 */
public class UnicodeBoundaryCharGenerator implements DataGenerator<String> {
    
    // Unicode character categories
    public enum UnicodeCategory {
        CONTROL,          // Control characters (U+0000-U+001F, U+007F-U+009F)
        SURROGATE,        // Surrogate pairs (U+D800-U+DFFF)
        ZERO_WIDTH,       // Zero-width characters (U+200B-U+200F, U+202A-U+202E, U+2060-U+206F)
        COMBINING,        // Combining characters (U+0300-U+036F, U+1AB0-U+1AFF, U+20D0-U+20FF)
        EMOJI,            // Emoji characters
        PRIVATE_USE,      // Private use area (U+E000-U+F8FF, U+F0000-U+FFFFD, U+100000-U+10FFFD)
        VARIATION_SELECTORS, // Variation selectors (U+FE00-U+FE0F, U+E0100-U+E01EF)
        MIXED             // Mixed types
    }
    
    // Sample emoji characters
    private static final List<String> EMOJI_CHARACTERS = Arrays.asList(
        "😀", "😂", "🤣", "😍", "🥰", "😎", "🤩", "🥳", "😭", "😡", 
        "🤯", "🥶", "😱", "🤠", "🥴", "😈", "👻", "👽", "🤖", "👾",
        "👋", "👍", "👏", "🙌", "👌", "🤝", "💪", "🦾", "🦿", "🧠"
    );
    
    // Zero-width characters
    private static final List<Character> ZERO_WIDTH_CHARS = Arrays.asList(
        '\u200B', '\u200C', '\u200D', '\u200E', '\u200F',
        '\u202A', '\u202B', '\u202C', '\u202D', '\u202E',
        '\u2060', '\u2061', '\u2062', '\u2063', '\u2064'
    );
    
    // Control characters
    private static final List<Character> CONTROL_CHARS = Arrays.asList(
        '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007',
        '\u0008', '\n', '\u000B', '\u000C', '\r', '\u000E', '\u000F',
        '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017',
        '\u0018', '\u0019', '\u001A', '\u001B', '\u001C', '\u001D', '\u001E', '\u001F',
        '\u007F', '\u0080', '\u0081', '\u0082', '\u0083', '\u0084', '\u0085', '\u0086',
        '\u0087', '\u0088', '\u0089', '\u008A', '\u008B', '\u008C', '\u008D', '\u008E',
        '\u008F', '\u0090', '\u0091', '\u0092', '\u0093', '\u0094', '\u0095', '\u0096',
        '\u0097', '\u0098', '\u0099', '\u009A', '\u009B', '\u009C', '\u009D', '\u009E', '\u009F'
    );
    
    // Combining characters
    private static final List<Character> COMBINING_CHARS = Arrays.asList(
        '\u0300', '\u0301', '\u0302', '\u0303', '\u0304', '\u0305', '\u0306', '\u0307', '\u0308', '\u0309',
        '\u030A', '\u030B', '\u030C', '\u030D', '\u030E', '\u030F', '\u0310', '\u0311', '\u0312', '\u0313',
        '\u0314', '\u0315', '\u0316', '\u0317', '\u0318', '\u0319', '\u031A', '\u031B', '\u031C', '\u031D'
    );
    
    // Private use area characters
    private static final List<Character> PRIVATE_USE_CHARS = Arrays.asList(
        '\uE000', '\uE001', '\uE002', '\uE003', '\uE004', '\uE005', '\uE006', '\uE007', '\uE008', '\uE009'
    );
    
    private final UnicodeCategory category;
    private final int minLength;
    private final int maxLength;
    private final boolean includeEmoji;
    
    /**
     * Creates a Unicode boundary character generator with default settings.
     */
    public UnicodeBoundaryCharGenerator() {
        this(UnicodeCategory.MIXED, 10, 100, true);
    }
    
    /**
     * Creates a Unicode boundary character generator.
     * 
     * @param category the Unicode character category to generate
     * @param minLength minimum length of the generated text
     * @param maxLength maximum length of the generated text
     * @param includeEmoji whether to include emoji characters
     */
    public UnicodeBoundaryCharGenerator(UnicodeCategory category, int minLength, int maxLength, boolean includeEmoji) {
        // Validate parameters
        if (minLength <= 0) {
            throw new IllegalArgumentException("minLength must be positive");
        }
        if (maxLength < minLength) {
            throw new IllegalArgumentException("maxLength must be >= minLength");
        }
        
        this.category = category;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.includeEmoji = includeEmoji;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Determine the target length
        int targetLength = minLength + random.nextInt(maxLength - minLength + 1);
        
        StringBuilder text = new StringBuilder();
        
        // Generate text with Unicode boundary characters
        while (text.length() < targetLength) {
            switch (category) {
                case CONTROL:
                    addControlCharacter(text, random);
                    break;
                case SURROGATE:
                    addSurrogatePair(text, random);
                    break;
                case ZERO_WIDTH:
                    addZeroWidthCharacter(text, random);
                    break;
                case COMBINING:
                    addCombiningCharacter(text, random);
                    break;
                case EMOJI:
                    addEmojiCharacter(text, random);
                    break;
                case PRIVATE_USE:
                    addPrivateUseCharacter(text, random);
                    break;
                case VARIATION_SELECTORS:
                    addVariationSelector(text, random);
                    break;
                case MIXED:
                default:
                    // Randomly select a category
                    UnicodeCategory randomCategory = UnicodeCategory.values()[random.nextInt(UnicodeCategory.values().length - 1)]; // Exclude MIXED
                    switch (randomCategory) {
                        case CONTROL:
                            addControlCharacter(text, random);
                            break;
                        case SURROGATE:
                            addSurrogatePair(text, random);
                            break;
                        case ZERO_WIDTH:
                            addZeroWidthCharacter(text, random);
                            break;
                        case COMBINING:
                            addCombiningCharacter(text, random);
                            break;
                        case EMOJI:
                            addEmojiCharacter(text, random);
                            break;
                        case PRIVATE_USE:
                            addPrivateUseCharacter(text, random);
                            break;
                        case VARIATION_SELECTORS:
                            addVariationSelector(text, random);
                            break;
                    }
                    break;
            }
            
            // Add base characters occasionally
            if (random.nextInt(5) == 0) { // 20% chance
                char baseChar = (char) ('A' + random.nextInt(26));
                text.append(baseChar);
            }
            
            // Include emoji if requested
            if (includeEmoji && random.nextInt(10) == 0) { // 10% chance
                addEmojiCharacter(text, random);
            }
        }
        
        // Ensure minimum length is met
        while (text.length() < minLength) {
            char baseChar = (char) ('A' + random.nextInt(26));
            text.append(baseChar);
        }
        
        return text.toString();
    }
    
    /**
     * Adds a control character to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addControlCharacter(StringBuilder text, Random random) {
        char controlChar = CONTROL_CHARS.get(random.nextInt(CONTROL_CHARS.size()));
        text.append(controlChar);
    }
    
    /**
     * Adds a surrogate pair to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addSurrogatePair(StringBuilder text, Random random) {
        // High surrogate: U+D800 to U+DBFF
        char highSurrogate = (char) (0xD800 + random.nextInt(0x400));
        // Low surrogate: U+DC00 to U+DFFF
        char lowSurrogate = (char) (0xDC00 + random.nextInt(0x400));
        text.append(highSurrogate).append(lowSurrogate);
    }
    
    /**
     * Adds a zero-width character to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addZeroWidthCharacter(StringBuilder text, Random random) {
        char zeroWidthChar = ZERO_WIDTH_CHARS.get(random.nextInt(ZERO_WIDTH_CHARS.size()));
        text.append(zeroWidthChar);
    }
    
    /**
     * Adds a combining character to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addCombiningCharacter(StringBuilder text, Random random) {
        // Add a base character first
        char baseChar = (char) ('A' + random.nextInt(26));
        text.append(baseChar);
        
        // Then add a combining character
        char combiningChar = COMBINING_CHARS.get(random.nextInt(COMBINING_CHARS.size()));
        text.append(combiningChar);
    }
    
    /**
     * Adds an emoji character to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addEmojiCharacter(StringBuilder text, Random random) {
        String emoji = EMOJI_CHARACTERS.get(random.nextInt(EMOJI_CHARACTERS.size()));
        text.append(emoji);
    }
    
    /**
     * Adds a private use character to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addPrivateUseCharacter(StringBuilder text, Random random) {
        char privateUseChar = PRIVATE_USE_CHARS.get(random.nextInt(PRIVATE_USE_CHARS.size()));
        text.append(privateUseChar);
    }
    
    /**
     * Adds a variation selector to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addVariationSelector(StringBuilder text, Random random) {
        // Add a base character first
        char baseChar = (char) ('A' + random.nextInt(26));
        text.append(baseChar);
        
        // Then add a variation selector
        char variationSelector = (char) (0xFE00 + random.nextInt(16));
        text.append(variationSelector);
    }
    
    @Override
    public String getName() {
        return "unicode_boundary";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("category", "min_length", "max_length", "include_emoji");
    }
}
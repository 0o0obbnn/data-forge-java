package com.dataforge.generators.text;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Special character generator for generating text with various special characters.
 * Useful for testing input validation and handling of unusual characters.
 */
public class SpecialCharGenerator implements DataGenerator<String> {
    
    // Categories of special characters
    public enum CharCategory {
        SYMBOLS,           // Symbols: !@#$%^&*()_+-=[]{}|;':",./<>?
        PUNCTUATION,       // Punctuation: .,;?!-—""''()
        MATHEMATICAL,      // Mathematical: ±×÷√∞∫∆∏∑µ≤≥≠≈
        TECHNICAL,         // Technical: ©®™§¶†‡‰‹›€£¥¢
        CURRENCY,          // Currency: $€£¥¢₹₽₿₣₩₪₨₧₯₠₫₮
        BRACKETS,          // Brackets: []{}()⟨⟩⟦⟧⟪⟫⟬⟭
        QUOTES,            // Quotes: ""''""''«»“”‘’ ‹› „‚ «»
        ACCENTS,           // Accents: àáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿ
        MATH_SYMBOLS,      // Math symbols
        GREEK_LETTERS,     // Greek letters
        CYRILLIC,          // Cyrillic
        ARABIC,            // Arabic numerals
        IDEOGRAPHIC,       // Ideographic
        MIXED              // Mixed categories
    }
    
    // Character sets for different categories
    private static final List<Character> SYMBOL_CHARS = new ArrayList<>(Arrays.asList(
        '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '-', '=', '[', ']', '{', '}',
        '|', ';', ':', '\'', '"', ',', '.', '/', '<', '>', '?', '~', '`', '\\', '/'
    ));
    
    private static final List<Character> PUNCTUATION_CHARS = new ArrayList<>(Arrays.asList(
        '.', ',', ';', ':', '?', '!', '-', '—', '"', '"', '\'', '\'', '(', ')'
    ));
    
    private static final List<Character> MATHEMATICAL_CHARS = new ArrayList<>(Arrays.asList(
        '±', '×', '÷', '√', '∞', '∫', '∆', '∏', '∑', 'µ', '≤', '≥', '≠', '≈'
    ));
    
    private static final List<Character> TECHNICAL_CHARS = new ArrayList<>(Arrays.asList(
        '©', '®', '™', '§', '¶', '†', '‡', '‰', '‹', '›', '€', '£', '¥', '¢'
    ));
    
    private static final List<Character> CURRENCY_CHARS = new ArrayList<>(Arrays.asList(
        '$', '€', '£', '¥', '¢', '₹', '₽', '₿', '₣', '₩', '₪', '₨', '₧', '₯', '₠', '₫', '₮'
    ));
    
    private static final List<Character> BRACKET_CHARS = new ArrayList<>(Arrays.asList(
        '[', ']', '{', '}', '(', ')', '⟨', '⟩', '⟦', '⟧', '⟪', '⟫', '⟬', '⟭'
    ));
    
    private static final List<Character> QUOTE_CHARS = new ArrayList<>(Arrays.asList(
        '"', '"', '\'', '\'', '«', '»', '“', '”', '‘', '’', '‹', '›', '„', '‚'
    ));
    
    private static final List<Character> ACCENT_CHARS = new ArrayList<>(Arrays.asList(
        'à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ç', 'è', 'é', 'ê', 'ë', 'ì', 'í', 'î', 'ï',
        'ð', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', 'ø', 'ù', 'ú', 'û', 'ü', 'ý', 'þ', 'ÿ'
    ));
    
    private static final List<Character> MATH_SYMBOL_CHARS = new ArrayList<>(Arrays.asList(
        '∀', '∁', '∂', '∃', '∇', '∈', '∉', '∋', '∌', '∏', '∐', '∑', '∓', '∔', '∕', '\\',
        '∗', '∘', '∙', '√', '∛', '∜', '∝', '∞', '∟', '∠', '∡', '∢', '∣', '∤', '∥', '∦',
        '∧', '∨', '∩', '∪', '∫', '∬', '∭', '∮', '∯', '∰', '∱', '∲', '∳'
    ));
    
    private static final List<Character> GREEK_LETTER_CHARS = new ArrayList<>(Arrays.asList(
        'Α', 'Β', 'Γ', 'Δ', 'Ε', 'Ζ', 'Η', 'Θ', 'Ι', 'Κ', 'Λ', 'Μ', 'Ν', 'Ξ', 'Ο', 'Π',
        'Ρ', 'Σ', 'Τ', 'Υ', 'Φ', 'Χ', 'Ψ', 'Ω', 'α', 'β', 'γ', 'δ', 'ε', 'ζ', 'η', 'θ',
        'ι', 'κ', 'λ', 'μ', 'ν', 'ξ', 'ο', 'π', 'ρ', 'σ', 'τ', 'υ', 'φ', 'χ', 'ψ', 'ω'
    ));
    
    private static final List<Character> CYRILLIC_CHARS = new ArrayList<>(Arrays.asList(
        'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П',
        'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я',
        'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п',
        'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я'
    ));
    
    private static final List<Character> ARABIC_NUMERAL_CHARS = new ArrayList<>(Arrays.asList(
        '٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'
    ));
    
    private static final List<Character> IDEOGRAPHIC_CHARS = new ArrayList<>(Arrays.asList(
        '〇', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '百', '千', '万', '亿', '兆'
    ));
    
    private final CharCategory category;
    private final int minLength;
    private final int maxLength;
    private final boolean includeSpaces;
    private final boolean includeNewlines;
    
    /**
     * Creates a special character generator with default settings.
     */
    public SpecialCharGenerator() {
        this(CharCategory.MIXED, 10, 100, true, true);
    }
    
    /**
     * Creates a special character generator.
     * 
     * @param category the special character category to generate
     * @param minLength minimum length of the generated text
     * @param maxLength maximum length of the generated text
     * @param includeSpaces whether to include spaces
     * @param includeNewlines whether to include newlines
     */
    public SpecialCharGenerator(CharCategory category, int minLength, int maxLength, 
                               boolean includeSpaces, boolean includeNewlines) {
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
        this.includeSpaces = includeSpaces;
        this.includeNewlines = includeNewlines;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Determine the target length
        int targetLength = minLength + random.nextInt(maxLength - minLength + 1);
        
        StringBuilder text = new StringBuilder();
        
        // Select the character set to use
        List<Character> charSet = getCharacterSet(category, random);
        
        // Generate text by randomly selecting and combining characters
        while (text.length() < targetLength) {
            // Add a character
            char character = charSet.get(random.nextInt(charSet.size()));
            text.append(character);
            
            // Add spaces occasionally
            if (includeSpaces && random.nextInt(5) == 0) { // 20% chance
                text.append(' ');
            }
            
            // Add newlines occasionally
            if (includeNewlines && random.nextInt(10) == 0 && text.length() > 20) { // 10% chance after 20 chars
                text.append('\n');
            }
            
            // If mixing categories, occasionally switch to another category
            if (category == CharCategory.MIXED && random.nextInt(8) == 0) { // 12.5% chance
                CharCategory newCategory = CharCategory.values()[random.nextInt(CharCategory.values().length - 1)]; // Exclude MIXED
                charSet = getCharacterSet(newCategory, random);
            }
        }
        
        // Ensure minimum length is met
        while (text.length() < minLength) {
            char character = charSet.get(random.nextInt(charSet.size()));
            text.append(character);
        }
        
        // Make sure we have enough content
        if (text.length() < targetLength) {
            // Add more characters until we reach target length
            while (text.length() < targetLength) {
                char character = charSet.get(random.nextInt(charSet.size()));
                if (text.length() + 1 <= targetLength) {
                    text.append(character);
                } else {
                    // If we're at exact target length, break
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
    
    /**
     * Gets the character set for the specified category.
     * 
     * @param category the character category
     * @param random the random number generator
     * @return the character set
     */
    private List<Character> getCharacterSet(CharCategory category, Random random) {
        switch (category) {
            case SYMBOLS:
                return SYMBOL_CHARS;
            case PUNCTUATION:
                return PUNCTUATION_CHARS;
            case MATHEMATICAL:
                return MATHEMATICAL_CHARS;
            case TECHNICAL:
                return TECHNICAL_CHARS;
            case CURRENCY:
                return CURRENCY_CHARS;
            case BRACKETS:
                return BRACKET_CHARS;
            case QUOTES:
                return QUOTE_CHARS;
            case ACCENTS:
                return ACCENT_CHARS;
            case MATH_SYMBOLS:
                return MATH_SYMBOL_CHARS;
            case GREEK_LETTERS:
                return GREEK_LETTER_CHARS;
            case CYRILLIC:
                return CYRILLIC_CHARS;
            case ARABIC:
                return ARABIC_NUMERAL_CHARS;
            case IDEOGRAPHIC:
                return IDEOGRAPHIC_CHARS;
            case MIXED:
            default:
                // Create a combined character set from multiple categories
                List<Character> mixedChars = new ArrayList<>();
                
                // Add characters from several categories
                mixedChars.addAll(SYMBOL_CHARS);
                mixedChars.addAll(PUNCTUATION_CHARS);
                mixedChars.addAll(MATHEMATICAL_CHARS);
                mixedChars.addAll(TECHNICAL_CHARS);
                mixedChars.addAll(CURRENCY_CHARS);
                mixedChars.addAll(BRACKET_CHARS);
                mixedChars.addAll(QUOTE_CHARS);
                mixedChars.addAll(ACCENT_CHARS);
                mixedChars.addAll(MATH_SYMBOL_CHARS);
                mixedChars.addAll(GREEK_LETTER_CHARS);
                mixedChars.addAll(CYRILLIC_CHARS);
                mixedChars.addAll(ARABIC_NUMERAL_CHARS);
                mixedChars.addAll(IDEOGRAPHIC_CHARS);
                
                return mixedChars;
        }
    }
    
    @Override
    public String getName() {
        return "special_char";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("category", "min_length", "max_length", "include_spaces", "include_newlines");
    }
}
package com.dataforge.generators.text;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Rich text generator for generating HTML or Markdown formatted text.
 * Supports various formatting options like bold, italic, headings, lists, links, etc.
 */
public class RichTextGenerator implements DataGenerator<String> {
    
    // Supported formats
    public enum Format {
        HTML, MARKDOWN
    }
    
    // Sample text fragments for content
    private static final List<String> TEXT_FRAGMENTS = Arrays.asList(
        "This is a sample text fragment for rich text generation.",
        "Rich text can include various formatting options like bold, italic, and underline.",
        "Headings help organize content and make it easier to read.",
        "Lists can be bulleted or numbered to present information clearly.",
        "Links can reference external resources or other sections of the document.",
        "Images can enhance the visual appeal of the content.",
        "Tables can be used to present structured data in a tabular format.",
        "Code blocks can highlight programming code or technical information.",
        "Blockquotes can be used to cite sources or highlight important quotes.",
        "Horizontal rules can separate sections of content."
    );
    
    // Sample link URLs
    private static final List<String> LINK_URLS = Arrays.asList(
        "https://www.example.com",
        "https://www.google.com",
        "https://www.github.com",
        "https://www.stackoverflow.com",
        "https://www.wikipedia.org"
    );
    
    // Sample image URLs
    private static final List<String> IMAGE_URLS = Arrays.asList(
        "https://www.example.com/image1.jpg",
        "https://www.example.com/image2.png",
        "https://www.example.com/image3.gif"
    );
    
    private final Format format;
    private final int minLength;
    private final int maxLength;
    private final boolean includeHeadings;
    private final boolean includeLists;
    private final boolean includeLinks;
    private final boolean includeImages;
    private final boolean includeCodeBlocks;
    
    /**
     * Creates a rich text generator with default settings.
     */
    public RichTextGenerator() {
        this(Format.HTML, 100, 1000, true, true, true, true, true);
    }
    
    /**
     * Creates a rich text generator.
     * 
     * @param format the output format (HTML or Markdown)
     * @param minLength minimum length of the generated text
     * @param maxLength maximum length of the generated text
     * @param includeHeadings whether to include headings
     * @param includeLists whether to include lists
     * @param includeLinks whether to include links
     * @param includeImages whether to include images
     * @param includeCodeBlocks whether to include code blocks
     */
    public RichTextGenerator(Format format, int minLength, int maxLength, 
                             boolean includeHeadings, boolean includeLists, 
                             boolean includeLinks, boolean includeImages, 
                             boolean includeCodeBlocks) {
        // Validate parameters
        if (minLength <= 0) {
            throw new IllegalArgumentException("minLength must be positive");
        }
        if (maxLength < minLength) {
            throw new IllegalArgumentException("maxLength must be >= minLength");
        }
        
        this.format = format;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.includeHeadings = includeHeadings;
        this.includeLists = includeLists;
        this.includeLinks = includeLinks;
        this.includeImages = includeImages;
        this.includeCodeBlocks = includeCodeBlocks;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        // Determine the target length
        int targetLength = minLength + random.nextInt(maxLength - minLength + 1);
        
        StringBuilder text = new StringBuilder();
        
        // Generate rich text content
        while (text.length() < targetLength) {
            // Add a plain text fragment
            if (text.length() > 0) {
                text.append(" ");
            }
            text.append(TEXT_FRAGMENTS.get(random.nextInt(TEXT_FRAGMENTS.size())));

            // Add other elements based on a random choice
            if (includeHeadings && random.nextBoolean()) {
                addHeading(text, random);
            }
            if (includeLists && random.nextBoolean()) {
                addList(text, random);
            }
            if (includeLinks && random.nextBoolean()) {
                addLink(text, random);
            }
            if (includeImages && random.nextBoolean()) {
                addImage(text, random);
            }
            if (includeCodeBlocks && random.nextBoolean()) {
                addCodeBlock(text, random);
            }
        }
        
        // Ensure minimum length is met
        while (text.length() < minLength) {
            text.append(TEXT_FRAGMENTS.get(random.nextInt(TEXT_FRAGMENTS.size())));
        }
        
        return text.toString();
    }
    
    /**
     * Adds a heading to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addHeading(StringBuilder text, Random random) {
        int level = 1 + random.nextInt(3); // H1 to H3
        
        if (format == Format.HTML) {
            text.append("<h").append(level).append(">")
                .append(TEXT_FRAGMENTS.get(random.nextInt(TEXT_FRAGMENTS.size())))
                .append("</h").append(level).append(">");
        } else {
            for (int i = 0; i < level; i++) {
                text.append("#");
            }
            text.append(" ")
                .append(TEXT_FRAGMENTS.get(random.nextInt(TEXT_FRAGMENTS.size())))
                .append("\n");
        }
    }
    
    /**
     * Adds a list to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addList(StringBuilder text, Random random) {
        int itemCount = 2 + random.nextInt(4); // 2-5 items
        
        if (format == Format.HTML) {
            text.append("<ul>");
            for (int i = 0; i < itemCount; i++) {
                text.append("<li>")
                    .append(TEXT_FRAGMENTS.get(random.nextInt(TEXT_FRAGMENTS.size())))
                    .append("</li>");
            }
            text.append("</ul>");
        } else {
            for (int i = 0; i < itemCount; i++) {
                text.append("\n- ")
                    .append(TEXT_FRAGMENTS.get(random.nextInt(TEXT_FRAGMENTS.size())));
            }
            text.append("\n");
        }
    }
    
    /**
     * Adds a link to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addLink(StringBuilder text, Random random) {
        String url = LINK_URLS.get(random.nextInt(LINK_URLS.size()));
        String linkText = TEXT_FRAGMENTS.get(random.nextInt(TEXT_FRAGMENTS.size()));
        
        if (format == Format.HTML) {
            text.append("<a href=\"").append(url).append("\">")
                .append(linkText)
                .append("</a>");
        } else {
            text.append("[").append(linkText).append("](").append(url).append(")");
        }
    }
    
    /**
     * Adds an image to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addImage(StringBuilder text, Random random) {
        String url = IMAGE_URLS.get(random.nextInt(IMAGE_URLS.size()));
        String altText = TEXT_FRAGMENTS.get(random.nextInt(TEXT_FRAGMENTS.size()));
        
        if (format == Format.HTML) {
            text.append("<img src=\"").append(url).append("\" alt=\"")
                .append(altText).append("\" />");
        } else {
            text.append("![").append(altText).append("](").append(url).append(")");
        }
    }
    
    /**
     * Adds a code block to the text.
     * 
     * @param text the text builder
     * @param random the random number generator
     */
    private void addCodeBlock(StringBuilder text, Random random) {
        String codeFragment = TEXT_FRAGMENTS.get(random.nextInt(TEXT_FRAGMENTS.size()));
        
        if (format == Format.HTML) {
            text.append("<pre><code>").append(codeFragment).append("</code></pre>");
        } else {
            text.append("\n```\n").append(codeFragment).append("\n```\n");
        }
    }
    
    @Override
    public String getName() {
        return "richtext";
    }
    
    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList(
            "format", "min_length", "max_length", 
            "include_headings", "include_lists", "include_links", 
            "include_images", "include_code_blocks"
        );
    }
}
package com.dataforge.generators.media;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 文件扩展名生成器
 * 支持生成合法和非法的文件扩展名，通过配置文件加载扩展名列表
 */
public class FileExtensionGenerator implements DataGenerator<String> {

    private static final String STANDARD_EXTENSIONS_FILE = "/config/extensions/standard_extensions.txt";
    private static final String DANGEROUS_EXTENSIONS_FILE = "/config/extensions/dangerous_extensions.txt";
    private static final String IMAGE_EXTENSIONS_FILE = "/config/extensions/image_extensions.txt";
    private static final String DOCUMENT_EXTENSIONS_FILE = "/config/extensions/document_extensions.txt";
    private static final String MEDIA_EXTENSIONS_FILE = "/config/extensions/media_extensions.txt";
    
    private static final List<String> standardExtensions = new ArrayList<>();
    private static final List<String> dangerousExtensions = new ArrayList<>();
    private static final List<String> imageExtensions = new ArrayList<>();
    private static final List<String> documentExtensions = new ArrayList<>();
    private static final List<String> mediaExtensions = new ArrayList<>();
    
    private static final ConcurrentMap<String, Boolean> generatedExtensions = new ConcurrentHashMap<>();
    
    static {
        loadConfiguration();
    }
    
    public FileExtensionGenerator() {
    }
    
    private static void loadConfiguration() {
        loadListFromFile(STANDARD_EXTENSIONS_FILE, standardExtensions, getDefaultStandardExtensions());
        loadListFromFile(DANGEROUS_EXTENSIONS_FILE, dangerousExtensions, getDefaultDangerousExtensions());
        loadListFromFile(IMAGE_EXTENSIONS_FILE, imageExtensions, getDefaultImageExtensions());
        loadListFromFile(DOCUMENT_EXTENSIONS_FILE, documentExtensions, getDefaultDocumentExtensions());
        loadListFromFile(MEDIA_EXTENSIONS_FILE, mediaExtensions, getDefaultMediaExtensions());
    }
    
    private static void loadListFromFile(String fileName, List<String> list, List<String> defaults) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        FileExtensionGenerator.class.getResourceAsStream(fileName), 
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    list.add(line);
                }
            }
        } catch (IOException | NullPointerException e) {
            // Fall back to defaults if file loading fails
            list.addAll(defaults);
        }
    }
    
    private static List<String> getDefaultStandardExtensions() {
        return List.of(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff", ".webp",
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx",
            ".txt", ".csv", ".json", ".xml", ".html", ".css", ".js",
            ".zip", ".rar", ".7z", ".tar", ".gz",
            ".mp3", ".mp4", ".avi", ".mov", ".wmv", ".flv",
            ".exe", ".msi", ".dmg", ".deb", ".rpm"
        );
    }
    
    private static List<String> getDefaultDangerousExtensions() {
        return List.of(
            ".php", ".asp", ".jsp", ".sh", ".bat", ".cmd", ".com",
            ".scr", ".vbs", ".js", ".jar", ".app", ".apk"
        );
    }
    
    private static List<String> getDefaultImageExtensions() {
        return List.of(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff", ".webp", ".svg", ".ico"
        );
    }
    
    private static List<String> getDefaultDocumentExtensions() {
        return List.of(
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt", ".csv"
        );
    }
    
    private static List<String> getDefaultMediaExtensions() {
        return List.of(
            ".mp3", ".mp4", ".avi", ".mov", ".wmv", ".flv", ".wav", ".flac"
        );
    }
    
    /**
     * Gets the total number of unique extensions generated.
     * 
     * @return the count of unique extensions
     */
    public static int getUniqueExtensionCount() {
        return generatedExtensions.size();
    }
    
    /**
     * Clears the generated extensions cache (for testing purposes).
     */
    public static void clearCache() {
        generatedExtensions.clear();
    }
    
    @Override
    public String generate(GenerationContext context) {
        boolean allowDangerous = (Boolean) context.getParameter("allow_dangerous", false);
        String category = (String) context.getParameter("category", "all");
        
        List<String> extensions = getExtensionsForCategory(category.toLowerCase(), allowDangerous);
        
        // Ensure we have extensions to choose from
        if (extensions.isEmpty()) {
            extensions = standardExtensions;
        }
        
        String extension = extensions.get(new Random().nextInt(extensions.size()));
        
        // Track uniqueness
        generatedExtensions.putIfAbsent(extension, Boolean.TRUE);
        
        return extension;
    }
    
    private List<String> getExtensionsForCategory(String category, boolean allowDangerous) {
        switch (category) {
            case "image":
                return imageExtensions.isEmpty() ? getDefaultImageExtensions() : imageExtensions;
            case "document":
                return documentExtensions.isEmpty() ? getDefaultDocumentExtensions() : documentExtensions;
            case "archive":
                return List.of(".zip", ".rar", ".7z", ".tar", ".gz");
            case "media":
                return mediaExtensions.isEmpty() ? getDefaultMediaExtensions() : mediaExtensions;
            case "dangerous":
                return dangerousExtensions.isEmpty() ? getDefaultDangerousExtensions() : dangerousExtensions;
            case "all":
            default:
                List<String> allExtensions = new ArrayList<>(standardExtensions);
                if (allowDangerous && !dangerousExtensions.isEmpty()) {
                    allExtensions.addAll(dangerousExtensions);
                }
                return allExtensions;
        }
    }

    @Override
    public String getName() {
        return "file_extension";
    }

    @Override
    public List<String> getSupportedParameters() {
        return List.of("allow_dangerous", "category");
    }

    public static List<String> getStandardExtensions() {
        return Collections.unmodifiableList(standardExtensions);
    }

    public static List<String> getDangerousExtensions() {
        return Collections.unmodifiableList(dangerousExtensions);
    }

    public static List<String> getImageExtensions() {
        return Collections.unmodifiableList(imageExtensions);
    }

    public static List<String> getDocumentExtensions() {
        return Collections.unmodifiableList(documentExtensions);
    }

    public static List<String> getMediaExtensions() {
        return Collections.unmodifiableList(mediaExtensions);
    }
}
package com.dataforge.generators.communication;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * Generates file paths.
 */
public class FilePathGenerator implements DataGenerator<String> {

    private final Random random = new Random();
    private static final String[] FOLDERS = {"docs", "images", "videos", "audio", "temp", "logs"};
    private static final String[] EXTENSIONS = {"txt", "pdf", "jpg", "png", "mp4", "mp3", "log"};

    @Override
    public String generate(GenerationContext context) {
        String os = (String) context.getParameter("os", "UNIX");
        int depth = (int) context.getParameter("depth", 3);

        StringBuilder path = new StringBuilder();
        String separator = os.equalsIgnoreCase("WINDOWS") ? "\\" : "/";

        if (os.equalsIgnoreCase("WINDOWS")) {
            path.append("C:\\");
        } else {
            path.append(separator);
        }

        for (int i = 0; i < depth; i++) {
            path.append(FOLDERS[random.nextInt(FOLDERS.length)]);
            path.append(separator);
        }

        path.append("file-");
        path.append(random.nextInt(1000));
        path.append(".");
        path.append(EXTENSIONS[random.nextInt(EXTENSIONS.length)]);

        return path.toString();
    }
}

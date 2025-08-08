package com.dataforge.generators.security;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Base64;
import java.util.Random;

/**
 * Binary/Base64 data generator for generating random binary data and Base64 encoded data.
 * Useful for security testing and file upload simulations.
 */
public class BinaryBase64DataGenerator implements DataGenerator<String> {
    
    private final Random random;
    
    /**
     * Creates a binary/Base64 data generator with default settings.
     */
    public BinaryBase64DataGenerator() {
        this.random = new Random();
    }
    
    @Override
    public String generate(GenerationContext context) {
        // Get parameters
        Object sizeObj = context.getParameter("size", "1024");
        int size = Integer.parseInt(sizeObj.toString());
        
        Object encodingObj = context.getParameter("encoding", "base64");
        String encoding = encodingObj.toString().toLowerCase();
        
        Object includeInvalidCharsObj = context.getParameter("include_invalid_chars", false);
        boolean includeInvalidChars = Boolean.parseBoolean(includeInvalidCharsObj.toString());
        
        // Generate random binary data
        byte[] data = new byte[size];
        random.nextBytes(data);
        
        // Optionally corrupt some bytes to include invalid Base64 characters
        if (includeInvalidChars && "base64".equals(encoding)) {
            // Corrupt up to 5% of the data
            int corruptionCount = Math.max(1, size / 20);
            for (int i = 0; i < corruptionCount; i++) {
                int index = random.nextInt(size);
                // Replace with an invalid Base64 character
                data[index] = (byte) (128 + random.nextInt(128)); // Non-ASCII bytes
            }
        }
        
        // Encode based on the specified encoding
        switch (encoding) {
            case "hex":
                return bytesToHex(data);
            case "base64":
            default:
                return Base64.getEncoder().encodeToString(data);
        }
    }
    
    /**
     * Converts byte array to hexadecimal string.
     * 
     * @param bytes the byte array to convert
     * @return hexadecimal string representation
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
    
    @Override
    public String getName() {
        return "binary_base64_data";
    }
    
    @Override
    public java.util.List<String> getSupportedParameters() {
        return java.util.Arrays.asList("size", "encoding", "include_invalid_chars");
    }
}
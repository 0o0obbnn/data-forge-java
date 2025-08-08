package com.dataforge.generators.security;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Base64;
import java.util.Random;

/**
 * Session ID/Token generator for generating session identifiers and tokens.
 */
public class SessionIdTokenGenerator implements DataGenerator<String> {
    
    private final int length;
    private final boolean asJwt;
    
    /**
     * Creates a session ID/token generator with default settings (32-character random string).
     */
    public SessionIdTokenGenerator() {
        this(32, false);
    }
    
    /**
     * Creates a session ID/token generator.
     * 
     * @param length the length of the session ID/token
     * @param asJwt whether to generate a JWT-like structure
     */
    public SessionIdTokenGenerator(int length, boolean asJwt) {
        this.length = length;
        this.asJwt = asJwt;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        
        if (asJwt) {
            // Generate a JWT-like structure (header.payload.signature)
            // Note: This is NOT a real JWT - just a mock structure for testing purposes
            
            // Generate header (base64 encoded JSON)
            String header = Base64.getEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
            
            // Generate payload (base64 encoded JSON with random data)
            StringBuilder payloadData = new StringBuilder();
            payloadData.append("{");
            payloadData.append("\"sub\":\"").append(generateRandomString(random, 10)).append("\",");
            payloadData.append("\"iat\":").append(System.currentTimeMillis() / 1000).append(",");
            payloadData.append("\"exp\":").append((System.currentTimeMillis() / 1000) + 3600); // 1 hour from now
            payloadData.append("}");
            String payload = Base64.getEncoder().encodeToString(payloadData.toString().getBytes());
            
            // Generate signature placeholder
            String signature = Base64.getEncoder().encodeToString(generateRandomString(random, 32).getBytes());
            
            // Combine parts
            return header + "." + payload + "." + signature;
        } else {
            // Generate a random session ID/token string
            return generateRandomString(random, length);
        }
    }
    
    /**
     * Generate a random string of specified length using alphanumeric characters.
     * 
     * @param random the random number generator
     * @param len the length of the string to generate
     * @return the generated random string
     */
    private String generateRandomString(Random random, int len) {
        StringBuilder sb = new StringBuilder(len);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
}
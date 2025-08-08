package com.dataforge.generators.media;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 模拟媒体文件生成器
 * 生成各种媒体文件的头部和少量随机数据，用于测试文件上传和处理
 */
public class SimulatedMediaFileGenerator implements DataGenerator<String> {

    private final Random random;

    public SimulatedMediaFileGenerator() {
        this.random = new Random();
    }

    @Override
    public String generate(GenerationContext context) {
        Object typeObj = context.getParameter("type", "image");
        String type = typeObj.toString();
        
        Object corruptedObj = context.getParameter("corrupted", false);
        boolean corrupted = Boolean.parseBoolean(corruptedObj.toString());
        
        Object sizeObj = context.getParameter("size", "1024");
        int size = Integer.parseInt(sizeObj.toString());
        
        byte[] header = generateFileHeader(type, corrupted);
        byte[] body = generateRandomBody(size);
        
        return bytesToHex(header) + bytesToHex(body);
    }

    private byte[] generateFileHeader(String type, boolean corrupted) {
        switch (type.toLowerCase()) {
            case "image_png":
                return corrupted ? new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x00} 
                               : new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            case "image_jpeg":
                return corrupted ? new byte[]{(byte) 0xFF, (byte) 0xD8, 0x00}
                               : new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
            case "image_gif":
                return new byte[]{'G', 'I', 'F', '8', '9', 'a'};
            case "pdf":
                return "%PDF-1.4\n".getBytes();
            case "zip":
                return "PK\003\004".getBytes();
            case "mp3":
                return "ID3".getBytes();
            case "mp4":
                return new byte[]{0x00, 0x00, 0x00, 0x20, 'f', 't', 'y', 'p'};
            case "docx":
                return "PK\003\004".getBytes(); // DOCX is ZIP format
            case "xlsx":
                return "PK\003\004".getBytes(); // XLSX is ZIP format
            default:
                return new byte[]{0x00, 0x01, 0x02, 0x03};
        }
    }

    private byte[] generateRandomBody(int size) {
        byte[] body = new byte[size];
        random.nextBytes(body);
        return body;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    @Override
    public String getName() {
        return "simulated_media_file";
    }

    @Override
    public List<String> getSupportedParameters() {
        return Arrays.asList("type", "corrupted", "size");
    }
}
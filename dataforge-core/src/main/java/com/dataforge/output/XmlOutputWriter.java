package com.dataforge.output;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * XML输出器
 * 将生成的数据输出为格式化的XML文档
 */
public class XmlOutputWriter {
    
    private final String filePath;
    private final List<String> fieldNames;
    private final Map<String, DataGenerator<?>> generators;
    private final String rootElement;
    private final String recordElement;
    
    public XmlOutputWriter(String filePath, List<String> fieldNames, Map<String, DataGenerator<?>> generators) {
        this(filePath, fieldNames, generators, "data", "record");
    }
    
    public XmlOutputWriter(String filePath, List<String> fieldNames, Map<String, DataGenerator<?>> generators, 
                          String rootElement, String recordElement) {
        this.filePath = filePath;
        this.fieldNames = fieldNames;
        this.generators = generators;
        this.rootElement = rootElement;
        this.recordElement = recordElement;
    }
    
    /**
     * 生成数据并写入XML文件
     */
    public void write(GenerationContext context) throws Exception {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            XMLStreamWriter writer = factory.createXMLStreamWriter(fileWriter);
            
            // 开始XML文档
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n");
            
            // 根元素
            writer.writeStartElement(rootElement);
            writer.writeAttribute("count", String.valueOf(context.getCount()));
            writer.writeAttribute("generated", java.time.LocalDateTime.now().toString());
            writer.writeCharacters("\n");
            
            // 生成数据记录
            for (int i = 0; i < context.getCount(); i++) {
                generateRecord(writer, context, i);
            }
            
            // 结束根元素
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndDocument();
            
            writer.flush();
            writer.close();
        }
    }
    
    private void generateRecord(XMLStreamWriter writer, GenerationContext context, int recordIndex) throws Exception {
        // 创建单条记录的上下文
        GenerationContext recordContext = new GenerationContext(1);
        if (context.getSeed() != null) {
            recordContext.setSeed(context.getSeed() + recordIndex);
        }
        
        writer.writeCharacters("  ");
        writer.writeStartElement(recordElement);
        writer.writeAttribute("id", String.valueOf(recordIndex + 1));
        writer.writeCharacters("\n");
        
        // 生成字段数据
        for (String fieldName : fieldNames) {
            DataGenerator<?> generator = generators.get(fieldName);
            Object value = generator.generate(recordContext);
            
            writer.writeCharacters("    ");
            writer.writeStartElement(fieldName);
            
            if (value != null) {
                String valueStr = escapeXmlContent(value.toString());
                writer.writeCharacters(valueStr);
            }
            
            writer.writeEndElement();
            writer.writeCharacters("\n");
        }
        
        writer.writeCharacters("  ");
        writer.writeEndElement();
        writer.writeCharacters("\n");
    }
    
    /**
     * 转义XML内容中的特殊字符
     */
    private String escapeXmlContent(String content) {
        if (content == null) {
            return "";
        }
        
        return content.replace("&", "&amp;")
                     .replace("<", "&lt;")
                     .replace(">", "&gt;")
                     .replace("\"", "&quot;")
                     .replace("'", "&apos;");
    }
}
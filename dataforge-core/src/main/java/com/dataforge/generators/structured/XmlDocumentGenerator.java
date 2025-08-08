package com.dataforge.generators.structured;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.Random;

/**
 * XML文档生成器
 * 根据配置生成各种结构的XML文档
 */
public class XmlDocumentGenerator implements DataGenerator<String> {
    
    private static final Random random = new Random();
    
    private final XmlDocumentType documentType;
    private final int maxDepth;
    private final boolean includeAttributes;
    private final boolean includeNamespaces;
    
    public enum XmlDocumentType {
        SIMPLE,             // 简单XML文档
        NESTED,             // 嵌套XML文档  
        DATA_EXCHANGE,      // 数据交换格式XML
        CONFIG_FILE         // 配置文件格式XML
    }
    
    public XmlDocumentGenerator() {
        this(XmlDocumentType.SIMPLE, 3, true, false);
    }
    
    public XmlDocumentGenerator(XmlDocumentType documentType, int maxDepth, boolean includeAttributes, boolean includeNamespaces) {
        this.documentType = documentType;
        this.maxDepth = maxDepth;
        this.includeAttributes = includeAttributes;
        this.includeNamespaces = includeNamespaces;
    }
    
    @Override
    public String generate(GenerationContext context) {
        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);
            
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n");
            
            switch (documentType) {
                case SIMPLE:
                    generateSimpleXml(writer);
                    break;
                case NESTED:
                    generateNestedXml(writer, 0);
                    break;
                case DATA_EXCHANGE:
                    generateDataExchangeXml(writer);
                    break;
                case CONFIG_FILE:
                    generateConfigFileXml(writer);
                    break;
            }
            
            writer.writeCharacters("\n");
            writer.writeEndDocument();
            writer.flush();
            writer.close();
            
            return stringWriter.toString();
            
        } catch (Exception e) {
            return generateFallbackXml();
        }
    }
    
    /**
     * 生成简单XML文档
     */
    private void generateSimpleXml(XMLStreamWriter writer) throws Exception {
        writer.writeStartElement("record");
        
        if (includeAttributes) {
            writer.writeAttribute("id", String.valueOf(random.nextInt(10000)));
            writer.writeAttribute("type", "simple");
        }
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("name");
        writer.writeCharacters(generateRandomName());
        writer.writeEndElement();
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("value");
        writer.writeCharacters(String.valueOf(random.nextInt(1000)));
        writer.writeEndElement();
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("active");
        writer.writeCharacters(String.valueOf(random.nextBoolean()));
        writer.writeEndElement();
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("created_at");
        writer.writeCharacters(generateRandomTimestamp());
        writer.writeEndElement();
        
        writer.writeCharacters("\n");
        writer.writeEndElement(); // record
    }
    
    /**
     * 生成嵌套XML文档
     */
    private void generateNestedXml(XMLStreamWriter writer, int currentDepth) throws Exception {
        writer.writeStartElement("document");
        
        if (includeAttributes) {
            writer.writeAttribute("version", "1.0");
            writer.writeAttribute("depth", String.valueOf(currentDepth));
        }
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("header");
        writer.writeCharacters("\n    ");
        writer.writeStartElement("title");
        writer.writeCharacters("Generated Document " + random.nextInt(100));
        writer.writeEndElement();
        
        writer.writeCharacters("\n    ");
        writer.writeStartElement("author");
        writer.writeCharacters("DataForge Generator");
        writer.writeEndElement();
        
        writer.writeCharacters("\n  ");
        writer.writeEndElement(); // header
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("body");
        
        // 生成多个段落
        int paragraphCount = random.nextInt(3) + 2;
        for (int i = 0; i < paragraphCount; i++) {
            writer.writeCharacters("\n    ");
            writer.writeStartElement("paragraph");
            writer.writeAttribute("id", "p" + (i + 1));
            writer.writeCharacters(generateRandomText());
            writer.writeEndElement();
        }
        
        // 嵌套结构
        if (currentDepth < maxDepth) {
            writer.writeCharacters("\n    ");
            writer.writeStartElement("nested");
            generateNestedContent(writer, currentDepth + 1);
            writer.writeCharacters("\n    ");
            writer.writeEndElement(); // nested
        }
        
        writer.writeCharacters("\n  ");
        writer.writeEndElement(); // body
        
        writer.writeCharacters("\n");
        writer.writeEndElement(); // document
    }
    
    /**
     * 生成数据交换XML
     */
    private void generateDataExchangeXml(XMLStreamWriter writer) throws Exception {
        writer.writeStartElement("dataExchange");
        
        if (includeNamespaces) {
            writer.writeNamespace("df", "http://dataforge.com/exchange");
            writer.writeAttribute("xmlns:df", "http://dataforge.com/exchange");
        }
        
        writer.writeAttribute("version", "2.0");
        writer.writeAttribute("timestamp", generateRandomTimestamp());
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("metadata");
        writer.writeCharacters("\n    ");
        writer.writeStartElement("source");
        writer.writeCharacters("DataForge Test System");
        writer.writeEndElement();
        
        writer.writeCharacters("\n    ");
        writer.writeStartElement("recordCount");
        writer.writeCharacters(String.valueOf(random.nextInt(1000) + 1));
        writer.writeEndElement();
        
        writer.writeCharacters("\n  ");
        writer.writeEndElement(); // metadata
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("records");
        
        // 生成多条记录
        int recordCount = random.nextInt(5) + 3;
        for (int i = 0; i < recordCount; i++) {
            writer.writeCharacters("\n    ");
            writer.writeStartElement("record");
            writer.writeAttribute("id", String.valueOf(i + 1));
            
            writer.writeCharacters("\n      ");
            writer.writeStartElement("data");
            writer.writeAttribute("type", getRandomDataType());
            writer.writeCharacters(generateRandomDataValue());
            writer.writeEndElement();
            
            writer.writeCharacters("\n      ");
            writer.writeStartElement("status");
            writer.writeCharacters(getRandomStatus());
            writer.writeEndElement();
            
            writer.writeCharacters("\n    ");
            writer.writeEndElement(); // record
        }
        
        writer.writeCharacters("\n  ");
        writer.writeEndElement(); // records
        
        writer.writeCharacters("\n");
        writer.writeEndElement(); // dataExchange
    }
    
    /**
     * 生成配置文件XML
     */
    private void generateConfigFileXml(XMLStreamWriter writer) throws Exception {
        writer.writeStartElement("configuration");
        writer.writeAttribute("version", "1.0");
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("database");
        writer.writeCharacters("\n    ");
        writer.writeStartElement("host");
        writer.writeCharacters("localhost");
        writer.writeEndElement();
        
        writer.writeCharacters("\n    ");
        writer.writeStartElement("port");
        writer.writeCharacters(String.valueOf(3306 + random.nextInt(1000)));
        writer.writeEndElement();
        
        writer.writeCharacters("\n    ");
        writer.writeStartElement("name");
        writer.writeCharacters("test_db_" + random.nextInt(100));
        writer.writeEndElement();
        
        writer.writeCharacters("\n  ");
        writer.writeEndElement(); // database
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("application");
        writer.writeCharacters("\n    ");
        writer.writeStartElement("name");
        writer.writeCharacters("TestApp");
        writer.writeEndElement();
        
        writer.writeCharacters("\n    ");
        writer.writeStartElement("debug");
        writer.writeCharacters(String.valueOf(random.nextBoolean()));
        writer.writeEndElement();
        
        writer.writeCharacters("\n    ");
        writer.writeStartElement("maxConnections");
        writer.writeCharacters(String.valueOf(random.nextInt(100) + 10));
        writer.writeEndElement();
        
        writer.writeCharacters("\n  ");
        writer.writeEndElement(); // application
        
        writer.writeCharacters("\n  ");
        writer.writeStartElement("features");
        
        String[] features = {"logging", "caching", "monitoring", "security"};
        for (String feature : features) {
            writer.writeCharacters("\n    ");
            writer.writeStartElement("feature");
            writer.writeAttribute("name", feature);
            writer.writeAttribute("enabled", String.valueOf(random.nextBoolean()));
            writer.writeEndElement();
        }
        
        writer.writeCharacters("\n  ");
        writer.writeEndElement(); // features
        
        writer.writeCharacters("\n");
        writer.writeEndElement(); // configuration
    }
    
    /**
     * 生成嵌套内容
     */
    private void generateNestedContent(XMLStreamWriter writer, int depth) throws Exception {
        writer.writeCharacters("\n      ");
        writer.writeStartElement("item");
        writer.writeAttribute("depth", String.valueOf(depth));
        
        writer.writeCharacters("\n        ");
        writer.writeStartElement("property");
        writer.writeCharacters("value_" + random.nextInt(1000));
        writer.writeEndElement();
        
        if (depth < maxDepth) {
            generateNestedContent(writer, depth + 1);
        }
        
        writer.writeCharacters("\n      ");
        writer.writeEndElement(); // item
    }
    
    /**
     * 辅助方法 - 生成随机名称
     */
    private String generateRandomName() {
        String[] names = {"John", "Jane", "Mike", "Sarah", "David", "Lisa", "Tom", "Anna"};
        return names[random.nextInt(names.length)];
    }
    
    /**
     * 辅助方法 - 生成随机文本
     */
    private String generateRandomText() {
        String[] texts = {
            "This is a sample paragraph generated by DataForge.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            "Test data for XML document generation.",
            "Random content for testing purposes."
        };
        return texts[random.nextInt(texts.length)];
    }
    
    /**
     * 辅助方法 - 生成随机时间戳
     */
    private String generateRandomTimestamp() {
        return java.time.LocalDateTime.now().minusDays(random.nextInt(30)).toString();
    }
    
    /**
     * 辅助方法 - 获取随机数据类型
     */
    private String getRandomDataType() {
        String[] types = {"string", "integer", "boolean", "date", "decimal"};
        return types[random.nextInt(types.length)];
    }
    
    /**
     * 辅助方法 - 生成随机数据值
     */
    private String generateRandomDataValue() {
        return "data_" + random.nextInt(10000);
    }
    
    /**
     * 辅助方法 - 获取随机状态
     */
    private String getRandomStatus() {
        String[] statuses = {"active", "inactive", "pending", "completed"};
        return statuses[random.nextInt(statuses.length)];
    }
    
    /**
     * 生成回退XML（当出现异常时）
     */
    private String generateFallbackXml() {
        return String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<fallback id=\"%d\" timestamp=\"%s\"/>\n", 
            random.nextInt(10000), java.time.LocalDateTime.now().toString());
    }
}
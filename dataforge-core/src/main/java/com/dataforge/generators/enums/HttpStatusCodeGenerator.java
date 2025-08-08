package com.dataforge.generators.enums;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * HTTP状态码生成器
 * 生成标准HTTP状态码及其描述信息
 */
public class HttpStatusCodeGenerator implements DataGenerator<String> {
    
    private static final Random random = new Random();
    
    private final StatusCodeFormat format;
    private final StatusCodeCategory category;
    
    public enum StatusCodeFormat {
        CODE_ONLY,          // 仅状态码: "200"
        CODE_WITH_REASON,   // 状态码+原因: "200 OK"
        FULL_DESCRIPTION,   // 完整描述: "200 OK - Request successful"
        JSON_FORMAT         // JSON格式: {"code": 200, "reason": "OK", "description": "Request successful"}
    }
    
    public enum StatusCodeCategory {
        ALL,                // 所有状态码
        SUCCESS,            // 2xx 成功状态码
        REDIRECT,           // 3xx 重定向状态码
        CLIENT_ERROR,       // 4xx 客户端错误状态码
        SERVER_ERROR,       // 5xx 服务器错误状态码
        COMMON_ONLY         // 仅常用状态码
    }
    
    // HTTP状态码定义
    private static final HttpStatusCode[] ALL_STATUS_CODES = {
        // 1xx Informational
        new HttpStatusCode(100, "Continue", "The server has received the request headers and should continue to send the request body"),
        new HttpStatusCode(101, "Switching Protocols", "The requester has asked the server to switch protocols"),
        new HttpStatusCode(102, "Processing", "The server has received and is processing the request"),
        
        // 2xx Success  
        new HttpStatusCode(200, "OK", "The request was successful"),
        new HttpStatusCode(201, "Created", "The request was successful and a resource was created"),
        new HttpStatusCode(202, "Accepted", "The request has been accepted for processing"),
        new HttpStatusCode(204, "No Content", "The request was successful but there is no content to return"),
        new HttpStatusCode(206, "Partial Content", "The server is delivering only part of the resource"),
        
        // 3xx Redirection
        new HttpStatusCode(301, "Moved Permanently", "The resource has been moved to a new URL permanently"),
        new HttpStatusCode(302, "Found", "The resource has been temporarily moved to a different URL"),
        new HttpStatusCode(304, "Not Modified", "The resource has not been modified since the last request"),
        new HttpStatusCode(307, "Temporary Redirect", "The resource has been temporarily moved"),
        new HttpStatusCode(308, "Permanent Redirect", "The resource has been permanently moved"),
        
        // 4xx Client Errors
        new HttpStatusCode(400, "Bad Request", "The request could not be understood by the server"),
        new HttpStatusCode(401, "Unauthorized", "Authentication is required and has failed or not been provided"),
        new HttpStatusCode(403, "Forbidden", "The server understood the request but refuses to authorize it"),
        new HttpStatusCode(404, "Not Found", "The requested resource could not be found"),
        new HttpStatusCode(405, "Method Not Allowed", "The request method is not supported for the requested resource"),
        new HttpStatusCode(406, "Not Acceptable", "The requested resource cannot generate content acceptable to the client"),
        new HttpStatusCode(408, "Request Timeout", "The server timed out waiting for the request"),
        new HttpStatusCode(409, "Conflict", "The request could not be processed due to a conflict"),
        new HttpStatusCode(410, "Gone", "The resource is no longer available and will not be available again"),
        new HttpStatusCode(413, "Payload Too Large", "The request is larger than the server is willing or able to process"),
        new HttpStatusCode(415, "Unsupported Media Type", "The request entity has a media type which the server does not support"),
        new HttpStatusCode(422, "Unprocessable Entity", "The request was well-formed but was unable to be followed due to semantic errors"),
        new HttpStatusCode(429, "Too Many Requests", "The user has sent too many requests in a given amount of time"),
        
        // 5xx Server Errors
        new HttpStatusCode(500, "Internal Server Error", "A generic error message when an unexpected condition was encountered"),
        new HttpStatusCode(501, "Not Implemented", "The server either does not recognize the request method or lacks the ability to fulfill the request"),
        new HttpStatusCode(502, "Bad Gateway", "The server was acting as a gateway or proxy and received an invalid response"),
        new HttpStatusCode(503, "Service Unavailable", "The server is currently unavailable due to temporary overload or maintenance"),
        new HttpStatusCode(504, "Gateway Timeout", "The server was acting as a gateway or proxy and did not receive a timely response"),
        new HttpStatusCode(505, "HTTP Version Not Supported", "The server does not support the HTTP protocol version used in the request")
    };
    
    // 常用状态码（更高概率被选中）
    private static final int[] COMMON_CODES = {200, 201, 400, 401, 403, 404, 500, 502, 503};
    
    public HttpStatusCodeGenerator() {
        this(StatusCodeFormat.CODE_WITH_REASON, StatusCodeCategory.ALL);
    }
    
    public HttpStatusCodeGenerator(StatusCodeFormat format, StatusCodeCategory category) {
        this.format = format;
        this.category = category;
    }
    
    @Override
    public String generate(GenerationContext context) {
        HttpStatusCode statusCode = selectStatusCode();
        return formatStatusCode(statusCode);
    }
    
    /**
     * 根据类别选择状态码
     */
    private HttpStatusCode selectStatusCode() {
        switch (category) {
            case SUCCESS:
                return selectByRange(200, 299);
            case REDIRECT:
                return selectByRange(300, 399);
            case CLIENT_ERROR:
                return selectByRange(400, 499);
            case SERVER_ERROR:
                return selectByRange(500, 599);
            case COMMON_ONLY:
                return selectCommonStatusCode();
            case ALL:
            default:
                return ALL_STATUS_CODES[random.nextInt(ALL_STATUS_CODES.length)];
        }
    }
    
    /**
     * 按状态码范围选择
     */
    private HttpStatusCode selectByRange(int min, int max) {
        HttpStatusCode[] filtered = java.util.Arrays.stream(ALL_STATUS_CODES)
            .filter(code -> code.getCode() >= min && code.getCode() <= max)
            .toArray(HttpStatusCode[]::new);
        
        if (filtered.length == 0) {
            return ALL_STATUS_CODES[0]; // 回退到第一个状态码
        }
        
        return filtered[random.nextInt(filtered.length)];
    }
    
    /**
     * 选择常用状态码
     */
    private HttpStatusCode selectCommonStatusCode() {
        int commonCode = COMMON_CODES[random.nextInt(COMMON_CODES.length)];
        
        for (HttpStatusCode statusCode : ALL_STATUS_CODES) {
            if (statusCode.getCode() == commonCode) {
                return statusCode;
            }
        }
        
        return ALL_STATUS_CODES[0]; // 回退
    }
    
    /**
     * 格式化状态码输出
     */
    private String formatStatusCode(HttpStatusCode statusCode) {
        switch (format) {
            case CODE_ONLY:
                return String.valueOf(statusCode.getCode());
                
            case CODE_WITH_REASON:
                return statusCode.getCode() + " " + statusCode.getReason();
                
            case FULL_DESCRIPTION:
                return statusCode.getCode() + " " + statusCode.getReason() + " - " + statusCode.getDescription();
                
            case JSON_FORMAT:
                return String.format("{\"code\": %d, \"reason\": \"%s\", \"description\": \"%s\"}", 
                    statusCode.getCode(), statusCode.getReason(), statusCode.getDescription());
                
            default:
                return statusCode.getCode() + " " + statusCode.getReason();
        }
    }
    
    /**
     * HTTP状态码数据类
     */
    private static class HttpStatusCode {
        private final int code;
        private final String reason;
        private final String description;
        
        public HttpStatusCode(int code, String reason, String description) {
            this.code = code;
            this.reason = reason;
            this.description = description;
        }
        
        public int getCode() {
            return code;
        }
        
        public String getReason() {
            return reason;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
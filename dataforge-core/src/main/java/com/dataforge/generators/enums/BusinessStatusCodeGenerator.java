package com.dataforge.generators.enums;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * 业务状态码生成器
 * 生成自定义业务状态码，支持多种业务场景
 */
public class BusinessStatusCodeGenerator implements DataGenerator<String> {
    
    private static final Random random = new Random();
    
    private final BusinessDomain domain;
    private final StatusFormat format;
    
    public enum BusinessDomain {
        GENERAL,            // 通用业务状态
        USER_MANAGEMENT,    // 用户管理
        ORDER_PROCESSING,   // 订单处理
        PAYMENT,            // 支付相关
        INVENTORY,          // 库存管理
        AUTHENTICATION,     // 认证授权
        DATA_PROCESSING,    // 数据处理
        SYSTEM_MAINTENANCE  // 系统维护
    }
    
    public enum StatusFormat {
        CODE_ONLY,          // 仅状态码: "1001"
        CODE_WITH_MESSAGE,  // 状态码+消息: "1001:用户创建成功"
        DESCRIPTIVE,        // 描述性: "USER_CREATED_SUCCESS"
        JSON_FORMAT         // JSON格式: {"code": "1001", "message": "用户创建成功", "type": "success"}
    }
    
    // 通用业务状态码
    private static final BusinessStatusCode[] GENERAL_CODES = {
        new BusinessStatusCode("0000", "SUCCESS", "操作成功", "success"),
        new BusinessStatusCode("0001", "SYSTEM_ERROR", "系统错误", "error"),
        new BusinessStatusCode("0002", "PARAMETER_ERROR", "参数错误", "error"), 
        new BusinessStatusCode("0003", "DATA_NOT_FOUND", "数据不存在", "warning"),
        new BusinessStatusCode("0004", "OPERATION_FAILED", "操作失败", "error"),
        new BusinessStatusCode("0005", "PERMISSION_DENIED", "权限不足", "error"),
        new BusinessStatusCode("0006", "SERVICE_UNAVAILABLE", "服务不可用", "error"),
        new BusinessStatusCode("0007", "REQUEST_TIMEOUT", "请求超时", "warning"),
        new BusinessStatusCode("0008", "DATA_VALIDATION_FAILED", "数据校验失败", "error"),
        new BusinessStatusCode("0009", "RESOURCE_CONFLICT", "资源冲突", "warning")
    };
    
    // 用户管理状态码
    private static final BusinessStatusCode[] USER_MANAGEMENT_CODES = {
        new BusinessStatusCode("1001", "USER_CREATED", "用户创建成功", "success"),
        new BusinessStatusCode("1002", "USER_UPDATED", "用户信息更新成功", "success"),
        new BusinessStatusCode("1003", "USER_DELETED", "用户删除成功", "success"),
        new BusinessStatusCode("1101", "USER_NOT_FOUND", "用户不存在", "warning"),
        new BusinessStatusCode("1102", "USER_ALREADY_EXISTS", "用户已存在", "warning"),
        new BusinessStatusCode("1103", "USER_DISABLED", "用户已被禁用", "warning"),
        new BusinessStatusCode("1104", "USER_EXPIRED", "用户已过期", "warning"),
        new BusinessStatusCode("1201", "INVALID_USERNAME", "用户名格式无效", "error"),
        new BusinessStatusCode("1202", "INVALID_EMAIL", "邮箱格式无效", "error"),
        new BusinessStatusCode("1203", "WEAK_PASSWORD", "密码强度不足", "error")
    };
    
    // 订单处理状态码
    private static final BusinessStatusCode[] ORDER_PROCESSING_CODES = {
        new BusinessStatusCode("2001", "ORDER_CREATED", "订单创建成功", "success"),
        new BusinessStatusCode("2002", "ORDER_CONFIRMED", "订单确认", "success"),
        new BusinessStatusCode("2003", "ORDER_SHIPPED", "订单已发货", "success"),
        new BusinessStatusCode("2004", "ORDER_DELIVERED", "订单已送达", "success"),
        new BusinessStatusCode("2005", "ORDER_COMPLETED", "订单完成", "success"),
        new BusinessStatusCode("2101", "ORDER_NOT_FOUND", "订单不存在", "warning"),
        new BusinessStatusCode("2102", "ORDER_CANCELLED", "订单已取消", "warning"),
        new BusinessStatusCode("2103", "ORDER_EXPIRED", "订单已过期", "warning"),
        new BusinessStatusCode("2201", "INSUFFICIENT_STOCK", "库存不足", "error"),
        new BusinessStatusCode("2202", "INVALID_ORDER_STATUS", "订单状态无效", "error")
    };
    
    // 支付状态码
    private static final BusinessStatusCode[] PAYMENT_CODES = {
        new BusinessStatusCode("3001", "PAYMENT_SUCCESS", "支付成功", "success"),
        new BusinessStatusCode("3002", "PAYMENT_PENDING", "支付处理中", "info"),
        new BusinessStatusCode("3003", "REFUND_SUCCESS", "退款成功", "success"),
        new BusinessStatusCode("3101", "PAYMENT_FAILED", "支付失败", "error"),
        new BusinessStatusCode("3102", "INSUFFICIENT_BALANCE", "余额不足", "error"),
        new BusinessStatusCode("3103", "PAYMENT_TIMEOUT", "支付超时", "warning"),
        new BusinessStatusCode("3104", "INVALID_PAYMENT_METHOD", "支付方式无效", "error"),
        new BusinessStatusCode("3105", "PAYMENT_CANCELLED", "支付已取消", "warning"),
        new BusinessStatusCode("3106", "REFUND_FAILED", "退款失败", "error"),
        new BusinessStatusCode("3107", "DUPLICATE_PAYMENT", "重复支付", "warning")
    };
    
    // 库存管理状态码
    private static final BusinessStatusCode[] INVENTORY_CODES = {
        new BusinessStatusCode("4001", "STOCK_UPDATED", "库存更新成功", "success"),
        new BusinessStatusCode("4002", "STOCK_REPLENISHED", "库存补充成功", "success"),
        new BusinessStatusCode("4101", "STOCK_SHORTAGE", "库存短缺", "warning"),
        new BusinessStatusCode("4102", "STOCK_DEPLETED", "库存耗尽", "error"),
        new BusinessStatusCode("4103", "INVALID_SKU", "SKU无效", "error"),
        new BusinessStatusCode("4104", "STOCK_LOCKED", "库存已锁定", "warning"),
        new BusinessStatusCode("4105", "STOCK_EXPIRED", "库存已过期", "warning")
    };
    
    // 认证授权状态码
    private static final BusinessStatusCode[] AUTHENTICATION_CODES = {
        new BusinessStatusCode("5001", "LOGIN_SUCCESS", "登录成功", "success"),
        new BusinessStatusCode("5002", "LOGOUT_SUCCESS", "退出成功", "success"),
        new BusinessStatusCode("5003", "TOKEN_REFRESHED", "令牌刷新成功", "success"),
        new BusinessStatusCode("5101", "INVALID_CREDENTIALS", "用户名或密码错误", "error"),
        new BusinessStatusCode("5102", "TOKEN_EXPIRED", "令牌已过期", "warning"),
        new BusinessStatusCode("5103", "TOKEN_INVALID", "令牌无效", "error"),
        new BusinessStatusCode("5104", "ACCOUNT_LOCKED", "账户已锁定", "error"),
        new BusinessStatusCode("5105", "LOGIN_ATTEMPTS_EXCEEDED", "登录尝试次数超限", "error"),
        new BusinessStatusCode("5106", "SESSION_EXPIRED", "会话已过期", "warning")
    };
    
    // 数据处理状态码
    private static final BusinessStatusCode[] DATA_PROCESSING_CODES = {
        new BusinessStatusCode("6001", "DATA_IMPORTED", "数据导入成功", "success"),
        new BusinessStatusCode("6002", "DATA_EXPORTED", "数据导出成功", "success"),
        new BusinessStatusCode("6003", "DATA_SYNCHRONIZED", "数据同步成功", "success"),
        new BusinessStatusCode("6101", "DATA_FORMAT_ERROR", "数据格式错误", "error"),
        new BusinessStatusCode("6102", "DATA_VALIDATION_FAILED", "数据验证失败", "error"),
        new BusinessStatusCode("6103", "DATA_CORRUPTED", "数据损坏", "error"),
        new BusinessStatusCode("6104", "IMPORT_FAILED", "数据导入失败", "error"),
        new BusinessStatusCode("6105", "EXPORT_FAILED", "数据导出失败", "error")
    };
    
    // 系统维护状态码
    private static final BusinessStatusCode[] SYSTEM_MAINTENANCE_CODES = {
        new BusinessStatusCode("7001", "MAINTENANCE_STARTED", "维护开始", "info"),
        new BusinessStatusCode("7002", "MAINTENANCE_COMPLETED", "维护完成", "success"),
        new BusinessStatusCode("7003", "BACKUP_SUCCESS", "备份成功", "success"),
        new BusinessStatusCode("7004", "RESTORE_SUCCESS", "恢复成功", "success"),
        new BusinessStatusCode("7101", "MAINTENANCE_FAILED", "维护失败", "error"),
        new BusinessStatusCode("7102", "BACKUP_FAILED", "备份失败", "error"),
        new BusinessStatusCode("7103", "RESTORE_FAILED", "恢复失败", "error"),
        new BusinessStatusCode("7104", "SYSTEM_OVERLOAD", "系统过载", "warning")
    };
    
    public BusinessStatusCodeGenerator() {
        this(BusinessDomain.GENERAL, StatusFormat.CODE_WITH_MESSAGE);
    }
    
    public BusinessStatusCodeGenerator(BusinessDomain domain, StatusFormat format) {
        this.domain = domain;
        this.format = format;
    }
    
    @Override
    public String generate(GenerationContext context) {
        BusinessStatusCode statusCode = selectStatusCode();
        return formatStatusCode(statusCode);
    }
    
    /**
     * 根据业务域选择状态码
     */
    private BusinessStatusCode selectStatusCode() {
        BusinessStatusCode[] codes;
        
        switch (domain) {
            case USER_MANAGEMENT:
                codes = USER_MANAGEMENT_CODES;
                break;
            case ORDER_PROCESSING:
                codes = ORDER_PROCESSING_CODES;
                break;
            case PAYMENT:
                codes = PAYMENT_CODES;
                break;
            case INVENTORY:
                codes = INVENTORY_CODES;
                break;
            case AUTHENTICATION:
                codes = AUTHENTICATION_CODES;
                break;
            case DATA_PROCESSING:
                codes = DATA_PROCESSING_CODES;
                break;
            case SYSTEM_MAINTENANCE:
                codes = SYSTEM_MAINTENANCE_CODES;
                break;
            case GENERAL:
            default:
                codes = GENERAL_CODES;
                break;
        }
        
        return codes[random.nextInt(codes.length)];
    }
    
    /**
     * 格式化状态码输出
     */
    private String formatStatusCode(BusinessStatusCode statusCode) {
        switch (format) {
            case CODE_ONLY:
                return statusCode.getCode();
                
            case CODE_WITH_MESSAGE:
                return statusCode.getCode() + ":" + statusCode.getMessage();
                
            case DESCRIPTIVE:
                return statusCode.getName();
                
            case JSON_FORMAT:
                return String.format("{\"code\": \"%s\", \"name\": \"%s\", \"message\": \"%s\", \"type\": \"%s\"}", 
                    statusCode.getCode(), statusCode.getName(), statusCode.getMessage(), statusCode.getType());
                
            default:
                return statusCode.getCode() + ":" + statusCode.getMessage();
        }
    }
    
    /**
     * 业务状态码数据类
     */
    private static class BusinessStatusCode {
        private final String code;
        private final String name;
        private final String message;
        private final String type;
        
        public BusinessStatusCode(String code, String name, String message, String type) {
            this.code = code;
            this.name = name;
            this.message = message;
            this.type = type;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getName() {
            return name;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getType() {
            return type;
        }
    }
}
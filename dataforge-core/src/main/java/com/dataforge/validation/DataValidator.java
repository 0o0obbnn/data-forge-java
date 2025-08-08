package com.dataforge.validation;

import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * 数据验证工具类
 * 提供各种常用数据格式的验证功能
 */
public class DataValidator {

    // 邮箱验证正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // 手机号验证正则表达式（中国）
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    );
    
    // 身份证号验证正则表达式（中国18位）
    private static final Pattern IDCARD_PATTERN = Pattern.compile(
        "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$"
    );
    
    // URL验证正则表达式
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$"
    );
    
    // IP地址验证正则表达式
    private static final Pattern IP_PATTERN = Pattern.compile(
        "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );
    
    // UUID验证正则表达式
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    
    // 统一社会信用代码验证正则表达式
    private static final Pattern USCC_PATTERN = Pattern.compile(
        "^[0-9A-Z]{18}$"
    );
    
    // 银行卡号验证正则表达式
    private static final Pattern BANKCARD_PATTERN = Pattern.compile(
        "^\\d{13,19}$"
    );

    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 验证手机号格式（中国）
     */
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * 验证身份证号格式（中国18位）
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || !IDCARD_PATTERN.matcher(idCard).matches()) {
            return false;
        }
        
        // 验证校验码
        return validateIdCardChecksum(idCard);
    }
    
    /**
     * 验证URL格式
     */
    public static boolean isValidUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }
    
    /**
     * 验证IP地址格式
     */
    public static boolean isValidIpAddress(String ip) {
        return ip != null && IP_PATTERN.matcher(ip).matches();
    }
    
    /**
     * 验证UUID格式
     */
    public static boolean isValidUuid(String uuid) {
        return uuid != null && UUID_PATTERN.matcher(uuid).matches();
    }
    
    /**
     * 验证统一社会信用代码格式
     */
    public static boolean isValidUnifiedSocialCreditCode(String uscc) {
        return uscc != null && USCC_PATTERN.matcher(uscc).matches() && validateUsccChecksum(uscc);
    }
    
    /**
     * 验证银行卡号格式
     */
    public static boolean isValidBankCardNumber(String cardNumber) {
        return cardNumber != null && BANKCARD_PATTERN.matcher(cardNumber).matches() && validateLuhn(cardNumber);
    }
    
    /**
     * 验证企业名称格式
     */
    public static boolean isValidCompanyName(String companyName) {
        if (companyName == null || companyName.trim().isEmpty()) {
            return false;
        }
        
        // 检查是否包含无效字符
        String invalidChars = "[<>\"'&]";
        return !companyName.matches(".*" + invalidChars + ".*");
    }
    
    /**
     * 验证地址格式
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        
        // 检查地址长度
        if (address.length() < 2 || address.length() > 200) {
            return false;
        }
        
        // 检查是否包含特殊字符
        String invalidPattern = "[^\\u4e00-\\u9fa5\\w\\s,，.。·-]+$";
        return !address.matches(".*" + invalidPattern + ".*");
    }
    
    /**
     * 验证身份证号校验码
     */
    private static boolean validateIdCardChecksum(String idCard) {
        if (idCard.length() != 18) {
            return false;
        }
        
        // 加权因子
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        // 校验码对应值
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char c = idCard.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
            sum += (c - '0') * weights[i];
        }
        
        char calculatedCheckCode = checkCodes[sum % 11];
        char providedCheckCode = Character.toUpperCase(idCard.charAt(17));
        
        return calculatedCheckCode == providedCheckCode;
    }
    
    /**
     * 验证统一社会信用代码校验码
     */
    private static boolean validateUsccChecksum(String uscc) {
        if (uscc.length() != 18) {
            return false;
        }
        
        // 统一社会信用代码校验规则
        String baseCode = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int[] weights = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char c = uscc.charAt(i);
            int index = baseCode.indexOf(c);
            if (index == -1) {
                return false;
            }
            sum += index * weights[i];
        }
        
        int mod = 31 - (sum % 31);
        char calculatedCheckCode = baseCode.charAt(mod % 31);
        char providedCheckCode = uscc.charAt(17);
        
        return calculatedCheckCode == providedCheckCode;
    }
    
    /**
     * Luhn算法验证（用于银行卡号等）
     */
    private static boolean validateLuhn(String number) {
        if (number == null || !number.matches("\\d+")) {
            return false;
        }
        
        int sum = 0;
        boolean alternate = false;
        
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));
            
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            
            sum += n;
            alternate = !alternate;
        }
        
        return sum % 10 == 0;
    }
    
    /**
     * 验证数据完整性
     */
    public static ValidationResult validateDataIntegrity(Map<String, Object> data) {
        List<String> errors = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            
            if (value == null) {
                errors.add("Field " + field + " is null");
                continue;
            }
            
            // 根据字段类型进行特定验证
            switch (field.toLowerCase()) {
                case "email":
                case "mail":
                    if (!isValidEmail(value.toString())) {
                        errors.add("Invalid email format: " + value);
                    }
                    break;
                case "phone":
                case "mobile":
                    if (!isValidPhoneNumber(value.toString())) {
                        errors.add("Invalid phone number format: " + value);
                    }
                    break;
                case "idcard":
                case "id":
                    if (!isValidIdCard(value.toString())) {
                        errors.add("Invalid ID card format: " + value);
                    }
                    break;
                case "url":
                case "website":
                    if (!isValidUrl(value.toString())) {
                        errors.add("Invalid URL format: " + value);
                    }
                    break;
                case "ip":
                case "ipaddress":
                    if (!isValidIpAddress(value.toString())) {
                        errors.add("Invalid IP address format: " + value);
                    }
                    break;
                case "uuid":
                    if (!isValidUuid(value.toString())) {
                        errors.add("Invalid UUID format: " + value);
                    }
                    break;
                case "company":
                case "companyname":
                    if (!isValidCompanyName(value.toString())) {
                        errors.add("Invalid company name format: " + value);
                    }
                    break;
                case "address":
                    if (!isValidAddress(value.toString())) {
                        errors.add("Invalid address format: " + value);
                    }
                    break;
                default:
                    // 默认验证非空和长度
                    String strValue = value.toString();
                    if (strValue.trim().isEmpty()) {
                        errors.add("Field " + field + " is empty");
                    } else if (strValue.length() > 500) {
                        errors.add("Field " + field + " exceeds maximum length");
                    }
            }
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? errors : new ArrayList<>();
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }
}
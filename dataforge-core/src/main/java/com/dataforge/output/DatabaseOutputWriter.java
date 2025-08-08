package com.dataforge.output;

import com.dataforge.core.DataGenerator;
import com.dataforge.core.GenerationContext;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 数据库直接写入器
 * 直接将生成的数据插入到数据库表中
 */
public class DatabaseOutputWriter {
    
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String tableName;
    private final List<String> fieldNames;
    private final Map<String, DataGenerator<?>> generators;
    private final int batchSize;
    private final Properties connectionProperties;
    
    public DatabaseOutputWriter(String jdbcUrl, String username, String password, 
                               String tableName, List<String> fieldNames, 
                               Map<String, DataGenerator<?>> generators) {
        this(jdbcUrl, username, password, tableName, fieldNames, generators, 1000);
    }
    
    public DatabaseOutputWriter(String jdbcUrl, String username, String password, 
                               String tableName, List<String> fieldNames, 
                               Map<String, DataGenerator<?>> generators, int batchSize) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.tableName = tableName;
        this.fieldNames = fieldNames;
        this.generators = generators;
        this.batchSize = batchSize;
        this.connectionProperties = new Properties();
        
        // 设置默认连接属性
        this.connectionProperties.setProperty("useUnicode", "true");
        this.connectionProperties.setProperty("characterEncoding", "UTF-8");
        this.connectionProperties.setProperty("autoReconnect", "true");
        this.connectionProperties.setProperty("failOverReadOnly", "false");
        this.connectionProperties.setProperty("rewriteBatchedStatements", "true");
    }
    
    /**
     * 添加连接属性
     */
    public DatabaseOutputWriter withProperty(String key, String value) {
        this.connectionProperties.setProperty(key, value);
        return this;
    }
    
    /**
     * 生成数据并直接写入数据库
     */
    public void write(GenerationContext context) throws SQLException {
        try (Connection connection = createConnection()) {
            connection.setAutoCommit(false);
            
            String insertSql = buildInsertSql();
            
            try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                int totalRecords = context.getCount();
                int processedRecords = 0;
                
                for (int i = 0; i < totalRecords; i++) {
                    // 创建单条记录的上下文
                    GenerationContext recordContext = new GenerationContext(1);
                    if (context.getSeed() != null) {
                        recordContext.setSeed(context.getSeed() + i);
                    }
                    
                    // 设置参数值
                    setStatementParameters(statement, recordContext);
                    statement.addBatch();
                    
                    processedRecords++;
                    
                    // 批量执行
                    if (processedRecords % batchSize == 0 || processedRecords == totalRecords) {
                        statement.executeBatch();
                        connection.commit();
                        
                        System.out.printf("已写入 %d/%d 条记录到数据库%n", processedRecords, totalRecords);
                    }
                }
                
                connection.commit();
                System.out.printf("成功写入 %d 条记录到表 %s%n", totalRecords, tableName);
            }
        }
    }
    
    /**
     * 创建数据库连接
     */
    private Connection createConnection() throws SQLException {
        try {
            // 尝试自动检测和加载JDBC驱动
            detectAndLoadDriver();
            
            if (username != null && password != null) {
                connectionProperties.setProperty("user", username);
                connectionProperties.setProperty("password", password);
                return DriverManager.getConnection(jdbcUrl, connectionProperties);
            } else {
                return DriverManager.getConnection(jdbcUrl, connectionProperties);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC驱动未找到: " + e.getMessage(), e);
        }
    }
    
    /**
     * 自动检测并加载JDBC驱动
     */
    private void detectAndLoadDriver() throws ClassNotFoundException {
        String driverClass = null;
        
        if (jdbcUrl.startsWith("jdbc:mysql:")) {
            driverClass = "com.mysql.cj.jdbc.Driver";
        } else if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            driverClass = "org.postgresql.Driver";
        } else if (jdbcUrl.startsWith("jdbc:oracle:")) {
            driverClass = "oracle.jdbc.driver.OracleDriver";
        } else if (jdbcUrl.startsWith("jdbc:sqlserver:")) {
            driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        } else if (jdbcUrl.startsWith("jdbc:sqlite:")) {
            driverClass = "org.sqlite.JDBC";
        } else if (jdbcUrl.startsWith("jdbc:h2:")) {
            driverClass = "org.h2.Driver";
        }
        
        if (driverClass != null) {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                // 尝试老版本的MySQL驱动
                if (driverClass.equals("com.mysql.cj.jdbc.Driver")) {
                    Class.forName("com.mysql.jdbc.Driver");
                } else {
                    throw e;
                }
            }
        }
    }
    
    /**
     * 构建INSERT SQL语句
     */
    private String buildInsertSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append(" (");
        
        // 添加列名
        for (int i = 0; i < fieldNames.size(); i++) {
            sql.append(fieldNames.get(i));
            if (i < fieldNames.size() - 1) {
                sql.append(", ");
            }
        }
        
        sql.append(") VALUES (");
        
        // 添加参数占位符
        for (int i = 0; i < fieldNames.size(); i++) {
            sql.append("?");
            if (i < fieldNames.size() - 1) {
                sql.append(", ");
            }
        }
        
        sql.append(")");
        
        return sql.toString();
    }
    
    /**
     * 设置PreparedStatement参数
     */
    private void setStatementParameters(PreparedStatement statement, GenerationContext context) throws SQLException {
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            DataGenerator<?> generator = generators.get(fieldName);
            Object value = generator.generate(context);
            
            if (value == null) {
                statement.setNull(i + 1, Types.VARCHAR);
            } else {
                setParameterValue(statement, i + 1, value);
            }
        }
    }
    
    /**
     * 根据值类型设置参数
     */
    private void setParameterValue(PreparedStatement statement, int parameterIndex, Object value) throws SQLException {
        if (value instanceof String) {
            statement.setString(parameterIndex, (String) value);
        } else if (value instanceof Integer) {
            statement.setInt(parameterIndex, (Integer) value);
        } else if (value instanceof Long) {
            statement.setLong(parameterIndex, (Long) value);
        } else if (value instanceof Double) {
            statement.setDouble(parameterIndex, (Double) value);
        } else if (value instanceof Float) {
            statement.setFloat(parameterIndex, (Float) value);
        } else if (value instanceof Boolean) {
            statement.setBoolean(parameterIndex, (Boolean) value);
        } else if (value instanceof Date) {
            statement.setDate(parameterIndex, (Date) value);
        } else if (value instanceof Timestamp) {
            statement.setTimestamp(parameterIndex, (Timestamp) value);
        } else {
            // 默认转换为字符串
            statement.setString(parameterIndex, value.toString());
        }
    }
    
    /**
     * 测试数据库连接
     */
    public boolean testConnection() {
        try (Connection connection = createConnection()) {
            return connection.isValid(5); // 5秒超时
        } catch (SQLException e) {
            System.err.println("数据库连接测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查表是否存在
     */
    public boolean tableExists() {
        try (Connection connection = createConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
                return tables.next();
            }
        } catch (SQLException e) {
            System.err.println("检查表存在性失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取表的列信息
     */
    public void printTableStructure() {
        try (Connection connection = createConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                System.out.println("表 " + tableName + " 的结构:");
                System.out.println("列名\t\t数据类型\t\t是否可空");
                System.out.println("-------------------------------------------");
                
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    String nullable = columns.getString("IS_NULLABLE");
                    
                    System.out.printf("%-15s\t%-15s(%d)\t%s%n", 
                        columnName, dataType, columnSize, nullable);
                }
            }
        } catch (SQLException e) {
            System.err.println("获取表结构失败: " + e.getMessage());
        }
    }
}
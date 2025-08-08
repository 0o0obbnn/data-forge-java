# TestNG 迁移计划

## 当前状态分析

当前项目使用JUnit 5作为测试框架，相关依赖如下：
1. JUnit Jupiter (5.8.2)
2. Mockito (4.6.1)
3. AssertJ (3.23.1)

测试文件使用了JUnit 5的注解和断言方式。

## 迁移步骤

### 1. 修改父POM (dataforge-parent/pom.xml)

替换JUnit依赖为TestNG：

```xml
<!-- 将原来的JUnit依赖 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>${junit.version}</version>
    <scope>test</scope>
</dependency>

<!-- 替换为TestNG依赖 -->
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.7.1</version>
    <scope>test</scope>
</dependency>
```

### 2. 修改子模块POM文件

同样在dataforge-core/pom.xml和dataforge-cli/pom.xml中进行上述替换。

### 3. 更新测试类

将测试类从JUnit 5风格转换为TestNG风格：

#### 原JUnit 5测试类示例：
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntegerGeneratorTest {
    
    @Test
    void testDefaultIntegerGeneration() {
        // 测试代码
    }
}
```

#### 转换后的TestNG测试类：
```java
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class IntegerGeneratorTest {
    
    @Test
    public void testDefaultIntegerGeneration() {
        // 测试代码
    }
}
```

主要变化：
1. 导入语句从`org.junit.jupiter.api.Test`改为`org.testng.annotations.Test`
2. 类访问修饰符从包私有改为public
3. 测试方法访问修饰符从包私有改为public
4. 断言导入从`org.junit.jupiter.api.Assertions`改为`org.testng.Assert`

### 4. Maven Surefire插件配置

在pom.xml中添加TestNG的配置：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M7</version>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>testng.xml</suiteXmlFile>
        </suiteXmlFiles>
    </configuration>
</plugin>
```

### 5. 创建TestNG配置文件 (testng.xml)

在src/test/resources目录下创建testng.xml文件：

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="DataForge Test Suite">
    <test name="Core Tests">
        <packages>
            <package name="com.dataforge.*"/>
        </packages>
    </test>
</suite>
```

## 优势和劣势分析

### TestNG的优势：
1. 更灵活的测试配置（通过XML文件）
2. 支持并行测试执行
3. 提供更多的注解选项（如@BeforeSuite, @AfterSuite等）
4. 内置参数化测试支持
5. 更好的报告功能

### TestNG的劣势：
1. 学习曲线稍陡峭
2. 社区活跃度可能不如JUnit
3. 需要修改现有测试代码

## 建议

考虑到项目当前已经使用JUnit 5且测试代码结构良好，建议保持使用JUnit 5，除非有特殊需求需要TestNG的特定功能。如果确实需要迁移到TestNG，可以按照上述步骤进行。
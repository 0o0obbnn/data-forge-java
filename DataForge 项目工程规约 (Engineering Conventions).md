### **DataForge 项目工程规约 (Engineering Conventions) v1.0**

---

#### **I. 总体原则 (Overall Principles)**

1. **清晰性优于机巧 (Clarity over Cleverness):** 代码首先是写给人读的，其次才是给机器执行的。避免使用过于晦涩或“抖机灵”的写法。
    
2. **单一职责原则 (Single Responsibility Principle - SRP):** 每个类、每个方法都应有且只有一个引起它变化的原因。IdCardGenerator 只负责生成身份证，不应掺杂地址生成的逻辑。
    
3. **文档驱动 (Documentation-Driven):** 公共API、复杂算法、核心配置项必须有对应的文档说明。这既包括代码中的Javadoc，也包括项目README.md和设计文档。
    
4. **自动化一切可自动化之事 (Automate Everything Possible):** 依赖重复的人工操作是不可靠的。我们将通过工具和CI/CD流水线，自动化代码格式化、静态检查、测试、构建和部署。
    

---

#### **II. 版本控制规约 (Version Control Conventions) - Git**

1. **工作流 (Workflow):**
    
    - 采用 **Git Flow** 的简化模型。
        
    - main: 永久性的主分支，存放稳定发布版本，受保护，只接受来自develop的合并请求(Merge Request)。
        
    - develop: 开发主干分支，集成所有已完成的功能，是新功能的汇集点。
        
    - feature/<feature-name>: 功能开发分支，从develop创建，完成后合并回develop。例如：feature/add-uscc-generator。
        
    - fix/<fix-name>: Bug修复分支，从develop创建，修复后合并回develop。
        
    - hotfix/<fix-name>: 紧急线上Bug修复分支，从main创建，修复后同时合并回main和develop。
        
2. **分支命名 (Branch Naming):**
    
    - **格式:** <type>/<issue-id>-<short-description>
        
    - **示例:** feature/DF-12-add-bank-card-generator, fix/DF-25-fix-idcard-validation-error
        
3. **提交信息 (Commit Messages):**
    
    - 严格遵循 **Conventional Commits** 规范。这便于生成CHANGELOG和进行版本语义化。
        
    - **格式:** <type>(<scope>): <subject>
        
    - **type 可选值:** feat (新功能), fix (Bug修复), docs (文档), style (代码格式), refactor (重构), test (测试), chore (构建、工具等杂务)。
        
    - **scope 可选值:** core, cli, config, generator, docs 等，指明影响范围。
        
    - **示例:**
        
        Generated code
        
        ```
        feat(generator): Add USCC generator with validation
        
        Implements the Unified Social Credit Code (USCC) generator based on GB32100-2015 standard. Includes region code support and a validator.
        
        Closes DF-15.
        ```
        
        Use code [with caution](https://support.google.com/legal/answer/13505487).
        

---

#### **III. 编码规约 (Coding Conventions) - Java**

1. **代码风格 (Code Style):**
    
    - 遵循 **Google Java Style Guide**。
        
    - **工具:** 在IDE中安装google-java-format插件，并配置pre-commit hook或在CI中自动格式化，确保风格统一。
        
2. **命名规范 (Naming Conventions):**
    
    - **包名:** com.dataforge.core, com.dataforge.generators.basic
        
    - **类/接口:** IdCardGenerator, DataGenerator (大驼峰)
        
    - **方法/变量:** generateIdCard, firstName (小驼峰)
        
    - **常量:** MAX_AGE, DEFAULT_DATE_FORMAT (全大写下划线)
        
    - **禁止使用魔法值 (No Magic Numbers):** 任何有业务含义的字面量（字符串、数字）都必须定义为常量。
        
        - **反例:** if (age > 18)
            
        - **正例:** private static final int ADULT_AGE_THRESHOLD = 18; if (age > ADULT_AGE_THRESHOLD)
            
3. **日志记录 (Logging):**
    
    - **框架:** 使用 **SLF4J** 作为门面，**Logback** 作为实现。
        
    - **日志级别:**
        
        - ERROR: 严重错误，影响系统正常运行（如配置文件加载失败）。
            
        - WARN: 潜在问题或异常流程（如生成了不符合校验规则的“无效”数据）。
            
        - INFO: 关键业务流程节点（如“DataForge started”, “Generated 1000 records to output.csv”）。
            
        - DEBUG: 开发调试信息（如“Using context value 'gender:MALE' for IdCardGenerator”）。
            
        - TRACE: 更细粒度的调试信息。
            
    - **实践:** 使用占位符{}进行参数化，避免字符串拼接。
        
        Generated java
        
        ```
        // private static final Logger log = LoggerFactory.getLogger(MyClass.class);
        log.info("Generating {} records for type '{}'", count, type);
        ```
        
        Use code [with caution](https://support.google.com/legal/answer/13505487).Java
        
4. **异常处理 (Exception Handling):**
    
    - 明确区分checked和unchecked异常。业务校验失败（如“无效的地区码”）应使用自定义的unchecked异常（如InvalidConfigurationException）。
        
    - 禁止捕获Exception或Throwable而不做任何处理（catch (Exception e) {}）。
        
    - 异常信息必须清晰，包含必要的上下文。
        

---

#### **IV. 测试规约 (Testing Conventions)**

1. **测试金字塔 (Testing Pyramid):**
    
    - **单元测试 (Unit Tests):** (占比 > 70%) 针对单个DataGenerator或工具类。必须覆盖所有公共方法、边界条件和核心逻辑。
        
    - **集成测试 (Integration Tests):** (占比 ~20%) 测试模块间的协作，如GenerationContext如何影响多个生成器，或引擎与输出模块的集成。
        
    - **端到端测试 (E2E Tests):** (占比 < 10%) 模拟用户通过CLI执行完整的命令，验证最终输出文件的正确性。
        
2. **工具栈 (Tool Stack):**
    
    - **测试框架:** **JUnit 5**
        
    - **断言库:** **AssertJ** (提供流式、可读性强的断言)
        
    - **Mocking框架:** **Mockito**
        
    - **代码覆盖率:** **JaCoCo**，CI流水线强制要求核心模块单元测试覆盖率不低于 **80%**。
        
3. **测试代码组织:**
    
    - 测试类与被测类在同一包下，位于src/test/java。
        
    - 测试类命名: ClassNameTest，例如IdCardGeneratorTest.java。
        
    - 测试方法命名: should_ExpectedBehavior_when_StateUnderTest，例如should_generateValidIdCard_when_genderIsMale()。
        

---

#### **V. 构建与依赖管理规约 (Build & Dependency Conventions)**

1. **构建工具 (Build Tool):** **Maven** 或 **Gradle** (团队统一即可)。
    
2. **依赖管理 (Dependency Management):**
    
    - 所有依赖版本必须在pom.xml的<dependencyManagement>或Gradle的version catalog中统一管理，禁止在具体<dependencies>中硬编码版本。
        
    - 定期使用mvn versions:display-dependency-updates或类似工具检查并更新依赖，处理安全漏洞。
        
    - 引入新依赖必须经过**代码评审**，评估其必要性、许可证（License）和安全性。
        

---

#### **VI. 文档与代码评审规约 (Documentation & Code Review Conventions)**

1. **文档 (Documentation):**
    
    - **Javadoc:** 所有public的类和方法必须有清晰的Javadoc。
        
    - **README.md:** 项目根目录的README.md是项目入口，必须包含项目简介、构建和运行方法、CLI基本用法。
        
    - **设计文档:** DataForge.md这类设计文档需与代码实现保持同步，并纳入版本控制。
        
2. **代码评审 (Code Review):**
    
    - 所有合并到develop和main分支的代码都必须经过至少一位其他成员的**Approve**。
        
    - **PR/MR 规范:**
        
        - 标题清晰，遵循Commit Message规范。
            
        - 描述中清晰说明“**What** (做了什么)”和“**Why** (为什么这么做)”。
            
        - 关联对应的Issue ID。
            
        - 确保CI流水线全部通过。
            
    - **评审者职责:**
        
        - 关注代码的**正确性、可读性、健壮性和安全性**。
            
        - 提出建设性意见，使用友善的语言。
            
        - 不仅仅是找茬，对优秀的设计和实现要给予肯定。
            

---

#### **3. 风险、最佳实践与后续步骤 (Risks, Best Practices & Next Steps)**

- **潜在风险：**
    
    1. **规则过载与僵化：** 规则过于繁琐或不被团队认同，可能成为“纸面制度”，反而降低效率。
        
    2. **工具配置成本：** 建立和维护一套支持这些规则的自动化工具链（CI/CD, 静态分析等）需要初期投入。
        
- **最佳实践建议：**
    
    1. **自动化强制优于人工遵守：** 将代码风格、静态检查（如Checkstyle, SpotBugs）等规则集成到CI流水线中，构建失败则无法合并。这比依赖人工记忆和审查要可靠得多。
        
    2. **文化建设：** 规约的生命力在于团队共识。定期（如每个迭代回顾会）讨论规约的合理性，并根据实践进行调整。让规约成为团队共同的财富，而非架构师的个人指令。
        
- **下一步行动：**
    
    1. **工具链搭建：**
        
        - 在项目中配置好Checkstyle/SpotBugs和JaCoCo的Maven/Gradle插件。
            
        - 搭建基础的CI流水线（如使用GitLab CI/CD或GitHub Actions），至少包含build、test、coverage-report和static-analysis四个阶段。
            
    2. **项目初始化：**
        
        - 创建符合规约的Git分支 (main, develop)。
            
        - 建立基础包结构 (com.dataforge.core, com.dataforge.cli等)。
            
    3. **团队宣贯：** 召开一次简短的 kickoff 会议，向所有团队成员讲解这套规约，听取反馈并达成共识。
        
    4. **开始实践：** 从第一个feature分支开始，严格执行上述所有规约。
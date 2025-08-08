### **DataForge 系统架构设计说明书 v2.1 (Final)**

---

#### **A. 架构愿景与驱动因素 (Architectural Vision & Drivers)**

**愿景:** DataForge 旨在成为一个高性能、高可配置、高扩展性的测试数据生成解决方案。它既是一个功能强大的**可嵌入Java核心库 (Core Library)**，也是一个基于该库的、用户友好的**命令行工具 (CLI Application)**。

**核心架构驱动因素:**

1. **可嵌入性与API驱动 (Embeddable & API-Driven):** 核心能力必须通过一个清晰、流畅的公共API暴露，以便被任何Java项目（如自动化测试框架、数据初始化脚本）无缝集成。CLI应用仅是此API的一个标准实现。
    
2. **高可扩展性 (High Extensibility):** 必须能通过插件化机制，轻松添加新的数据类型生成器和数据输出器，而无需修改核心代码。这是保障项目长期生命力的基石。
    
3. **高性能与资源效率 (High Performance & Resource Efficiency):** 在生成海量数据时，必须具备高吞吐量并有效控制内存占用，特别是避免因大数据集导致内存溢出。
    
4. **数据关联性 (Data Cohesion):** 生成的数据集必须支持复杂的内部逻辑关联（如身份证号与性别、生日、地址的关联），确保数据的真实性和有效性。
    
5. **高可用性与健壮性 (High Usability & Robustness):** 作为开发/测试工具，其本身必须稳定可靠，提供清晰的错误反馈和友好的用户交互。
    

---

#### **B. 高层架构：分层与模块化 (Layered & Modular Architecture)**

DataForge 采用严格的多模块项目结构，以实现关注点分离和高复用性。

**项目模块图 (Project Module Diagram):**

Generated mermaid

```
graph TD
    subgraph "DataForge Project"
        CLI_APP[dataforge-cli]
        CORE_LIB[dataforge-core]
    end

    CLI_APP -- "Maven/Gradle Dependency" --> CORE_LIB

    style CORE_LIB fill:#e6f3ff,stroke:#0055cc,stroke-width:2px,stroke-dasharray: 5 5
    style CLI_APP fill:#f0e6ff,stroke:#6600cc,stroke-width:2px
```

Use code [with caution](https://support.google.com/legal/answer/13505487).Mermaid

- **dataforge-core (核心库):**
    
    - **定位:** 一个纯粹的、零依赖于CLI框架的Java库。它包含了所有的数据生成、配置处理和输出逻辑。这是DataForge的核心资产。
        
    - **产出:** dataforge-core.jar，可被任何其他项目作为依赖引入。
        
- **dataforge-cli (命令行应用):**
    
    - **定位:** dataforge-core库的一个“客户端”或“门面”。它依赖于core模块，负责解析CLI命令和参数，并将其翻译成对核心库API的调用。
        
    - **产出:** 一个包含所有依赖的可执行“胖Jar” (dataforge-cli-uber.jar)，供终端用户直接运行。
        

---

#### **C. dataforge-core 核心库架构详解**

**核心组件图 (Core Library Component Diagram):**

Generated mermaid

```
graph TD
    subgraph "Public API"
        F[Fluent API Facade (DataForge.java)]
    end

    subgraph "Internal Engine"
        OE[Orchestration Engine]
        CM[Configuration Model (POJOs)]
        GC[GenerationContext]
    end

    subgraph "Subsystems (Pluggable)"
        GS[Generator Subsystem (SPI)]
        OS[Output Subsystem (Streaming)]
    end

    subgraph "Shared Kernel"
        SK[Common Models & Utilities]
    end

    F -- "Builds & Validates" --> CM
    F -- "Invokes" --> OE
    OE -- "Driven by" --> CM
    OE -- "Manages" --> GC
    OE -- "Uses" --> GS
    OE -- "Uses" --> OS

    GS -- "Extends/Uses" --> SK
    OS -- "Extends/Uses" --> SK
    
    style F fill:#d4edda,stroke:#155724,stroke-width:2px
```

Use code [with caution](https://support.google.com/legal/answer/13505487).Mermaid

1. **公共API层 (Public API Layer):**
    
    - **组件:** Fluent API Facade (实现类: DataForge.java)
        
    - **职责:** 作为core库的唯一官方入口，提供一个优雅、链式调用的编程接口（Facade模式）。它隐藏了内部实现的复杂性，引导用户以声明式的方式构建数据生成任务。
        
    - **API设计:**
        
        Generated java
        
        ```
        // 示例：API驱动的数据生成
        List<User> users = DataForge.source() // 1. 入口
            .schema(schema -> schema         // 2. 定义Schema和字段
                .field("name", "name.cn")
                .field("age", "age", c -> c.min(18))
            )
            .count(10)                       // 3. 配置任务参数
            .outputAs(User.class)            // 4. 定义输出目标
            .generate();                     // 5. 执行
        ```
        
        Use code [with caution](https://support.google.com/legal/answer/13505487).Java
        
2. **内部引擎层 (Internal Engine Layer):**
    
    - **组件:** OrchestrationEngine, Configuration Model, GenerationContext
        
    - **职责:**
        
        - Configuration Model: 一组强类型的POJO（如ForgeConfig, FieldConfig），作为内部所有组件工作的统一数据模型。
            
        - OrchestrationEngine: 数据生成任务的指挥中心。它接收ForgeConfig，管理GenerationContext的生命周期，协调生成器和输出器的工作。
            
        - GenerationContext: 行级数据生成的上下文载体。在生成单条记录时创建，用于存储已生成字段的值（如gender），供后续关联字段（如idcard）查询使用，实现数据关联。
            
3. **子系统层 (Subsystems Layer):**
    
    - **组件:** Generator Subsystem, Output Subsystem
        
    - **职责:**
        
        - **Generator Subsystem:**
            
            - **DataGenerator<T> (接口):** 所有数据生成器的契约。
                
            - **GeneratorFactory (工厂):** 使用Java **SPI (Service Provider Interface)** 机制，在启动时自动发现并注册所有可用的DataGenerator实现。这使得添加新生成器无需修改工厂代码，实现了真正的**插件化**。
                
        - **Output Subsystem:**
            
            - **DataWriter (接口):** 所有数据输出器的契约，定义了open(), writeRow(), close()等方法。
                
            - **实现类 (e.g., CsvWriter, JsonWriter):** 必须采用**流式处理 (Streaming)**，逐条将数据写入目标，确保在处理海量数据时内存占用恒定。
                
4. **共享内核 (Shared Kernel):**
    
    - 存放被所有其他组件依赖的通用类和工具，如自定义异常、字符串工具、注解等。
        

---

#### **D. dataforge-cli 命令行应用架构**

- **职责:** 作为一个轻量级适配器，将用户友好的CLI交互转换为对dataforge-core的程序化调用。
    
- **技术栈:** picocli
    
- **工作流程:**
    
    1. **解析 (Parse):** picocli注解驱动的命令类（如GenerateCommand）解析命令行参数和选项。
        
    2. **翻译 (Translate):** 命令类将解析出的CLI参数，逐一调用DataForge的流式API，构建一个完整的生成任务。
        
    3. **执行 (Execute):** 调用流式API的最终执行方法（如.run()）。
        
    4. **反馈 (Feedback):** 捕获core库可能抛出的异常，将其转换为格式化的、对用户友好的错误信息，并控制程序的退出码。
        

---

#### **E. 关键交互与数据流 (Key Interactions & Data Flow)**

**编程调用流程:**  
External App -> Fluent API -> Orchestration Engine -> Generator -> Context -> Writer

**CLI调用流程:**  
User -> Terminal -> dataforge-cli -> Fluent API -> [同上]

---

#### **F. 目录结构与打包策略 (Directory Structure & Packaging)**

- **目录结构:**
    
    Generated code
    
    ```
    dataforge-project/
    ├── dataforge-core/ (产出: dataforge-core-${version}.jar)
    ├── dataforge-cli/  (产出: dataforge-cli-${version}-uber.jar)
    └── pom.xml
    ```
    
    Use code [with caution](https://support.google.com/legal/answer/13505487).
    
- **打包:**
    
    - dataforge-core打包成一个标准的jar，可供发布到Maven Central。
        
    - dataforge-cli使用maven-shade-plugin或类似工具，将其自身代码和所有依赖（包括dataforge-core）打包成一个可独立执行的胖Jar。
        

---

#### **3. 风险、最佳实践与后续步骤 (Risks, Best Practices & Next Steps)**

- **潜在风险:**
    
    - **API演进:** 作为公共API，Fluent API Facade的演进需要谨慎，遵循语义化版本控制，避免破坏性变更。
        
    - **配置收敛:** 必须确保CLI和编程式配置都能有效、无冲突地转换为内部的Configuration Model。
        
- **最佳实践建议:**
    
    - **API优先开发 (API-First Development):** 任何新功能都应先在dataforge-core中通过Fluent API实现和测试，然后再为它添加CLI支持。
        
    - **分层测试 (Layered Testing):**
        
        - core模块应有极高的单元测试覆盖率（>80%），重点测试每个生成器和核心逻辑。
            
        - cli模块的测试重点在于验证参数解析和到API调用的翻译是否正确。
            
    - **文档即代码 (Docs-as-Code):** 公共API的Javadoc必须详尽，并作为发布的一部分。README.md应同时包含CLI用法和编程示例。
        
- **下一步行动:**
    
    1. **项目初始化:** 按照上述多模块结构搭建项目骨架。
        
    2. **定义核心API:** 在dataforge-core中，优先定义DataForge流式API的接口和骨架实现。
        
    3. **实现MVP功能切片:**
        
        - 实现1-2个简单的DataGenerator和CsvWriter。
            
        - 构建OrchestrationEngine以驱动一个基本的生成流程。
            
        - 在dataforge-cli中实现一个最简GenerateCommand来调用核心API。
            
    4. **建立CI/CD:** 立即配置自动化构建、测试和代码质量检查流水线，确保架构设计的健康度得到持续保障。


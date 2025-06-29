# Spring Batch 集成示例

这是一个Spring Batch框架的集成示例项目，展示了如何在Spring Boot应用中使用Spring Batch进行批处理任务。

## 功能特性

- 基于Spring Boot 3.x和Spring Batch
- 使用H2内存数据库存储批处理元数据
- 提供了两种批处理任务实现方式：
  - 基于Chunk的CSV文件处理任务
  - 基于Tasklet的简单任务

## 项目结构

```
src/main/java/io/github/yaojiqunaer/batch/
├── BatchApplication.java               # 应用程序入口
├── config/                             # 配置类
│   ├── BatchConfig.java                # 批处理基本配置
│   └── TaskletJobConfig.java           # Tasklet任务配置
├── job/                                # 任务控制器
│   ├── BatchJobController.java         # 批处理任务控制器
│   └── TaskletJobController.java       # Tasklet任务控制器
├── listener/                           # 监听器
│   └── JobCompletionNotificationListener.java  # 任务完成通知监听器
├── model/                              # 数据模型
│   └── Person.java                     # 人员信息模型
└── tasklet/                            # 任务执行器
    └── HelloWorldTasklet.java          # Hello World任务执行器
```

## 如何使用

1. 启动应用程序：
   ```
   mvn spring-boot:run
   ```

2. 访问以下URL启动批处理任务：
   - 基于Chunk的CSV处理任务：http://localhost:8080/batch-demo/batch/start
   - 基于Tasklet的简单任务：http://localhost:8080/batch-demo/tasklet/start

3. 查看H2数据库控制台（可选）：
   - 访问：http://localhost:8080/batch-demo/h2-console
   - JDBC URL: `jdbc:h2:mem:testdb`
   - 用户名：`sa`
   - 密码：(留空)

## 技术栈

- Spring Boot 3.x
- Spring Batch
- H2数据库
- Lombok
- Spring Web

## 示例说明

1. **CSV文件处理任务**：读取`sample-data.csv`文件，处理数据后写入到`target/processed-persons.txt`文件。

2. **Tasklet任务**：简单的Hello World任务，仅打印日志信息。

## 注意事项

- 应用启动时不会自动执行批处理任务，需要通过API手动触发
- 所有批处理元数据存储在H2内存数据库中，应用重启后数据会丢失 
# Java技术栈 (Java Technology Stack)

🚀 Java技术栈使用集合，包含各种Java技术的示例代码和最佳实践

## 项目简介

本项目是一个Java技术栈的集合，旨在提供各种Java相关技术的示例代码、最佳实践和集成方案。项目包含多个模块，每个模块专注于特定的技术领域或框架集成。

## 项目结构

项目主要分为两大部分：

### 1. 框架模块 (framework)

提供可复用的框架组件和工具类：

- **commons-lang**: 通用工具类库
- **exception-spring-boot-starter**: 全局异常处理启动器
- **jpa-plus-spring-boot-starter**: JPA增强功能启动器
- **resttemplate-spring-boot-starter**: RestTemplate客户端启动器
- **distributedlock-spring-boot-starter**: 分布式锁实现启动器

### 2. 示例模块 (sample)

展示各种技术的集成和使用方式：

- **spring-batch-integration**: Spring Batch批处理框架集成示例
- **spring-kafka-integration**: Spring Kafka消息队列集成示例
- **spring-redistempalte-integration**: Spring Redis集成示例
- **spring-logback-integration**: Logback日志框架集成示例
- **spring-webflux-integration**: Spring WebFlux响应式编程示例
- **spring-https-integration**: HTTPS服务器配置示例
- **spring-security-integration**: Spring Security安全框架集成示例
- **spring-openapi-codegen-integration**: OpenAPI代码生成集成示例
- **spring-easyexcel-integration**: EasyExcel表格处理集成示例
- **spring-plugins-gitinfo**: Git信息插件集成示例
- **lombok-mapstruct-integration**: Lombok和MapStruct集成示例
- **jvm-gc-test**: JVM垃圾回收测试示例

## 技术栈

- **基础框架**: Spring Boot 3.x
- **构建工具**: Maven
- **Java版本**: JDK 17
- **数据库**: H2, MySQL等
- **其他技术**: Kafka, Redis, Batch, WebFlux, Security等

## 如何使用

### 克隆项目

```bash
git clone https://github.com/yaojiqunaer/java-technology-stack.git
cd java-technology-stack
```

### 构建项目

```bash
mvn clean install
```

### 运行示例

每个示例模块都可以独立运行，例如运行Spring Batch示例：

```bash
cd sample/spring-batch-integration
mvn spring-boot:run
```

## 文档

项目包含以下文档：

- **docs/**: 包含各种技术文档和笔记
  - computer-network/: 网络协议相关文档
  - jvm/: JVM相关文档
  - kubernetes/: Kubernetes相关文档
  - linux/: Linux命令相关文档
  - spring/: Spring框架相关文档

## 贡献指南

欢迎提交Pull Request或Issue来完善本项目。贡献前请确保：

1. 代码符合项目的编码规范
2. 新功能或修改有完善的测试
3. 文档已更新

## 许可证

本项目采用Apache 2.0许可证，详情请参见[LICENSE](LICENSE)文件。

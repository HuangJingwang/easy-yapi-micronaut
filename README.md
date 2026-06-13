# easy-yapi-micronaut

基于 [tangcent/easy-yapi](https://github.com/tangcent/easy-yapi) 的 IntelliJ IDEA 插件分支，主要增加对 [Micronaut](https://micronaut.io/) 项目的 API 导出支持，并适配 JDK 21 构建环境。

> 原项目文档请参考 [easyyapi.com](https://easyyapi.com)。

## 功能概览

- 从 Java/Kotlin 项目中解析接口信息并导出 API 文档。
- 支持导出到 YApi、Postman、Markdown、Curl、HTTP Client 等格式。
- 支持在 IDE 内进行接口调用调试。
- 在原 easy-yapi 能力基础上扩展 Micronaut Controller 和路由注解解析。
- 支持使用 JDK 21 进行本地构建，产物字节码目标保持 Java 17。

## Micronaut 支持

当前分支已支持以下 Micronaut 常用注解：

- `@Controller`
- `@Get`
- `@Post`
- `@Put`
- `@Delete`
- `@Patch`
- `@PathVariable`
- `@Header`
- `@Body`

后续可继续完善的方向：

- `@QueryValue`
- Micronaut 自定义组合注解和更多参数绑定场景
- 更完整的测试用例覆盖

## 支持范围

| 类别 | 默认支持 | 扩展支持 |
| --- | --- | --- |
| 语言 | Java, Kotlin | Scala |
| Web 框架 | Spring, Feign, JAX-RS, Micronaut | Dubbo |
| 导出渠道 | YApi, Postman, Markdown, Curl, HTTP Client | - |
| 数据/注解 | javax.validation, Jackson, Gson | Swagger |

## 环境要求

| 项目 | 要求 |
| --- | --- |
| IDE | IntelliJ IDEA Community / Ultimate 2021.2.1 及以上 |
| 构建 JDK | JDK 17 或 JDK 21 |
| 字节码目标 | Java 17 |
| Gradle | 使用仓库内置 Gradle Wrapper |

JDK 与 IDEA 版本选择逻辑：

| 构建 JDK | IntelliJ IDEA 版本 | since-build |
| --- | --- | --- |
| 11 | 2021.2.1 | 212 |
| 15 | 2022.2.3 | 223 |
| 17 | 2023.1.3 | 231 |
| 21 | 2023.3.1 | 233 |

## 快速开始

克隆项目：

```bash
git clone https://github.com/HuangJingwang/easy-yapi-micronaut.git
cd easy-yapi-micronaut
```

运行插件开发环境：

```bash
./gradlew :idea-plugin:runIde
```

Windows PowerShell:

```powershell
.\gradlew.bat :idea-plugin:runIde
```

构建插件：

```bash
./gradlew :idea-plugin:buildPlugin
```

运行测试：

```bash
./gradlew :idea-plugin:test
```

如果只是验证编译和 IntelliJ instrumentation：

```bash
./gradlew :idea-plugin:compileTestKotlin
```

## 使用说明

1. 在 IntelliJ IDEA 中打开待导出的项目。
2. 选中包含 Controller 的类、文件或目录。
3. 使用 easy-yapi 的导出入口选择导出渠道。
4. Micronaut 项目会根据 Controller 和 HTTP 方法注解解析接口路径、请求方法、Header、Path 参数和 Body。

更多基础用法可参考：

- [安装说明](https://easyyapi.com/documents/installation.html)
- [导出 API 文档](https://easyyapi.com/documents/use.html)
- [导出到 YApi](https://easyyapi.com/documents/export2yapi.html)
- [导出到 Postman](https://easyyapi.com/documents/export2postman.html)
- [导出到 Markdown](https://easyyapi.com/documents/export2markdown.html)

## 开发说明

本分支在 Micronaut 支持上主要涉及以下模块：

- `idea-plugin/src/main/kotlin/com/itangcent/idea/plugin/api/export/micronaut`
- `idea-plugin/src/main/resources/META-INF/services`

Micronaut Controller 识别和路由解析使用 SPI 扩展机制。服务注册顺序需要与 Spring 相关实现保持一致，标准实现优先，自定义实现随后补充，以避免组合代理返回值被覆盖。

## 常见问题

### 为什么 JDK 21 构建会失败？

旧配置将 Java toolchain 固定为 JDK 17。使用 JDK 21 运行 Gradle 时，Gradle 仍然会寻找本地 JDK 17；如果本机没有安装 JDK 17，就会出现 `No matching toolchains found`。

当前分支已支持使用 JDK 21 作为构建 JDK，同时通过 `jvmTarget = 17` 和 `options.release = 17` 保持 Java 17 字节码兼容性。

### 为什么 SPI 看起来没有生效？

如果多个 SPI 实现由组合代理依次调用，返回值可能受实现顺序影响。Micronaut Controller Resolver 的服务注册顺序应保持为：

```text
com.itangcent.idea.plugin.api.export.micronaut.StandardMicronautControllerAnnotationResolver
com.itangcent.idea.plugin.api.export.micronaut.CustomMicronautControllerAnnotationResolver
```

同时业务类应注入接口 `MicronautControllerAnnotationResolver`，不要直接注入具体实现。

## 致谢

本项目基于 [tangcent/easy-yapi](https://github.com/tangcent/easy-yapi) 开发，感谢原项目作者和社区贡献者。

## 许可证

本项目沿用原项目许可证，详见 [LICENSE](LICENSE)。

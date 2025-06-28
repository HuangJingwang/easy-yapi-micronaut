# easy-yapi-micronaut

> ✨ Forked from [tangcent/easy-yapi](https://github.com/tangcent/easy-yapi)
>  ✅ 增加对 [Micronaut](https://micronaut.io/) 框架的支持
>  ☕ 兼容 JDK 21

------

## 🌟 Features

- [API 文档导出](https://easyyapi.com/documents/use.html)
- [接口调用调试](http://easyyapi.com/documents/call.html)

------

## ✅ 支持情况一览

| 类别         | 原生支持                                                     | 扩展支持                          |
| ------------ | ------------------------------------------------------------ | --------------------------------- |
| **语言**     | Java, Kotlin                                                 | Scala                             |
| **Web 框架** | [Spring](https://spring.io/), [Feign](https://spring.io/projects/spring-cloud-openfeign), [JAX-RS](https://www.oracle.com/technical-resources/articles/java/jax-rs.html)（支持 [Quarkus](https://quarkus.io/)、[Jersey](https://eclipse-ee4j.github.io/jersey/)）, [Micronaut](https://micronaut.io/) | [Dubbo](https://dubbo.apache.org) |
| **导出渠道** | [Postman](https://easyyapi.com/documents/export2postman.html), [YApi](https://easyyapi.com/documents/export2yapi.html), [Markdown](https://easyyapi.com/documents/export2markdown.html), [Curl](https://curl.se/), [HttpClient](https://plugins.jetbrains.com/plugin/13121-http-client) | -                                 |
| **数据格式** | `javax.validation`, Jackson, Gson                            | [Swagger](https://swagger.io/)    |



------

## 💻 兼容性

| JDK 版本 | IntelliJ IDEA 版本 | 状态 |
| -------- | ------------------ | ---- |
| 11       | 2021.2.1           | ✅    |
| 15       | 2022.2.3           | ✅    |
| 17       | 2023.1.3           | ✅    |
| 21       | 2024.2.4           | ✅    |



------

## 📌 当前注意事项

1. **构建时可能抛出异常**，但不影响插件功能运行。
2. **SPI 动态加载机制当前可能不生效**，原因可能与依赖升级有关，待进一步验证。
3. **Micronaut 基本注解支持完备**，如 `@Controller`、`@Get`、`@Post` 等均可正常导出至 YApi，@QueryValue`, `@PathVariable还未支持，待改进。

## 2025.6.28 更新
1. 目前支持了Micronaut的@PathVariable和@Header注解
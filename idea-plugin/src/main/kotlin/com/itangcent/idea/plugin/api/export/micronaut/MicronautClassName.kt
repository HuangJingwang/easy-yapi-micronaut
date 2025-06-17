package com.itangcent.idea.plugin.api.export.micronaut


/**
 * 该对象用于统一管理 Micronaut 中常见的类名和注解名常量，
 * 以便在代码中进行注解解析与识别时使用。
 */
object MicronautClassName {

    val MICRONAUT_REQUEST_RESPONSE: Array<String> = arrayOf(
        "io.micronaut.http.HttpRequest",
        "io.micronaut.http.HttpResponse"
    )

    const val MICRONAUT_CONTROLLER_ANNOTATION = "io.micronaut.http.annotation.Controller"

    // 文件上传
    const val COMPLETED_FILE_UPLOAD = "io.micronaut.http.multipart.CompletedFileUpload"

    // 注解
    const val GET_ANNOTATION = "io.micronaut.http.annotation.Get"
    const val POST_ANNOTATION = "io.micronaut.http.annotation.Post"
    const val PUT_ANNOTATION = "io.micronaut.http.annotation.Put"
    const val DELETE_ANNOTATION = "io.micronaut.http.annotation.Delete"
    const val PATCH_ANNOTATION = "io.micronaut.http.annotation.Patch"

    const val BODY_ANNOTATION = "io.micronaut.http.annotation.Body"
    const val QUERY_VALUE_ANNOTATION = "io.micronaut.http.annotation.QueryValue"
    const val PATH_VARIABLE_ANNOTATION = "io.micronaut.http.annotation.PathVariable"
    const val HEADER_ANNOTATION = "io.micronaut.http.annotation.Header"
    const val COOKIE_VALUE_ANNOTATION = "io.micronaut.http.annotation.CookieValue"

    val MICRONAUT_SINGLE_REQUEST_MAPPING_ANNOTATIONS: Set<String> = setOf(
        GET_ANNOTATION,
        POST_ANNOTATION,
        PUT_ANNOTATION,
        DELETE_ANNOTATION,
        PATCH_ANNOTATION
    )

    val MICRONAUT_REQUEST_MAPPING_ANNOTATIONS: Set<String> =
        MICRONAUT_SINGLE_REQUEST_MAPPING_ANNOTATIONS

    // 特殊默认值（如果 Micronaut 有类似语义需要）
    const val HEADER_DEFAULT_NONE = ""
    const val ESCAPE_HEADER_DEFAULT_NONE = ""

    // Micronaut Endpoint annotations (如适用，Actuator 类似功能)
    const val ENDPOINT_ANNOTATION = "io.micronaut.management.endpoint.annotation.Endpoint"
    const val READ_OPERATION_ANNOTATION = "io.micronaut.management.endpoint.annotation.Read"
    const val WRITE_OPERATION_ANNOTATION = "io.micronaut.management.endpoint.annotation.Write"
    const val DELETE_OPERATION_ANNOTATION = "io.micronaut.management.endpoint.annotation.Delete"

    val ENDPOINT_ANNOTATIONS: Set<String> = setOf(
        ENDPOINT_ANNOTATION
    )

    val ENDPOINT_OPERATION_ANNOTATIONS: Set<String> = setOf(
        READ_OPERATION_ANNOTATION,
        WRITE_OPERATION_ANNOTATION,
        DELETE_OPERATION_ANNOTATION
    )
}
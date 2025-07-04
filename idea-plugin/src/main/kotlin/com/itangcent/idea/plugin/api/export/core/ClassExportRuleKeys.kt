package com.itangcent.idea.plugin.api.export.core

import com.itangcent.intellij.config.rule.*

object ClassExportRuleKeys {

    val IS_MICRONAUT_CTRL: RuleKey<Boolean> = SimpleRuleKey(
        "class.is.micronaut.ctrl",
        arrayOf("class.is.ctrl"),
        BooleanRuleMode.ANY
    )

    val MODULE: RuleKey<String> = SimpleRuleKey(
        "module",
        StringRuleMode.SINGLE
    )

    val IGNORE: RuleKey<Boolean> = SimpleRuleKey(
        "ignore",
        BooleanRuleMode.ANY
    )

    val IS_FEIGN_CTRL: RuleKey<Boolean> = SimpleRuleKey(
        "class.is.feign.ctrl",
        BooleanRuleMode.ANY
    )

    val IS_SPRING_CTRL: RuleKey<Boolean> = SimpleRuleKey(
        "class.is.spring.ctrl",
        arrayOf("class.is.ctrl"),
        BooleanRuleMode.ANY
    )

    val IS_JAXRS_CTRL: RuleKey<Boolean> = SimpleRuleKey(
        "class.is.jaxrs.ctrl",
        arrayOf("class.is.quarkus.ctrl"),
        BooleanRuleMode.ANY
    )

    //filter class for methodDoc(rpc)
    val CLASS_FILTER: RuleKey<Boolean> = SimpleRuleKey(
        "mdoc.class.filter",
        BooleanRuleMode.ANY
    )

    //filter method for methodDoc(rpc)
    val METHOD_FILTER: RuleKey<Boolean> = SimpleRuleKey(
        "mdoc.method.filter",
        BooleanRuleMode.ANY
    )

    val METHOD_DOC_PATH: RuleKey<String> = SimpleRuleKey(
        "mdoc.method.path",
        StringRuleMode.SINGLE
    )

    val METHOD_DOC_METHOD: RuleKey<String> = SimpleRuleKey(
        "mdoc.method.http.method",
        StringRuleMode.SINGLE
    )

    val PARAM_DOC: RuleKey<String> = SimpleRuleKey(
        "param.doc",
        arrayOf("doc.param"),

        StringRuleMode.MERGE_DISTINCT
    )

    val METHOD_DOC: RuleKey<String> = SimpleRuleKey(
        "method.doc",
        arrayOf("doc.method"),

        StringRuleMode.MERGE_DISTINCT
    )

    val CLASS_DOC: RuleKey<String> = SimpleRuleKey(
        "class.doc",
        arrayOf("doc.class"),

        StringRuleMode.MERGE_DISTINCT
    )

    val METHOD_ADDITIONAL_HEADER: RuleKey<String> = SimpleRuleKey(
        "method.additional.header",
        StringRuleMode.MERGE_DISTINCT
    )

    val METHOD_ADDITIONAL_PARAM: RuleKey<String> = SimpleRuleKey(
        "method.additional.param",
        StringRuleMode.MERGE_DISTINCT
    )

    val METHOD_ADDITIONAL_RESPONSE_HEADER: RuleKey<String> = SimpleRuleKey(
        "method.additional.response.header",
        StringRuleMode.MERGE_DISTINCT
    )

    val PARAM_REQUIRED: RuleKey<Boolean> = SimpleRuleKey(
        "param.required",
        BooleanRuleMode.ANY
    )

    val PARAM_IGNORE: RuleKey<Boolean> = SimpleRuleKey(
        "param.ignore",
        BooleanRuleMode.ANY
    )

    val PARAM_NAME: RuleKey<String> = SimpleRuleKey(
        "param.name",
        StringRuleMode.SINGLE
    )

    val PARAM_TYPE: RuleKey<String> = SimpleRuleKey(
        "param.type",
        StringRuleMode.SINGLE
    )

    val PARAM_DEFAULT_VALUE: RuleKey<String> = SimpleRuleKey(
        "param.default.value",
        StringRuleMode.MERGE_DISTINCT
    )

    val FIELD_REQUIRED: RuleKey<Boolean> = SimpleRuleKey(
        "field.required",
        BooleanRuleMode.ANY
    )

    val FIELD_ORDER_WITH: RuleKey<Int> = SimpleRuleKey(
        "field.order.with",
        IntRuleMode
    )

    val CLASS_PREFIX_PATH: RuleKey<String> = SimpleRuleKey(
        "class.prefix.path",
        StringRuleMode.SINGLE
    )

    val ENDPOINT_PREFIX_PATH: RuleKey<String> = SimpleRuleKey(
        "endpoint.prefix.path",
        StringRuleMode.SINGLE
    )

    /**
     * the main goal of the {@return}
     */
    val METHOD_RETURN_MAIN: RuleKey<String> = SimpleRuleKey(
        "method.return.main",
        StringRuleMode.SINGLE
    )

    /**
     * the real return type of method
     */
    val METHOD_RETURN: RuleKey<String> = SimpleRuleKey(
        "method.return",
        StringRuleMode.SINGLE
    )

    /**
     * The content-type of the api.
     */
    val METHOD_CONTENT_TYPE: RuleKey<String> = SimpleRuleKey(
        "method.content.type",
        StringRuleMode.SINGLE
    )

    /**
     * The type of the param in http request.
     * Param with annotation like @RequestBody/@ModelAttribute/... do not compute this rule.
     * should return body/form/query
     */
    val PARAM_HTTP_TYPE: RuleKey<String> = SimpleRuleKey(
        "param.http.type",
        StringRuleMode.SINGLE
    )

    /**
     * name of api
     */
    val API_NAME: RuleKey<String> = SimpleRuleKey(
        "api.name",
        StringRuleMode.SINGLE
    )

    /**
     * folder of api
     */
    val API_FOLDER: RuleKey<String> = SimpleRuleKey(
        "folder.name",
        StringRuleMode.SINGLE
    )

    //default http method of api(method)
    val METHOD_DEFAULT_HTTP_METHOD: RuleKey<String> = SimpleRuleKey(
        "method.default.http.method",
        StringRuleMode.SINGLE
    )

    val FIELD_DEFAULT_VALUE: RuleKey<String> = SimpleRuleKey(
        "field.default.value",
        StringRuleMode.SINGLE
    )

    val FIELD_DEMO: RuleKey<String> = SimpleRuleKey(
        "field.demo",
        StringRuleMode.SINGLE
    )

    val POST_MAN_HOST: RuleKey<String> = SimpleRuleKey(
        "postman.host",
        StringRuleMode.SINGLE
    )

    val HTTP_CLIENT_BEFORE_CALL: RuleKey<Boolean> = SimpleRuleKey(
        "http.call.before",
        EventRuleMode.IGNORE_ERROR
    )

    val HTTP_CLIENT_AFTER_CALL: RuleKey<Boolean> = SimpleRuleKey(
        "http.call.after",
        EventRuleMode.IGNORE_ERROR
    )

    val PATH_MULTI: RuleKey<String> = SimpleRuleKey(
        "path.multi",
        StringRuleMode.SINGLE
    )

    val AFTER_EXPORT: RuleKey<String> = SimpleRuleKey(
        "export.after",
        EventRuleMode.IGNORE_ERROR
    )

    val JSON_FIELD_PARSE_BEFORE: RuleKey<Boolean> = SimpleRuleKey(
        "json.field.parse.before", arrayOf("field.parse.before"),
        EventRuleMode.THROW_IN_ERROR
    )

    val JSON_FIELD_PARSE_AFTER: RuleKey<Boolean> = SimpleRuleKey(
        "json.field.parse.after", arrayOf("field.parse.after"),
        EventRuleMode.THROW_IN_ERROR
    )

    val JSON_CLASS_PARSE_BEFORE: RuleKey<Boolean> = SimpleRuleKey(
        "json.class.parse.before", arrayOf("class.parse.before"),
        EventRuleMode.THROW_IN_ERROR
    )

    val JSON_CLASS_PARSE_AFTER: RuleKey<Boolean> = SimpleRuleKey(
        "json.class.parse.after", arrayOf("class.parse.after"),
        EventRuleMode.THROW_IN_ERROR
    )

    val JSON_ADDITIONAL_FIELD: RuleKey<String> = SimpleRuleKey(
        "json.additional.field",
        StringRuleMode.MERGE_DISTINCT
    )

    //region api parse - event
    val API_CLASS_PARSE_BEFORE: RuleKey<Boolean> = SimpleRuleKey(
        "api.class.parse.before", EventRuleMode.THROW_IN_ERROR
    )

    val API_CLASS_PARSE_AFTER: RuleKey<Boolean> = SimpleRuleKey(
        "api.class.parse.after", EventRuleMode.THROW_IN_ERROR
    )

    val API_METHOD_PARSE_BEFORE: RuleKey<Boolean> = SimpleRuleKey(
        "api.method.parse.before", EventRuleMode.THROW_IN_ERROR
    )

    val API_METHOD_PARSE_AFTER: RuleKey<Boolean> = SimpleRuleKey(
        "api.method.parse.after", EventRuleMode.THROW_IN_ERROR
    )

    val API_PARAM_BEFORE: RuleKey<String> = SimpleRuleKey(
        "api.param.parse.before", arrayOf("param.before"),
        EventRuleMode.IGNORE_ERROR
    )

    val API_PARAM_AFTER: RuleKey<String> = SimpleRuleKey(
        "api.param.parse.after", arrayOf("param.after"),
        EventRuleMode.IGNORE_ERROR
    )
    //endregion


    val PROPERTIES_PREFIX: RuleKey<String> = SimpleRuleKey(
        "properties.prefix",
        StringRuleMode.SINGLE
    )
}

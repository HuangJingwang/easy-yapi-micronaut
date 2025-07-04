package com.itangcent.idea.plugin.api.export.micronaut
import com.google.inject.Inject
import com.google.inject.Singleton
import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.itangcent.common.constant.HttpMethod
import com.itangcent.common.model.Header
import com.itangcent.common.model.Request
import com.itangcent.common.model.URL
import com.itangcent.common.utils.*
import com.itangcent.idea.condition.annotation.ConditionOnClass
import com.itangcent.idea.plugin.api.export.condition.ConditionOnSimple
import com.itangcent.idea.plugin.api.export.core.*
import com.itangcent.intellij.config.rule.computer
import com.itangcent.intellij.jvm.AnnotationHelper
import com.itangcent.intellij.jvm.asPsiClass
import com.itangcent.intellij.util.hasFile
import kotlin.math.log

/**
 * Support export apis from Micronaut controllers.
 */
@Singleton
@ConditionOnSimple(false)
@ConditionOnClass(MicronautClassName.MICRONAUT_CONTROLLER_ANNOTATION)
open class MicronautRequestClassExporter : RequestClassExporter() {

    @Inject
    protected lateinit var annotationHelper: AnnotationHelper

    @Inject
    protected lateinit var micronautRequestMappingResolver: MicronautRequestMappingResolver

    @Inject
    private lateinit var micronautControllerAnnotationResolver: StandardMicronautControllerAnnotationResolver


    override fun processClass(cls: PsiClass, classExportContext: ClassExportContext) {
//        logger.info("执行了micronaut的processClass")

        val ctrlRequestMappingAnn = findRequestMappingInAnn(cls)
        var basePath: URL = findHttpPath(ctrlRequestMappingAnn)
        val prefixPath = ruleComputer.computer(ClassExportRuleKeys.CLASS_PREFIX_PATH, cls)
        if (prefixPath.notNullOrBlank()) {
            basePath = URL.of(prefixPath).concat(basePath)
        }

        val ctrlHttpMethod = findHttpMethod(ctrlRequestMappingAnn)

        classExportContext.setExt("basePath", basePath)
        classExportContext.setExt("ctrlHttpMethod", ctrlHttpMethod)
    }

    override fun hasApi(psiClass: PsiClass): Boolean {
//        logger.info("执行了micronaut的hasApi")
//        logger.info("是否存在controller注解" + micronautControllerAnnotationResolver.hasControllerAnnotation(psiClass));

//        logger.info("是否存在controller注解" + ruleComputer.computer(ClassExportRuleKeys.IS_MICRONAUT_CTRL, psiClass));
//        logger.info("MicronautControllerAnnotationResolver impl11 = ${micronautControllerAnnotationResolver.javaClass.simpleName}")
//        logger.info("当前的实现使用的是" + (micronautControllerAnnotationResolver is CustomMicronautControllerAnnotationResolver))
//        logger.info("当前的实现使用的是" + (micronautControllerAnnotationResolver is StandardMicronautControllerAnnotationResolver))
//        logger.info("micronautControllerAnnotationResolver的类是：" + micronautControllerAnnotationResolver.javaClass)

//        logger.info("执行了micronaut的hasApi" + micronautControllerAnnotationResolver.hasControllerAnnotation(psiClass))
//        logger.info( "执行了micronaut的hasApi111"+ ruleComputer.computer(ClassExportRuleKeys.IS_MICRONAUT_CTRL, psiClass))
        return true
    }

    override fun isApi(psiMethod: PsiMethod): Boolean {
//        logger.info("执行了micronaut的isApi" + psiMethod.name)
        return micronautRequestMappingResolver.resolveRequestMapping(psiMethod) != null
    }

    override fun processMethodParameter(
        request: Request,
        parameterExportContext: ParameterExportContext,
        paramDesc: String?,
    ) {

        //RequestBody(json)
        if (isRequestBody(parameterExportContext.psi())) {
            setRequestBody(
                parameterExportContext,
                request, parameterExportContext.originalReturnObject(), paramDesc
            )
            return
        }


        val ultimateComment = getUltimateCommentOfParam(paramDesc, parameterExportContext)

        //head
        val requestHeaderAnn = findRequestHeader(parameterExportContext.psi())
        if (requestHeaderAnn != null) {

            var headName = requestHeaderAnn.any("value", "name")?.toString()
            if (headName.isNullOrEmpty()) {
                headName = parameterExportContext.name()
            } else {
                parameterExportContext.setResolvedName(headName)
            }

            var required = findRequired(requestHeaderAnn)
            if (!required && ruleComputer.computer(
                    ClassExportRuleKeys.PARAM_REQUIRED,
                    parameterExportContext.element()
                ) == true
            ) {
                required = true
            }

            val defaultValue = findDefaultValue(requestHeaderAnn) ?: ""

            val header = Header()
            header.name = headName
            header.value = parameterExportContext.defaultVal() ?: defaultValue
            header.desc = ultimateComment
            header.required = required
            requestBuilderListener.addHeader(parameterExportContext, request, header)
            return
        }

        //path
        val pathVariableAnn = findPathVariable(parameterExportContext.psi())
        if (pathVariableAnn != null) {
            logger.warn("pathVariable is not supported in micronaut", )
            var pathName = pathVariableAnn["value"]?.toString()

            if (pathName == null) {
                pathName = parameterExportContext.name()
            } else {
                parameterExportContext.setResolvedName(pathName)
            }

            requestBuilderListener.addPathParam(parameterExportContext, request, pathName, ultimateComment)
            return
        }

        //cookie
        val cookieValueAnn = findCookieValue(parameterExportContext.psi())
        if (cookieValueAnn != null) {

            var cookieName = cookieValueAnn["value"]?.toString()

            if (cookieName == null) {
                cookieName = parameterExportContext.name()
            } else {
                parameterExportContext.setResolvedName(cookieName)
            }


            var required = findRequired(cookieValueAnn)
            if (!required && ruleComputer.computer(
                    ClassExportRuleKeys.PARAM_REQUIRED,
                    parameterExportContext.element()
                ) == true
            ) {
                required = true
            }

            requestBuilderListener.appendDesc(
                parameterExportContext,
                request, if (required) {
                    "Need cookie:$cookieName ($ultimateComment)"
                } else {
                    val defaultValue = findDefaultValue(cookieValueAnn)
                    if (defaultValue.isNullOrBlank()) {
                        "Cookie:$cookieName ($ultimateComment)"
                    } else {
                        "Cookie:$cookieName=$defaultValue ($ultimateComment)"
                    }
                }
            )

            return
        }


        //form/body/query
        var paramType: String? = parameterExportContext.searchExt("paramType")

        val readParamDefaultValue = readParamDefaultValue(parameterExportContext.element())

        if (readParamDefaultValue.notNullOrBlank()) {
            parameterExportContext.setDefaultVal(readParamDefaultValue!!)
        }

        if (parameterExportContext.required() == null) {
            ruleComputer.computer(ClassExportRuleKeys.PARAM_REQUIRED, parameterExportContext.element())?.let {
                parameterExportContext.setRequired(it)
            }
        }

        if (request.method == HttpMethod.GET) {
            addParamAsQuery(parameterExportContext, request, parameterExportContext.unboxedReturnObject(), ultimateComment)
            return
        }

        if (paramType.isNullOrBlank()) {
            paramType = ruleComputer.computer(
                ClassExportRuleKeys.PARAM_HTTP_TYPE,
                parameterExportContext.element()
            ) ?: "query"
        }

        if (paramType.notNullOrBlank()) {
            when (paramType) {
                "body" -> {
                    requestBuilderListener.setMethodIfMissed(parameterExportContext, request, HttpMethod.POST)
                    setRequestBody(parameterExportContext, request, parameterExportContext.originalReturnObject(), ultimateComment)
                    return
                }

                "form" -> {
                    requestBuilderListener.setMethodIfMissed(parameterExportContext, request, HttpMethod.POST)
                    addParamAsForm(
                        parameterExportContext, request, parameterExportContext.defaultVal()
                            ?: parameterExportContext.unboxedReturnObject(), ultimateComment
                    )
                    return
                }

                "query" -> {
                    addParamAsQuery(
                        parameterExportContext, request, parameterExportContext.defaultVal()
                            ?: parameterExportContext.unboxedReturnObject(), ultimateComment
                    )
                    return
                }

                else -> {
                    logger.warn(
                        "Unknown param type:$paramType." +
                                "Return of rule `param.without.ann.type`" +
                                "should be `body/form/query`"
                    )
                }
            }
        }

        if (parameterExportContext.unboxedReturnObject().hasFile()) {
            addParamAsForm(parameterExportContext, request, parameterExportContext.unboxedReturnObject(), ultimateComment)
            return
        }

        if (parameterExportContext.defaultVal() != null) {
            requestBuilderListener.addParam(
                parameterExportContext,
                request,
                parameterExportContext.name(),
                parameterExportContext.defaultVal(),
                parameterExportContext.required()
                    ?: false,
                ultimateComment
            )
            return
        }

//        if (request.hasForm()) {
//            addParamAsForm(parameter, request, typeObject, ultimateComment)
//            return
//        }

        //else
        addParamAsQuery(parameterExportContext, request, parameterExportContext.unboxedReturnObject(), ultimateComment)
    }

    protected fun getUltimateCommentOfParam(
        paramDesc: String?,
        parameterExportContext: ParameterExportContext
    ): String {
        var ultimateComment = (paramDesc ?: "")
        parameterExportContext.type()?.let { duckType ->
            commentResolver.resolveCommentForType(duckType, parameterExportContext.psi())?.let {
                ultimateComment = "$ultimateComment $it"
            }
        }
        return ultimateComment
    }

    override fun processMethod(methodExportContext: MethodExportContext, request: Request) {
        super.processMethod(methodExportContext, request)

        val classExportContext = methodExportContext.classContext()
        val basePath: URL = classExportContext?.getExt("basePath") ?: URL.nil()
        val ctrlHttpMethod: String? = classExportContext?.getExt("ctrlHttpMethod")
        val requestMapping = findRequestMappingInAnn(methodExportContext.psi())
        methodExportContext.setExt("requestMapping", requestMapping)
        var httpMethod = findHttpMethod(requestMapping)
        if (httpMethod == HttpMethod.NO_METHOD
            && ctrlHttpMethod != null
            && ctrlHttpMethod != HttpMethod.NO_METHOD
        ) {
            httpMethod = ctrlHttpMethod
        }

        requestBuilderListener.setMethodIfMissed(methodExportContext, request, httpMethod)
        val httpPath = basePath.concat(findHttpPath(requestMapping))
        requestBuilderListener.setPath(methodExportContext, request, httpPath)
    }

    override fun processCompleted(methodExportContext: MethodExportContext, request: Request) {
        val requestMapping: Map<String, Any?>? = methodExportContext.getExt("requestMapping")
        requestMapping?.let {
            resolveParamInRequestMapping(methodExportContext, request, it)
            resolveHeaderInRequestMapping(methodExportContext, request, it)
        }

        super.processCompleted(methodExportContext, request)
    }

    //region process spring annotation-------------------------------------------------------------------

    protected fun findHttpPath(requestMappingAnn: Map<String, Any?>?): URL {
        return when (val path = requestMappingAnn?.any("path", "value")) {
            null -> URL.nil()
            is Array<*> -> URL.of(path.mapNotNull { it?.toString() })
            else -> URL.of(path.toString())
        }
    }

    protected open fun resolveParamInRequestMapping(
        methodExportContext: MethodExportContext,
        request: Request, requestMappingAnn: Map<String, Any?>,
    ) {
        val params = requestMappingAnn["params"] ?: return
        if (params is Array<*>) {
            params.asSequence()
                .map { it.tinyString() }
                .filter { it.notNullOrEmpty() }
                .forEach { resolveParamStr(methodExportContext, request, it!!) }
        } else {
            params.tinyString()
                ?.takeIf { it.notNullOrEmpty() }
                ?.let { resolveParamStr(methodExportContext, request, it) }
        }
    }

    protected open fun resolveParamStr(
        methodExportContext: MethodExportContext,
        request: Request, params: String,
    ) {
        when {
            params.startsWith("!") -> {
                requestBuilderListener.appendDesc(
                    methodExportContext,
                    request, "parameter [${params.removeSuffix("!")}] should not be present"
                )
            }

            params.contains("!=") -> {
                val name = params.substringBefore("!=").trim()
                val value = params.substringAfter("!=").trim()
                val param = request.querys?.find { it.name == name }
                if (param == null) {
                    requestBuilderListener.appendDesc(
                        methodExportContext,
                        request, "parameter [$name] " +
                                "should not equal to [$value]"
                    )
                } else {
                    param.desc = param.desc.append("should not equal to [$value]", "\n")
                }
            }

            !params.contains('=') -> {
                val param = request.querys?.find { it.name == params }
                if (param == null) {
                    requestBuilderListener.addParam(
                        methodExportContext,
                        request, params, null, true, null
                    )
                } else {
                    param.required = true
                }
            }

            else -> {
                val name = params.substringBefore("=").trim()
                val value = params.substringAfter("=").trim()
                val param = request.querys?.find { it.name == name }
                if (param == null) {
                    requestBuilderListener.addParam(
                        methodExportContext,
                        request, name, value, true, null
                    )
                } else {
                    param.required = true
                    param.value = value
                }
            }
        }
    }

    protected open fun resolveHeaderInRequestMapping(
        methodExportContext: MethodExportContext,
        request: Request, requestMappingAnn: Map<String, Any?>,
    ) {
        val headers = requestMappingAnn["headers"] ?: return
        if (headers is Array<*>) {
            headers.asSequence()
                .map { it.tinyString() }
                .filter { it.notNullOrEmpty() }
                .forEach { resolveHeaderStr(methodExportContext, request, it!!) }
        } else {
            headers.tinyString()
                ?.takeIf { it.isNotEmpty() }
                ?.let { resolveHeaderStr(methodExportContext, request, it) }
        }
    }

    protected open fun resolveHeaderStr(methodExportContext: MethodExportContext, request: Request, headers: String) {
        when {
            headers.startsWith("!") -> {
                requestBuilderListener.appendDesc(
                    methodExportContext,
                    request,
                    "header [${headers.removeSuffix("!")}] should not be present"
                )
            }

            headers.contains("!=") -> {
                val name = headers.substringBefore("!=").trim()
                val value = headers.substringAfter("!=").trim()
                val header = request.querys?.find { it.name == name }
                if (header == null) {
                    requestBuilderListener.appendDesc(
                        methodExportContext,
                        request, "header [$name] " +
                                "should not equal to [$value]"
                    )
                } else {
                    header.desc = header.desc.append("should not equal to [$value]", "\n")
                }
            }

            !headers.contains('=') -> {
                val header = request.querys?.find { it.name == headers }
                if (header == null) {
                    requestBuilderListener.addHeader(methodExportContext, request, headers, "")
                } else {
                    header.required = true
                }
            }

            else -> {
                val name = headers.substringBefore("=").trim()
                val value = headers.substringAfter("=").trim()
                val header = request.querys?.find { it.name == name }
                if (header == null) {
                    requestBuilderListener.addHeader(methodExportContext, request, name, value)
                } else {
                    header.required = true
                    header.value = value
                }
            }
        }
    }

    protected fun findHttpMethod(requestMappingAnn: Map<String, Any?>?): String {
        if (requestMappingAnn == null) {
            return HttpMethod.NO_METHOD
        }
        var method = requestMappingAnn["method"].tinyString() ?: return HttpMethod.NO_METHOD
        if (method.contains(",")) {
            method = method.substringBefore(",")
        }
        return when {
            method.isBlank() -> {
                HttpMethod.NO_METHOD
            }

            method.startsWith("RequestMethod.") -> {
                method.removePrefix("RequestMethod.")
            }

            method.contains("RequestMethod.") -> {
                method.substringAfterLast("RequestMethod.")
            }

            else -> method
        }
    }

    protected fun findRequestMappingInAnn(ele: PsiElement): Map<String, Any?>? {
        return micronautRequestMappingResolver.resolveRequestMapping(ele)
    }

    protected fun isRequestBody(parameter: PsiParameter): Boolean {
        return annotationHelper.hasAnn(parameter, MicronautClassName.BODY_ANNOTATION)
    }


    protected fun findRequestHeader(parameter: PsiParameter): Map<String, Any?>? {
        return annotationHelper.findAnnMap(parameter, MicronautClassName.HEADER_ANNOTATION)
    }

    protected fun findPathVariable(parameter: PsiParameter): Map<String, Any?>? {
        return annotationHelper.findAnnMap(parameter, MicronautClassName.PATH_VARIABLE_ANNOTATION)
    }

    protected fun findCookieValue(parameter: PsiParameter): Map<String, Any?>? {
        return annotationHelper.findAnnMap(parameter, MicronautClassName.COOKIE_VALUE_ANNOTATION)
    }

    protected fun findRequired(annMap: Map<String, Any?>): Boolean {
        val required = annMap["required"]?.toString()
        return when {
            required?.contains("false") == true -> false
            else -> true
        }
    }

    protected fun findDefaultValue(annMap: Map<String, Any?>): String? {
        val defaultValue = annMap["defaultValue"]?.toString()
        if (defaultValue == null
            || defaultValue == MicronautClassName.HEADER_DEFAULT_NONE
            || defaultValue == MicronautClassName.HEADER_ANNOTATION
        ) {
            return null
        }
        return defaultValue
    }

    //endregion process spring annotation-------------------------------------------------------------------

}

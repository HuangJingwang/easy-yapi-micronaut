package com.itangcent.idea.plugin.api.export.micronaut

import com.google.inject.Singleton
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.itangcent.common.constant.HttpMethod
import com.itangcent.common.kit.KVUtils
import com.itangcent.common.model.Request
import com.itangcent.common.model.URL
import com.itangcent.common.utils.cache
import com.itangcent.idea.plugin.api.export.core.*

@Singleton
class ActuatorEndpointExporter : MicronautRequestClassExporter() {

    override fun processClass(cls: PsiClass, classExportContext: ClassExportContext) {
        val controllerAnn = annotationHelper.findAnnMap(cls, "io.micronaut.http.annotation.Controller")
        if (controllerAnn != null) {
            val basePathStr = controllerAnn["value"] as? String ?: ""
            val basePath = URL.of(basePathStr)
            classExportContext.setExt("basePath", basePath)
        }
    }

    override fun hasApi(psiClass: PsiClass): Boolean {
        return annotationHelper.hasAnn(psiClass, "io.micronaut.http.annotation.Controller")
    }

    override fun isApi(psiMethod: PsiMethod): Boolean {
        return getOperation(psiMethod) != null
    }

    override fun processMethod(methodExportContext: MethodExportContext, request: Request) {
        val operation = getOperation(methodExportContext.psi())
        if (operation != null) {
            val (annMap, annName) = operation
            when (annName) {
                "io.micronaut.http.annotation.Get" -> {
                    requestBuilderListener.setMethodIfMissed(
                        methodExportContext, request, HttpMethod.GET
                    )
                }
                "io.micronaut.http.annotation.Post" -> {
                    requestBuilderListener.setMethodIfMissed(
                        methodExportContext, request, HttpMethod.POST
                    )
                    methodExportContext.setExt("hasWriteOrDeleteOperation", true)
                }
                "io.micronaut.http.annotation.Delete" -> {
                    requestBuilderListener.setMethodIfMissed(
                        methodExportContext, request, HttpMethod.DELETE
                    )
                    methodExportContext.setExt("hasWriteOrDeleteOperation", true)
                }
            }
            val pathValue = annMap["value"] as? String
            if (pathValue != null && pathValue.isNotBlank()) {
                requestBuilderListener.setPath(methodExportContext, request, URL.of(pathValue))
            }
        }
    }

    override fun processMethodParameter(
        request: Request,
        parameterExportContext: ParameterExportContext,
        paramDesc: String?
    ) {
        val psiParameter = parameterExportContext.psi()
        val methodExportContext = parameterExportContext.methodContext()!!
        val paramName = parameterExportContext.name()

        if (annotationHelper.hasAnn(psiParameter, "io.micronaut.http.annotation.PathVariable")) {
            request.path?.concat(URL.of("{$paramName}"))?.let {
                requestBuilderListener.setPath(parameterExportContext, request, it)
            }
            requestBuilderListener.addPathParam(
                parameterExportContext, request,
                paramName,
                getUltimateCommentOfParam(paramDesc, parameterExportContext)
            )
            return
        }

        if (methodExportContext.getExt<Boolean>("hasWriteOrDeleteOperation") == true) {
            val body = methodExportContext.cache("body") { hashMapOf<String, Any?>() }!!
            body[paramName] = psiClassHelper!!.getTypeObject(
                parameterExportContext.type(), psiParameter
            )
            KVUtils.addKeyComment(
                body, paramName,
                getUltimateCommentOfParam(paramDesc, parameterExportContext)
            )
            return
        }

        super.processMethodParameter(request, parameterExportContext, paramDesc)
    }

    override fun processCompleted(methodExportContext: MethodExportContext, request: Request) {
        super.processCompleted(methodExportContext, request)
        methodExportContext.getExt<Any?>("body")?.let {
            setRequestBody(methodExportContext, request, it, "")
        }
    }

    private fun getOperation(psiMethod: PsiMethod): Pair<Map<String, Any?>, String>? {
        val annotations = listOf(
            "io.micronaut.http.annotation.Get",
            "io.micronaut.http.annotation.Post",
            "io.micronaut.http.annotation.Put",
            "io.micronaut.http.annotation.Delete",
            "io.micronaut.http.annotation.Patch"
        )
        return annotations.asSequence()
            .mapNotNull { ann -> annotationHelper.findAnnMap(psiMethod, ann)?.to(ann) }
            .firstOrNull()
    }
}

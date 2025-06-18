package com.itangcent.idea.plugin.api.export.micronaut

import com.google.inject.Inject
import com.google.inject.Singleton
import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.itangcent.common.model.Request
import com.itangcent.idea.condition.annotation.ConditionOnClass
import com.itangcent.idea.plugin.api.ClassApiExporterHelper
import com.itangcent.idea.plugin.api.export.condition.ConditionOnDoc
import com.itangcent.idea.plugin.api.export.condition.ConditionOnSimple
import com.itangcent.idea.plugin.api.export.core.*
import com.itangcent.intellij.config.rule.RuleComputer
import com.itangcent.intellij.context.ActionContext
import com.itangcent.intellij.jvm.AnnotationHelper
import com.itangcent.idea.psi.PsiMethodResource
import com.itangcent.intellij.jvm.asPsiClass
import com.itangcent.intellij.logger.Logger
import kotlin.reflect.KClass

/**
 * A simplified version of Micronaut request class exporter that focuses on basic request mapping information.
 * This exporter provides a lightweight alternative to a full MicronautRequestClassExporter,
 * processing only essential information from Micronaut controllers.
 */
@Singleton
@ConditionOnSimple(value = false)
@ConditionOnClass("io.micronaut.http.annotation.Controller")
open class SimpleMicronautRequestClassExporter : ClassExporter {

    @Inject
    protected lateinit var annotationHelper: AnnotationHelper

    @Inject
    protected lateinit var micronautRequestMappingResolver: MicronautRequestMappingResolver

    @Inject
    protected lateinit var classApiExporterHelper: ClassApiExporterHelper

    @Inject
    protected lateinit var micronautControllerAnnotationResolver: MicronautControllerAnnotationResolver

    @Inject
    protected lateinit var ruleComputer: RuleComputer

    @Inject
    protected lateinit var actionContext: ActionContext

    @Inject
    protected lateinit var apiHelper: ApiHelper

    @Inject
    private lateinit var logger: Logger

    override fun support(docType: KClass<*>): Boolean {
        return docType == Request::class
    }

    override fun export(cls: Any, docHandle: DocHandle): Boolean {
        logger.info("search Micronaut api from: " + cls.toString())

        if (cls !is PsiClass) return false

        val clsQualifiedName = actionContext.callInReadUI { cls.qualifiedName }
        try {
            when {
                !isCtrl(cls) -> {
                    return false
                }
                shouldIgnore(cls) -> {
                    logger.debug("ignore class: $clsQualifiedName")

                    // skip this controller entirely
                    return true
                }
                else -> {
                    logger.info("search Micronaut api from: $clsQualifiedName")
                    classApiExporterHelper.foreachPsiMethod(cls) { method ->
                        exportMethodApi(cls, method, docHandle)
                    }
                }
            }
        } catch (e: Exception) {
            // swallow errors to avoid breaking export pipeline
        }
        return true
    }

    protected open fun isCtrl(psiClass: PsiClass): Boolean {
        return micronautControllerAnnotationResolver.hasControllerAnnotation(psiClass)
                || (ruleComputer.computer(ClassExportRuleKeys.IS_MICRONAUT_CTRL, psiClass) ?: false)
    }

    private fun shouldIgnore(ele: PsiElement): Boolean {
        return ruleComputer.computer(ClassExportRuleKeys.IGNORE, ele) ?: false
    }

    private fun exportMethodApi(
        psiClass: PsiClass,
        method: PsiMethod,
        docHandle: DocHandle
    ) {
        actionContext.checkStatus()
        val mapping = findRequestMappingInAnn(method) ?: return

        val request = Request()
        request.resource = PsiMethodResource(method, psiClass)
        request.name = apiHelper.nameOfApi(method)
        logger.info("exporting method: ${request.name}")
        docHandle(request)
    }

    protected fun findRequestMappingInAnn(ele: PsiElement): Map<String, Any?>? {
        return micronautRequestMappingResolver.resolveRequestMapping(ele)
    }
}
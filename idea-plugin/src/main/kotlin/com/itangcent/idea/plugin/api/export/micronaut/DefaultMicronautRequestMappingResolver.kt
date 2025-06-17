package com.itangcent.idea.plugin.api.export.micronaut

import com.google.inject.Inject
import com.google.inject.Singleton
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.itangcent.intellij.context.ActionContext
import com.itangcent.spi.SpiCompositeLoader

/**
 * 该类提供了 MicronautRequestMappingResolver 的默认实现，
 * 使用组合模式将请求映射的解析委托给多个具体的解析器。
 */
@Singleton
class DefaultMicronautRequestMappingResolver : MicronautRequestMappingResolver {

    @Inject
    private lateinit var actionContext: ActionContext

    private val delegate: MicronautRequestMappingResolver by lazy {
        SpiCompositeLoader.loadComposite()
    }

    /**
     * @param psiElement 被注解的元素（方法或类）
     * @return 注解属性的键值对
     */
    override fun resolveRequestMapping(psiElement: PsiElement): Map<String, Any?>? {
        return if (psiElement is PsiClass) {
            resolveRequestMappingFromClass(psiElement)
        } else {
            resolveRequestMappingFromElement(psiElement)
        }
    }

    private fun resolveRequestMappingFromClass(psiClass: PsiClass): Map<String, Any?>? {
        resolveRequestMappingFromElement(psiClass)?.let { return it }

        actionContext.callInReadUI { psiClass.interfaces }?.forEach { inter ->
            resolveRequestMappingFromElement(inter)?.let { return it }
        }

        var superCls = actionContext.callInReadUI { psiClass.superClass }
        while (superCls != null) {
            resolveRequestMappingFromElement(superCls)?.let { return it }
            superCls = actionContext.callInReadUI { superCls?.superClass }
        }
        return null
    }

    private fun resolveRequestMappingFromElement(ele: PsiElement): Map<String, Any?>? {
        return delegate.resolveRequestMapping(ele)
    }
}

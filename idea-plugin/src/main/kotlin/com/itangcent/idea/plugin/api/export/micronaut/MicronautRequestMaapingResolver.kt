package com.itangcent.idea.plugin.api.export.micronaut

import com.google.inject.ImplementedBy
import com.intellij.psi.PsiElement

/**
 * 此接口定义了解析 Micronaut 请求映射注解的契约。
 * 它用于从类和方法上的 Micronaut HTTP 注解中提取请求映射信息，
 * 包括标准 Micronaut 控制器注解（如 @Controller, @Get, @Post 等）。
 */
@ImplementedBy(DefaultMicronautRequestMappingResolver::class)
interface MicronautRequestMappingResolver {

    /**
     * 从给定的 PSI 元素（方法或类）中解析出 Micronaut 请求映射的注解信息。
     *
     * @param psiElement 被注解的元素（通常是 PsiMethod 或 PsiClass）
     * @return 注解的属性键值对，若不存在则返回 null
     */
    fun resolveRequestMapping(psiElement: PsiElement): Map<String, Any?>?
}

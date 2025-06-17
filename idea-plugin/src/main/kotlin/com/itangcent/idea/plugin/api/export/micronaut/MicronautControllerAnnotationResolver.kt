package com.itangcent.idea.plugin.api.export.micronaut
import com.google.inject.ProvidedBy
import com.google.inject.Singleton
import com.intellij.psi.PsiClass
import com.itangcent.spi.SpiCompositeBeanProvider

/**
 * 该接口定义了一个用于判断 PsiClass 是否为 Micronaut 控制器的约定。
 * 它由多个实现提供不同的判断策略，例如判断是否包含 @Controller 或 @Get 等注解。
 */
@ProvidedBy(MicronautControllerAnnotationResolverCompositeProvider::class)
interface MicronautControllerAnnotationResolver {
    fun hasControllerAnnotation(psiClass: PsiClass): Boolean
}

/**
 * MicronautControllerAnnotationResolver 的组合实现提供者，
 * 用于加载所有通过 SPI 注册的实现。
 */
@Singleton
class MicronautControllerAnnotationResolverCompositeProvider :
    SpiCompositeBeanProvider<MicronautControllerAnnotationResolver>()
package com.itangcent.idea.plugin.api.export.micronaut


import com.google.inject.Inject
import com.google.inject.Singleton
import com.intellij.psi.PsiClass
import com.itangcent.intellij.jvm.AnnotationHelper
import java.util.logging.Logger

/**
 * 该类提供 MicronautControllerAnnotationResolver 的标准实现，
 * 用于判断一个 PsiClass 是否直接标注了 Micronaut 的 Controller 注解。
 */
@Singleton
class StandardMicronautControllerAnnotationResolver : MicronautControllerAnnotationResolver {

    @Inject
    private lateinit var annotationHelper: AnnotationHelper
    @Inject
    private lateinit var logger: Logger;

    override fun hasControllerAnnotation(psiClass: PsiClass): Boolean {
        logger.info("Checking if class ${psiClass.name} has Micronaut controller annotation...")
        // 检查是否直接拥有 Micronaut 控制器注解
        return MicronautClassName.MICRONAUT_CONTROLLER_ANNOTATION.any {
            annotationHelper.hasAnn(psiClass, it.toString())
        }
    }
}

package com.itangcent.idea.plugin.api.export.micronaut

import com.google.inject.Inject
import com.intellij.psi.PsiClass
import com.itangcent.intellij.context.ActionContext
import com.itangcent.intellij.jvm.PsiResolver
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

/**
 * 该类实现了 MicronautControllerAnnotationResolver，
 * 支持识别被 @Controller 或元注解为 @Controller 的注解所标注的类。
 * 例如：
 * ```java
 * @Target({ElementType.TYPE})
 * @Retention(RetentionPolicy.RUNTIME)
 * @Controller
 * public @interface CustomController {}
 * ```
 */
class CustomMicronautControllerAnnotationResolver : MicronautControllerAnnotationResolver {

    @Inject
    private lateinit var psiResolver: PsiResolver

    @Inject
    private lateinit var standardMicronautControllerAnnotationResolver: StandardMicronautControllerAnnotationResolver

    @Inject
    private lateinit var actionContext: ActionContext
    @Inject
    private lateinit var logger: Logger

    /**
     * 缓存注解名是否是 Micronaut 控制器注解的判断结果。
     */
    private val controllerAnnotationLookup = ConcurrentHashMap<String, Boolean>()

    override fun hasControllerAnnotation(psiClass: PsiClass): Boolean {
        logger.info("Checking if class ${psiClass.name} has Micronaut controller annotation...")

        return actionContext.callInReadUI {
            psiClass.annotations.any { annotation ->
                val annotationQualifiedName = annotation.qualifiedName ?: return@any false
                controllerAnnotationLookup.computeIfAbsent(annotationQualifiedName) {
                    val annotationClass = annotation.resolveAnnotationType()
                        ?: psiResolver.resolveClass(annotationQualifiedName, psiClass)
                        ?: return@computeIfAbsent false
                    return@computeIfAbsent standardMicronautControllerAnnotationResolver.hasControllerAnnotation(
                        annotationClass
                    )
                }
            }
        } ?: false
    }
}
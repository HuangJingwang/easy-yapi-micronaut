package com.itangcent.idea.plugin.api.export.micronaut

import com.google.inject.Inject
import com.google.inject.Singleton
import com.itangcent.common.constant.HttpMethod
import com.itangcent.common.utils.firstOrNull
import com.itangcent.intellij.jvm.AnnotationHelper
import com.itangcent.order.Order
import com.itangcent.order.Ordered
import com.intellij.psi.PsiElement

/**
 * Processes Micronaut's standard controller/mapping annotations:
 *  - @Controller      (class‐level)
 *  - @Get, @Post, @Put, @Delete, @Patch  (method‐level)
 *
 * Runs at highest precedence to ensure these built‑ins are handled
 * before any custom or meta‑annotation resolvers.
 */
@Singleton
@Order(Ordered.HIGHEST_PRECEDENCE)
class StandardMicronautRequestMappingResolver : MicronautRequestMappingResolver {

    @Inject
    private lateinit var annotationHelper: AnnotationHelper

    companion object {
        private const val CONTROLLER = "io.micronaut.http.annotation.Controller"
        private const val GET       = "io.micronaut.http.annotation.Get"
        private const val POST      = "io.micronaut.http.annotation.Post"
        private const val PUT       = "io.micronaut.http.annotation.Put"
        private const val DELETE    = "io.micronaut.http.annotation.Delete"
        private const val PATCH     = "io.micronaut.http.annotation.Patch"

        private val ALL = listOf(CONTROLLER, GET, POST, PUT, DELETE, PATCH)
    }

    /**
     * @param psiElement either a PsiClass or PsiMethod
     * @return a map of annotation attributes, plus a "method" key for HTTP verbs
     */
    override fun resolveRequestMapping(psiElement: PsiElement): Map<String, Any?>? {
        // find the first matching mapping annotation on this element
        val found = ALL
            .asSequence()
            .mapNotNull { ann -> annotationHelper.findAnnMap(psiElement, ann)?.to(ann) }
            .firstOrNull() ?: return null

        val (attributes, annName) = found

        return when (annName) {
            CONTROLLER -> {
                // class‐level only carries path info
                attributes
            }
            GET, POST, PUT, DELETE, PATCH -> {
                // method‐level: carry path and also set HTTP method
                val m = when (annName) {
                    GET    -> HttpMethod.GET
                    POST   -> HttpMethod.POST
                    PUT    -> HttpMethod.PUT
                    DELETE -> HttpMethod.DELETE
                    PATCH  -> HttpMethod.PATCH
                    else   -> HttpMethod.NO_METHOD
                }
                HashMap(attributes).apply { put("method", m) }
            }
            else -> null
        }
    }
}
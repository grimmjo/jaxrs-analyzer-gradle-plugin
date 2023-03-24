package io.github.grimmjo.jaxrs_analyzer

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property

/**
 * JaxRsAnalyzerExtension
 * @author grimmjo
 */
open class JaxRsAnalyzerExtension(objects: ObjectFactory) {

    var backend = objects.listProperty<String>().convention(listOf("swagger"))

    var outputDirectory = objects.property<String>()

    var outputFileBaseName = objects.property<String>().convention("rest-resources-")

    var schemes = objects.listProperty<String>().convention(listOf("http"))

    var domain = objects.property<String>()

    var renderTags = objects.property<Boolean>().convention(false)

    var tagPathOffset = objects.property<Number>().convention(0)
}

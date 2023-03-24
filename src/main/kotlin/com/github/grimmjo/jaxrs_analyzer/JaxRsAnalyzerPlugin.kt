package com.github.grimmjo.jaxrs_analyzer

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * JaxRsAnalyzerPlugin
 * @author grimmjo
 */
class JaxRsAnalyzerPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val jaxRsAnalyzerExtension = project.extensions.create("jaxRsAnalyzer", JaxRsAnalyzerExtension::class.java)

        project.tasks.register("analyze", JaxRsAnalyserTask::class.java) {
            dependsOn += project.tasks.named("classes")
            mainSourceSet.set(
                project.extensions.getByType(SourceSetContainer::class.java).getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            )
            backend.set(jaxRsAnalyzerExtension.backend)
            outputFileBaseName.set(jaxRsAnalyzerExtension.outputFileBaseName)
            schemes.set(jaxRsAnalyzerExtension.schemes)
            domain.set(jaxRsAnalyzerExtension.domain)
            renderTags.set(jaxRsAnalyzerExtension.renderTags)
            tagPathOffset.set(jaxRsAnalyzerExtension.tagPathOffset)
            outputDirectory.set(project.buildDir.resolve(jaxRsAnalyzerExtension.outputDirectory.get()))
        }
    }
}

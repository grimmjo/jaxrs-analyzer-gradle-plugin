package com.github.grimmjo.jaxrs_analyzer.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 * @author grimmjo
 */
class JaxRsAnalyzerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.with {
            extensions.create('jaxRsAnalyzer', JaxRsAnalyzerExtension)
            tasks.create(name: 'analyze', type: JaxRsAnalyzerTask, dependsOn: tasks.compileJava) {
                def buildDirectory = project.buildDir.toPath()
                if (project.jaxRsAnalyzer.outputDirectory == null) {
                    it.outputDirectory = buildDirectory.resolve("jaxrs-analyzer")
                } else {
                    it.outputDirectory = buildDirectory.resolve(project.jaxRsAnalyzer.outputDirectory)
                }
                it.inputDirectory = file(project.sourceSets.main.java.destinationDirectory)
            }
            dependencies {
                delegate.compile("com.sebastian-daschner:jaxrs-analyzer:0.17")
            }
        }
    }
}

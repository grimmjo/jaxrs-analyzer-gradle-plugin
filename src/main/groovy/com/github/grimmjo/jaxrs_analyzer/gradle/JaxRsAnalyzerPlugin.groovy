package com.github.finrod2002.jaxrs_analyzer.gradle

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
                if (project.jaxRsAnalyzer.outputDirectory == null) {
                    it.outputDirectory = new File(project.buildDir, "jaxrs-analyzer")
                } else {
                    it.outputDirectory = new File(project.buildDir, project.jaxRsAnalyzer.outputDirectory)
                }
                it.inputDirectory = project.sourceSets.main.java.outputDir
            }
            dependencies {
                delegate.compile("com.sebastian-daschner:jaxrs-analyzer:0.16")
            }
        }
    }
}

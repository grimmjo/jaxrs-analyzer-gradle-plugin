package com.github.grimmjo.jaxrs_analyser.gradle

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

            tasks.create(name: 'analyze', type: JaxRsAnalyzerTask, dependsOn: tasks.compileJava)
        }
    }
}

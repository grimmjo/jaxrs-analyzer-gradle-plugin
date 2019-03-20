package com.github.grimmjo.jaxrs_analyzer.gradle

import com.sebastian_daschner.jaxrs_analyzer.JAXRSAnalyzer.Analysis
import com.sebastian_daschner.jaxrs_analyzer.JAXRSAnalyzer
import com.sebastian_daschner.jaxrs_analyzer.backend.Backend
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerOptions
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.model.Path

import java.util.stream.Collectors
import java.util.stream.Stream

/**
 *
 * @author grimmjo
 * @author finrod2002
 */
class JaxRsAnalyzerTask extends DefaultTask {

    @InputDirectory
    File inputDirectory

    @OutputDirectory
    File outputDirectory

    @TaskAction
    void analyze() {

        Analysis analysis = new Analysis()
        project.configurations.compileClasspath.each {
            analysis.addClassPath(it.toPath())
        }
        project.configurations.runtimeClasspath.each {
            analysis.addClassPath(it.toPath())
        }
        project.configurations.compile.each {
            analysis.addClassPath(it.toPath())
        }
        project.configurations.runtime.each {
            analysis.addClassPath(it.toPath())
        }
        analysis.addProjectClassPath(inputDirectory.toPath())
        project.sourceSets.main.java.getSrcDirs().each {
            analysis.addProjectSourcePath(it.toPath())
        }

        final Map<String, String> config = new HashMap<>()
        config.put(SwaggerOptions.SWAGGER_SCHEMES, Stream.of(project.jaxRsAnalyzer.schemes).collect(Collectors.joining(',')))
        config.put(SwaggerOptions.DOMAIN, project.jaxRsAnalyzer.domain)
        config.put(SwaggerOptions.RENDER_SWAGGER_TAGS, project.jaxRsAnalyzer.renderTags.toString())
        config.put(SwaggerOptions.SWAGGER_TAGS_PATH_OFFSET, project.jaxRsAnalyzer.tagPathOffset.toString())

        project.jaxRsAnalyzer.backend.each {
            final Backend backend = JAXRSAnalyzer.constructBackend(it)
            backend.configure(config)
            String outputFileBaseName = project.jaxRsAnalyzer.outputFileBaseName == null ? 'rest-resources' : project.jaxRsAnalyzer.outputFileBaseName
            File outputFile = null
            switch (it) {
                case 'swagger':
                    outputFile = new File(outputDirectory, outputFileBaseName + '-swagger.json')
                    break
                case 'plaintext':
                    outputFile = new File(outputDirectory, outputFileBaseName + '-plaintext.txt')
                    break
                case 'asciidoc':
                    outputFile = new File(outputDirectory, outputFileBaseName + '-asciidoc.adoc')
                    break
            }

            if (outputFile != null) {
                if (outputFile.exists()) {
                    outputFile.delete()
                }

                analysis.setProjectName(project.getName())
                analysis.setProjectVersion(project.getVersion())
                analysis.setBackend(backend)
                analysis.setOutputLocation(outputFile.toPath())

                new JAXRSAnalyzer(analysis).analyze()
            }
        }
    }

}

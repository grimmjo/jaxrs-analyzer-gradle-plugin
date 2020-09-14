package com.github.grimmjo.jaxrs_analyzer.gradle

import com.sebastian_daschner.jaxrs_analyzer.JAXRSAnalyzer
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerOptions
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 *
 * @author grimmjo
 * @author finrod2002
 */
@CacheableTask
class JaxRsAnalyzerTask extends DefaultTask {

    @InputDirectory
    @PathSensitive(PathSensitivity.ABSOLUTE)
    File inputDirectory

    @OutputDirectory
    Path outputDirectory

    @TaskAction
    void analyze() {
        JAXRSAnalyzer.Analysis analysis = new JAXRSAnalyzer.Analysis()
        analysis.projectName = project.name
        analysis.projectVersion = project.version
        project.configurations.compileClasspath.each {
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


        project.jaxRsAnalyzer.backend.each {
            final Map<String, String> config = new HashMap<>()
            analysis.backend = JAXRSAnalyzer.constructBackend(it)
            String outputFileBaseName = project.jaxRsAnalyzer.outputFileBaseName ?: 'rest-resources-'
            Path outputFile = null
            switch (it) {
                case 'swagger':
                    config.put(SwaggerOptions.SWAGGER_SCHEMES, Stream.of(project.jaxRsAnalyzer.schemes).collect(Collectors.joining(',')))
                    config.put(SwaggerOptions.DOMAIN, project.jaxRsAnalyzer.domain)
                    config.put(SwaggerOptions.RENDER_SWAGGER_TAGS, project.jaxRsAnalyzer.renderTags.toString())
                    config.put(SwaggerOptions.SWAGGER_TAGS_PATH_OFFSET, project.jaxRsAnalyzer.tagPathOffset.toString())
                    outputFile = outputDirectory.resolve(outputFileBaseName + 'swagger.json')
                    break
                case 'plaintext':
                    outputFile = outputDirectory.resolve(outputFileBaseName + 'plaintext.txt')
                    break
                case 'asciidoc':
                    outputFile = outputDirectory.resolve(outputFileBaseName + 'asciidoc.adoc')
                    break
            }
            analysis.configureBackend(config)
            analysis.outputLocation = outputFile

            if (outputFile != null) {
                Files.deleteIfExists(outputFile)
            }
            new JAXRSAnalyzer(analysis).analyze()
        }
    }

}

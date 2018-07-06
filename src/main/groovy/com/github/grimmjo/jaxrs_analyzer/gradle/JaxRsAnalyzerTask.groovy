package com.github.grimmjo.jaxrs_analyzer.gradle

import com.sebastian_daschner.jaxrs_analyzer.JAXRSAnalyzer
import com.sebastian_daschner.jaxrs_analyzer.backend.Backend
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerOptions
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.model.Path

import java.util.stream.Collectors
import java.util.stream.Stream

/**
 *
 * @author grimmjo
 */
class JaxRsAnalyzerTask extends DefaultTask {

    @TaskAction
    void analyze() {
        File outputDirectory
        if (outputDirectory == null) {
            outputDirectory = new File(project.buildDir, "jaxrs-analyzer")
        } else {
            outputDirectory = new File(project.buildDir, project.jaxRsAnalyzer.outputDirectory)
        }

        Set<Path> dependencies = new HashSet<>()
        project.configurations.compile.each {
            dependencies.add(it.toPath())
        }
        project.configurations.runtime.each {
            dependencies.add(it.toPath())
        }
        Set<Path> outputDir = new HashSet<>()
        outputDir.add(project.sourceSets.main.java.outputDir.toPath())

        Set<Path> sourceDirs = new HashSet<>()
        project.sourceSets.main.java.getSrcDirs().each {
            sourceDirs.add(it.toPath())
        }

        final Map<String, String> config = new HashMap<>()
        config.put(SwaggerOptions.SWAGGER_SCHEMES, Stream.of(project.jaxRsAnalyzer.schemes).collect(Collectors.joining(',')))
        config.put(SwaggerOptions.DOMAIN, project.jaxRsAnalyzer.domain)
        config.put(SwaggerOptions.RENDER_SWAGGER_TAGS, project.jaxRsAnalyzer.renderTags.toString())
        config.put(SwaggerOptions.SWAGGER_TAGS_PATH_OFFSET, project.jaxRsAnalyzer.tagPathOffset.toString())

        final Backend backend = JAXRSAnalyzer.constructBackend(project.jaxRsAnalyzer.backend)
        backend.configure(config)
        outputDirectory.mkdirs()
        File outputFile
        switch (project.jaxRsAnalyzer.backend) {
            case 'swagger':
                outputFile = new File(outputDirectory, 'swagger.json')
                break
            case 'plaintext':
                outputFile = new File(outputDirectory, 'rest-resources.txt')
                break
            case 'asciidoc':
                outputFile = new File(outputDirectory, 'rest-resources.adoc')
                break
        }

        if (outputFile.exists()) {
            outputFile.delete()
        }

        new JAXRSAnalyzer(outputDir, sourceDirs, dependencies, project.getName(), project.getVersion(), backend, outputFile.toPath()).analyze()
    }
}

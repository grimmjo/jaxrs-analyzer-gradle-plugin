package com.github.finrod2002.jaxrs_analyzer.gradle

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

        Set<Path> classPaths = new HashSet<>()
        project.configurations.implementation.each {
            classPaths.add(it.toPath())
        }
        project.configurations.compile.each {
            classPaths.add(it.toPath())
        }
        project.configurations.runtime.each {
            classPaths.add(it.toPath())
        }
        Set<Path> projectClasspaths = new HashSet<>()
        projectClasspaths.add(inputDirectory.toPath())

        Set<Path> projectSourcePaths = new HashSet<>()
        project.sourceSets.main.java.getSrcDirs().each {
            projectSourcePaths.add(it.toPath())
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
                new JAXRSAnalyzer(projectClasspaths, projectSourcePaths, classPaths, project.getName(), project.getVersion(), backend, outputFile.toPath()).analyze()
            }
        }
    }

}

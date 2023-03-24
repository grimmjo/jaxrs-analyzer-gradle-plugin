package io.github.grimmjo.jaxrs_analyzer

import com.sebastian_daschner.jaxrs_analyzer.JAXRSAnalyzer
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerOptions
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import java.nio.file.Path
import kotlin.io.path.deleteIfExists


/**
 * JaxRsAnalyserTask
 * @author grimmjo
 */
@CacheableTask
abstract class JaxRsAnalyserTask : DefaultTask() {

    @get:Input
    abstract val mainSourceSet: Property<SourceSet>

    @get:Input
    abstract val backend: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val outputFileBaseName: Property<String>

    @get:Input
    abstract val schemes: ListProperty<String>

    @get:Input
    abstract val domain: Property<String>

    @get:Input
    abstract val renderTags: Property<Boolean>

    @get:Input
    abstract val tagPathOffset: Property<Number>

    @TaskAction
    fun analyse() {
        val analysis = JAXRSAnalyzer.Analysis()
        analysis.setProjectName(project.name)
        analysis.setProjectVersion(project.version.toString())

        mainSourceSet.get().compileClasspath.forEach { analysis.addClassPath(it.toPath()) }
        mainSourceSet.get().runtimeClasspath.forEach { analysis.addClassPath(it.toPath()) }
        mainSourceSet.get().allSource.sourceDirectories.forEach { analysis.addProjectSourcePath(it.toPath()) }
        mainSourceSet.get().output.classesDirs.forEach { analysis.addProjectClassPath(it.toPath()) }


        backend.get().forEach {
            analysis.backend = JAXRSAnalyzer.constructBackend(it)

            var config: Map<String, String> = mapOf()
            val outputFile: Path

            when (it) {
                "swagger" -> {
                    config = mapOf(
                        SwaggerOptions.SWAGGER_SCHEMES to schemes.get()
                            .joinToString(separator = ","),
                        SwaggerOptions.DOMAIN to domain.get(),
                        SwaggerOptions.RENDER_SWAGGER_TAGS to renderTags.get().toString(),
                        SwaggerOptions.SWAGGER_TAGS_PATH_OFFSET to tagPathOffset.get().toString()
                    )
                    outputFile =
                        outputDirectory.get().asFile.toPath().resolve(outputFileBaseName.get() + "swagger.json")
                }

                "plaintext" -> {
                    outputFile =
                        outputDirectory.get().asFile.toPath().resolve(outputFileBaseName.get() + "plaintext.txt")
                }

                "asciidoc" -> {
                    outputFile =
                        outputDirectory.get().asFile.toPath().resolve(outputFileBaseName.get() + "asciidoc.adoc")
                }

                "markdown" -> {
                    outputFile =
                        outputDirectory.get().asFile.toPath().resolve(outputFileBaseName.get() + "markdown.md")
                }

                else -> {
                    throw IllegalArgumentException("Invalid backend type. Only 'swagger', 'plaintext' and  'asciidoc' are allowed.")
                }
            }

            analysis.configureBackend(config)
            analysis.setOutputLocation(outputFile)

            outputFile.deleteIfExists()

            JAXRSAnalyzer(analysis).analyze()
        }
    }
}

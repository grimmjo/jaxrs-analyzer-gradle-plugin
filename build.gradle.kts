plugins {
    id("com.gradle.plugin-publish") version "1.1.0"
    `java-gradle-plugin`
    `kotlin-dsl`

}

repositories {
    mavenCentral()
}

group = "io.github.grimmjo"
version = "0.4"

dependencies {
    implementation(gradleApi())
    implementation("com.sebastian-daschner:jaxrs-analyzer:0.17")
}

gradlePlugin {
    website.set("https://github.com/grimmjo/jaxrs-analyzer-gradle-plugin")
    vcsUrl.set("https://github.com/grimmjo/jaxrs-analyzer-gradle-plugin")
    plugins {
        create("analyzerPlugin") {
            id = "io.github.grimmjo.jaxrs-analyzer"
            implementationClass = "io.github.grimmjo.jaxrs_analyzer.JaxRsAnalyzerPlugin"
            displayName = "JAX-RS Analyzer Gradle Plugin"
            description = "Gradle plugin for the JAX-RS Analyzer."
            tags.set(listOf("jaxrs", "swagger", "asciidoc"))
        }
    }
}

plugins {
    id("com.gradle.plugin-publish") version "1.1.0"
    `java-gradle-plugin`
    `kotlin-dsl`

}

repositories {
    mavenCentral()
}

group = "com.github.grimmjo"
version = "0.4-SNAPSHOT"

dependencies {
    implementation(gradleApi())
    implementation("com.sebastian-daschner:jaxrs-analyzer:0.17")
}

gradlePlugin {
    website.set("https://github.com/grimmjo/jaxrs-analyzer-gradle-plugin")
    vcsUrl.set("https://github.com/grimmjo/jaxrs-analyzer-gradle-plugin")
    plugins {
        create("analyzerPlugin") {
            id = "com.github.grimmjo.jaxrs-analyzer"
            implementationClass = "com.github.grimmjo.jaxrs_analyzer.gradle.JaxRsAnalyzerPlugin"
            displayName = "JAX-RS Analyzer Gradle Plugin"
            description = "Gradle plugin for the JAX-RS Analyzer."
            tags.set(listOf("jaxrs", "swagger", "asciidoc"))
        }
    }
}

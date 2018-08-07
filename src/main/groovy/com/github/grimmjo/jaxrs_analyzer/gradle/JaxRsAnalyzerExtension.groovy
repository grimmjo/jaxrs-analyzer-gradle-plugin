package com.github.grimmjo.jaxrs_analyzer.gradle
/**
 *
 * @author grimmjo
 */
class JaxRsAnalyzerExtension {

    String[] backend = ['swagger']
    String outputDirectory

    String[] schemes = ['http']
    String domain = ''
    Boolean renderTags = false
    Integer tagPathOffset = 0
}


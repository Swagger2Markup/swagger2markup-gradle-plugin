/*
 *
 *  Copyright 2015 Robert Winkler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package io.github.swagger2markup

import groovy.io.FileType
import io.github.swagger2markup.markup.builder.MarkupLanguage
import io.github.swagger2markup.tasks.Swagger2MarkupTask
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class Swagger2MarkupTaskSpec extends Specification{

    private static final String INPUT_DIR = 'src/test/resources/yaml'
    private static final String SNIPPETS_DIR = 'src/test/resources/docs/asciidoc/paths'

    Project project

    def setup(){
        project = ProjectBuilder.builder().build()
    }

    def "Swagger2MarkupTask should convert Swagger to AsciiDoc"() {
        given:
            FileUtils.deleteQuietly(new File('build/asciidoc').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                outputDir new File('build/asciidoc').absoluteFile
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.inputDir == new File(INPUT_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.outputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.adoc', 'overview.adoc', 'paths.adoc', 'security.adoc']
    }

    def "Swagger2MarkupTask should be configurable via a Map"() {
        given:
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                outputDir new File('build/asciidoc').absoluteFile
                config = ['swagger2markup.markupLanguage' : MarkupLanguage.MARKDOWN.toString(),
                          'swagger2markup.outputLanguage' : Language.RU.toString()]
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask.config['swagger2markup.markupLanguage'] == MarkupLanguage.MARKDOWN.toString()
            swagger2MarkupTask.config['swagger2markup.outputLanguage'] == Language.RU.toString()
            !swagger2MarkupTask.config.containsKey('swagger2markup.generatedExamplesEnabled')
    }

    def "Swagger2MarkupTask should convert Swagger to Markdown"() {
        given:
            FileUtils.deleteQuietly(new File('build/markdown').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                outputDir new File('build/markdown').absoluteFile
                markupLanguage MarkupLanguage.MARKDOWN
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.inputDir == new File(INPUT_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.outputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.md', 'overview.md', 'paths.md', 'security.md']
    }
    def "Swagger2MarkupTask should generate asciidoc with russian labels"() {
        given:
            FileUtils.deleteQuietly(new File('build/asciidoc').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                outputDir new File('build/asciidoc').absoluteFile
                outputLanguage Language.RU
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            String fileContents = new File(swagger2MarkupTask.outputDir, "definitions.adoc").getText('UTF-8')
            fileContents.contains("== Определения")
    }

    def "Swagger2MarkupTask should group paths by tag"() {
        given:
            FileUtils.deleteQuietly(new File('build/asciidoc').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                outputDir new File('build/asciidoc').absoluteFile
                pathsGroupedBy = GroupBy.TAGS
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            String fileContents = new File(swagger2MarkupTask.outputDir, "paths.adoc").getText('UTF-8')
            fileContents.contains("=== Pet")
    }
}

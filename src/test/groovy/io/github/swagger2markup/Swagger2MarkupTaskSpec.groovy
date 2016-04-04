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

    Project project

    def setup(){
        project = ProjectBuilder.builder().build()
    }

    /*
    NOTE: Does not work, because gradleApi brings ASM v5 and markdown-to-asciidoc requires ASM v4
    def "Swagger2MarkupTask should convert Swagger to AsciiDoc"() {
        given:
            FileUtils.deleteQuietly(new File('build/asciidoc').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                swaggerInput new File(INPUT_DIR).absoluteFile
                markupOutputDir new File('build/asciidoc').absoluteFile
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.swaggerInput == new File(INPUT_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.markupOutputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.adoc', 'overview.adoc', 'paths.adoc', 'security.adoc']
    }
    */

    def "Swagger2MarkupTask should convert Swagger file from folder to Markdown"() {
        given:
            FileUtils.deleteQuietly(new File('build/markdown').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                swaggerInput new File(INPUT_DIR).absoluteFile
                markupOutputDir new File('build/markdown').absoluteFile
                config = [(Swagger2MarkupProperties.MARKUP_LANGUAGE) : MarkupLanguage.MARKDOWN.toString()]
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.swaggerInput == new File(INPUT_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.markupOutputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.md', 'overview.md', 'paths.md', 'security.md']
    }

    def "Swagger2MarkupTask should convert Swagger file to Markdown"() {
        given:
            FileUtils.deleteQuietly(new File('build/markdown').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                swaggerInput new File(INPUT_DIR, "swagger_petstore.yaml").absoluteFile
                markupOutputDir new File('build/markdown').absoluteFile
                config = [(Swagger2MarkupProperties.MARKUP_LANGUAGE) : MarkupLanguage.MARKDOWN.toString()]
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.swaggerInput == new File(INPUT_DIR, "swagger_petstore.yaml").absoluteFile
            def list = []
            def dir = swagger2MarkupTask.markupOutputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.md', 'overview.md', 'paths.md', 'security.md']
    }

    def "Swagger2MarkupTask should convert Swagger file to a file"() {
        given:
            FileUtils.deleteQuietly(new File('build/markdown').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                swaggerInput new File(INPUT_DIR, "swagger_petstore.yaml").absoluteFile
                markupOutputFile new File('build/markdown', "swagger").absoluteFile
                config = [(Swagger2MarkupProperties.MARKUP_LANGUAGE) : MarkupLanguage.MARKDOWN.toString()]
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
        swagger2MarkupTask != null
            swagger2MarkupTask.swaggerInput == new File(INPUT_DIR, "swagger_petstore.yaml").absoluteFile
            def list = []
            def dir = new File('build/markdown')
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['swagger.md']
    }


    def "Swagger2MarkupTask should use russian labels"() {
        given:
            FileUtils.deleteQuietly(new File('build/markdown').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                swaggerInput new File(INPUT_DIR).absoluteFile
                markupOutputDir new File('build/markdown').absoluteFile
                config = [(Swagger2MarkupProperties.MARKUP_LANGUAGE) : MarkupLanguage.MARKDOWN.toString(),
                          (Swagger2MarkupProperties.OUTPUT_LANGUAGE) : Language.RU.toString()]
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            String fileContents = new File(swagger2MarkupTask.markupOutputDir, "definitions.md").getText('UTF-8')
            fileContents.contains("## Определения")
    }

    def "Swagger2MarkupTask should group paths by tag"() {
        given:
            FileUtils.deleteQuietly(new File('build/markdown').absoluteFile);
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                swaggerInput new File(INPUT_DIR).absoluteFile
                markupOutputDir new File('build/markdown').absoluteFile
                config = [(Swagger2MarkupProperties.MARKUP_LANGUAGE) : MarkupLanguage.MARKDOWN.toString(),
                          (Swagger2MarkupProperties.PATHS_GROUPED_BY) : GroupBy.TAGS.toString()]
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            String fileContents = new File(swagger2MarkupTask.markupOutputDir, "paths.md").getText('UTF-8')
            fileContents.contains("### Pet")
    }
}

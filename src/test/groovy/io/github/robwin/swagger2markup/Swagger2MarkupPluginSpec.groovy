package io.github.robwin.swagger2markup

import groovy.io.FileType
import io.github.robwin.markup.builder.MarkupLanguage
import io.github.robwin.swagger2markup.tasks.Swagger2MarkupTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class Swagger2MarkupPluginSpec extends Specification{

    private static final String SWAGGER_RESOURCES_DIR = 'src/test/resources/docs/swagger'

    Project project

    def setup(){
        project = ProjectBuilder.builder().build()
    }

    def "Swagger2MarkupTask should be applied to project with default setup"() {
        expect:
             project.tasks.findByName(Swagger2MarkupPlugin.TASK_NAME) == null
        when:
            project.pluginManager.apply 'io.github.robwin.swagger2markup'
        then:
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.findByName(Swagger2MarkupPlugin.TASK_NAME)
            swagger2MarkupTask != null
            swagger2MarkupTask.group == 'Documentation'
            swagger2MarkupTask.inputDir == project.file('src/docs/swagger')
            swagger2MarkupTask.outputDir == new File(project.buildDir, 'markup')
    }

    def "Swagger2MarkupTask should convert Swagger to AsciiDoc"() {
        given:
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(SWAGGER_RESOURCES_DIR).absoluteFile
            }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.inputDir == new File(SWAGGER_RESOURCES_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.outputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.adoc', 'overview.adoc', 'paths.adoc']
    }

    def "Swagger2MarkupTask should convert Swagger to Markdown"() {
        given:
        Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
            inputDir new File(SWAGGER_RESOURCES_DIR).absoluteFile
            markupLanguage = MarkupLanguage.MARKDOWN
        }
        when:
            swagger2MarkupTask.convertSwagger2markup()
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.inputDir == new File(SWAGGER_RESOURCES_DIR).absoluteFile
            def list = []
            def dir = swagger2MarkupTask.outputDir
            dir.eachFileRecurse(FileType.FILES) { file ->
                list << file.name
            }
            list.sort() == ['definitions.md', 'overview.md', 'paths.md']
    }
}

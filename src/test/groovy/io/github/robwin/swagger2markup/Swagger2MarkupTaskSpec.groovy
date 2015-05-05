package io.github.robwin.swagger2markup
import groovy.io.FileType
import io.github.robwin.markup.builder.MarkupLanguage
import io.github.robwin.swagger2markup.tasks.Swagger2MarkupTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class Swagger2MarkupTaskSpec extends Specification{

    private static final String INPUT_DIR = 'src/test/resources/docs/swagger'
    private static final String DOCS_DIR = 'src/test/resources/docs'

    Project project

    def setup(){
        project = ProjectBuilder.builder().build()
    }

    def "Swagger2MarkupTask should convert Swagger to AsciiDoc"() {
        given:
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
            list.sort() == ['definitions.adoc', 'overview.adoc', 'paths.adoc']
    }

    def "Swagger2MarkupTask should convert Swagger to Markdown"() {
        given:
        Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
            inputDir new File(INPUT_DIR).absoluteFile
            outputDir new File('build/markdown').absoluteFile
            markupLanguage = MarkupLanguage.MARKDOWN
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
        list.sort() == ['definitions.md', 'overview.md', 'paths.md']
    }

    def "Swagger2MarkupTask should enable withDescriptions, withExamples and withSchemas"() {
        when:
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.create(name: Swagger2MarkupPlugin.TASK_NAME, type: Swagger2MarkupTask) {
                inputDir new File(INPUT_DIR).absoluteFile
                examplesDir new File(DOCS_DIR).absoluteFile
                descriptionsDir new File(DOCS_DIR).absoluteFile
                schemasDir new File(DOCS_DIR).absoluteFile
            }
        then:
            swagger2MarkupTask != null
            swagger2MarkupTask.outputDir == new File(project.buildDir, 'asciidoc')
            swagger2MarkupTask.examplesDir == new File(DOCS_DIR).absoluteFile
            swagger2MarkupTask.descriptionsDir == new File(DOCS_DIR).absoluteFile
            swagger2MarkupTask.schemasDir == new File(DOCS_DIR).absoluteFile
    }
}

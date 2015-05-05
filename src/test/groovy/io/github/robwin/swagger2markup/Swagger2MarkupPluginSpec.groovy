package io.github.robwin.swagger2markup

import io.github.robwin.swagger2markup.tasks.Swagger2MarkupTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class Swagger2MarkupPluginSpec extends Specification{

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
            swagger2MarkupTask.outputDir == new File(project.buildDir, 'asciidoc')
    }
}

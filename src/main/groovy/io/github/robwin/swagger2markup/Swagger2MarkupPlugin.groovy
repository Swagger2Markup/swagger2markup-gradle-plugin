package io.github.robwin.swagger2markup

import io.github.robwin.swagger2markup.tasks.Swagger2MarkupTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class Swagger2MarkupPlugin implements Plugin<Project> {

    static final String TASK_NAME = 'convertSwagger2markup'

    @Override
    void apply(Project project) {
        project.task(TASK_NAME, type: Swagger2MarkupTask, group: 'Documentation',
                description: 'Converts a Swagger JSON or YAML file into Markdown or AsciiDoc files and copies the output files to the build directory.')
    }

}

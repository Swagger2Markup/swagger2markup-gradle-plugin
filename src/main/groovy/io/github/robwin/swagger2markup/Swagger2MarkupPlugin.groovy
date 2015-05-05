package io.github.robwin.swagger2markup
import io.github.robwin.swagger2markup.extensions.RmsUploadExtension
import io.github.robwin.swagger2markup.tasks.RmsUploadTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class RmsUploadPlugin implements Plugin<Project> {

    private static final String EXTENSION_NAME = 'rmsUpload'
    private static final String TASK_NAME = 'rmsUpload'

    @Override
    void apply(Project project) {
        RmsUploadExtension extension = project.extensions.create(EXTENSION_NAME, RmsUploadExtension, project)
        project.task(TASK_NAME, type: RmsUploadTask, group: 'Telekom',
                description: 'Uploads an artifact to the Telekom Rollout Management System (RMS).'){RmsUploadTask task ->
            task.conventionMapping.uploadUrl = { extension.uploadUrl }
            task.conventionMapping.username = { extension.username }
            task.conventionMapping.password = { extension.password }
            task.conventionMapping.distributionsDir = { extension.distributionsDir }
            task.conventionMapping.artifactVersion = { extension.artifactVersion }
            task.conventionMapping.artifactDescription = { extension.artifactDescription }
            task.conventionMapping.serviceId = { extension.serviceId }
            task.conventionMapping.platformId = { extension.platformId }
            task.conventionMapping.dryRun = { extension.dryRun }
        }
    }

}

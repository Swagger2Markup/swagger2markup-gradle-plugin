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

import io.github.swagger2markup.tasks.Swagger2MarkupTask
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
            project.pluginManager.apply 'io.github.swagger2markup'
        then:
            Swagger2MarkupTask swagger2MarkupTask = (Swagger2MarkupTask) project.tasks.findByName(Swagger2MarkupPlugin.TASK_NAME)
            swagger2MarkupTask != null
            swagger2MarkupTask.group == 'Documentation'
            swagger2MarkupTask.inputDir == project.file('src/docs/swagger')
            swagger2MarkupTask.outputDir == new File(project.buildDir, 'asciidoc')
    }
}

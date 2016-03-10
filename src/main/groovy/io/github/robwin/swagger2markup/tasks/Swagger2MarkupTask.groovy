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
package io.github.robwin.swagger2markup.tasks

import io.github.robwin.markup.builder.MarkupLanguage
import io.github.robwin.swagger2markup.Swagger2MarkupConfig
import io.github.robwin.swagger2markup.Swagger2MarkupConverter
import io.github.robwin.swagger2markup.Swagger2MarkupExtensionRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

class Swagger2MarkupTask extends DefaultTask {

    @Optional
    @InputDirectory
    def File inputDir

    @Optional
    @OutputDirectory
    def File outputDir

    private MarkupLanguage markupLanguage;

    @Optional
    @Input
    Map<String, String> config = [:]

    Swagger2MarkupTask() {
        inputDir = project.file('src/docs/swagger')
        markupLanguage = MarkupLanguage.ASCIIDOC
        outputDir = new File(project.buildDir, markupLanguage.toString().toLowerCase())
    }

    @TaskAction
    void convertSwagger2markup() {
        if (logger.isDebugEnabled()) {
            logger.debug("convertSwagger2markup task started")
            logger.debug("InputDir: {}", inputDir)
            logger.debug("OutputDir: {}", outputDir)
            config.each { k, v ->
                logger.debug("k: {}", v)
            }
         }
        inputDir.eachFile { file ->
            if (logger.isDebugEnabled()) {
                logger.debug("File: {}", file.absolutePath)
            }
            Properties properties = new Properties();
            properties.putAll(config);
            Swagger2MarkupConfig config = Swagger2MarkupConfig.ofProperties(properties).build();

            Swagger2MarkupExtensionRegistry registry = Swagger2MarkupExtensionRegistry.ofDefaults().build();

            Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(file.toPath())
                    .withConfig(config)
                    .withExtensionRegistry(registry)
                    .build();

            converter.intoFolder(outputDir.toPath())
        }
        logger.debug("convertSwagger2markup task finished")
    }
}

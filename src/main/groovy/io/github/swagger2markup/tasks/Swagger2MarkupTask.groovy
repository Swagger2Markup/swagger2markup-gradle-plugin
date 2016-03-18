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
package io.github.swagger2markup.tasks

import io.github.swagger2markup.GroupBy
import io.github.swagger2markup.Language
import io.github.swagger2markup.Swagger2MarkupConverter
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder
import io.github.swagger2markup.markup.builder.MarkupLanguage
import org.apache.commons.lang3.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

class Swagger2MarkupTask extends DefaultTask {

    @Optional
    @InputDirectory
    def File inputDir

    @Optional
    @Input
    String swaggerFile;

    @Optional
    @Input
    MarkupLanguage markupLanguage;

    @Optional
    @Input
    GroupBy pathsGroupedBy;

    @Optional
    @Input
    Language outputLanguage

    @Optional
    @OutputDirectory
    def File outputDir

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
            logger.debug("SwaggerFile: {}", swaggerFile)
            logger.debug("MarkupLanguage: {}", markupLanguage)
            logger.debug("OutputLanguage: {}", outputLanguage)
            config.each { k, v ->
                logger.debug("k: {}", v)
            }
         }

        if (StringUtils.isEmpty(swaggerFile)) {
            inputDir.eachFile { file ->
                if(!file.isHidden()) {
                    convertSwaggerFileToMarkup(file)
                }
            }
        } else {
            convertSwaggerFileToMarkup(new File(inputDir, swaggerFile));
        }

        logger.debug("convertSwagger2markup task finished")
    }

    void convertSwaggerFileToMarkup(File file) {
        if (logger.isDebugEnabled()) {
            logger.debug("File: {}", file.absolutePath)
        }
        Swagger2MarkupConfigBuilder configBuilder = new Swagger2MarkupConfigBuilder(config)
                .withMarkupLanguage(markupLanguage);
        if(outputLanguage){
            configBuilder.withOutputLanguage(outputLanguage)
        }
        if(pathsGroupedBy){
            configBuilder.withPathsGroupedBy(pathsGroupedBy)
        }

        Swagger2MarkupConverter converter = Swagger2MarkupConverter.from(file.toPath())
                .withConfig(configBuilder.build())
                .build();

        converter.toFolder(outputDir.toPath())
    }
}

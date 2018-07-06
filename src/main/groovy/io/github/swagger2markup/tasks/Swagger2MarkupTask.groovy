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

import io.github.swagger2markup.Swagger2MarkupConfig
import io.github.swagger2markup.Swagger2MarkupConverter
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder
import io.github.swagger2markup.utils.URIUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.*

class Swagger2MarkupTask extends DefaultTask {

    @InputFile
    @Optional
    def File swaggerInputFile

    @Input
    @Optional
    def String swaggerInput

    @Optional
    @OutputDirectory
    def File outputDir

    @Optional
    @OutputFile
    def File outputFile

    @Optional
    @Input
    Map<String, String> config = [:]

    Swagger2MarkupTask() {
    }

    @TaskAction
    void convertSwagger2markup() {
        if (logger.isDebugEnabled()) {
            logger.debug("convertSwagger2markup task started")
            logger.debug("Input: {}", swaggerInput)
            logger.debug("OutputDir: {}", outputDir)
            logger.debug("OutputFile: {}", outputFile)
            config.each { k, v ->
                logger.debug("k: {}", v)
            }
        }
        try {
            if (swaggerInputFile == null && swaggerInput == null) {
                throw new IllegalArgumentException("Either swaggerInputFile or swaggerInputUrl parameter must be used")
            }
            Swagger2MarkupConfig swagger2MarkupConfig = new Swagger2MarkupConfigBuilder(config).build()
            Swagger2MarkupConverter.Builder converterBuilder = swaggerInput != null ?
                    Swagger2MarkupConverter.from(URIUtils.create(swaggerInput))
                    : Swagger2MarkupConverter.from(swaggerInputFile.toPath())

            Swagger2MarkupConverter converter = converterBuilder.withConfig(swagger2MarkupConfig).build()

            if (outputFile != null) {
                converter.toFile(outputFile.toPath())
            } else if (outputDir != null) {
                converter.toFolder(outputDir.toPath())
            } else {
                throw new IllegalArgumentException("Either outputFile or outputDir parameter must be used")
            }
        } catch (Exception e) {
            throw new GradleException("Failed to execute task 'convertSwagger2markup'", e)
        }
        logger.debug("convertSwagger2markup task finished")
    }
}

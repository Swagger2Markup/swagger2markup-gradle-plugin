package io.github.swagger2markup

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Paths

class IntegrationTestSpec extends Specification {
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    def "Plugin works with json file input and markdown output"() {
        given:
        copyFile('json/swagger_petstore.json', 'input.json')

        buildFile << """plugins {
                    id 'io.github.swagger2markup'
                }
                convertSwagger2markup {
                  swaggerInputFile file('input.json')
                  outputFile file(new File(project.buildDir, "output"))
                  config = ['swagger2markup.markupLanguage' : 'MARKDOWN']
                }
                """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments('convertSwagger2markup')
                .withGradleVersion(gradleVersion)
                .forwardOutput()
                .build()

        then:
        result.output.contains(":convertSwagger2markup")
        result.tasks.get(0).path == ":convertSwagger2markup"
        result.tasks.get(0).outcome == TaskOutcome.SUCCESS
        Paths.get(testProjectDir.root.path, "build", "output.md").toFile().exists()

        where:
        gradleVersion << ['3.3', '3.5.1', '4.2.1', '4.3.1', '4.6']
    }

    def "Plugin correctly performs incremental builds with file input"() {
        given:
        copyFile('json/swagger_petstore.json', 'input.json')

        buildFile << """plugins {
                    id 'io.github.swagger2markup'
                }
                convertSwagger2markup {
                  swaggerInputFile file('input.json')
                  outputFile file(new File(project.buildDir, "output"))
                  config = ['swagger2markup.markupLanguage' : 'MARKDOWN']
                }
                """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments('convertSwagger2markup')
                .withGradleVersion(gradleVersion)
                .forwardOutput()
                .build()
        def result2 = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments('convertSwagger2markup')
                .withGradleVersion(gradleVersion)
                .forwardOutput()
                .build()
        copyFile('json/swagger_petstore_v2.json', 'input.json')
        def result3 = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments('convertSwagger2markup')
                .withGradleVersion(gradleVersion)
                .forwardOutput()
                .build()

        then:
        result.tasks.get(0).path == ":convertSwagger2markup"
        result.tasks.get(0).outcome == TaskOutcome.SUCCESS
        Paths.get(testProjectDir.root.path, "build", "output.md").toFile().exists()
        result2.tasks.get(0).path == ":convertSwagger2markup"
        result2.tasks.get(0).outcome == TaskOutcome.UP_TO_DATE
        result3.tasks.get(0).path == ":convertSwagger2markup"
        result3.tasks.get(0).outcome == TaskOutcome.SUCCESS
        Paths.get(testProjectDir.root.path, "build", "output.md").toFile().exists()

        where:
        gradleVersion << ['3.3', '3.5.1', '4.2.1', '4.3.1', '4.6']
    }

    void copyFile(String source, String destination) {
        File target = new File(testProjectDir.root, destination)
        target.parentFile.mkdirs()
        target << this.getClass().getClassLoader().getResourceAsStream(source).text
    }

}

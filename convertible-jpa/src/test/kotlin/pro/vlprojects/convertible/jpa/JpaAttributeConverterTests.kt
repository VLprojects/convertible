package pro.vlprojects.convertible.jpa

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.OK
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.kspSourcesDir
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.io.File
import java.util.stream.Stream
import kotlin.test.assertEquals

@OptIn(ExperimentalCompilerApi::class)
class JpaAttributeConverterTests {

	@ParameterizedTest(name = "{index}: {0}")
	@ArgumentsSource(SuccessScenarioDataProvider::class)
	fun `Should succeed to generate JPA converter from resource`(case: TestCase) {

		val originalContent = readResource("vo/${case.originalName}")
		val expectedContent = readResource("converter/${case.expectedName}")

		val compilation = prepareCompilation(case.originalName, originalContent)
		val result = compilation.compile()
		val generated = compilation.readKspGeneratedFile(case.expectedName)

		assertEquals(OK, result.exitCode, "Failed to compile example")
		assertEquals(expectedContent, generated, "Generated file does not match expectation")
	}

	private fun prepareCompilation(originalName: String, originalContent: String) = KotlinCompilation()
		.apply {
			configureKsp(useKsp2 = true) {
				workingDir = File("build/ksp-test")
				sources = listOf(SourceFile.kotlin(originalName, originalContent))
				symbolProcessorProviders += JpaConvertibleProcessorProvider()
				inheritClassPath = true
				verbose = false
				messageOutputStream = System.out
			}
		}

	private fun readResource(path: String): String = javaClass
		.classLoader
		.getResource(path)
		.let(::checkNotNull)
		.readText()
		.trim()

	private fun KotlinCompilation.readKspGeneratedFile(name: String): String? = this
		.kspSourcesDir
		.walk()
		.firstOrNull { it.name == name }
		?.readText()
		?.trim()

	class SuccessScenarioDataProvider : ArgumentsProvider {
		override fun provideArguments(context: ExtensionContext): Stream<out Arguments> = Stream.of(
			Arguments.of(
				Named.of(
					"Fallback value. Fallback factory",
					TestCase("FallbackAll.kt", "FallbackAllConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nullable. Fallback value. Fallback factory",
					TestCase("NullableFallbackAll.kt", "NullableFallbackAllConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Fallback value. Factory declared",
					TestCase("FallbackValue.kt", "FallbackValueConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nullable. Fallback value. Factory declared",
					TestCase("NullableFallbackValue.kt", "NullableFallbackValueConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Fallback factory. Value declared",
					TestCase("FallbackFactory.kt", "FallbackFactoryConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nullable. Fallback factory. Value declared",
					TestCase("NullableFallbackFactory.kt", "NullableFallbackFactoryConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Factory declared. Value declared",
					TestCase("DeclaredAll.kt", "DeclaredAllConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nullable. Factory declared. Value declared",
					TestCase("NullableDeclaredAll.kt", "NullableDeclaredAllConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Enum. Factory declared. Value declared via method",
					TestCase("EnumExample.kt", "EnumExampleConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nullable. Enum. Factory declared. Value declared via method",
					TestCase("NullableEnumExample.kt", "NullableEnumExampleConverter.kt"),
				)
			),
		)
	}

	data class TestCase(val originalName: String, val expectedName: String)
}

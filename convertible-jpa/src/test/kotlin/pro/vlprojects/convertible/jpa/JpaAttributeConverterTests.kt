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
					TestCase("FallbackAll.kt", "FallbackAllJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nullable. Fallback value. Fallback factory",
					TestCase("NullableFallbackAll.kt", "NullableFallbackAllJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Fallback value. Factory declared",
					TestCase("FallbackValue.kt", "FallbackValueJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nullable. Fallback value. Factory declared",
					TestCase("NullableFallbackValue.kt", "NullableFallbackValueJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Fallback factory. Value declared",
					TestCase("FallbackFactory.kt", "FallbackFactoryJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nullable. Fallback factory. Value declared",
					TestCase("NullableFallbackFactory.kt", "NullableFallbackFactoryJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Factory declared. Value declared",
					TestCase("DeclaredAll.kt", "DeclaredAllJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nullable. Factory declared. Value declared",
					TestCase("NullableDeclaredAll.kt", "NullableDeclaredAllJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Enum. Factory declared. Value declared via method",
					TestCase("EnumExample.kt", "EnumExampleJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nullable. Enum. Factory declared. Value declared via method",
					TestCase("NullableEnumExample.kt", "NullableEnumExampleJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Prefix. Factory declared. Value declared",
					TestCase("Prefix.kt", "SomePrefixJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nested. Factory declared. Value declared",
					TestCase("NestedDeclaredAll.kt", "SubNestedDeclaredAllJpaConverter.kt"),
				)
			),
			Arguments.of(
				Named.of(
					"Nested. Fallback factory. Fallback value",
					TestCase("NestedFallbackAll.kt", "SubNestedFallbackAllJpaConverter.kt"),
				)
			),
		)
	}

	data class TestCase(val originalName: String, val expectedName: String)
}

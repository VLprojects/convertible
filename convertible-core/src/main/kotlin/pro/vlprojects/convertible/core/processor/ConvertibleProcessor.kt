package pro.vlprojects.convertible.core.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import pro.vlprojects.convertible.core.annotation.Convertible
import pro.vlprojects.convertible.core.definition.ConvertibleDefinition
import pro.vlprojects.convertible.core.strategy.ConvertibleStrategyLoader
import java.io.OutputStreamWriter

class ConvertibleProcessor(
	private val generator: CodeGenerator,
	private val logger: KSPLogger,
) : SymbolProcessor {

	private val strategies = ConvertibleStrategyLoader(logger)
		.load()
		.also { logger.info("${it.size} strategies found for code generating") }

	private val definitions = mutableListOf<ConvertibleDefinition>()
	private val visitor = ConvertibleVisitor(definitions)

	override fun process(resolver: Resolver): List<KSAnnotated> {
		resolver
			.getSymbolsWithAnnotation(Convertible::class.qualifiedName!!)
			.filterIsInstance<KSClassDeclaration>()
			.filter { it.validate() }
			.forEach {
				logger.info("Found @Convertible annotated class: ${it.qualifiedName?.asString()}")
				it.accept(visitor, Unit)
			}

		return emptyList()
	}

	override fun finish() = definitions
		.also { logger.info("${it.size} definitions found for code generating") }
		.forEach { definition ->
			logger.info("Processing definition: $definition")
			val targetPackage = "${definition.objectClassName.packageName}.${definition.scope.lowercase()}"
			strategies
				.filter { it.supports(definition) }
				.forEach { strategy ->
					val specification = strategy.build(definition)
					logger.info("Generating file: $targetPackage.${specification.name}")
					specification.writeWith(generator, targetPackage, definition.source)
					logger.info("Generated file: $targetPackage.${specification.name}")
				}
		}

	override fun onError() {
		super.onError()
		logger.error("Failed to process @Convertible")
	}

	private fun FileSpec.writeWith(generator: CodeGenerator, targetPackage: String, source: KSFile) = generator
		.createNewFile(Dependencies(false, source), targetPackage, name)
		.use { stream ->
			OutputStreamWriter(stream)
				.use { writer -> writeTo(writer) }
		}
}

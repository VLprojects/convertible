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
import pro.vlprojects.convertible.core.strategy.ConvertibleStrategy
import java.io.OutputStreamWriter

class ConvertibleProcessor(
	private val strategy: ConvertibleStrategy,
	private val generator: CodeGenerator,
	private val logger: KSPLogger,
) : SymbolProcessor {

	private val definitions = mutableListOf<ConvertibleDefinition>()

	override fun process(resolver: Resolver): List<KSAnnotated> {
		val visitor = ConvertibleVisitor(strategy.scope(), definitions)

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
			check(strategy.supports(definition)) { "The strategy does not support the definition: $definition" }

			val targetPackage = definition.objectClassName.packageName
			val specification = strategy.build(definition)

			specification.writeWith(generator, targetPackage, definition.source)
			logger.info("Generated file: $targetPackage.${specification.name}")
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

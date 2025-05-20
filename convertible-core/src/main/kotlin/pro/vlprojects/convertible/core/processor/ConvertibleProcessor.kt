package pro.vlprojects.convertible.core.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import pro.vlprojects.convertible.core.annotation.Convertible
import pro.vlprojects.convertible.core.definition.ConvertibleDefinition
import pro.vlprojects.convertible.core.strategy.ConvertibleStrategy
import java.io.OutputStreamWriter
import java.util.ServiceLoader

class ConvertibleProcessor(
	private val generator: CodeGenerator,
	private val logger: KSPLogger,
) : SymbolProcessor {

	private val strategies = ServiceLoader.load(ConvertibleStrategy::class.java).toList()
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
		.forEach { definition ->
			val targetPackage = "${definition.objectClassName.packageName}.${definition.scope.lowercase()}"
			strategies
				.filter { it.supports(definition) }
				.forEach { strategy ->
					val specification = strategy.build(definition)
					specification.writeWith(generator, targetPackage)

					logger.info("Generated file: $targetPackage.${specification.name}")
				}
		}

	override fun onError() {
		super.onError()
		logger.error("Failed to process @Convertible")
	}

	private fun FileSpec.writeWith(generator: CodeGenerator, targetPackage: String) = generator
		.createNewFile(Dependencies(false), targetPackage, name)
		.use { stream ->
			OutputStreamWriter(stream)
				.use { writer -> writeTo(writer) }
		}
}

package pro.vlprojects.convertible.jpa

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import pro.vlprojects.convertible.core.processor.ConvertibleProcessor

class JpaConvertibleProcessorProvider : SymbolProcessorProvider {
	override fun create(environment: SymbolProcessorEnvironment) = ConvertibleProcessor(
		strategy = JpaAttributeConverter(),
		generator = environment.codeGenerator,
		logger = environment.logger,
	)
}

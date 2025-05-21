package pro.vlprojects.convertible.core.strategy

import com.google.devtools.ksp.processing.KSPLogger
import java.net.URL

class ConvertibleStrategyLoader(
	private val logger: KSPLogger,
) {

	companion object {
		const val RESOURCE_NAME = "META-INF/services/pro.vlprojects.convertible.core.strategy.ConvertibleStrategy"
	}

	fun load(): List<ConvertibleStrategy> {

		val classLoader = Thread.currentThread().contextClassLoader
		val result = mutableListOf<ConvertibleStrategy>()

		readResources(classLoader)
			.forEach { resource ->
				logger.info("Found resource: $resource")

				readClassNames(resource)
					.forEach { className ->
						logger.info("Found class: $className")

						runCatching { createStrategy(className, classLoader) }
							.onFailure { logger.error("Failed to create strategy for class $className. Details: ${it.message}") }
							.onSuccess {
								result.add(it)
								logger.info("Created strategy for class $className")
							}
					}
			}

		return result
	}

	private fun readResources(classLoader: ClassLoader) = classLoader
		.runCatching { getResources(RESOURCE_NAME) }
		.onFailure { logger.error("Failed to read resources $RESOURCE_NAME. Details: ${it.message}") }
		.getOrNull()
		?.toList()
		?: emptyList()

	private fun readClassNames(resource: URL) = resource
		.runCatching {
			openStream()
				.bufferedReader()
				.readLines()
				.map { it.trim() }
				.filter { it.isNotBlank() && it.startsWith("#").not() }
		}
		.onFailure { logger.error("Failed to read resource $resource. Details: ${it.message}") }
		.getOrDefault(emptyList())

	private fun createStrategy(className: String, classLoader: ClassLoader): ConvertibleStrategy {
		val clazz = Class.forName(className, true, classLoader)

		require(ConvertibleStrategy::class.java.isAssignableFrom(clazz)) {
			"Class $className is not assignable to ${ConvertibleStrategy::class.java.canonicalName}"
		}

		return clazz.getDeclaredConstructor().newInstance() as ConvertibleStrategy
	}
}

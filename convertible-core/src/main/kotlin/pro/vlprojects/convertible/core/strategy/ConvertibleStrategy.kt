package pro.vlprojects.convertible.core.strategy

import com.squareup.kotlinpoet.FileSpec
import pro.vlprojects.convertible.core.definition.ConvertibleDefinition

interface ConvertibleStrategy {
	fun scope(): String
	fun supports(definition: ConvertibleDefinition): Boolean = scope() == definition.scope
	fun build(definition: ConvertibleDefinition): FileSpec
}

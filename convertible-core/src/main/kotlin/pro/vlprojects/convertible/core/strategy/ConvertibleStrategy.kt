package pro.vlprojects.convertible.core.strategy

import com.squareup.kotlinpoet.FileSpec
import pro.vlprojects.convertible.core.definition.ConvertibleDefinition

interface ConvertibleStrategy {
	fun supports(definition: ConvertibleDefinition): Boolean
	fun build(definition: ConvertibleDefinition): FileSpec
}

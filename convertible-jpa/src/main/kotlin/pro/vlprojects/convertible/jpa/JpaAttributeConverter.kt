package pro.vlprojects.convertible.jpa

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component
import pro.vlprojects.convertible.core.annotation.Scope
import pro.vlprojects.convertible.core.definition.ConvertibleDefinition
import pro.vlprojects.convertible.core.strategy.ConvertibleStrategy

class JpaAttributeConverter : ConvertibleStrategy {

	override fun scope() = Scope.JPA

	override fun build(definition: ConvertibleDefinition): FileSpec {

		val scope = Scope.JPA
		val packageName = "${definition.objectClassName.packageName}.$scope"
		val fileName = "${definition.objectClassName.simpleName}Converter"
		val nullable = definition.nullable
		val objectType = definition.objectClassName.copy(nullable = nullable)
		val primitiveType = definition.valueAccessor.returnType.copy(nullable = nullable)

		return FileSpec
			.builder(packageName = packageName, fileName = fileName)
			.addType(
				TypeSpec
					.classBuilder(fileName)
					.addModifiers()
					.addAnnotation(
						AnnotationSpec
							.builder(Component::class)
							.addMember("value = \"%L\"", "$scope.$fileName")
							.build()
					)
					.addAnnotation(
						AnnotationSpec
							.builder(Converter::class)
							.addMember("autoApply = true")
							.build()
					)
					.addSuperinterface(
						AttributeConverter::class
							.asClassName()
							.parameterizedBy(objectType, primitiveType)
					)
					.addFunction(buildToDatabaseColumn(definition))
					.addFunction(buildToEntityAttribute(definition))
					.build()
			)
			.build()
	}

	private fun buildToDatabaseColumn(definition: ConvertibleDefinition): FunSpec {

		val nullable = definition.nullable
		val safeAccess = if (definition.nullable) "?." else "."
		val invocation = if (definition.valueAccessor.isMethod) "()" else ""
		val expression = "attribute$safeAccess${definition.valueAccessor.name}$invocation"

		return FunSpec
			.builder("convertToDatabaseColumn")
			.addModifiers(KModifier.OVERRIDE)
			.addParameter("attribute", definition.objectClassName.copy(nullable))
			.returns(definition.valueAccessor.returnType.copy(nullable))
			.addCode(CodeBlock.of("return %L", expression))
			.build()
	}

	private fun buildToEntityAttribute(definition: ConvertibleDefinition): FunSpec {
		val nullable = definition.nullable
		val safeAccess = if (definition.nullable) "?." else "."
		val isConstructor = definition.factoryAccessor.isConstructor
		val factoryCall = if (isConstructor) "::${definition.objectClassName.simpleName}" else "${definition.objectClassName.simpleName}::${definition.factoryAccessor.name}"
		val expression = "source${safeAccess}let($factoryCall)"

		return FunSpec
			.builder("convertToEntityAttribute")
			.addModifiers(KModifier.OVERRIDE)
			.addParameter("source", definition.valueAccessor.returnType.copy(nullable))
			.returns(definition.objectClassName.copy(nullable))
			.addCode(CodeBlock.of("return %L", expression))
			.build()
	}
}

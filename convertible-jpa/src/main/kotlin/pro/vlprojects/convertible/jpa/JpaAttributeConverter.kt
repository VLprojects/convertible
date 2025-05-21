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

		val fileName = "${definition.prefix}${definition.objectClassName.simpleName}JpaConverter"
		val nullable = definition.nullable
		val objectType = definition.objectClassName.copy(nullable = nullable)
		val primitiveType = definition.valueAccessor.returnType.copy(nullable = nullable)

		return FileSpec
			.builder(packageName = definition.objectClassName.packageName, fileName = fileName)
			.addType(
				TypeSpec
					.classBuilder(fileName)
					.addModifiers()
					.addAnnotation(Component::class)
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

		return FunSpec
			.builder("convertToDatabaseColumn")
			.addModifiers(KModifier.OVERRIDE)
			.addParameter("attribute", definition.objectClassName.copy(nullable))
			.returns(definition.valueAccessor.returnType.copy(nullable))
			.addCode(
				CodeBlock
					.builder()
					.add("return attribute")
					.apply { if (nullable) add("?") }
					.add(".")
					.add(definition.valueAccessor.name)
					.apply { if (definition.valueAccessor.isMethod) add("()") }
					.build()
			)
			.build()
	}

	private fun buildToEntityAttribute(definition: ConvertibleDefinition): FunSpec {

		val nullable = definition.nullable
		val className = definition.objectClassName
		val accessor = definition.factoryAccessor

		val factoryReference = when {
			accessor.isConstructor && className.enclosingClassName() != null -> CodeBlock.of("%T::%L", className.topLevelClassName(), className.simpleName)
			accessor.isConstructor -> CodeBlock.of("::%L", className.simpleName)
			className.enclosingClassName() != null -> CodeBlock.of("%T::%L", className, accessor.name)
			else -> CodeBlock.of("%T::%L", className.topLevelClassName(), accessor.name)
		}

		return FunSpec
			.builder("convertToEntityAttribute")
			.addModifiers(KModifier.OVERRIDE)
			.addParameter("source", definition.valueAccessor.returnType.copy(nullable))
			.returns(definition.objectClassName.copy(nullable))
			.addCode(
				CodeBlock
					.builder()
					.add("return source")
					.apply { if (nullable) add("?") }
					.add(".let(")
					.add(factoryReference)
					.add(")")
					.build()
			)
			.build()
	}
}

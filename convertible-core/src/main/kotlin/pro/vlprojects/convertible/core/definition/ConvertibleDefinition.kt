package pro.vlprojects.convertible.core.definition

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName

data class ConvertibleDefinition(
	val objectClassName: ClassName,
	val source: KSFile,
	val scope: String,
	val nullable: Boolean,
	val valueAccessor: ValueAccessor,
	val factoryAccessor: FactoryAccessor,
) {
	data class ValueAccessor(
		val name: String,
		val returnType: TypeName,
		val isMethod: Boolean,
	) {
		companion object Factory {
			fun from(declaration: KSPropertyDeclaration) = ValueAccessor(
				name = declaration.simpleName.asString(),
				returnType = declaration.type.resolve().toTypeName(),
				isMethod = false
			)

			fun from(declaration: KSFunctionDeclaration) = ValueAccessor(
				name = declaration.simpleName.asString(),
				returnType = declaration.returnType.let(::checkNotNull).resolve().toTypeName(),
				isMethod = true,
			)
		}
	}

	data class FactoryAccessor(
		val name: String,
		val firstParameterType: TypeName,
		val isConstructor: Boolean,
	) {
		companion object Factory {
			fun from(declaration: KSFunctionDeclaration) = FactoryAccessor(
				name = declaration.simpleName.asString(),
				firstParameterType = declaration.parameters.first().type.resolve().toTypeName(),
				isConstructor = declaration.isConstructor()
			)
		}
	}
}

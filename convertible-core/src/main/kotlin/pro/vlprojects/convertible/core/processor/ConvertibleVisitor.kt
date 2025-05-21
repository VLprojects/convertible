package pro.vlprojects.convertible.core.processor

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import pro.vlprojects.convertible.core.annotation.Convertible
import pro.vlprojects.convertible.core.annotation.ConvertibleFactory
import pro.vlprojects.convertible.core.annotation.ConvertibleValue
import pro.vlprojects.convertible.core.definition.ConvertibleDefinition
import pro.vlprojects.convertible.core.definition.ConvertibleDefinition.FactoryAccessor
import pro.vlprojects.convertible.core.definition.ConvertibleDefinition.ValueAccessor
import kotlin.reflect.KClass

class ConvertibleVisitor(
	private val definitions: MutableList<ConvertibleDefinition>,
) : KSVisitorVoid() {

	override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

		val className = classDeclaration.toClassName()
		val convertibles = classDeclaration
			.annotations
			.filter { it.isOf(Convertible::class) }
			.toList()

		check(convertibles.isNotEmpty()) { "No @Convertible annotations on class $className" }

		convertibles.forEach { annotation ->
			val nullable = annotation.getArgument<Boolean>("nullable").let(::checkNotNull)
			val scopes = annotation.getScopes()

			check(scopes.isNotEmpty()) { "At least one scope must be specified for @Convertible in $className" }

			scopes.forEach { scope ->
				val valueAccessor = resolveValueAccessor(classDeclaration, scope)
				val factoryAccessor = resolveFactoryAccessor(classDeclaration, valueAccessor.returnType, scope)

				val definition = ConvertibleDefinition(
					objectClassName = className,
					source = classDeclaration.containingFile.let(::checkNotNull),
					scope = scope,
					nullable = nullable,
					valueAccessor = valueAccessor,
					factoryAccessor = factoryAccessor,
				)

				definitions.add(definition)
			}
		}
	}

	private fun resolveValueAccessor(declaration: KSClassDeclaration, scope: String) : ValueAccessor {

		val className = declaration.simpleName.asString()

		val properties = declaration
			.getAllProperties()
			.filter { it.hasAnnotationWithScope(ConvertibleValue::class, scope) }
			.toList()

		val methods = declaration
			.getAllFunctions()
			.filter { it.hasAnnotationWithScope(ConvertibleValue::class, scope) }
			.toList()

		check(properties.size + methods.size <= 1) { "Multiple @ConvertibleValue annotated members for scope: $scope found in class $className" }

		if (methods.isNotEmpty()) {
			val method = methods.first()
			val methodName = method.simpleName.asString()
			val returnType = method.returnType?.resolve()

			check(returnType != null) { "Return type of method $className.$methodName is null" }
			check(!returnType.isMarkedNullable) { "Return type of $className.$methodName must NOT be nullable" }
			check(method.isPublic()) { "Method $className.$methodName must be public" }
			check(method.parameters.all { it.hasDefault }) { "Method $className.$methodName must have default params only" }

			return ValueAccessor.Factory.from(method)
		}

		if (properties.isNotEmpty()) {
			val property = properties.first()
			val propertyName = property.simpleName.asString()
			val type = property.type.resolve()

			check(property.isPublic()) { "Property $className.$propertyName must be public" }
			check(!type.isMarkedNullable) { "Type of $className.$propertyName must NOT be nullable" }

			return ValueAccessor.Factory.from(property)
		}

		// Fallback: use first public property as ValueAccessor
		val fallbackProperty = declaration.getAllProperties().firstOrNull { it.isPublic() }

		check(fallbackProperty != null) { "No public property found in $className to use as fallback ConvertibleValue for scope $scope" }
		check(!fallbackProperty.type.resolve().isMarkedNullable) { "Fallback property $className.$fallbackProperty must NOT be nullable" }

		return ValueAccessor.Factory.from(fallbackProperty)
	}

	private fun resolveFactoryAccessor(
		declaration: KSClassDeclaration,
		argumentType: TypeName,
		scope: String,
	) : FactoryAccessor {

		val className = declaration.simpleName.asString()

		val methods = declaration
			.declarations
			.filterIsInstance<KSClassDeclaration>()
			.firstOrNull { it.isCompanionObject }
			?.getAllFunctions()
			?.filter { it.hasAnnotationWithScope(ConvertibleFactory::class, scope) }
			?.toList()
			?: emptyList()

		check(methods.size <= 1) { "Multiple @ConvertibleFactory annotated methods for scope: $scope found in class $className" }

		if (methods.isNotEmpty()) {
			val method = methods.first()
			val methodName = method.simpleName.asString()
			val arguments = method
				.parameters
				.filter { it.type.resolve().toTypeName() == argumentType }

			check(method.isPublic()) { "Method $className.$methodName is not public" }
			check(arguments.size == 1) { "Exactly one argument of type $argumentType expected in method $className.$methodName" }

			return FactoryAccessor.Factory.from(method)
		}

		// Fallback: use public constructor as FactoryAccessor
		val constructor = declaration.getConstructors().filter { it.isPublic() }.firstOrNull()
		val constructorArguments = constructor
			?.parameters
			?.filter { it.type.resolve().toTypeName() == argumentType }

		check(constructor != null) { "No public constructor found in $className" }
		check(constructorArguments?.size == 1) { "Exactly one argument of type $argumentType expected in constructor $className" }

		return FactoryAccessor.Factory.from(constructor)
	}

	private fun KSAnnotated.hasAnnotationWithScope(annotation: KClass<*>, scope: String) = annotations
		.filter { it.isOf(annotation) }
		.flatMap { it.getScopes() }
		.contains(scope)

	private fun KSAnnotation.getScopes() = getArgument<List<*>>("scopes")
		?.filterIsInstance<String>()
		?: emptyList()

	private fun KSAnnotation.isOf(annotation: KClass<*>) = shortName.asString() == annotation.simpleName

	private fun KSDeclaration.isPublic() =
		modifiers.contains(Modifier.PUBLIC) || modifiers.none { it == Modifier.PRIVATE || it == Modifier.PROTECTED || it == Modifier.INTERNAL }

	private inline fun <reified T> KSAnnotation.getArgument(name: String): T? = arguments
		.firstOrNull { it.name?.asString() == name }
		?.value as? T
}

package pro.vlprojects.convertible.core.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class ConvertibleValue(
	val scopes: Array<String> = [],
)

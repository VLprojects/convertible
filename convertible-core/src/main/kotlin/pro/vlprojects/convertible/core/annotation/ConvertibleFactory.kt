package pro.vlprojects.convertible.core.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.SOURCE)
annotation class ConvertibleFactory(
	val scopes: Array<Scope> = [],
)

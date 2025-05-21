package pro.vlprojects.convertible.core.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@Repeatable
annotation class Convertible(
	val scopes: Array<String> = [],
	val nullable: Boolean = false,
	val prefix: String = "",
)

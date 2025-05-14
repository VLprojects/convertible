package pro.vlprojects.convertible.core.annotation

enum class Scope {
	JPA,
	MONGODB,
	JACKSON,
	MVC,
	;

	companion object Factory {
		fun from(name: String) = entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
	}
}

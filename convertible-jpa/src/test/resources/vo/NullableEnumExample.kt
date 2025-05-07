package com.example.vo

import pro.vlprojects.convertible.core.annotation.Convertible
import pro.vlprojects.convertible.core.annotation.ConvertibleFactory
import pro.vlprojects.convertible.core.annotation.ConvertibleValue
import pro.vlprojects.convertible.core.annotation.Scope

@Convertible(scopes = [Scope.JPA], nullable = true)
enum class NullableEnumExample {
	FIRST,
	SECOND,
	;

	@ConvertibleValue(scopes = [Scope.JPA])
	fun getValue() = name.lowercase()

	companion object Factory {
		@ConvertibleFactory(scopes = [Scope.JPA])
		fun from(input: String) = entries.first { it.name.equals(input, ignoreCase = true) }
	}
}

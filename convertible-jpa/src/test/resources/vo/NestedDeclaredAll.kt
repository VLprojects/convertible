package com.example.vo

import pro.vlprojects.convertible.core.annotation.Convertible
import pro.vlprojects.convertible.core.annotation.ConvertibleFactory
import pro.vlprojects.convertible.core.annotation.ConvertibleValue
import pro.vlprojects.convertible.core.annotation.Scope

data class NestedDeclaredAll(
	val nested: SubNestedDeclaredAll,
) {
	@Convertible(scopes = [Scope.JPA])
	data class SubNestedDeclaredAll(val raw: String) {

		@ConvertibleValue(scopes = [Scope.JPA])
		val value = raw

		companion object Factory {
			@ConvertibleFactory(scopes = [Scope.JPA])
			fun from(input: String) = SubNestedDeclaredAll(input)
		}
	}
}

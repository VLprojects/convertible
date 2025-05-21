package com.example.vo

import pro.vlprojects.convertible.core.annotation.Convertible
import pro.vlprojects.convertible.core.annotation.Scope

data class NestedFallbackAll(
	val nested: SubNestedFallbackAll,
) {
	@Convertible(scopes = [Scope.JPA])
	data class SubNestedFallbackAll(val raw: String)
}

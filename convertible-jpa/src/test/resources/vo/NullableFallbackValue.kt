package com.example.vo

import pro.vlprojects.convertible.core.annotation.Convertible
import pro.vlprojects.convertible.core.annotation.ConvertibleFactory
import pro.vlprojects.convertible.core.annotation.Scope
import java.util.UUID

@Convertible(scopes = [Scope.JPA], nullable = true)
data class NullableFallbackValue(val raw: UUID) {
	companion object Factory {
		@ConvertibleFactory(scopes = [Scope.JPA])
		fun from(input: UUID) = NullableFallbackValue(input)
	}
}

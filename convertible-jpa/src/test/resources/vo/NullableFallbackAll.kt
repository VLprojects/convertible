package com.example.vo

import pro.vlprojects.convertible.core.annotation.Convertible
import pro.vlprojects.convertible.core.annotation.Scope
import java.util.UUID

@Convertible(scopes = [Scope.JPA], nullable = true)
data class NullableFallbackAll(val raw: UUID)

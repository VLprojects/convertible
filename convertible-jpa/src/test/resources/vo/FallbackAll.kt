package com.example.vo

import pro.vlprojects.convertible.core.annotation.Convertible
import pro.vlprojects.convertible.core.annotation.Scope
import java.util.UUID

@Convertible(scopes = [Scope.JPA])
data class FallbackAll(val raw: UUID)

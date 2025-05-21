# Convertible

**Convertible** is a Kotlin Symbol Processor (KSP) library that automatically generates 
type-safe converters for your custom types. It eliminates the need to manually write converters for 
frameworks and modules like JPA, Jackson, Spring Data MongoDB, and Spring MVC by generating 
them at compile time.

When you define value classes, data wrappers, or enums, you often need to integrate them with frameworks 
that expect primitive types. Writing boilerplate `Converter`, `Serializer`, or `Mapper` classes for each of them
is repetitive, error-prone, and clutters your codebase.

**Convertible** solves this problem by using a single annotation and inferring everything needed 
to generate converters for the targets you use — while remaining fully type-safe, pluggable, and easy to extend.

---

## Features

- Annotation-driven with smart defaults
- Supports `data class`, `value class`, `enum class`, companion factories
- Handles nullable fields correctly
- Modular architecture — use only the targets (scopes) you need

## Installation

For Gradle with Kotlin DSL:

```kotlin
plugins {
	id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

dependencies { 
	implementation("pro.vlprojects:convertible-core:0.1.0")
	implementation("pro.vlprojects:convertible-jpa:0.1.0") // or other scopes
	ksp("pro.vlprojects:convertible-jpa:0.1.0")
}
```

## Usage

1. Define your VO class
```kotlin
@Convertible(scopes = [Scope.JPA])
data class UserId(val raw: String)
```

2. Use in entity
```kotlin
@Entity
class User(
	@Id lateinit var id: UserId
)
```

3. Auto-generated converter
```kotlin
@Component(value = "jpa.UserIdConverter")
@Converter(autoApply = true)
class UserIdConverter : AttributeConverter<UserId, String> {
	override fun convertToDatabaseColumn(attribute: UserId): String = attribute.raw
	override fun convertToEntityAttribute(dbData: String): UserId = UserId(dbData)
}
```

## Supported Targets (Scopes)
| Scope     | Description                   | Module                            |
|-----------|-------------------------------|-----------------------------------|
| `JPA`     | JPA `AttributeConverter`      | `convertible-jpa`                 |
| `JACKSON` | Jackson (de)serializer module | `convertible-jackson` *(planned)* |
| `MONGODB` | MongoDB custom converters     | `convertible-mongo` *(planned)*   |
| `MVC`     | Web argument/resolver support | `convertible-mvc` *(planned)*     |



## ⚠️ Constraints

- Classes must be `public` and not inner/nested.
- `@Convertible` supports only types with exactly one extractable primitive value.
- Companion factories must be annotated with `@ConvertibleFactory` and must be `public static` or in `companion object`.
- Only one `@ConvertibleValue` or eligible getter per type is allowed.
- No runtime reflection is used — converters are fully generated at compile-time.

## License

This project is licensed under the MIT License

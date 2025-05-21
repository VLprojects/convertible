package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class NestedJpaConverter : AttributeConverter<Base.Nested, String> {
  override fun convertToDatabaseColumn(attribute: Base.Nested): String = attribute.value

  override fun convertToEntityAttribute(source: String): Base.Nested = source.let(Base.Nested::from)
}

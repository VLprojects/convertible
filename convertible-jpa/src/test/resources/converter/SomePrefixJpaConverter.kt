package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class SomePrefixJpaConverter : AttributeConverter<Prefix, String> {
  override fun convertToDatabaseColumn(attribute: Prefix): String = attribute.value

  override fun convertToEntityAttribute(source: String): Prefix = source.let(Prefix::from)
}

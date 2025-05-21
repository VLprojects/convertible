package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class FallbackFactoryJpaConverter : AttributeConverter<FallbackFactory, String> {
  override fun convertToDatabaseColumn(attribute: FallbackFactory): String = attribute.value

  override fun convertToEntityAttribute(source: String): FallbackFactory = source.let(::FallbackFactory)
}

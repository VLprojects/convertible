package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class NullableFallbackFactoryJpaConverter : AttributeConverter<NullableFallbackFactory?, String?> {
  override fun convertToDatabaseColumn(attribute: NullableFallbackFactory?): String? = attribute?.value

  override fun convertToEntityAttribute(source: String?): NullableFallbackFactory? = source?.let(::NullableFallbackFactory)
}

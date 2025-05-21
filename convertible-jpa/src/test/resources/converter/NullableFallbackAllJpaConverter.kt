package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.UUID
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class NullableFallbackAllJpaConverter : AttributeConverter<NullableFallbackAll?, UUID?> {
  override fun convertToDatabaseColumn(attribute: NullableFallbackAll?): UUID? = attribute?.raw

  override fun convertToEntityAttribute(source: UUID?): NullableFallbackAll? = source?.let(::NullableFallbackAll)
}

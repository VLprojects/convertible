package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.UUID
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class FallbackValueJpaConverter : AttributeConverter<FallbackValue, UUID> {
  override fun convertToDatabaseColumn(attribute: FallbackValue): UUID = attribute.raw

  override fun convertToEntityAttribute(source: UUID): FallbackValue = source.let(FallbackValue::from)
}

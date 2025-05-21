package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.UUID
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class FallbackAllJpaConverter : AttributeConverter<FallbackAll, UUID> {
  override fun convertToDatabaseColumn(attribute: FallbackAll): UUID = attribute.raw

  override fun convertToEntityAttribute(source: UUID): FallbackAll = source.let(::FallbackAll)
}

package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class SubNestedFallbackAllJpaConverter : AttributeConverter<NestedFallbackAll.SubNestedFallbackAll, String> {
  override fun convertToDatabaseColumn(attribute: NestedFallbackAll.SubNestedFallbackAll): String = attribute.raw

  override fun convertToEntityAttribute(source: String): NestedFallbackAll.SubNestedFallbackAll = source.let(NestedFallbackAll::SubNestedFallbackAll)
}

package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class NullableEnumExampleJpaConverter : AttributeConverter<NullableEnumExample?, String?> {
  override fun convertToDatabaseColumn(attribute: NullableEnumExample?): String? = attribute?.getValue()

  override fun convertToEntityAttribute(source: String?): NullableEnumExample? = source?.let(NullableEnumExample::from)
}

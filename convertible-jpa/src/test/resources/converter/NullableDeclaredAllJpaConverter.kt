package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class NullableDeclaredAllJpaConverter : AttributeConverter<NullableDeclaredAll?, String?> {
  override fun convertToDatabaseColumn(attribute: NullableDeclaredAll?): String? = attribute?.value

  override fun convertToEntityAttribute(source: String?): NullableDeclaredAll? = source?.let(NullableDeclaredAll::from)
}

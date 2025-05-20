package com.example.vo.jpa

import com.example.vo.NullableDeclaredAll
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component(value = "jpa.NullableDeclaredAllConverter")
@Converter(autoApply = true)
public class NullableDeclaredAllConverter : AttributeConverter<NullableDeclaredAll?, String?> {
  override fun convertToDatabaseColumn(attribute: NullableDeclaredAll?): String? = attribute?.value

  override fun convertToEntityAttribute(source: String?): NullableDeclaredAll? = source?.let(NullableDeclaredAll::from)
}

package com.example.vo.jpa

import com.example.vo.DeclaredAll
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class DeclaredAllConverter : AttributeConverter<DeclaredAll, String> {
  override fun convertToDatabaseColumn(attribute: DeclaredAll): String = attribute.value

  override fun convertToEntityAttribute(source: String): DeclaredAll = source.let(DeclaredAll::from)
}

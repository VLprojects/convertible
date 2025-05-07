package com.example.vo.jpa

import com.example.vo.EnumExample
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class EnumExampleConverter : AttributeConverter<EnumExample, String> {
  override fun convertToDatabaseColumn(attribute: EnumExample): String = attribute.getValue()

  override fun convertToEntityAttribute(source: String): EnumExample = source.let(EnumExample::from)
}

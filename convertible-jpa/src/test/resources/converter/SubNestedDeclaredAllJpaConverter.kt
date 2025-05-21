package com.example.vo

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class SubNestedDeclaredAllJpaConverter : AttributeConverter<NestedDeclaredAll.SubNestedDeclaredAll, String> {
  override fun convertToDatabaseColumn(attribute: NestedDeclaredAll.SubNestedDeclaredAll): String = attribute.value

  override fun convertToEntityAttribute(source: String): NestedDeclaredAll.SubNestedDeclaredAll = source.let(NestedDeclaredAll.SubNestedDeclaredAll::from)
}

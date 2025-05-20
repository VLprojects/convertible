package com.example.vo.jpa

import com.example.vo.NullableEnumExample
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.String
import org.springframework.stereotype.Component

@Component(value = "jpa.NullableEnumExampleConverter")
@Converter(autoApply = true)
public class NullableEnumExampleConverter : AttributeConverter<NullableEnumExample?, String?> {
  override fun convertToDatabaseColumn(attribute: NullableEnumExample?): String? = attribute?.getValue()

  override fun convertToEntityAttribute(source: String?): NullableEnumExample? = source?.let(NullableEnumExample::from)
}

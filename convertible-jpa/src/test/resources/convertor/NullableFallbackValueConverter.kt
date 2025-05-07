package com.example.vo.jpa

import com.example.vo.NullableFallbackValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.UUID
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class NullableFallbackValueConverter : AttributeConverter<NullableFallbackValue?, UUID?> {
  override fun convertToDatabaseColumn(attribute: NullableFallbackValue?): UUID? = attribute?.raw

  override fun convertToEntityAttribute(source: UUID?): NullableFallbackValue? = source?.let(NullableFallbackValue::from)
}

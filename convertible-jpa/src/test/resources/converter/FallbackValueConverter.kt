package com.example.vo.jpa

import com.example.vo.FallbackValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.UUID
import org.springframework.stereotype.Component

@Component(value = "jpa.FallbackValueConverter")
@Converter(autoApply = true)
public class FallbackValueConverter : AttributeConverter<FallbackValue, UUID> {
  override fun convertToDatabaseColumn(attribute: FallbackValue): UUID = attribute.raw

  override fun convertToEntityAttribute(source: UUID): FallbackValue = source.let(FallbackValue::from)
}

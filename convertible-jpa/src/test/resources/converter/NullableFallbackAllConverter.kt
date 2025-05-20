package com.example.vo.jpa

import com.example.vo.NullableFallbackAll
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.UUID
import org.springframework.stereotype.Component

@Component(value = "jpa.NullableFallbackAllConverter")
@Converter(autoApply = true)
public class NullableFallbackAllConverter : AttributeConverter<NullableFallbackAll?, UUID?> {
  override fun convertToDatabaseColumn(attribute: NullableFallbackAll?): UUID? = attribute?.raw

  override fun convertToEntityAttribute(source: UUID?): NullableFallbackAll? = source?.let(::NullableFallbackAll)
}

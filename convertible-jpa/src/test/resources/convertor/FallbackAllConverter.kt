package com.example.vo.jpa

import com.example.vo.FallbackAll
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.UUID
import org.springframework.stereotype.Component

@Component
@Converter(autoApply = true)
public class FallbackAllConverter : AttributeConverter<FallbackAll, UUID> {
  override fun convertToDatabaseColumn(attribute: FallbackAll): UUID = attribute.raw

  override fun convertToEntityAttribute(source: UUID): FallbackAll = source.let(::FallbackAll)
}

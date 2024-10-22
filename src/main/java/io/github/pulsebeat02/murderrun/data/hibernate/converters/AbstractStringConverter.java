package io.github.pulsebeat02.murderrun.data.hibernate.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jetbrains.annotations.Nullable;

@Converter(autoApply = true) // add annotation all subclasses
public abstract class AbstractStringConverter<T> implements AttributeConverter<T, String> {

  @Override
  public @Nullable String convertToDatabaseColumn(final T data) {
    if (data == null) {
      return null;
    }
    return data.toString();
  }

  @Override
  public @Nullable T convertToEntityAttribute(final String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return null;
    }
    return this.fromString(dbData);
  }

  public abstract T fromString(final String dbData);
}

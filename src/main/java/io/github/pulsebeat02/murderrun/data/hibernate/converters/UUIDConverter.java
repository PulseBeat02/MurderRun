package io.github.pulsebeat02.murderrun.data.hibernate.converters;

import jakarta.persistence.Converter;
import java.util.UUID;

@Converter(autoApply = true)
public final class UUIDConverter extends AbstractStringConverter<UUID> {

  @Override
  public UUID fromString(final String dbData) {
    return UUID.fromString(dbData);
  }
}

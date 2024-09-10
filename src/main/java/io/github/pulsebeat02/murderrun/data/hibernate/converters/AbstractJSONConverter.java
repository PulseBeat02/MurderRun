package io.github.pulsebeat02.murderrun.data.hibernate.converters;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.github.pulsebeat02.murderrun.gson.GsonProvider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.lang.reflect.Type;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all") // not dealing with this checker bs
@Converter(autoApply = true) // add annotation all subclasses
public abstract class AbstractJSONConverter<T> implements AttributeConverter<T, String> {

  private final TypeToken<T> token = new TypeToken<>(this.getClass()) {};

  @Override
  public @Nullable String convertToDatabaseColumn(final T data) {

    if (data == null) {
      return null;
    }

    // use json to serialize and deserialize data
    final Gson gson = GsonProvider.getGson();
    return gson.toJson(data);
  }

  @Override
  public @Nullable T convertToEntityAttribute(final String dbData) {

    if (dbData == null || dbData.isEmpty()) {
      return null;
    }

    final Gson gson = GsonProvider.getGson();
    final Type type = this.token.getType();
    return gson.fromJson(dbData, type);
  }
}

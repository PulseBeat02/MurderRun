package io.github.pulsebeat02.murderrun.data;

import java.util.Map;

public enum RelationalDataMethod {
  JSON,
  SQL;

  public static final Map<String, RelationalDataMethod> LOOKUP_TABLE = Map.of("JSON", JSON, "SQL", SQL);

  public static RelationalDataMethod fromString(final String locale) {
    final String upper = locale.toUpperCase();
    return LOOKUP_TABLE.getOrDefault(upper, JSON);
  }
}

package io.github.pulsebeat02.murderrun.locale;

import java.util.Map;

public enum Locale {
  EN,
  ZH_CN;

  private static final Map<String, Locale> LOOKUP_TABLE = Map.of("EN", EN, "ZH_CN", ZH_CN);

  public static Locale fromString(final String locale) {
    final String upper = locale.toUpperCase();
    return LOOKUP_TABLE.getOrDefault(upper, EN);
  }
}

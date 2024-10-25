package io.github.pulsebeat02.murderrun.resourcepack.provider;

import java.util.Map;

public enum ProviderMethod {
  MC_PACK_HOSTING,
  LOCALLY_HOSTED_DAEMON,
  ON_SERVER;

  private static final Map<String, ProviderMethod> LOOKUP_TABLE = Map.of(
    "MC_PACK_HOSTING",
    MC_PACK_HOSTING,
    "LOCALLY_HOSTED_DAEMON",
    LOCALLY_HOSTED_DAEMON,
    "ON_SERVER",
    ON_SERVER
  );

  public static ProviderMethod fromString(final String locale) {
    final String upper = locale.toUpperCase();
    return LOOKUP_TABLE.getOrDefault(upper, ProviderMethod.MC_PACK_HOSTING);
  }
}

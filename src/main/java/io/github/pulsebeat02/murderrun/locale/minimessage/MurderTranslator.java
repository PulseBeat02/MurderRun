package io.github.pulsebeat02.murderrun.locale.minimessage;

import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MurderTranslator extends MiniMessageTranslator {

  private final Key key;
  private final TranslationRegistry registry;

  public MurderTranslator(final Key key, final TranslationRegistry registry) {
    this.key = key;
    this.registry = registry;
  }

  @Override
  protected @Nullable String getMiniMessageString(
      @NotNull final String key, @NotNull final Locale locale) {
    return this.registry.translate(key, locale).toPattern();
  }

  @Override
  public @NotNull Key name() {
    return this.key;
  }
}

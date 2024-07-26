package io.github.pulsebeat02.murderrun.locale.minimessage;

import java.text.MessageFormat;
import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class MurderTranslator extends MiniMessageTranslator {

  private final Key key;
  private final TranslationRegistry registry;

  public MurderTranslator(final Key key, final TranslationRegistry registry) {
    this.key = key;
    this.registry = registry;
  }

  @Override
  protected String getMiniMessageString(final String key, final Locale locale) {
    final MessageFormat format = this.registry.translate(key, locale);
    return format == null ? "" : format.toPattern();
  }

  @Override
  public @NonNull Key name() {
    return this.key;
  }
}

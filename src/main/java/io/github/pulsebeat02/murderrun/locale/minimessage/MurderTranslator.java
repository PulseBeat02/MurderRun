package io.github.pulsebeat02.murderrun.locale.minimessage;

import java.util.Locale;
import java.util.ResourceBundle;
import net.kyori.adventure.key.Key;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class MurderTranslator extends MiniMessageTranslator {

  private final Key key;
  private final ResourceBundle bundle;

  public MurderTranslator(final Key key, final ResourceBundle bundle) {
    this.key = key;
    this.bundle = bundle;
  }

  @Override
  protected String getMiniMessageString(final String key, final Locale locale) {
    return this.bundle.getString(key);
  }

  @Override
  public @NonNull Key name() {
    return this.key;
  }
}

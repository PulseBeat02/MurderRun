package io.github.pulsebeat02.murderrun.locale;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.locale.minimessage.MurderTranslator;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class TranslationManager {

  private static final java.util.Locale DEFAULT_LOCALE = Locale.ENGLISH;
  private static final Key ADVENTURE_KEY = Key.key("murder_run", "main");

  private final ResourceBundle bundle;
  private final MurderTranslator translator;

  public TranslationManager() {
    this.bundle = this.getBundle();
    this.translator = new MurderTranslator(ADVENTURE_KEY, this.bundle);
    GlobalTranslator.translator().addSource(this.translator);
  }

  private PropertyResourceBundle getBundle(@UnderInitialization TranslationManager this) {
    try (final Reader reader =
        ResourceUtils.getResourceAsReader("locale/murder_run_en.properties")) {
      return new PropertyResourceBundle(reader);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getProperty(final String key) {
    return this.bundle.getString(key);
  }

  public Component render(final TranslatableComponent component) {
    final Component translated = this.translator.translate(component, DEFAULT_LOCALE);
    return translated == null ? empty() : translated;
  }
}

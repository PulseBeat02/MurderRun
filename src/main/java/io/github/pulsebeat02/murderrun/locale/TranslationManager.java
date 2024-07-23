package io.github.pulsebeat02.murderrun.locale;

import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;

public final class TranslationManager {

  private static final java.util.Locale DEFAULT_LOCALE = Locale.ENGLISH;
  private static final Key ADVENTURE_KEY = Key.key("murder_run", "main");

  private final TranslationRegistry registry;

  public TranslationManager() {
    this.registry = TranslationRegistry.create(ADVENTURE_KEY);
    this.registry.defaultLocale(DEFAULT_LOCALE);
    this.registerTranslations();
  }

  private void registerTranslations() {
    this.registerLocale();
    this.addGlobalRegistry();
  }

  private void addGlobalRegistry() {
    GlobalTranslator.translator().addSource(this.registry);
  }

  private void registerLocale() {
    final ResourceBundle bundle = this.getBundle();
    this.registry.registerAll(DEFAULT_LOCALE, bundle, false);
  }

  private PropertyResourceBundle getBundle() {
    try (final Reader reader =
        ResourceUtils.getResourceAsReader("locale/murder_run_en.properties")) {
      return new PropertyResourceBundle(reader);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Component render(final Component component) {
    return GlobalTranslator.render(component, DEFAULT_LOCALE);
  }
}

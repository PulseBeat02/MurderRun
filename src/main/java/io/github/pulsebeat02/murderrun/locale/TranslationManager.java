package io.github.pulsebeat02.murderrun.locale;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.locale.minimessage.MurderTranslator;
import io.github.pulsebeat02.murderrun.utils.CursedPluginInstanceRetrieverOnlyForUtilityClassesProvider;
import io.github.pulsebeat02.murderrun.utils.Keys;
import io.github.pulsebeat02.murderrun.utils.ResourceUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class TranslationManager {

  private static final java.util.Locale DEFAULT_LOCALE = Locale.ENGLISH;
  private static final Key ADVENTURE_KEY = key(Keys.NAMESPACE, "main");
  private static final String PROPERTIES_PATH = "locale/murder_run_en.properties";
  private static final Plugin PLUGIN =
      CursedPluginInstanceRetrieverOnlyForUtilityClassesProvider.retrievePluginInstance();

  private final ResourceBundle bundle;
  private final MurderTranslator translator;

  public TranslationManager() {
    this.bundle = this.getBundle();
    this.translator = new MurderTranslator(ADVENTURE_KEY, this.bundle);
  }

  private PropertyResourceBundle getBundle(@UnderInitialization TranslationManager this) {
    final Path resource = this.copyResourceToFolder();
    try (final Reader reader = Files.newBufferedReader(resource)) {
      return new PropertyResourceBundle(reader);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private Path copyResourceToFolder(@UnderInitialization TranslationManager this) {
    final Path folder = PLUGIN.getDataFolder().toPath();
    final Path locale = folder.resolve(PROPERTIES_PATH);
    if (Files.notExists(locale)) {
      ResourceUtils.createFile(locale);
      copyLocaleProperties(locale);
    }
    return locale;
  }

  private static void copyLocaleProperties(final Path locale) {
    try (final InputStream stream = ResourceUtils.getResourceAsStream(PROPERTIES_PATH)) {
      Files.copy(stream, locale, StandardCopyOption.REPLACE_EXISTING);
    } catch (final IOException e) {
      throw new AssertionError(e);
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

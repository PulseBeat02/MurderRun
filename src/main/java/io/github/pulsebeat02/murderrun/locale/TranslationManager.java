/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.locale;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.data.yaml.PluginDataConfigurationMapper;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.minimessage.PluginTranslator;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class TranslationManager {

  private static final Locale DEFAULT_LOCALE = Locale.getDefault();
  private static final Key ADVENTURE_KEY = key(Keys.NAMESPACE, "main");

  private final String propertiesPath;
  private final ResourceBundle bundle;
  private final PluginTranslator translator;

  public TranslationManager() {
    final MurderRun plugin = JavaPlugin.getPlugin(MurderRun.class);
    this.propertiesPath = this.getPropertiesPath(plugin);
    this.bundle = this.getBundle(this.propertiesPath);
    this.translator = new PluginTranslator(ADVENTURE_KEY, this.bundle);
  }

  private String getPropertiesPath(@UnderInitialization TranslationManager this, final MurderRun plugin) {
    final PluginDataConfigurationMapper mapper = plugin.getConfiguration();
    final io.github.pulsebeat02.murderrun.locale.Locale locale = mapper.getLocale();
    final String name = locale.name();
    final String lower = name.toLowerCase();
    return "locale/murderrun_%s.properties".formatted(lower);
  }

  private PropertyResourceBundle getBundle(@UnderInitialization TranslationManager this, final String propertiesPath) {
    final Path resource = this.copyResourceToFolder(propertiesPath);
    try (final Reader reader = Files.newBufferedReader(resource)) {
      return new PropertyResourceBundle(reader);
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private Path copyResourceToFolder(@UnderInitialization TranslationManager this, final String propertiesPath) {
    final Path folder = IOUtils.getPluginDataFolderPath();
    final Path locale = folder.resolve(propertiesPath);
    if (Files.notExists(locale)) {
      IOUtils.createFile(locale);
      this.copyLocaleProperties(propertiesPath, locale);
    }
    return locale;
  }

  private void copyLocaleProperties(@UnderInitialization TranslationManager this, final String propertiesPath, final Path locale) {
    try (final InputStream stream = IOUtils.getResourceAsStream(propertiesPath)) {
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

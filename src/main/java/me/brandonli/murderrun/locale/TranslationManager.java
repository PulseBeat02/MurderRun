/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.locale;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.text.Component.empty;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.data.dfu.PropertyFixerManager;
import me.brandonli.murderrun.data.yaml.PluginDataConfigurationMapper;
import me.brandonli.murderrun.locale.minimessage.PluginTranslator;
import me.brandonli.murderrun.utils.IOUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
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
    final me.brandonli.murderrun.locale.Locale locale = mapper.getLocale();
    final String name = locale.name();
    final String lower = name.toLowerCase();
    return "locale/murderrun_%s.properties".formatted(lower);
  }

  private ResourceBundle getBundle(@UnderInitialization TranslationManager this, final String propertiesPath) {
    final Path resource = this.copyResourceToFolder(propertiesPath);
    try (final Reader reader = Files.newBufferedReader(resource)) {
      final ResourceBundle bundle = new PropertyResourceBundle(reader);
      final PropertyFixerManager fixer = new PropertyFixerManager();
      fixer.registerLocalePropertiesFixer();
      fixer.applyFixersUpTo(bundle);
      return bundle;
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

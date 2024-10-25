package io.github.pulsebeat02.murderrun.locale;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.minimessage.PluginTranslator;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class TranslationManager {

  private static final Key ADVENTURE_KEY = key(Keys.NAMESPACE, "main");
  private static final String CONFIG_PATH = "config.yml";
  private static final String LANGUAGE_FOLDER = "languages/";
  private ResourceBundle bundle;
  private PluginTranslator translator;
  // 语言代码到属性文件名的映射
  private static final Map<String, String> LANGUAGE_FILE_MAP = new HashMap<>();

  static {
    LANGUAGE_FILE_MAP.put("en", "murderrun_en.properties");
    LANGUAGE_FILE_MAP.put("zh_cn", "murderrun_zh_cn.properties");
    // 添加更多的映射关系
  }

  public TranslationManager() {
    // 初始化成员变量为默认值
    this.bundle = ResourceBundle.getBundle("murderrun_en.properties", Locale.getDefault());
    this.translator = new PluginTranslator(ADVENTURE_KEY, this.bundle);
    // 加载配置
    loadConfig();
  }

  private void loadConfig() {
    final Path configPath = IOUtils.getPluginDataFolderPath().resolve(CONFIG_PATH);
    FileConfiguration config = new YamlConfiguration();
    try {
      if (Files.notExists(configPath)) {
        Files.createFile(configPath); // 如果文件不存在，则创建一个新文件
        config.save(configPath.toFile()); // 保存默认配置
      }
      config.load(configPath.toFile());
      final String languageCode = config.getString("languages", "en"); // 默认为英文
      // 确保 languageCode 不是 null
      if (languageCode != null) {
        final String propertiesFile = LANGUAGE_FILE_MAP.getOrDefault(languageCode, "murderrun_en.properties");
        final Locale locale = Locale.forLanguageTag(languageCode);
        this.bundle = loadBundle(locale, propertiesFile);
        this.translator = new PluginTranslator(ADVENTURE_KEY, this.bundle);
      }
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  private ResourceBundle loadBundle(Locale locale, String propertiesFile) {
    final Path resourcePath = IOUtils.getPluginDataFolderPath().resolve(LANGUAGE_FOLDER + propertiesFile);
    try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(resourcePath), StandardCharsets.UTF_8)) {
      return new PropertyResourceBundle(reader);
    } catch (IOException e) {
      e.printStackTrace();
      // 返回一个默认的资源包或者抛出异常
      return ResourceBundle.getBundle("murderrun_en.properties", locale);
    }
  }

  // 获取属性文件中的值
  public String getProperty(String key) {
    return this.bundle.getString(key);
  }

  // 渲染可翻译组件，如果翻译失败则返回一个空的组件
  public Component render(TranslatableComponent component) {
    final Component translated = this.translator.translate(component, Locale.getDefault());
    return translated != null ? translated : empty();
  }
}

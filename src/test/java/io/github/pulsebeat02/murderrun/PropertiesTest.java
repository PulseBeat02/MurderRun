/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public final class PropertiesTest {

  public static void main(final String[] args) throws IOException {
    final Properties enUs = loadProperties("src/main/resources/locale/murderrun_en_us.properties");
    final Properties zhCn = loadProperties("src/main/resources/locale/murderrun_zh_cn.properties");
    final Properties zhHk = loadProperties("src/main/resources/locale/murderrun_zh_hk.properties");

    final Set<String> enUsKeys = enUs.stringPropertyNames();
    final Set<String> zhCnKeys = zhCn.stringPropertyNames();
    final Set<String> zhHkKeys = zhHk.stringPropertyNames();

    final Set<String> missingZhCn = enUsKeys.stream().filter(key -> !zhCnKeys.contains(key)).collect(Collectors.toSet());
    final Set<String> missingZhHk = enUsKeys.stream().filter(key -> !zhHkKeys.contains(key)).collect(Collectors.toSet());

    System.out.println("Missing keys in murderrun_zh_cn.properties: " + missingZhCn);
    System.out.println("Missing keys in murderrun_zh_hk.properties: " + missingZhHk);
  }

  private static Properties loadProperties(final String path) throws IOException {
    final Properties properties = new Properties();
    try (final var reader = Files.newBufferedReader(Path.of(path))) {
      properties.load(reader);
    }
    return properties;
  }
}

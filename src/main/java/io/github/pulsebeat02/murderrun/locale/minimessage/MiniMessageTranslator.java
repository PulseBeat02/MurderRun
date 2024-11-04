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
package io.github.pulsebeat02.murderrun.locale.minimessage;

import static java.util.Objects.requireNonNull;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.Translator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class MiniMessageTranslator implements Translator {

  private static final PlainTextComponentSerializer PLAIN_TEST_SERIALIZER = PlainTextComponentSerializer.plainText();
  private static final Set<String> SPECIAL_PLACEHOLDERS = Set.of("$GAME_ID$");

  private final MiniMessage miniMessage;

  public MiniMessageTranslator() {
    this(MiniMessage.miniMessage());
  }

  public MiniMessageTranslator(final MiniMessage miniMessage) {
    this.miniMessage = miniMessage;
  }

  @Override
  public @Nullable MessageFormat translate(final @NonNull String key, final @NonNull Locale locale) {
    throw new UnsupportedOperationException();
  }

  @Override
  public @Nullable Component translate(final TranslatableComponent component, final @NonNull Locale locale) {
    final String key = component.key();
    final String miniMessageString = requireNonNull(this.getMiniMessageString(key, locale));
    final String content = this.checkIfSpecialString(miniMessageString, component);
    final List<? extends ComponentLike> args = component.arguments();
    final boolean empty = args.isEmpty();
    final MiniMessage parser = MiniMessage.miniMessage();
    final ArgumentTag tag = new ArgumentTag(args);
    final Component resultingComponent = empty ? parser.deserialize(content) : parser.deserialize(content, tag);
    final List<Component> children = component.children();
    return children.isEmpty() ? resultingComponent : resultingComponent.children(children);
  }

  // replacing the tag inside the command argument
  private String checkIfSpecialString(final String value, final TranslatableComponent component) {
    final List<? extends ComponentLike> args = component.arguments();
    for (final String placeholder : SPECIAL_PLACEHOLDERS) {
      if (!value.contains(placeholder)) {
        continue;
      }
      final ComponentLike arg = args.getFirst();
      final Component comp = arg.asComponent();
      final String name = PLAIN_TEST_SERIALIZER.serialize(comp);
      return value.replace("$GAME_ID$", name);
    }
    return value;
  }

  protected abstract String getMiniMessageString(final String key, final Locale locale);
}

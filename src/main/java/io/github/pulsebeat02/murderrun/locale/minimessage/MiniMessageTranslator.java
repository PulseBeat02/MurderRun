package io.github.pulsebeat02.murderrun.locale.minimessage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.Translator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

public abstract class MiniMessageTranslator implements Translator {

  final MiniMessage miniMessage;

  public MiniMessageTranslator() {
    this(MiniMessage.miniMessage());
  }

  public MiniMessageTranslator(final MiniMessage miniMessage) {
    this.miniMessage = miniMessage;
  }

  @Override
  public @Nullable MessageFormat translate(
      final @NonNull  String key,  final @NonNull Locale locale) {
    return null;
  }

  @Override
  public @Nullable Component translate(
      final TranslatableComponent component, final @NonNull Locale locale) {

    final String miniMessageString = this.getMiniMessageString(component.key(), locale);
    if (miniMessageString == null) {
      return null;
    }

    final Component resultingComponent;
    if (component.arguments().isEmpty()) {
      resultingComponent = MiniMessage.miniMessage().deserialize(miniMessageString);
    } else {
      resultingComponent = MiniMessage.miniMessage()
          .deserialize(miniMessageString, new ArgumentTag(component.arguments()));
    }

    if (component.children().isEmpty()) {
      return resultingComponent;
    } else {
      return resultingComponent.children(component.children());
    }
  }

  protected abstract String getMiniMessageString(final String key, final Locale locale);
}

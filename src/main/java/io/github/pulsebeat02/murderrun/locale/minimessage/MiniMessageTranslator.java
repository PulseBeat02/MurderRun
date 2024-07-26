package io.github.pulsebeat02.murderrun.locale.minimessage;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MiniMessageTranslator implements Translator {

  final MiniMessage miniMessage;

  public MiniMessageTranslator() {
    this(MiniMessage.miniMessage());
  }

  public MiniMessageTranslator(final @NotNull MiniMessage miniMessage) {
    this.miniMessage = Objects.requireNonNull(miniMessage, "miniMessage");
  }

  protected abstract @Nullable String getMiniMessageString(
      final @NotNull String key, final @NotNull Locale locale);

  @Override
  public final @Nullable MessageFormat translate(
      final @NotNull String key, final @NotNull Locale locale) {
    return null;
  }

  @Override
  public @Nullable Component translate(
      final @NotNull TranslatableComponent component, final @NotNull Locale locale) {
    final String miniMessageString = this.getMiniMessageString(component.key(), locale);

    if (miniMessageString == null) {
      return null;
    }

    final Component resultingComponent;

    if (component.args().isEmpty()) {
      resultingComponent = MiniMessage.miniMessage().deserialize(miniMessageString);
    } else {
      resultingComponent =
          MiniMessage.miniMessage()
              .deserialize(miniMessageString, new ArgumentTag(component.args()));
    }

    if (component.children().isEmpty()) {
      return resultingComponent;
    } else {
      return resultingComponent.children(component.children());
    }
  }
}

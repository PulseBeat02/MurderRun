package io.github.pulsebeat02.murderrun.locale.minimessage;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.Translator;

import javax.annotation.Nullable;

public abstract class MiniMessageTranslator implements Translator {

  final MiniMessage miniMessage;

  public MiniMessageTranslator() {
    this(MiniMessage.miniMessage());
  }

  public MiniMessageTranslator(final MiniMessage miniMessage) {
    this.miniMessage = Objects.requireNonNull(miniMessage, "miniMessage");
  }

  protected abstract String getMiniMessageString(final String key, final Locale locale);

  @Override
  public @Nullable final MessageFormat translate(final String key, final Locale locale) {
    return null;
  }

  @Override
  public @Nullable Component translate(final TranslatableComponent component, final Locale locale) {
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

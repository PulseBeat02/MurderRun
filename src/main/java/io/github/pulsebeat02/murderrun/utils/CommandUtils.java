package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.locale.LocaleParent;
import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.translatable;

public final class CommandUtils {

  private CommandUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static String getCommandDescription(final String id) {
    final Component component = LocaleParent.MANAGER.render(translatable(id));
    return AdventureUtils.serializeComponentToLegacy(component);
  }
}

package io.github.pulsebeat02.murderrun.locale;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;

import java.util.List;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface LocaleParent {

  TranslationManager MANAGER = new TranslationManager();

  NullComponent<Sender> PREFIX =
      () -> text().color(RED).append(text('['), text("Murder Run", AQUA), text(']')).build();

  static NullComponent<Sender> error(final String key) {
    return () -> MANAGER.render(translatable(key, RED));
  }

  static NullComponent<Sender> colored(final String key, final NamedTextColor color) {
    return () -> translatable(key, color);
  }

  static <T> UniComponent<Sender, T> colored(
      final String key, final NamedTextColor color, final NamedTextColor argColor) {
    return argument ->
        MANAGER.render(translatable(key, color, text(argument.toString()).color(argColor)));
  }

  static <T, U> BiComponent<Sender, T, U> colored(
      final String key,
      final NamedTextColor color,
      final NamedTextColor argColor1,
      final NamedTextColor argColor2) {
    return (argument1, argument2) ->
        MANAGER.render(
            translatable(
                key,
                color,
                text(argument1.toString()).color(argColor1),
                text(argument2.toString()).color(argColor2)));
  }

  static <T, U> BiComponent<Sender, T, U> info(
      final String key, final Function<T, String> function1, final Function<U, String> function2) {
    return (argument1, argument2) ->
        info0(
            key,
            List.of(
                text(createFinalText(argument1, function1), AQUA),
                text(createFinalText(argument2, function2), AQUA)));
  }

  static Component info0(final String key, final List<Component> arguments) {
    return internal0(key, GOLD, arguments);
  }

  static <T> String createFinalText(final T argument, final Function<T, String> function) {
    return function == null ? argument.toString() : function.apply(argument);
  }

  static Component internal0(
      final String key, final NamedTextColor color, final List<Component> arguments) {
    return MANAGER.render(translatable(key, color, arguments));
  }

  static NullComponent<Sender> info(final String key) {
    return () -> format(translatable(key, GOLD));
  }

  static Component format(final Component message) {
    return MANAGER.render(join(separator(space()), PREFIX.build(), message));
  }

  static <T> UniComponent<Sender, T> info(final String key, final Function<T, String> function) {
    return argument -> format(info0(key, List.of(text(createFinalText(argument, function), AQUA))));
  }

  static <T, U, V> TriComponent<Sender, T, U, V> info(
      final String key,
      final Function<T, String> function1,
      final Function<U, String> function2,
      final Function<V, String> function3) {
    return (argument1, argument2, argument3) ->
        format(
            info0(
                key,
                List.of(
                    text(createFinalText(argument1, function1), AQUA),
                    text(createFinalText(argument2, function2), AQUA),
                    text(createFinalText(argument3, function3), AQUA))));
  }

  @FunctionalInterface
  interface NullComponent<S extends Sender> {

    default void send(final S sender) {
      sender.sendMessage(format(this.build()));
    }

    Component build();
  }

  @FunctionalInterface
  interface UniComponent<S extends Sender, A0> {

    default void send(final S sender, final A0 arg0) {
      sender.sendMessage(format(this.build(arg0)));
    }

    Component build(A0 arg0);
  }

  @FunctionalInterface
  interface BiComponent<S extends Sender, A0, A1> {

    default void send(final S sender, final A0 arg0, final A1 arg1) {
      sender.sendMessage(format(this.build(arg0, arg1)));
    }

    Component build(A0 arg0, A1 arg1);
  }

  @FunctionalInterface
  interface TriComponent<S extends Sender, A0, A1, A2> {

    default void send(final S sender, final A0 arg0, final A1 arg1, final A2 arg2) {
      sender.sendMessage(format(this.build(arg0, arg1, arg2)));
    }

    Component build(A0 arg0, A1 arg1, A2 arg2);
  }
}

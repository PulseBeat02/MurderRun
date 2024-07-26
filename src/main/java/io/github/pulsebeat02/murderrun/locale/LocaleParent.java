package io.github.pulsebeat02.murderrun.locale;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;

import java.util.function.Function;
import net.kyori.adventure.text.Component;

public interface LocaleParent {

  TranslationManager MANAGER = new TranslationManager();

  NullComponent<Sender> PREFIX =
      () -> text().color(RED).append(text('['), text("Murder Run", AQUA), text(']')).build();

  static NullComponent<Sender> direct(final String key) {
    return () -> format(MANAGER.render(translatable(key)));
  }

  static <T> UniComponent<Sender, T> direct(final String key, final Function<T, String> function) {
    return (arg) -> format(MANAGER.render(translatable(key, createFinalText(arg, function))));
  }

  static <T, U> BiComponent<Sender, T, U> direct(
      final String key, final Function<T, String> function1, final Function<U, String> function2) {
    return (arg1, arg2) ->
        format(
            MANAGER.render(
                translatable(
                    key, createFinalText(arg1, function1), createFinalText(arg2, function2))));
  }

  static <T, U, V> TriComponent<Sender, T, U, V> direct(
      final String key,
      final Function<T, String> function1,
      final Function<U, String> function2,
      final Function<V, String> function3) {
    return (arg1, arg2, arg3) ->
        format(
            MANAGER.render(
                translatable(
                    key,
                    createFinalText(arg1, function1),
                    createFinalText(arg2, function2),
                    createFinalText(arg3, function3))));
  }

  static <T> Component createFinalText(final T argument, final Function<T, String> function) {
    return text(function == null ? argument.toString() : function.apply(argument));
  }

  static Component format(final Component message) {
    return join(separator(space()), PREFIX.build(), message);
  }

  @FunctionalInterface
  interface NullComponent<S extends Sender> {

    default void send(final S sender) {
      sender.sendMessage(this.build());
    }

    Component build();
  }

  @FunctionalInterface
  interface UniComponent<S extends Sender, A0> {

    default void send(final S sender, final A0 arg0) {
      sender.sendMessage(this.build(arg0));
    }

    Component build(A0 arg0);
  }

  @FunctionalInterface
  interface BiComponent<S extends Sender, A0, A1> {

    default void send(final S sender, final A0 arg0, final A1 arg1) {
      sender.sendMessage(this.build(arg0, arg1));
    }

    Component build(A0 arg0, A1 arg1);
  }

  @FunctionalInterface
  interface TriComponent<S extends Sender, A0, A1, A2> {

    default void send(final S sender, final A0 arg0, final A1 arg1, final A2 arg2) {
      sender.sendMessage(this.build(arg0, arg1, arg2));
    }

    Component build(A0 arg0, A1 arg1, A2 arg2);
  }
}

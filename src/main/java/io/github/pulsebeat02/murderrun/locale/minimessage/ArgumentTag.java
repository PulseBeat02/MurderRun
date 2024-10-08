package io.github.pulsebeat02.murderrun.locale.minimessage;

import java.util.List;
import java.util.OptionalInt;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.Tag.Argument;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ArgumentTag implements TagResolver {

  private static final String NAME = "arg";

  private final List<? extends ComponentLike> argumentComponents;

  public ArgumentTag(final List<? extends ComponentLike> argumentComponents) {
    this.argumentComponents = argumentComponents;
  }

  @Override
  public Tag resolve(final @NonNull String name, final ArgumentQueue arguments, final @NonNull Context ctx)
    throws ParsingException {
    final Argument pop = arguments.pop();
    final OptionalInt optional = pop.asInt();
    final int index = optional.orElseThrow();
    final ComponentLike like = this.argumentComponents.get(index);
    return Tag.inserting(like);
  }

  @Override
  public boolean has(final String name) {
    return name.equals(NAME);
  }
}

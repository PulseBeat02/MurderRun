package io.github.pulsebeat02.murderrun.locale.minimessage;

import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class ArgumentTag implements TagResolver {

  private static final String NAME = "argument";
  private static final String NAME_1 = "arg";

  private final List<? extends ComponentLike> argumentComponents;

  public ArgumentTag(final List<? extends ComponentLike> argumentComponents) {
    this.argumentComponents = Objects.requireNonNull(argumentComponents, "argumentComponents");
  }

  @Override
  public Tag resolve(final String name, final ArgumentQueue arguments, final Context ctx)
      throws ParsingException {
    final int index =
        arguments
            .popOr("No argument number provided")
            .asInt()
            .orElseThrow(() -> ctx.newException("Invalid argument number", arguments));
    if (index < 0 || index >= this.argumentComponents.size()) {
      throw ctx.newException("Invalid argument number", arguments);
    }
    return Tag.inserting(this.argumentComponents.get(index));
  }

  @Override
  public boolean has(final String name) {
    return name.equals(NAME) || name.equals(NAME_1);
  }
}

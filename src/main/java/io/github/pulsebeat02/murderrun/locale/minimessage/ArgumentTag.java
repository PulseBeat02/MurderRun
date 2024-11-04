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
  public Tag resolve(final @NonNull String name, final ArgumentQueue arguments, final @NonNull Context ctx) throws ParsingException {
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

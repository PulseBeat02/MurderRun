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
package io.github.pulsebeat02.murderrun.commmand.game;

import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class PlayerInviteManager {

  private final Set<CommandSender> invites;

  public PlayerInviteManager() {
    this.invites = this.createExpiringCache();
  }

  private Set<CommandSender> createExpiringCache(@UnderInitialization PlayerInviteManager this) {
    final Duration duration = Duration.ofMinutes(5);
    final Map<CommandSender, Boolean> map = CacheBuilder.newBuilder().expireAfterWrite(duration).<CommandSender, Boolean>build().asMap();
    return Collections.newSetFromMap(map);
  }

  public boolean addInvite(final CommandSender sender) {
    return this.invites.add(sender);
  }

  public Set<CommandSender> getInvites() {
    return this.invites;
  }

  public boolean removeInvite(final CommandSender sender) {
    return this.invites.remove(sender);
  }

  public boolean hasInvite(final CommandSender sender) {
    return this.invites.contains(sender);
  }
}

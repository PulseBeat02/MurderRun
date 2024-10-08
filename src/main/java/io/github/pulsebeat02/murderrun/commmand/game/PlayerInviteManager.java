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

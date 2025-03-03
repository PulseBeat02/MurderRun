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
package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class GamePlayer extends AbstractPlayer {

  private final Game game;
  private final UUID uuid;

  private final Map<Attribute, Double> attributes;

  private MetadataManager metadata;
  private DeathManager deathManager;
  private PlayerAudience audience;

  private long lastPortalUse;
  private boolean canDismount;
  private boolean canSpectatorTeleport;
  private boolean alive;
  private boolean loggingOut;

  public GamePlayer(final Game game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.alive = true;
    this.canDismount = true;
    this.attributes = new HashMap<>();
  }

  public void start() {
    this.audience = new PlayerAudience(this.game, this.uuid);
    this.metadata = new MetadataManager(this);
    this.deathManager = new DeathManager();
    this.metadata.start();
    this.setDefaultAttributes();
  }

  private void setDefaultAttributes() {
    final Registry<Attribute> attributes = Registry.ATTRIBUTE;
    for (final Attribute attribute : attributes) {
      final AttributeInstance instance = this.getAttribute(attribute);
      if (instance != null) {
        final double value = instance.getValue();
        this.attributes.put(attribute, value);
      }
    }
  }

  @Override
  public Player getInternalPlayer() {
    return requireNonNull(Bukkit.getPlayer(this.uuid));
  }

  @Override
  public boolean isAlive() {
    return this.alive;
  }

  @Override
  public void setAlive(final boolean alive) {
    this.alive = alive;
  }

  @Override
  public Game getGame() {
    return this.game;
  }

  @Override
  public MetadataManager getMetadataManager() {
    return this.metadata;
  }

  @Override
  public DeathManager getDeathManager() {
    return this.deathManager;
  }

  @Override
  public PlayerAudience getAudience() {
    return this.audience;
  }

  @Override
  public boolean canDismount() {
    return this.canDismount;
  }

  @Override
  public void setCanDismount(final boolean canDismount) {
    this.canDismount = canDismount;
  }

  @Override
  public void setAllowSpectatorTeleport(final boolean allow) {
    this.canSpectatorTeleport = allow;
  }

  @Override
  public boolean canSpectatorTeleport() {
    return this.canSpectatorTeleport;
  }

  @Override
  public void setLastPortalUse(final long cooldown) {
    this.lastPortalUse = cooldown;
  }

  @Override
  public long getLastPortalUse() {
    return this.lastPortalUse;
  }

  @Override
  public boolean isLoggingOut() {
    return this.loggingOut;
  }

  @Override
  public void setLoggingOut(final boolean loggingOut) {
    this.loggingOut = loggingOut;
  }

  @Override
  public Map<Attribute, Double> getDefaultAttributes() {
    return this.attributes;
  }

  @Override
  public boolean getPlayer() {
    return this.isAlive();
  }
}

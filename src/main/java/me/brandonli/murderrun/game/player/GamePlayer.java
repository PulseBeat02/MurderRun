/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import java.util.*;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
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

  private volatile long lastPortalUse;
  private volatile boolean canDismount;
  private volatile boolean canSpectatorTeleport;
  private volatile boolean alive;
  private volatile boolean loggingOut;

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
}

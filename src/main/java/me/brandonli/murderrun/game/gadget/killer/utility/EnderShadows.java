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
package me.brandonli.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import java.util.Collection;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.extension.GameExtensionManager;
import me.brandonli.murderrun.game.extension.citizens.CitizensManager;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.*;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.MergedReference;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPC.Metadata;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;

public final class EnderShadows extends KillerGadget {

  private static final String TEXTURE_SIGNATURE =
      "JJLyJh0n4sr4EwvWlIHu6Rz+eiCv6gIte/HZa4z1XH0CSnUBcrKXfIlzaLKo24k6OmJMysIRRtGVjhBYpyTe0ggCdFSibp6hDOfH1j/BR8ZmJkBn4ylpZZZmc4fxqsEc04AuxhkAUkGqpseirS2p44eQb60CyVwCf8kfh4sSSvmgaORx+aEENpwALbx6aUBJ2DRlzBRtftTo3kSTWnyKJznbQyMQcFHyCXHuT96gfavJ1acavZtFcMw/xBpZM4X36Z8jR9srOF2W3y0RttyJkMR7xuWaidVg7X17GoRDkChsnK0KdawkWD+u/LVZM2mzdOSqKKHXMle2qLCLdWYTrmCufT+t/G6BrvyEtmflnP81ciVbfA7utpKH6XDzEKpA4mRIHtIRIfctO2ltTbWft5/VhXWqB+dgBuOErdUtW9qkGlg5au5LK/laDgTTQprnpq8Hd287X4AL2aAghMPCcTfIrE0Wnd2n6JbkIrXx5kA4F8K2f+N78TkXhGbbtMh1ktNzNvZXi47PFijuqalBPhhaAjCOJiWQx5b6PoCg6FWXhdZxC8ndCPB2xHtiqOKUWnCLkhBBtg/Lj+WETVvUP/GLjbMzKxljMycZHHxq9fZlWvnFtOnoiTWrljVUO5oLnR5bO0+MelTb7vN3pswLU2qO71okwCndfMhvXEnhZTs=";
  private static final String TEXTURE_VALUE =
      "ewogICJ0aW1lc3RhbXAiIDogMTY3Mjc3NTg4Mzk5MywKICAicHJvZmlsZUlkIiA6ICI2MDJmMjA0M2YzYjU0OGU1ODQyYjE4ZjljMDg2Y2U0ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCb3J5c18iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM5M2Q2NzU2M2VlNTYwMTg3NjZkMjFiMzY2OTJjYmU1NjNkYzBhOTFiYWNmYzBkYjU0YjMwYzJiOWEyNDg3MyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";

  public EnderShadows(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "ender_shadows",
        properties.getEnderShadowsCost(),
        ItemFactory.createGadget(
            "ender_shadows",
            properties.getEnderShadowsMaterial(),
            Message.ENDER_SHADOWS_NAME.build(),
            Message.ENDER_SHADOWS_LORE.build()));
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GamePlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location spawn = arena.getSpawn();
    if (!(player instanceof final Killer killer)) {
      return true;
    }
    item.remove();

    final GameExtensionManager extensionManager = game.getExtensionManager();
    final CitizensManager npcManager = extensionManager.getNPCManager();
    final GameProperties properties = game.getProperties();
    manager.applyToLivingSurvivors(
        survivor -> this.handleAllSurvivors(npcManager, scheduler, killer, survivor, spawn));
    manager.playSoundForAllParticipants(properties.getEnderShadowsSound());

    return false;
  }

  private void handleAllSurvivors(
      final CitizensManager manager,
      final GameScheduler scheduler,
      final Killer killer,
      final GamePlayer survivor,
      final Location spawn) {
    final PlayerAudience audience = survivor.getAudience();
    final Component msg = Message.ENDER_SHADOWS_ACTIVATE.build();
    audience.sendMessage(msg);

    final Entity shadow = this.getNPCEntity(manager, spawn);
    final StrictPlayerReference survivorRef = StrictPlayerReference.of(survivor);
    final StrictPlayerReference killerRef = StrictPlayerReference.of(killer);
    final MergedReference<Participant, Participant> merged =
        MergedReference.of(survivorRef, killerRef);
    scheduler.scheduleRepeatedTask(
        () -> this.handleSurvivorTeleport(killer, survivor, shadow), 2 * 20L, 20L, merged);

    final Location[] old = {survivor.getLocation()};
    scheduler.scheduleRepeatedTask(
        () -> this.teleportShadow(survivor, shadow, old), 0, 10 * 20L, survivorRef);
  }

  private void teleportShadow(
      final GamePlayer survivor, final Entity shadow, final Location[] old) {
    shadow.teleport(old[0]);
    old[0] = survivor.getLocation();
  }

  private void handleSurvivorTeleport(
      final Killer killer, final GamePlayer survivor, final Entity shadow) {
    final Collection<GamePlayer> players = killer.getEnderShadowsGlowing();
    final Component msg = Message.ENDER_SHADOWS_EFFECT.build();
    final MetadataManager metadata = killer.getMetadataManager();
    final Location location = survivor.getLocation();
    final Location other = shadow.getLocation();
    final double distance = location.distanceSquared(other);
    if (distance < 1) {
      players.add(survivor);
      final PlayerAudience audience = survivor.getAudience();
      audience.showTitle(empty(), msg);
      metadata.setEntityGlowing(survivor, NamedTextColor.RED, true);
    } else if (players.contains(survivor)) {
      players.remove(survivor);
      metadata.setEntityGlowing(survivor, NamedTextColor.RED, false);
    }
  }

  private Entity getNPCEntity(final CitizensManager manager, final Location location) {
    final NPC npc = this.spawnNPC(manager, location);
    final Entity entity = npc.getEntity();
    entity.setInvulnerable(true);
    return entity;
  }

  private NPC spawnNPC(final CitizensManager manager, final Location location) {
    final NPCRegistry registry = manager.getRegistry();
    final NPC npc = registry.createNPC(EntityType.PLAYER, "");

    final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
    trait.setSkinPersistent("Shadow", TEXTURE_SIGNATURE, TEXTURE_VALUE);

    final MetadataStore metadata = npc.data();
    metadata.set(Metadata.NAMEPLATE_VISIBLE, false);

    npc.spawn(location);
    return npc;
  }
}

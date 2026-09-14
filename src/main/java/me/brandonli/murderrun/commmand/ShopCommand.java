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
package me.brandonli.murderrun.commmand;

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.gui.ability.selection.NPCSelectEvent;
import me.brandonli.murderrun.gui.gadget.shop.NPCShopEvent;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.locale.PaperAudiences;
import me.brandonli.murderrun.locale.TranslationManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@SuppressWarnings("initialization.field.uninitialized")
public final class ShopCommand implements AnnotationCommandFeature {

  private static final String NPC_GADGET_KEY = "murderrun-gui";
  private static final String NPC_ABILITY_KEY = "murderrun-select";

  private PaperAudiences audiences;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider audienceProvider = plugin.getAudience();
    final NPCShopEvent event = new NPCShopEvent(plugin);
    final NPCSelectEvent selectEvent = new NPCSelectEvent(plugin);
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    this.audiences = audienceProvider.retrieve();
    manager.registerEvents(event, plugin);
    manager.registerEvents(selectEvent, plugin);
  }

  @Permission("murderrun.command.npc.remove")
  @CommandDescription("murderrun.command.npc.remove.info")
  @Command(value = "murder npc remove", requiredSender = Player.class)
  public void removeClosestMerchant(final Player sender) {
    final Audience audience = this.audiences.player(sender);
    final Location location = sender.getLocation();
    final NPC closest = this.findClosestMerchant(location);
    if (closest == null) {
      audience.sendMessage(Message.NPC_REMOVE_ERROR.build());
      return;
    }
    closest.destroy();
    audience.sendMessage(Message.NPC_REMOVE_DONE.build());
  }

  private @Nullable NPC findClosestMerchant(final Location location) {
    final World world = location.getWorld();
    final NPCRegistry registry = CitizensAPI.getNPCRegistry();
    double min = Double.MAX_VALUE;
    NPC closest = null;
    for (final NPC npc : registry) {
      if (!this.isMerchant(npc)) {
        continue;
      }
      final Location stored = npc.getStoredLocation();
      if (stored == null) {
        continue;
      }
      final World storedWorld = stored.getWorld();
      if (!world.equals(storedWorld)) {
        continue;
      }
      final double distance = location.distanceSquared(stored);
      if (distance < min) {
        min = distance;
        closest = npc;
      }
    }
    return closest;
  }

  private boolean isMerchant(final NPC npc) {
    final MetadataStore store = npc.data();
    return store.has(NPC_GADGET_KEY) || store.has(NPC_ABILITY_KEY);
  }

  @Permission("murderrun.command.npc.spawn.ability.survivor")
  @CommandDescription("murderrun.command.npc.spawn.ability.survivor.info")
  @Command(value = "murder npc spawn ability survivor", requiredSender = Player.class)
  public void createSurvivorAbilityMerchant(final Player sender) {
    final Location location = sender.getLocation();
    this.createAbilityNPC(location, true);
  }

  @Permission("murderrun.command.npc.spawn.ability.killer")
  @CommandDescription("murderrun.command.npc.spawn.ability.killer.info")
  @Command(value = "murder npc spawn ability killer", requiredSender = Player.class)
  public void createKillerAbilityMerchant(final Player sender) {
    final Location location = sender.getLocation();
    this.createAbilityNPC(location, false);
  }

  private void createAbilityNPC(final Location location, final boolean survivor) {
    final TranslationManager manager = Message.MANAGER;
    final String survivorMM = manager.getProperty("murderrun.npc.ability.survivor.name");
    final String killerMM = manager.getProperty("murderrun.npc.ability.killer.name");
    final String raw = survivor ? survivorMM : killerMM;
    final NPCRegistry registry = CitizensAPI.getNPCRegistry();
    final NPC npc = registry.createNPC(EntityType.PLAYER, raw);
    final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
    final GameProperties properties = GameProperties.COMMON;
    if (survivor) {
      trait.setSkinPersistent(
          "Angel Spirit",
          properties.getAngelSpiritTextureSignature(),
          properties.getAngelSpiritTextureData());
    } else {
      trait.setSkinPersistent(
          "Weeping Angel",
          properties.getWeepingAngelTextureSignature(),
          properties.getWeepingAngelTextureData());
    }
    npc.spawn(location);

    final MetadataStore store = npc.data();
    store.setPersistent(NPC_ABILITY_KEY, survivor);
  }

  @Permission("murderrun.command.npc.spawn.gadget.survivor")
  @CommandDescription("murderrun.command.npc.spawn.gadget.survivor.info")
  @Command(value = "murder npc spawn gadget survivor", requiredSender = Player.class)
  public void createSurvivorGadgetMerchant(final Player sender) {
    final Location location = sender.getLocation();
    this.createGadgetNPC(location, true);
  }

  @Permission("murderrun.command.npc.spawn.gadget.killer")
  @CommandDescription("murderrun.command.npc.spawn.gadget.killer.info")
  @Command(value = "murder npc spawn gadget killer", requiredSender = Player.class)
  public void createKillerGadgetMerchant(final Player sender) {
    final Location location = sender.getLocation();
    this.createGadgetNPC(location, false);
  }

  private void createGadgetNPC(final Location location, final boolean survivor) {
    final TranslationManager manager = Message.MANAGER;
    final String survivorMM = manager.getProperty("murderrun.npc.gadget.survivor.name");
    final String killerMM = manager.getProperty("murderrun.npc.gadget.killer.name");
    final String raw = survivor ? survivorMM : killerMM;
    final NPCRegistry registry = CitizensAPI.getNPCRegistry();
    final NPC npc = registry.createNPC(EntityType.PLAYER, raw);
    final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
    final GameProperties properties = GameProperties.COMMON;
    if (survivor) {
      trait.setSkinPersistent(
          "Guardian Angel",
          properties.getGuardianAngelTextureSignature(),
          properties.getGuardianAngelTextureData());
    } else {
      trait.setSkinPersistent(
          "Grim Reaper",
          properties.getGrimReaperTextureSignature(),
          properties.getGrimReaperTextureData());
    }
    npc.spawn(location);

    final MetadataStore store = npc.data();
    store.setPersistent(NPC_GADGET_KEY, survivor);
  }
}

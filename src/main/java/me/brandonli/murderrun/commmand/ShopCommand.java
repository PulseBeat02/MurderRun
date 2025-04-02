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
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.locale.TranslationManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public final class ShopCommand implements AnnotationCommandFeature {

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final NPCShopEvent event = new NPCShopEvent(plugin);
    final NPCSelectEvent selectEvent = new NPCSelectEvent(plugin);
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    manager.registerEvents(event, plugin);
    manager.registerEvents(selectEvent, plugin);
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
    if (survivor) {
      trait.setSkinPersistent("Angel Spirit", GameProperties.ANGEL_SPIRIT_TEXTURE_SIGNATURE, GameProperties.ANGEL_SPIRIT_TEXTURE_DATA);
    } else {
      trait.setSkinPersistent("Weeping Angel", GameProperties.WEEPING_ANGEL_TEXTURE_SIGNATURE, GameProperties.WEEPING_ANGEL_TEXTURE_DATA);
    }
    npc.spawn(location);

    final MetadataStore store = npc.data();
    store.setPersistent("murderrun-select", survivor);
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
    if (survivor) {
      trait.setSkinPersistent(
        "Guardian Angel",
        GameProperties.GUARDIAN_ANGEL_TEXTURE_SIGNATURE,
        GameProperties.GUARDIAN_ANGEL_TEXTURE_DATA
      );
    } else {
      trait.setSkinPersistent("Grim Reaper", GameProperties.GRIM_REAPER_TEXTURE_SIGNATURE, GameProperties.GRIM_REAPER_TEXTURE_DATA);
    }
    npc.spawn(location);

    final MetadataStore store = npc.data();
    store.setPersistent("murderrun-gui", survivor);
  }
}

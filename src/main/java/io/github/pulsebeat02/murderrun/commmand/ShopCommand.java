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
package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.gui.ability.selection.NPCSelectEvent;
import io.github.pulsebeat02.murderrun.gui.gadget.shop.NPCShopEvent;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.locale.TranslationManager;
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

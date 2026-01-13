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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.extension.GameExtensionManager;
import me.brandonli.murderrun.game.extension.citizens.CitizensManager;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.MirrorTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Decoy extends SurvivorGadget {

  public Decoy(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "decoy",
      properties.getDecoyCost(),
      ItemFactory.createGadget("decoy", properties.getDecoyMaterial(), Message.DECOY_NAME.build(), Message.DECOY_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final org.bukkit.entity.Item item = packet.getItem();
    item.remove();

    final GameExtensionManager extensions = game.getExtensionManager();
    final CitizensManager manager = extensions.getNPCManager();
    final String name = player.getDisplayName();
    final NPC npc = this.customizeNPC(manager, player, name);
    final Location location = player.getLocation();
    npc.spawn(location);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getDecoySound());

    return false;
  }

  private NPC customizeNPC(final CitizensManager manager, final GamePlayer player, final String name) {
    final NPCRegistry registry = manager.getRegistry();
    final NPC npc = registry.createNPC(EntityType.PLAYER, name);
    this.customizeNPC(npc);
    this.setNPCArmor(player, npc);
    this.setMirrorTrait(player, npc);

    return npc;
  }

  private void customizeNPC(final NPC npc) {
    npc.setUseMinecraftAI(true);
    npc.setProtected(false);
  }

  private void setMirrorTrait(final GamePlayer player, final NPC npc) {
    player.apply(internal -> {
      final MirrorTrait mirror = npc.getOrAddTrait(MirrorTrait.class);
      mirror.isMirroring(internal);
      mirror.setMirrorName(true);
      mirror.setEnabled(true);
    });
  }

  private void setNPCArmor(final GamePlayer player, final NPC npc) {
    final PlayerInventory inventory = player.getInventory();
    final Equipment equipment = npc.getOrAddTrait(Equipment.class);
    final ItemStack helmet = inventory.getHelmet();
    final ItemStack chestplate = inventory.getChestplate();
    final ItemStack leggings = inventory.getLeggings();
    final ItemStack boots = inventory.getBoots();
    final ItemStack air = Item.create(Material.AIR);
    equipment.set(EquipmentSlot.HELMET, helmet == null ? air : helmet);
    equipment.set(EquipmentSlot.CHESTPLATE, chestplate == null ? air : chestplate);
    equipment.set(EquipmentSlot.LEGGINGS, leggings == null ? air : leggings);
    equipment.set(EquipmentSlot.BOOTS, boots == null ? air : boots);
  }
}

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

  public Decoy() {
    super(
      "decoy",
      GameProperties.DECOY_COST,
      ItemFactory.createGadget("decoy", GameProperties.DECOY_MATERIAL, Message.DECOY_NAME.build(), Message.DECOY_LORE.build())
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

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.DECOY_SOUND);

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

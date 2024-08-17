package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.MirrorTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Decoy extends SurvivorGadget {

  public Decoy() {
    super("decoy", Material.PLAYER_HEAD, Message.DECOY_NAME.build(), Message.DECOY_LORE.build(), 8);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final String name = player.getDisplayName();
    final NPC npc = this.customizeNPC(player, name);
    npc.spawn(location);
  }

  private NPC customizeNPC(final Player player, final String name) {

    final NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
    final PlayerInventory inventory = player.getInventory();
    final Equipment equipment = npc.getOrAddTrait(Equipment.class);
    equipment.set(EquipmentSlot.HELMET, this.equipArmorSlot(EquipmentSlot.HELMET, inventory));
    equipment.set(
        EquipmentSlot.CHESTPLATE, this.equipArmorSlot(EquipmentSlot.CHESTPLATE, inventory));
    equipment.set(EquipmentSlot.LEGGINGS, this.equipArmorSlot(EquipmentSlot.LEGGINGS, inventory));
    equipment.set(EquipmentSlot.BOOTS, this.equipArmorSlot(EquipmentSlot.BOOTS, inventory));
    npc.setUseMinecraftAI(true);

    final MirrorTrait mirror = npc.getOrAddTrait(MirrorTrait.class);
    mirror.isMirroring(player);
    mirror.setMirrorName(true);

    return npc;
  }

  private ItemStack equipArmorSlot(final EquipmentSlot slot, final PlayerInventory inventory) {
    final ItemStack stack =
        switch (slot) {
          case HELMET -> inventory.getHelmet();
          case CHESTPLATE -> inventory.getChestplate();
          case LEGGINGS -> inventory.getLeggings();
          case BOOTS -> inventory.getBoots();
          default -> null;
        };
    return stack == null ? new ItemStack(Material.AIR) : stack;
  }
}

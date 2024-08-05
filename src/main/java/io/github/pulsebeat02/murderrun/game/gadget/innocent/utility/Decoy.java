package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
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

public final class Decoy extends MurderGadget {

  public Decoy() {
    super(
        "decoy",
        Material.GHAST_SPAWN_EGG,
        Locale.DECOY_TRAP_NAME.build(),
        Locale.DECOY_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onDropEvent(game, event, true);

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
    equipment.set(
        Equipment.EquipmentSlot.HELMET, this.equipArmorSlot(EquipmentSlot.HELMET, inventory));
    equipment.set(
        Equipment.EquipmentSlot.CHESTPLATE,
        this.equipArmorSlot(EquipmentSlot.CHESTPLATE, inventory));
    equipment.set(
        Equipment.EquipmentSlot.LEGGINGS, this.equipArmorSlot(EquipmentSlot.LEGGINGS, inventory));
    equipment.set(
        Equipment.EquipmentSlot.BOOTS, this.equipArmorSlot(EquipmentSlot.BOOTS, inventory));
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

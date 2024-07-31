package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.MirrorTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
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
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final String name = player.getDisplayName();
    final NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
    this.customizeNPC(player, npc);
    npc.spawn(location);
  }

  public void customizeNPC(final Player player, final NPC npc) {

    final PlayerInventory inventory = player.getInventory();
    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, inventory.getHelmet());
    npc.getOrAddTrait(Equipment.class)
        .set(Equipment.EquipmentSlot.CHESTPLATE, inventory.getChestplate());
    npc.getOrAddTrait(Equipment.class)
        .set(Equipment.EquipmentSlot.LEGGINGS, inventory.getLeggings());
    npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, inventory.getBoots());
    npc.setUseMinecraftAI(true);

    final MirrorTrait trait = npc.getOrAddTrait(MirrorTrait.class);
    trait.isMirroring(player);
    trait.setMirrorName(true);
  }
}

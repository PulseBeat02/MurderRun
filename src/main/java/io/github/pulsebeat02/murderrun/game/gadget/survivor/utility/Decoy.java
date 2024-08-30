package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.CitizensManager;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.MirrorTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Decoy extends SurvivorGadget {

  public Decoy() {
    super("decoy", Material.PLAYER_HEAD, Message.DECOY_NAME.build(), Message.DECOY_LORE.build(), 8);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game,
      final GamePlayer player,
      final org.bukkit.entity.Item item,
      final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final CitizensManager manager = game.getNPCManager();
    final String name = player.getDisplayName();
    final NPC npc = this.customizeNPC(manager, player, name);
    final Location location = player.getLocation();
    npc.spawn(location);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GadgetConstants.DECOY_SOUND);

    return false;
  }

  private NPC customizeNPC(
      final CitizensManager manager, final GamePlayer player, final String name) {

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
    final Player internal = player.getInternalPlayer();
    final MirrorTrait mirror = npc.getOrAddTrait(MirrorTrait.class);
    mirror.isMirroring(internal);
    mirror.setMirrorName(true);
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

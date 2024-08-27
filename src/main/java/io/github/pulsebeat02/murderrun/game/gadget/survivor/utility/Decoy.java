package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.CitizensManager;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.MirrorTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.PlayerInventory;

public final class Decoy extends SurvivorGadget {

  private static final String DECOY_SOUND = "block.beehive.enter";

  public Decoy() {
    super("decoy", Material.PLAYER_HEAD, Message.DECOY_NAME.build(), Message.DECOY_LORE.build(), 8);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager playerManager = game.getPlayerManager();
    final GamePlayer gamePlayer = playerManager.getGamePlayer(player);
    final CitizensManager manager = game.getNPCManager();

    final String name = gamePlayer.getDisplayName();
    final NPC npc = this.customizeNPC(manager, player, name);
    final Location location = player.getLocation();
    npc.spawn(location);

    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound(DECOY_SOUND);
  }

  private NPC customizeNPC(final CitizensManager manager, final Player player, final String name) {

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

  private void setMirrorTrait(final Player player, final NPC npc) {
    final MirrorTrait mirror = npc.getOrAddTrait(MirrorTrait.class);
    mirror.isMirroring(player);
    mirror.setMirrorName(true);
  }

  private void setNPCArmor(final Player player, final NPC npc) {
    final PlayerInventory inventory = player.getInventory();
    final Equipment equipment = npc.getOrAddTrait(Equipment.class);
    equipment.set(EquipmentSlot.HELMET, inventory.getHelmet());
    equipment.set(EquipmentSlot.CHESTPLATE, inventory.getChestplate());
    equipment.set(EquipmentSlot.LEGGINGS, inventory.getLeggings());
    equipment.set(EquipmentSlot.BOOTS, inventory.getBoots());
  }
}

package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameNPCManager;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

public final class Camera extends KillerGadget {

  private static final String TEXTURE_SIGNATURE =
      "NnBRwHr2aCWajkQvLsyjbDNa3EBUzn+r/uf4TOsHG+owrq9B0yyr1mModVd/T+K6c5TjbL4ozVs0oOUOaKnohj8BSCIeANe5I2FlFj/xV2mX0wnK1vxhwZ05u8uP7jqHJcly8Vhhc4pm907ol1Ozi+9cln8nPuJQ7AjKElwJjijQJ6CGcJiZ2p+kgByc4jxj1WCAwIgzEFhMi3VmXPc33sSMWiiOfHDTtO78fP1liOQNyxudiHDc6WWBDrNNHmS8eggxE/8ODniENFiitXC3YzZE7Y3PFvj6zia+m24mjO5ZG8bTWiDhmNnX7Bqvs/r3rv6ctxopYbmnQkvAN1DZ7cpOPe9b7zsBNtajVTLIzPqemfmKTJRtboGR/wXPOD/TrkhouN5tH9MY5MLN0xUXCSDSnL6IoQm509kSDdVh6Msci9QU88sn++TRTAtNfngySyUlgUMCAMinjikNi52fH0ccrJkn/UcnRDhWpfBRhmOdg1rs1rYO5f963nWJKDGckoBEDGwDmDP0PowI0iDWKERDvunw4hM+oEsOvk1dzHp3o2qBV1Xyts1uYQZ5jjw8iQpC/Y6htB4mV5RLbInDlcxQ2WoS9v9CFQMdXAnCW80i5ffMzq5Km+CnPt74m5tbqZOFBEFnu1ux+3M6C/ko3jLcyWNHCRf8H+7uMr0BqHk=";
  private static final String TEXTURE_DATA =
      "ewogICJ0aW1lc3RhbXAiIDogMTYyNjU5NDkyMjYxMCwKICAicHJvZmlsZUlkIiA6ICI0ZjU2ZTg2ODk2OGU0ZWEwYmNjM2M2NzRlNzQ3ODdjOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhmMzQ0Y2VhNGFhYjVjYTRlYjZmMjcwZGZhNjZlYWU5ODgzZGI2Y2NjYzVmYjIxMDYzZDc0ZjQ1ZWE2OWI2ZTMiCiAgICB9CiAgfQp9";

  private final Set<GamePlayer> glowPlayers;

  public Camera() {
    super(
        "killer_camera",
        Material.OBSERVER,
        Message.KILLER_CAMERA_NAME.build(),
        Message.KILLER_CAMERA_LORE.build(),
        48);
    this.glowPlayers = Collections.newSetFromMap(new WeakHashMap<>());
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer killer = manager.getGamePlayer(player);
    final Location location = player.getLocation();
    final GameNPCManager npcManager = game.getNPCManager();
    final NPC npc = this.spawnNPC(npcManager, location);
    final Entity entity = npc.getEntity();
    entity.setInvulnerable(true);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> this.handleCameraWatch(manager, entity, killer), 0, 3 * 20L);
  }

  private void handleCameraWatch(
      final PlayerManager manager, final Entity entity, final GamePlayer killer) {
    manager.applyToAllLivingInnocents(
        survivor -> this.handleGlowInnocent(survivor, entity, killer));
  }

  private void handleGlowInnocent(
      final GamePlayer survivor, final Entity entity, final GamePlayer killer) {
    if (survivor.canSeeEntity(entity, 64d)) {
      this.glowPlayers.add(survivor);
      killer.setEntityGlowingForPlayer(survivor);
      this.setLookDirection(survivor, entity);
    } else if (this.glowPlayers.contains(survivor)) {
      this.glowPlayers.remove(survivor);
      killer.removeEntityGlowingForPlayer(survivor);
    }
  }

  private void setLookDirection(final GamePlayer killer, final Entity entity) {
    final Location origin = entity.getLocation();
    final Location look = killer.getLocation();
    final Vector direction = look.getDirection().subtract(origin.toVector());
    origin.setDirection(direction);
    entity.teleport(origin);
  }

  private NPC spawnNPC(final GameNPCManager manager, final Location location) {
    final NPCRegistry registry = manager.getRegistry();
    final NPC npc = registry.createNPC(EntityType.PLAYER, "Killer Camera");
    final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
    trait.setSkinPersistent("Camera", TEXTURE_SIGNATURE, TEXTURE_DATA);
    npc.spawn(location);
    return npc;
  }
}

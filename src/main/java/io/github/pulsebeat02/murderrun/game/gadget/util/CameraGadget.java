package io.github.pulsebeat02.murderrun.game.gadget.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.CitizensManager;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.AbstractGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.util.Collection;
import java.util.function.Consumer;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

public class CameraGadget {

  private static final String CAMERA_SOUND = "entity.ender_eye.death";

  private static final String TEXTURE_SIGNATURE =
      "NnBRwHr2aCWajkQvLsyjbDNa3EBUzn+r/uf4TOsHG+owrq9B0yyr1mModVd/T+K6c5TjbL4ozVs0oOUOaKnohj8BSCIeANe5I2FlFj/xV2mX0wnK1vxhwZ05u8uP7jqHJcly8Vhhc4pm907ol1Ozi+9cln8nPuJQ7AjKElwJjijQJ6CGcJiZ2p+kgByc4jxj1WCAwIgzEFhMi3VmXPc33sSMWiiOfHDTtO78fP1liOQNyxudiHDc6WWBDrNNHmS8eggxE/8ODniENFiitXC3YzZE7Y3PFvj6zia+m24mjO5ZG8bTWiDhmNnX7Bqvs/r3rv6ctxopYbmnQkvAN1DZ7cpOPe9b7zsBNtajVTLIzPqemfmKTJRtboGR/wXPOD/TrkhouN5tH9MY5MLN0xUXCSDSnL6IoQm509kSDdVh6Msci9QU88sn++TRTAtNfngySyUlgUMCAMinjikNi52fH0ccrJkn/UcnRDhWpfBRhmOdg1rs1rYO5f963nWJKDGckoBEDGwDmDP0PowI0iDWKERDvunw4hM+oEsOvk1dzHp3o2qBV1Xyts1uYQZ5jjw8iQpC/Y6htB4mV5RLbInDlcxQ2WoS9v9CFQMdXAnCW80i5ffMzq5Km+CnPt74m5tbqZOFBEFnu1ux+3M6C/ko3jLcyWNHCRf8H+7uMr0BqHk=";
  private static final String TEXTURE_DATA =
      "ewogICJ0aW1lc3RhbXAiIDogMTYyNjU5NDkyMjYxMCwKICAicHJvZmlsZUlkIiA6ICI0ZjU2ZTg2ODk2OGU0ZWEwYmNjM2M2NzRlNzQ3ODdjOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhmMzQ0Y2VhNGFhYjVjYTRlYjZmMjcwZGZhNjZlYWU5ODgzZGI2Y2NjYzVmYjIxMDYzZDc0ZjQ1ZWE2OWI2ZTMiCiAgICB9CiAgfQp9";

  private final AbstractGadget gadget;
  private final Multimap<GamePlayer, GamePlayer> glowingPlayers;

  public CameraGadget(final AbstractGadget gadget) {
    this.gadget = gadget;
    this.glowingPlayers = HashMultimap.create();
  }

  public void handleCamera(final Game game, final PlayerDropItemEvent event) {

    this.gadget.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);

    final Location location = player.getLocation();
    final CitizensManager npcManager = game.getNPCManager();
    final NPC npc = this.customizeNPC(npcManager);
    npc.spawn(location);

    final LivingEntity entity = (LivingEntity) npc.getEntity();
    final GameScheduler scheduler = game.getScheduler();
    final Consumer<GamePlayer> handleGlow =
        opponent -> this.handleGlow(gamePlayer, opponent, entity);

    final Runnable task;
    if (gamePlayer instanceof Survivor) {
      task = () -> manager.applyToAllMurderers(handleGlow);
    } else {
      task = () -> manager.applyToAllInnocents(handleGlow);
    }
    scheduler.scheduleRepeatedTask(task, 0, 20L);

    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound(CAMERA_SOUND);
  }

  private void handleGlow(
      final GamePlayer owner, final GamePlayer opponent, final LivingEntity npc) {
    final Player internal = opponent.getInternalPlayer();
    final boolean detected = npc.hasLineOfSight(internal);
    final MetadataManager metadata = owner.getMetadataManager();
    final Collection<GamePlayer> glow = this.glowingPlayers.get(owner);
    if (detected) {
      glow.add(opponent);
      this.setLookDirection(opponent, npc);
      metadata.setEntityGlowing(opponent, ChatColor.RED, true);
    } else if (glow.contains(opponent)) {
      glow.remove(opponent);
      metadata.setEntityGlowing(opponent, ChatColor.RED, false);
    }
  }

  private void setLookDirection(final GamePlayer target, final Entity entity) {

    final Location origin = entity.getLocation();
    final Location look = target.getLocation();
    final Vector direction = look.toVector().subtract(origin.toVector());
    origin.setDirection(direction);

    entity.teleport(origin);
  }

  private NPC customizeNPC(final CitizensManager manager) {

    final NPCRegistry registry = manager.getRegistry();
    final NPC npc = registry.createNPC(EntityType.PLAYER, "");
    this.addSkinTrait(npc);

    return npc;
  }

  private void addSkinTrait(final NPC npc) {
    final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
    trait.setSkinPersistent("Camera", TEXTURE_SIGNATURE, TEXTURE_DATA);
  }
}

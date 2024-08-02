package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

public final class Camera extends MurderGadget {

  private static final String TEXTURE_SIGNATURE =
      "NnBRwHr2aCWajkQvLsyjbDNa3EBUzn+r/uf4TOsHG+owrq9B0yyr1mModVd/T+K6c5TjbL4ozVs0oOUOaKnohj8BSCIeANe5I2FlFj/xV2mX0wnK1vxhwZ05u8uP7jqHJcly8Vhhc4pm907ol1Ozi+9cln8nPuJQ7AjKElwJjijQJ6CGcJiZ2p+kgByc4jxj1WCAwIgzEFhMi3VmXPc33sSMWiiOfHDTtO78fP1liOQNyxudiHDc6WWBDrNNHmS8eggxE/8ODniENFiitXC3YzZE7Y3PFvj6zia+m24mjO5ZG8bTWiDhmNnX7Bqvs/r3rv6ctxopYbmnQkvAN1DZ7cpOPe9b7zsBNtajVTLIzPqemfmKTJRtboGR/wXPOD/TrkhouN5tH9MY5MLN0xUXCSDSnL6IoQm509kSDdVh6Msci9QU88sn++TRTAtNfngySyUlgUMCAMinjikNi52fH0ccrJkn/UcnRDhWpfBRhmOdg1rs1rYO5f963nWJKDGckoBEDGwDmDP0PowI0iDWKERDvunw4hM+oEsOvk1dzHp3o2qBV1Xyts1uYQZ5jjw8iQpC/Y6htB4mV5RLbInDlcxQ2WoS9v9CFQMdXAnCW80i5ffMzq5Km+CnPt74m5tbqZOFBEFnu1ux+3M6C/ko3jLcyWNHCRf8H+7uMr0BqHk=";
  private static final String TEXTURE_DATA =
      "ewogICJ0aW1lc3RhbXAiIDogMTYyNjU5NDkyMjYxMCwKICAicHJvZmlsZUlkIiA6ICI0ZjU2ZTg2ODk2OGU0ZWEwYmNjM2M2NzRlNzQ3ODdjOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhmMzQ0Y2VhNGFhYjVjYTRlYjZmMjcwZGZhNjZlYWU5ODgzZGI2Y2NjYzVmYjIxMDYzZDc0ZjQ1ZWE2OWI2ZTMiCiAgICB9CiAgfQp9";

  private final Set<GamePlayer> glowPlayers;

  public Camera() {
    super(
        "camera",
        Material.ENDER_EYE,
        Locale.CAMERA_TRAP_NAME.build(),
        Locale.CAMERA_TRAP_LORE.build());
    this.glowPlayers = Collections.newSetFromMap(new WeakHashMap<>());
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {
    super.onDropEvent(game, event, true);
    final MurderPlayerManager manager = game.getPlayerManager();
    final Collection<InnocentPlayer> players = manager.getInnocentPlayers();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Camera");
    this.customizeNPC(npc);
    npc.spawn(location);

    final Entity entity = npc.getEntity();
    entity.setInvulnerable(true);

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllMurderers(
            murderer -> this.handleGlowMurderer(murderer, entity, players)),
        0,
        3 * 20);
  }

  public void handleGlowMurderer(
      final Murderer murderer, final Entity entity, final Collection<InnocentPlayer> innocents) {
    final Collection<GamePlayer> higher =
        innocents.stream().map(player -> (GamePlayer) player).toList();
    if (PlayerUtils.canEntitySeePlayer(entity, murderer, 64d)) {
      this.glowPlayers.add(murderer);
      PlayerUtils.setGlowColor(murderer, ChatColor.RED, higher);
      this.setLookDirection(murderer, entity);
    } else if (this.glowPlayers.contains(murderer)) {
      this.glowPlayers.remove(murderer);
      PlayerUtils.removeGlow(murderer, higher);
    }
  }

  public void setLookDirection(final Murderer murderer, final Entity entity) {
    final Location origin = entity.getLocation();
    final Location look = murderer.getLocation();
    final Vector direction = look.getDirection().subtract(origin.toVector());
    origin.setDirection(direction);
    entity.teleport(origin);
  }

  public void customizeNPC(final NPC npc) {
    final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
    trait.setSkinPersistent("Camera", TEXTURE_SIGNATURE, TEXTURE_DATA);
  }
}

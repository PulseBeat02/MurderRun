package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.BoundingBox;

public final class EnderShadows extends KillerGadget {

  private static final String TEXTURE_SIGNATURE = "JJLyJh0n4sr4EwvWlIHu6Rz+eiCv6gIte/HZa4z1XH0CSnUBcrKXfIlzaLKo24k6OmJMysIRRtGVjhBYpyTe0ggCdFSibp6hDOfH1j/BR8ZmJkBn4ylpZZZmc4fxqsEc04AuxhkAUkGqpseirS2p44eQb60CyVwCf8kfh4sSSvmgaORx+aEENpwALbx6aUBJ2DRlzBRtftTo3kSTWnyKJznbQyMQcFHyCXHuT96gfavJ1acavZtFcMw/xBpZM4X36Z8jR9srOF2W3y0RttyJkMR7xuWaidVg7X17GoRDkChsnK0KdawkWD+u/LVZM2mzdOSqKKHXMle2qLCLdWYTrmCufT+t/G6BrvyEtmflnP81ciVbfA7utpKH6XDzEKpA4mRIHtIRIfctO2ltTbWft5/VhXWqB+dgBuOErdUtW9qkGlg5au5LK/laDgTTQprnpq8Hd287X4AL2aAghMPCcTfIrE0Wnd2n6JbkIrXx5kA4F8K2f+N78TkXhGbbtMh1ktNzNvZXi47PFijuqalBPhhaAjCOJiWQx5b6PoCg6FWXhdZxC8ndCPB2xHtiqOKUWnCLkhBBtg/Lj+WETVvUP/GLjbMzKxljMycZHHxq9fZlWvnFtOnoiTWrljVUO5oLnR5bO0+MelTb7vN3pswLU2qO71okwCndfMhvXEnhZTs=";
  private static final String TEXTURE_VALUE = "ewogICJ0aW1lc3RhbXAiIDogMTY3Mjc3NTg4Mzk5MywKICAicHJvZmlsZUlkIiA6ICI2MDJmMjA0M2YzYjU0OGU1ODQyYjE4ZjljMDg2Y2U0ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCb3J5c18iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM5M2Q2NzU2M2VlNTYwMTg3NjZkMjFiMzY2OTJjYmU1NjNkYzBhOTFiYWNmYzBkYjU0YjMwYzJiOWEyNDg3MyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";

  private final Multimap<GamePlayer, Entity> shadows;

  public EnderShadows() {
    super("ender_shadows", Material.ENDER_PEARL, Locale.ENDER_SHADOWS_TRAP_NAME.build(),
        Locale.ENDER_SHADOWS_TRAP_LORE.build(), 48);
    this.shadows = ArrayListMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location spawn = arena.getSpawn();
    manager.applyToAllLivingInnocents(
        survivor -> this.handleAllSurvivors(scheduler, survivor, spawn));
  }

  private void handleAllSurvivors(final GameScheduler scheduler, final GamePlayer survivor,
      final Location spawn) {

    final Component msg = Locale.ENDER_SHADOWS_ACTIVATE.build();
    survivor.sendMessage(msg);

    // TODO STUFF

    final Entity shadow = this.getNPCEntity(spawn);
    scheduler.scheduleRepeatedTask(() -> this.handleSurvivorTeleport(scheduler, survivor, shadow),
        2 * 20L, 5 * 20L);
  }

  private void handleSurvivorTeleport(final GameScheduler scheduler, final GamePlayer killer, final GamePlayer survivor,
      final Entity shadow) {

    final Location old = survivor.getLocation();
    scheduler.scheduleTask(() -> shadow.teleport(old), 5 * 20L);

    final BoundingBox shadowBox = shadow.getBoundingBox();
    survivor.apply(player -> {
      final BoundingBox playerBox = player.getBoundingBox();
      if (shadowBox.overlaps(playerBox)) {
        killer.setEntityGlowingForPlayer(player);
      }
    });
  }

  private Entity getNPCEntity(final Location location) {
    final NPC npc = this.spawnNPC(location);
    final Entity entity = npc.getEntity();
    entity.setInvulnerable(true);
    return entity;
  }

  private NPC spawnNPC(final Location location) {
    final NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
    final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
    trait.setSkinPersistent("Shadow", TEXTURE_SIGNATURE, TEXTURE_VALUE);
    npc.spawn(location);
    return npc;
  }
}

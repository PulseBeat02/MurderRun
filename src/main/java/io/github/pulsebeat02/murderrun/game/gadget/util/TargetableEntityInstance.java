package io.github.pulsebeat02.murderrun.game.gadget.util;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class TargetableEntityInstance {

  private final Game game;

  public TargetableEntityInstance(final Game game) {
    this.game = game;
  }

  public void onDormagoggDamage(final EntityDamageByEntityEvent event) {

    final Entity entity = event.getDamager();
    if (!(entity instanceof final Zombie zombie)) {
      return;
    }

    final Entity attacked = event.getEntity();
    if (!(attacked instanceof final Player player)) {
      return;
    }

    final PlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    final String owner = container.get(Keys.DORMAGOGG_OWNER, PersistentDataType.STRING);
    if (owner == null) {
      return;
    }
    zombie.remove();

    final UUID ownerUuid = UUID.fromString(owner);
    if (!manager.checkPlayerExists(ownerUuid)) {
      return;
    }

    final GamePlayer nearest = manager.getGamePlayer(player);
    final GamePlayer killer = manager.getGamePlayer(ownerUuid);
    final GameScheduler scheduler = this.game.getScheduler();
    nearest.disableJump(scheduler, 7 * 20L);
    nearest.disableWalkWithFOVEffects(10 * 20);
    nearest.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1));

    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(scheduler, nearest, ChatColor.RED, 7 * 20L);
  }

  public void onIceSpiritDamage(final EntityDamageByEntityEvent event) {

    final Entity entity = event.getDamager();
    if (!(entity instanceof final Zombie zombie)) {
      return;
    }

    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    final String target = container.get(Keys.ICE_SPIRIT_OWNER, PersistentDataType.STRING);
    if (target == null) {
      return;
    }
    zombie.remove();

    final UUID uuid = UUID.fromString(target);
    final PlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(uuid)) {
      return;
    }

    final GamePlayer nearest = manager.getGamePlayer(uuid);
    final GameScheduler scheduler = this.game.getScheduler();
    nearest.disableJump(scheduler, 7 * 20L);
    nearest.setFreezeTicks(7 * 20);
    nearest.disableWalkWithFOVEffects(10 * 20);

    final Component msg = Message.FREEZE_ACTIVATE.build();
    manager.sendMessageToAllSurvivors(msg);
  }

  public void onIceSpiritTarget(final EntityTargetEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Zombie zombie)) {
      return;
    }

    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    final String target = container.get(Keys.ICE_SPIRIT_OWNER, PersistentDataType.STRING);
    if (target == null) {
      return;
    }

    this.handle(event, target, zombie);
  }

  public void onDeathHoundTarget(final EntityTargetEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Wolf wolf)) {
      return;
    }

    final PersistentDataContainer container = wolf.getPersistentDataContainer();
    final String target = container.get(Keys.DEATH_HOUND_OWNER, PersistentDataType.STRING);
    if (target == null) {
      return;
    }

    this.handle(event, target, wolf);
  }

  public void onDormagoggTarget(final EntityTargetEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Zombie zombie)) {
      return;
    }

    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    final String target = container.get(Keys.DORMAGOGG_OWNER, PersistentDataType.STRING);
    if (target == null) {
      return;
    }

    this.handle(event, target, zombie);
  }

  private void handle(final EntityTargetEvent event, final String target, final Mob entity) {

    final UUID uuid = UUID.fromString(target);
    final PlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(uuid)) {
      entity.remove();
      return;
    }

    final Location location = entity.getLocation();
    final GamePlayer nearest = manager.getNearestKiller(location);
    if (nearest == null) {
      entity.remove();
      return;
    }

    event.setCancelled(true);

    final Player internal = nearest.getInternalPlayer();
    entity.setTarget(internal);
  }
}

package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Wolf.Variant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class DeathHound extends KillerGadget implements Listener {

  private final Game game;

  public DeathHound(final Game game) {
    super(
        "death_hound",
        Material.BONE,
        Message.DEATH_HOUND_NAME.build(),
        Message.DEATH_HOUND_LORE.build(),
        16);
    this.game = game;
  }

  @EventHandler
  public void onTargetChange(final EntityTargetEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Wolf wolf)) {
      return;
    }

    final PersistentDataContainer container = wolf.getPersistentDataContainer();
    final String target = container.get(Keys.DEATH_HOUND_TARGET, PersistentDataType.STRING);
    if (target == null) {
      return;
    }

    final UUID uuid = UUID.fromString(target);
    final PlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(uuid)) {
      return;
    }

    final GamePlayer nearest = manager.getGamePlayer(uuid);
    event.setCancelled(true);
    nearest.apply(wolf::setTarget);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer nearest = manager.getNearestSurvivor(location);
    if (nearest == null) {
      return;
    }

    this.spawnWolf(location, player, nearest);
  }

  private void spawnWolf(final Location location, final Player owner, final GamePlayer target) {
    final World world = requireNonNull(location.getWorld());
    final Wolf wolf = world.spawn(location, Wolf.class, entity -> {
      this.customizeProperties(owner, entity);
      this.addPotionEffects(entity);
      this.addMetadata(entity, target);
    });
    target.apply(wolf::setTarget);
  }

  private void addMetadata(final Wolf entity, final GamePlayer target) {
    final UUID uuid = target.getUUID();
    final String data = uuid.toString();
    final PersistentDataContainer container = entity.getPersistentDataContainer();
    container.set(Keys.DEATH_HOUND_TARGET, PersistentDataType.STRING, data);
  }

  private void addPotionEffects(final Wolf entity) {
    entity.addPotionEffect(
        new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 1));
    entity.addPotionEffect(
        new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2));
  }

  private void customizeProperties(final Player owner, final Wolf entity) {
    entity.setOwner(owner);
    entity.setTamed(true);
    entity.setAngry(true);
    entity.setVariant(Variant.BLACK);
    entity.setInvulnerable(true);
  }
}

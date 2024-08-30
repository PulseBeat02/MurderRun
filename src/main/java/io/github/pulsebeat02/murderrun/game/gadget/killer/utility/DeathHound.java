package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.misc.TargetableEntity;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Wolf.Variant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class DeathHound extends KillerGadget implements Listener, TargetableEntity {

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
    final String target = container.get(Keys.DEATH_HOUND_OWNER, PersistentDataType.STRING);
    if (target == null) {
      return;
    }

    this.handle(event, target, wolf, false);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final Location location = player.getLocation();
    final GamePlayer nearest = manager.getNearestSurvivor(location);
    if (nearest == null) {
      return true;
    }

    this.spawnWolf(location, player, nearest);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GadgetConstants.DEATH_HOUND_SOUND);

    return false;
  }

  private void spawnWolf(
      final Location location, final GamePlayer owner, final GamePlayer nearest) {
    final World world = requireNonNull(location.getWorld());
    world.spawn(location, Wolf.class, entity -> {
      this.customizeProperties(entity, owner, nearest);
      this.addPotionEffects(entity);
      this.addMetadata(entity, owner);
    });
  }

  private void addMetadata(final Wolf entity, final GamePlayer owner) {
    final UUID uuid = owner.getUUID();
    final String data = uuid.toString();
    final PersistentDataContainer container = entity.getPersistentDataContainer();
    container.set(Keys.DEATH_HOUND_OWNER, PersistentDataType.STRING, data);
  }

  private void addPotionEffects(final Wolf entity) {
    entity.addPotionEffects(Set.of(
        new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2),
        new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 1)));
  }

  private void customizeProperties(
      final Wolf entity, final GamePlayer owner, final GamePlayer target) {
    final Player internal = owner.getInternalPlayer();
    final Player internalTarget = target.getInternalPlayer();
    entity.setOwner(internal);
    entity.setTarget(internalTarget);
    entity.setTamed(true);
    entity.setAngry(true);
    entity.setVariant(Variant.BLACK);
  }

  @Override
  public Game getGame() {
    return this.game;
  }
}

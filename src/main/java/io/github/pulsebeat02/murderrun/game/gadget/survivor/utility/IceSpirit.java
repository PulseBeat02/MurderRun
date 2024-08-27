package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.helper.TargetEntityInstance;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class IceSpirit extends SurvivorGadget implements Listener {

  private static final String ICE_SPIRIT_SOUND = "entity.zombie.ambient";

  private final TargetEntityInstance target;

  public IceSpirit(final Game game) {
    super(
        "ice_spirit",
        Material.ZOMBIE_HEAD,
        Message.ICE_SPIRIT_NAME.build(),
        Message.ICE_SPIRIT_LORE.build(),
        16);
    this.target = new TargetEntityInstance(game);
  }

  @EventHandler
  public void onTargetChange(final EntityTargetEvent event) {
    this.target.onIceSpiritTarget(event);
  }

  @EventHandler
  public void onEntityDamage(final EntityDamageByEntityEvent event) {
    this.target.onIceSpiritDamage(event);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GamePlayer owner = manager.getGamePlayer(player);
    world.spawn(location, Zombie.class, zombie -> {
      this.customizeAttributes(zombie);
      this.setTargetMetadata(owner, zombie);
      this.setEquipment(zombie);
    });

    final PlayerAudience audience = owner.getAudience();
    audience.playSound(ICE_SPIRIT_SOUND);
  }

  private void customizeAttributes(final Zombie zombie) {
    zombie.setInvulnerable(true);
    zombie.addPotionEffect(
        new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2));
    if (zombie instanceof final Ageable ageable) {
      ageable.setBaby();
    }
  }

  private void setTargetMetadata(final GamePlayer owner, final Zombie zombie) {
    final UUID uuid = owner.getUUID();
    final String data = uuid.toString();
    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    container.set(Keys.ICE_SPIRIT_OWNER, PersistentDataType.STRING, data);
  }

  private void setEquipment(final Zombie zombie) {
    final EntityEquipment equipment = requireNonNull(zombie.getEquipment());
    equipment.setHelmet(Item.create(Material.ICE));
    equipment.setChestplate(Item.create(Material.DIAMOND_CHESTPLATE));
    equipment.setLeggings(Item.create(Material.DIAMOND_LEGGINGS));
    equipment.setBoots(Item.create(Material.DIAMOND_BOOTS));
  }
}

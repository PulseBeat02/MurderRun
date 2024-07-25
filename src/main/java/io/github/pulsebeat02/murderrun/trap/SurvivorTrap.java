package io.github.pulsebeat02.murderrun.trap;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract non-sealed class SurvivorTrap extends MurderTrap {

  private final Material material;
  private final Component itemName;
  private final Component itemLore;
  private final Component announcement;

  public SurvivorTrap(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final Component announcement) {
    super(name);
    this.material = material;
    this.itemName = itemName;
    this.itemLore = itemLore;
    this.announcement = announcement;
  }

  public Component getAnnouncement() {
    return this.announcement;
  }

  public Component getItemLore() {
    return this.itemLore;
  }

  public Component getItemName() {
    return this.itemName;
  }

  public Material getMaterial() {
    return this.material;
  }

  @Override
  public ItemStack constructItemStack() {
    final String name = AdventureUtils.serializeComponentToLegacy(this.itemName);
    final String rawLore = AdventureUtils.serializeComponentToLegacy(this.itemLore);
    final List<String> lore = List.of(rawLore);
    final ItemStack stack = new ItemStack(this.material);
    final ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(name);
    meta.setLore(lore);
    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public void activate(final MurderGame game) {
    if (this.announcement == null) {
      return;
    }
    final MurderRun plugin = game.getPlugin();
    final AudienceHandler handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    final PlayerManager manager = game.getPlayerManager();
    final Collection<GamePlayer> players = manager.getParticipants();
    for (final GamePlayer gamePlayer : players) {
      final Player player = gamePlayer.getPlayer();
      final Audience audience = audiences.player(player);
      audience.sendMessage(this.announcement);
    }
  }
}

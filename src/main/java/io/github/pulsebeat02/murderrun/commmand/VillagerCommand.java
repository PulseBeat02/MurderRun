package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyTrader;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class VillagerCommand implements AnnotationCommandFeature {

  private static final Stream<String> TRADE_SUGGESTIONS = TradingUtils.getTradeSuggestions();

  private MurderRun plugin;
  private BukkitAudiences audiences;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
    this.registerFeature(plugin, parser);
  }

  @CommandDescription("murder_run.command.villager.spawn.info")
  @Command(value = "murder villager spawn [args]", requiredSender = Player.class)
  public void createMerchant(
      final Player sender,
      @Argument(value = "args", suggestions = "traps") @Default("0") final String[] args) {
    final Location location = sender.getLocation();
    final List<MerchantRecipe> recipes = TradingUtils.parseRecipes(args);
    final LobbyTrader trader = new LobbyTrader(location, recipes);
    trader.spawnVillager();
    this.sendSuccessMessage(sender, Locale.VILLAGER_SPAWN.build());
  }

  private void sendSuccessMessage(final Player player, final Component component) {
    final Audience audience = this.audiences.player(player);
    audience.sendMessage(component);
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public void setPlugin(final MurderRun plugin) {
    this.plugin = plugin;
  }

  @Suggestions("traps")
  public Stream<String> suggestTrades(
      final CommandContext<CommandSender> context, final String input) {
    return TRADE_SUGGESTIONS;
  }
}

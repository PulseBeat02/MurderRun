package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.gadget.GameTrap;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyTrade;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyTrader;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.incendo.cloud.annotations.*;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class VillagerCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
  }

  @CommandDescription("murder_run.command.villager.spawn.info")
  @Command(value = "murder villager spawn [args]", requiredSender = Player.class)
  public void createMerchant(
      final Player sender,
      @Argument(value = "args", suggestions = "traps") @Default("0") final String[] args) {
    final Location location = sender.getLocation();
    final List<MerchantRecipe> recipes = this.parseRecipeOptions(args);
    final LobbyTrader trader = new LobbyTrader(location, recipes);
    trader.spawnVillager();
    this.sendSuccessMessage(sender, Locale.VILLAGER_SPAWN.build());
  }

  private List<MerchantRecipe> parseRecipeOptions(final String[] args) {
    final List<MerchantRecipe> recipes = new ArrayList<>();
    for (final String trapName : args) {
      final LobbyTrade trade = LobbyTrade.get(trapName);
      if (trade != null) {
        final ItemStack ingredient = trade.getCost();
        final ItemStack reward = trade.getStack();
        final List<ItemStack> ingredients = List.of(ingredient);
        final MerchantRecipe recipe = new MerchantRecipe(reward, Integer.MAX_VALUE);
        recipe.setIngredients(ingredients);
        recipes.add(recipe);
      }
    }
    return recipes;
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

  public BukkitAudiences getAudiences() {
    return this.audiences;
  }

  public void setAudiences(final BukkitAudiences audiences) {
    this.audiences = audiences;
  }

  @Suggestions("traps")
  public Stream<String> suggestTrades(
      final CommandContext<CommandSender> context, final String input) {
    return Arrays.stream(GameTrap.values()).map(GameTrap::name);
  }
}

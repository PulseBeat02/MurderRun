package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.lobby.VillagerLobbyTrader;
import io.github.pulsebeat02.murderrun.lobby.VillagerTrade;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.trap.GameTrap;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class MurderVillagerCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceHandler handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
  }

  @CommandDescription("Creates a villager that sells traps")
  @Command(value = "murder villager spawn [args]", requiredSender = Player.class)
  public void createMerchant(
      final Player sender,
      @Argument(value = "args", suggestions = "traps") @Default("") final String[] args) {
    final Location location = sender.getLocation();
    final List<MerchantRecipe> recipes = this.parseRecipeOptions(args);
    final VillagerLobbyTrader trader = new VillagerLobbyTrader(location, recipes);
    trader.spawnVillager();
    this.sendSuccessMessage(sender, Locale.VILLAGER_SPAWN.build());
  }

  private void sendSuccessMessage(final Player player, final Component component) {
    final Audience audience = this.audiences.player(player);
    audience.sendMessage(component);
  }

  private List<MerchantRecipe> parseRecipeOptions(final String[] args) {
    final List<MerchantRecipe> recipes = new ArrayList<>();
    for (final String trapName : args) {
      final VillagerTrade trade = VillagerTrade.get(trapName);
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

  @Suggestions("traps")
  public Stream<String> suggestTrades(
      final CommandContext<CommandSender> context, final String input) {
    return Arrays.stream(GameTrap.values()).map(GameTrap::name);
  }
}
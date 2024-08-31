package io.github.pulsebeat02.murderrun.commmand;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class DebugCommand implements AnnotationCommandFeature {

  private BukkitAudiences audiences;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
  }

  @Permission("murderrun.command.debug.start")
  @Command(value = "murder debug start", requiredSender = Player.class)
  public void startDebugGame(final Player sender) {

    sender.performCommand("murder game create TestArena TestLobby");
    sender.performCommand("murder game set murderer PulseBeat_02");
    sender.performCommand("murder game invite Player1");
    sender.performCommand("murder game invite Player2");

    final Player other = requireNonNull(Bukkit.getPlayer("Player1"));
    other.performCommand("murder game join PulseBeat_02");

    final Player other1 = requireNonNull(Bukkit.getPlayer("Player2"));
    other1.performCommand("murder game join PulseBeat_02");

    sender.performCommand("murder game start");
  }

  @Permission("murderrun.command.debug.gadget")
  @Command(value = "murder debug gadget <gadgetName>", requiredSender = Player.class)
  public void debugGadget(
      final Player sender,
      @Argument(suggestions = "gadget-suggestions") @Quoted final String gadgetName) {

    final Audience audience = this.audiences.player(sender);
    final List<MerchantRecipe> allGadgets = TradingUtils.parseRecipes(gadgetName);
    if (this.checkIfInvalidGadget(audience, allGadgets)) {
      return;
    }

    final MerchantRecipe recipe = allGadgets.getFirst();
    final ItemStack result = recipe.getResult();
    final Location location = sender.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.dropItem(location, result);
  }

  public boolean checkIfInvalidGadget(final Audience audience, final List<MerchantRecipe> recipes) {
    if (recipes.isEmpty()) {
      final Component msg = Message.GADGET_RETRIEVE_ERROR.build();
      audience.sendMessage(msg);
      return true;
    }
    return false;
  }

  @Suggestions("gadget-suggestions")
  public Stream<String> suggestTrades(
      final CommandContext<CommandSender> context, final String input) {
    return TradingUtils.getTradeSuggestions();
  }
}

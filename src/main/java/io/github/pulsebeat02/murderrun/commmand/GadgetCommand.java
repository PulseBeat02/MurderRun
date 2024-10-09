package io.github.pulsebeat02.murderrun.commmand;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class GadgetCommand implements AnnotationCommandFeature {

  private BukkitAudiences audiences;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
  }

  @Permission("murderrun.command.gadget.retrieve-all")
  @CommandDescription("murderrun.command.gadget.retrieve.all.info")
  @Command(value = "murder gadget retrieve-all", requiredSender = Player.class)
  public void giveAllGadgets(final Player sender) {
    final PlayerInventory inventory = sender.getInventory();
    final List<MerchantRecipe> allGadgets = TradingUtils.getAllRecipes();
    final List<ItemStack> stacks = allGadgets.stream().map(MerchantRecipe::getResult).toList();
    final Location location = sender.getLocation();
    final World world = requireNonNull(location.getWorld());
    for (final ItemStack stack : stacks) {
      final Map<Integer, ItemStack> remaining = inventory.addItem(stack);
      if (remaining.isEmpty()) {
        continue;
      }
      final Collection<ItemStack> left = remaining.values();
      left.forEach(item -> world.dropItemNaturally(location, item));
    }
  }

  @Permission("murderrun.command.gadget.retrieve")
  @CommandDescription("murderrun.command.gadget.retrieve.info")
  @Command(value = "murder gadget retrieve <gadgetName>", requiredSender = Player.class)
  public void retrieveGadget(final Player sender, @Argument(suggestions = "gadget-suggestions") @Quoted final String gadgetName) {
    final Audience audience = this.audiences.player(sender);
    final List<MerchantRecipe> allGadgets = TradingUtils.parseRecipes(gadgetName);
    if (this.checkIfInvalidGadget(audience, allGadgets)) {
      return;
    }

    final Location location = sender.getLocation();
    final World world = requireNonNull(location.getWorld());
    final MerchantRecipe recipe = allGadgets.getFirst();
    final ItemStack result = recipe.getResult();
    final PlayerInventory inventory = sender.getInventory();
    final Map<Integer, ItemStack> remaining = inventory.addItem(result);
    final Collection<ItemStack> left = remaining.values();
    left.forEach(item -> world.dropItemNaturally(location, item));
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
  public Stream<String> suggestTrades(final CommandContext<CommandSender> context, final String input) {
    return TradingUtils.getTradeSuggestions();
  }
}

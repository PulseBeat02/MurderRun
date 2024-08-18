package io.github.pulsebeat02.murderrun.commmand;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;

public final class GadgetCommand implements AnnotationCommandFeature {

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {}

  @CommandDescription("murder_run.command.gadget.retrieve-all.info")
  @Command(value = "murder gadget retrieve-all", requiredSender = Player.class)
  public void giveAllGadgets(final Player sender) {

    if (!MurderRun.DEVELOPMENT_SWITCH) {
      return;
    }

    final PlayerInventory inventory = sender.getInventory();
    final List<MerchantRecipe> allGadgets = TradingUtils.getAllRecipes();
    final List<ItemStack> stacks =
        allGadgets.stream().map(MerchantRecipe::getResult).toList();
    final Location location = sender.getLocation();
    final World world = requireNonNull(location.getWorld());

    for (final ItemStack stack : stacks) {

      final Map<Integer, ItemStack> remaining = inventory.addItem(stack);
      if (remaining.isEmpty()) {
        continue;
      }

      final Collection<ItemStack> left = remaining.values();
      for (final ItemStack leftover : left) {
        world.dropItemNaturally(location, leftover);
      }
    }
  }
}

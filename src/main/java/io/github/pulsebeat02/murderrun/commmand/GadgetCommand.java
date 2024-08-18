package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.utils.TradingUtils;
import java.util.List;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;

public final class GadgetCommand implements AnnotationCommandFeature {

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

  @CommandDescription("murder_run.command.gadget.retrieve-all")
  @Command(value = "murder gadget retrieve-all", requiredSender = Player.class)
  public void giveAllGadgets(final Player sender) {

    if (!MurderRun.DEVELOPMENT_SWITCH) {
      return;
    }

    final PlayerInventory inventory = sender.getInventory();
    final List<MerchantRecipe> allGadgets = TradingUtils.getAllRecipes();
    for (MerchantRecipe recipe : allGadgets) {
      final ItemStack result = recipe.getResult();
      inventory.addItem(result);
    }
  }
}

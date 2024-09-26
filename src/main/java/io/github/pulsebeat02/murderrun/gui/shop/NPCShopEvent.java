package io.github.pulsebeat02.murderrun.gui.shop;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.PluginManager;

public final class NPCShopEvent implements Listener {

  private final BukkitAudiences audiences;
  private final MurderRun plugin;

  public NPCShopEvent(final MurderRun plugin) {
    final AudienceProvider provider = plugin.getAudience();
    this.plugin = plugin;
    this.audiences = provider.retrieve();
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    manager.registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onNPCRightClick(final NPCRightClickEvent event) {

    final NPC npc = event.getNPC();
    final MetadataStore store = npc.data();
    if (!store.has("murderrun-gui")) {
      return;
    }

    final boolean value = store.get("murderrun-gui");
    final Player clicker = event.getClicker();
    final PersistentDataContainer container = clicker.getPersistentDataContainer();
    final boolean isKiller = container.has(Keys.KILLER_ROLE);
    if (isKiller && value || !isKiller && !value) {
      final Component msg = Message.SHOP_NPC_ERROR.build();
      final Audience audience = this.audiences.player(clicker);
      audience.sendMessage(msg);
      return;
    }

    final GadgetShopGui gui = new GadgetShopGui(this.plugin, value);
    gui.showGUI(clicker);
  }
}

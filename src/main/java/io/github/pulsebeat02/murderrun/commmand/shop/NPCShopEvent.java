package io.github.pulsebeat02.murderrun.commmand.shop;

import io.github.pulsebeat02.murderrun.MurderRun;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public final class NPCShopEvent implements Listener {

  private final MurderRun plugin;

  public NPCShopEvent(final MurderRun plugin) {
    this.plugin = plugin;
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
    final GadgetShopGui gui = new GadgetShopGui(this.plugin, value);
    final Player clicker = event.getClicker();
    gui.showGUI(clicker);
  }
}

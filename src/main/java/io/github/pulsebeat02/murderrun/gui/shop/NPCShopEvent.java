/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;

public final class NPCShopEvent implements Listener {

  private final BukkitAudiences audiences;
  private final MurderRun plugin;

  public NPCShopEvent(final MurderRun plugin) {
    final AudienceProvider provider = plugin.getAudience();
    this.plugin = plugin;
    this.audiences = provider.retrieve();
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
    if (isKiller == value) {
      final Component msg = Message.SHOP_NPC_ERROR.build();
      final Audience audience = this.audiences.player(clicker);
      audience.sendMessage(msg);
      return;
    }

    final GadgetShopGui gui = new GadgetShopGui(this.plugin, value);
    gui.showGUI(clicker);
  }
}

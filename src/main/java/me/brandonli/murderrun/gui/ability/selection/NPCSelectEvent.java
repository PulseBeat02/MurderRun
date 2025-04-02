/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.gui.ability.selection;

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.lobby.GameManager;
import me.brandonli.murderrun.game.lobby.PreGameManager;
import me.brandonli.murderrun.game.lobby.PreGamePlayerManager;
import me.brandonli.murderrun.game.lobby.player.PlayerSelection;
import me.brandonli.murderrun.game.lobby.player.PlayerSelectionManager;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.immutable.Keys;
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

public final class NPCSelectEvent implements Listener {

  private final BukkitAudiences audiences;
  private final MurderRun plugin;

  public NPCSelectEvent(final MurderRun plugin) {
    final AudienceProvider provider = plugin.getAudience();
    this.plugin = plugin;
    this.audiences = provider.retrieve();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onNPCRightClick(final NPCRightClickEvent event) {
    final NPC npc = event.getNPC();
    final MetadataStore store = npc.data();
    if (!store.has("murderrun-select")) {
      return;
    }

    final boolean value = store.get("murderrun-select");
    final Player clicker = event.getClicker();
    final PersistentDataContainer container = clicker.getPersistentDataContainer();
    final boolean isKiller = container.has(Keys.KILLER_ROLE);
    final Audience audience = this.audiences.player(clicker);
    if (isKiller == value) {
      final Component msg = Message.SELECT_NPC_ERROR.build();
      audience.sendMessage(msg);
      return;
    }

    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager preGameManager = manager.getGame(clicker);
    if (preGameManager == null) {
      audience.sendMessage(Message.GAME_INVALID_ERROR.build());
      return;
    }

    final PreGamePlayerManager playerManager = preGameManager.getPlayerManager();
    final PlayerSelectionManager selectionManager = playerManager.getSelectionManager();
    final PlayerSelection selection = selectionManager.getOrCreateSelection(clicker, isKiller);
    final AbilitySelectGui gui = selection.getAbilitySelectGui();
    gui.open(clicker);
  }
}

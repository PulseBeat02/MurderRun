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
package me.brandonli.murderrun.commmand;

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.gui.CentralGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public final class GuiCommand implements AnnotationCommandFeature {

  private MurderRun plugin;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    this.plugin = plugin;
  }

  @Permission("murderrun.command.gui")
  @CommandDescription("murderrun.command.gui.info")
  @Command(value = "murder gui", requiredSender = Player.class)
  public void openGui(final Player sender) {
    final CentralGui gui = new CentralGui(this.plugin, sender);
    gui.update();
    gui.open(sender);
  }
}

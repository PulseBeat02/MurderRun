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
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.resourcepack.provider.ResourcePackProvider;
import me.brandonli.murderrun.utils.ComponentUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public final class ResourcesCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
  }

  @Permission("murderrun.command.resources")
  @Command(value = "murder resources", requiredSender = Player.class)
  @CommandDescription("murderrun.command.resources.info")
  public void sendResourcePack(final Player sender) {
    this.setResourcePack(sender);
  }

  private void setResourcePack(final Player player) {
    final ResourcePackProvider daemon = this.plugin.getProvider();
    final ResourcePackRequest request = daemon.getResourcePackRequest();
    ComponentUtils.sendPacksLegacy(player, request);
  }
}

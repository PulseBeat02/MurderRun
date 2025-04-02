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

import java.util.concurrent.CompletableFuture;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.DumpUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public final class DumpCommand implements AnnotationCommandFeature {

  private BukkitAudiences audiences;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
  }

  @Permission("murderrun.command.dump")
  @Command(value = "murder dump", requiredSender = CommandSender.class)
  @CommandDescription("murderrun.command.dump.info")
  public void startDebugGame(final CommandSender sender) {
    final Audience audience = this.audiences.sender(sender);
    audience.sendMessage(Message.LOAD_DUMP.build());
    CompletableFuture.supplyAsync(DumpUtils::createAndUploadDump).thenAccept(url -> {
      final Component component = Message.SEND_DUMP.build(url);
      audience.sendMessage(component);
    });
  }
}

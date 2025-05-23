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

import io.github.classgraph.ScanResult;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.utils.ClassGraphUtils;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public final class AnnotationParserHandler {

  private final CommandManager<CommandSender> manager;
  private final AnnotationParser<CommandSender> parser;
  private final MurderRun plugin;

  public AnnotationParserHandler(final MurderRun plugin) {
    this.plugin = plugin;
    this.manager = this.getCommandManager(plugin);
    this.parser = this.getAnnotationParser(this.manager);
  }

  private AnnotationParser<CommandSender> getAnnotationParser(
    @UnderInitialization AnnotationParserHandler this,
    final CommandManager<CommandSender> manager
  ) {
    final Class<CommandSender> sender = CommandSender.class;
    final AnnotationParser<CommandSender> parser = new AnnotationParser<>(manager, sender);
    parser.descriptionMapper(RichDescription::translatable);
    return parser;
  }

  private CommandManager<CommandSender> getCommandManager(@UnderInitialization AnnotationParserHandler this, final MurderRun plugin) {
    final ExecutionCoordinator<CommandSender> coordinator = ExecutionCoordinator.simpleCoordinator();
    final LegacyPaperCommandManager<CommandSender> manager = LegacyPaperCommandManager.createNative(plugin, coordinator);
    this.registerBrigadierCapability(manager);
    return manager;
  }

  private void registerBrigadierCapability(
    @UnderInitialization AnnotationParserHandler this,
    final LegacyPaperCommandManager<CommandSender> manager
  ) {
    if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
      manager.registerBrigadier();
    }
  }

  public CommandManager<CommandSender> getManager() {
    return this.manager;
  }

  public void registerCommands() {
    try {
      final ScanResult result = ClassGraphUtils.getCachedScanResult();
      final List<Class<?>> features = result.getClassesImplementing(AnnotationCommandFeature.class).loadClasses();
      final MethodHandles.Lookup lookup = MethodHandles.lookup();
      final MethodType type = MethodType.methodType(void.class);
      for (final Class<?> feature : features) {
        final MethodHandle constructor = lookup.findConstructor(feature, type);
        final AnnotationCommandFeature instance = (AnnotationCommandFeature) constructor.invoke();
        instance.registerFeature(this.plugin, this.parser);
        this.parser.parse(instance);
      }
    } catch (final Throwable e) {
      throw new AssertionError(e);
    }
  }
}

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
package io.github.pulsebeat02.murderrun.commmand;

import io.github.classgraph.ScanResult;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.utils.ClassGraphUtils;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
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

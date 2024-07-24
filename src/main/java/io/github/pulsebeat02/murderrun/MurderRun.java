package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.arena.MurderArenaManager;
import io.github.pulsebeat02.murderrun.commmand.AnnotationParserHandler;
import io.github.pulsebeat02.murderrun.config.PluginConfiguration;
import io.github.pulsebeat02.murderrun.data.ArenaDataManager;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.resourcepack.server.PackHostingDaemon;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderRun extends JavaPlugin {

  /*

  TODO:

  - Add Innocent Traps for Survival
  - Add Murderer Traps for Killing
  - Add Commands (Villager Spawns, Setting Game Configuration)
  - Add Villager Trades for Traps
  - Give Murderer Sword
  - Make Textures Transparent

   */

  private PluginConfiguration configuration;
  private AudienceHandler audience;
  private PackHostingDaemon daemon;
  private ArenaDataManager arenaDataManager;
  private MurderArenaManager arenaManager;
  private AnnotationParserHandler commandHandler;
  private Metrics metrics;

  @Override
  public void onEnable() {
    this.readPluginData();
    this.startHostingDaemon();
    this.registerCommmands();
    this.registerAudienceHandler();
    this.enableBStats();
  }

  @Override
  public void onDisable() {
    this.writePluginData();
    this.stopHostingDaemon();
    this.shutdownMetrics();
  }

  private void shutdownMetrics() {
    this.metrics.shutdown();
  }

  private void enableBStats() {
    this.metrics = new Metrics(this, 22728);
  }

  private void registerAudienceHandler() {
    this.audience = new AudienceHandler(this);
  }

  private void registerCommmands() {
    this.commandHandler = new AnnotationParserHandler(this);
    this.commandHandler.registerCommands();
  }

  private void stopHostingDaemon() {
    this.daemon.stop();
  }

  private void startHostingDaemon() {
    final String hostName = this.configuration.getHostName();
    final int port = this.configuration.getPort();
    this.daemon = new PackHostingDaemon(hostName, port);
    this.daemon.start();
  }

  private void readPluginData() {
    this.configuration = new PluginConfiguration(this);
    this.arenaDataManager = new ArenaDataManager(this);
    this.arenaManager = this.arenaDataManager.deserialize();
    this.configuration.deserialize();
  }

  private void writePluginData() {
    this.arenaDataManager.serialize(this.arenaManager);
    this.configuration.serialize();
  }

  public PluginConfiguration getConfiguration() {
    return this.configuration;
  }

  public AudienceHandler getAudience() {
    return this.audience;
  }

  public PackHostingDaemon getDaemon() {
    return this.daemon;
  }

  public MurderArenaManager getArenaManager() {
    return this.arenaManager;
  }

  public ArenaDataManager getArenaDataManager() {
    return this.arenaDataManager;
  }

  public AnnotationParserHandler getCommandHandler() {
    return this.commandHandler;
  }
}

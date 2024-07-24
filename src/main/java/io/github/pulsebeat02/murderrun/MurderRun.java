package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.arena.MurderArenaManager;
import io.github.pulsebeat02.murderrun.commmand.AnnotationParserHandler;
import io.github.pulsebeat02.murderrun.config.PluginConfiguration;
import io.github.pulsebeat02.murderrun.data.MurderArenaDataManager;
import io.github.pulsebeat02.murderrun.data.MurderLobbyDataManager;
import io.github.pulsebeat02.murderrun.lobby.MurderLobbyManager;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.resourcepack.server.PackHostingDaemon;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderRun extends JavaPlugin {

  /*

  TODO:

  - Add Innocent Traps for Survival
  - Add Murderer Traps for Killing
  - Add Commands (Villager Spawns, Setting Game Configuration, Help Command)
  - Add Villager Trades for Traps

   */

  private PluginConfiguration configuration;
  private AudienceHandler audience;
  private PackHostingDaemon daemon;
  private MurderArenaDataManager murderArenaDataManager;
  private MurderLobbyDataManager murderLobbyDataManager;
  private MurderArenaManager arenaManager;
  private MurderLobbyManager lobbyManager;
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
    this.updatePluginData();
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
    this.murderArenaDataManager = new MurderArenaDataManager(this);
    this.murderLobbyDataManager = new MurderLobbyDataManager(this);
    this.arenaManager = this.murderArenaDataManager.deserialize();
    this.lobbyManager = this.murderLobbyDataManager.deserialize();
    this.configuration.deserialize();
  }

  public void updatePluginData() {
    this.murderArenaDataManager.serialize(this.arenaManager);
    this.murderLobbyDataManager.serialize(this.lobbyManager);
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

  public MurderArenaDataManager getArenaDataManager() {
    return this.murderArenaDataManager;
  }

  public AnnotationParserHandler getCommandHandler() {
    return this.commandHandler;
  }

  public MurderLobbyDataManager getLobbyDataManager() {
    return this.murderLobbyDataManager;
  }

  public MurderLobbyManager getLobbyManager() {
    return this.lobbyManager;
  }

  public Metrics getMetrics() {
    return this.metrics;
  }
}

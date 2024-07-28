package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.arena.MurderArenaManager;
import io.github.pulsebeat02.murderrun.commmand.AnnotationParserHandler;
import io.github.pulsebeat02.murderrun.config.PluginConfiguration;
import io.github.pulsebeat02.murderrun.data.MurderArenaDataManager;
import io.github.pulsebeat02.murderrun.data.MurderLobbyDataManager;
import io.github.pulsebeat02.murderrun.lobby.MurderLobbyManager;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.reflect.NMSHandler;
import io.github.pulsebeat02.murderrun.resourcepack.server.PackHostingDaemon;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderRun extends JavaPlugin {

  private static final int BSTATS_SERVER_ID = 22728;

  /*

  TODO:

  - Add Innocent Traps for Survival
  - Add Murderer Traps for Killing
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
  public void onDisable() {
    this.dependencyCheck();
    this.updatePluginData();
    this.stopHostingDaemon();
    this.shutdownMetrics();
    this.shutdownAudience();
    this.sendConsoleMessage(Locale.PLUGIN_DISABLE.build());
  }

  @Override
  public void onEnable() {
    this.registerAudienceHandler();
    this.dependencyCheck();
    this.registerNMS();
    this.readPluginData();
    this.startHostingDaemon();
    this.registerCommands();
    this.enableBStats();
    this.sendConsoleMessage(Locale.PLUGIN_ENABLE.build());
  }

  private void dependencyCheck() {
    final PluginManager manager = Bukkit.getPluginManager();
    final boolean we = manager.isPluginEnabled("WorldEdit");
    if (!we) {
      final Component error = Locale.PLUGIN_DEPENDENCY_ERROR.build();
      this.sendConsoleMessage(error);
      manager.disablePlugin(this);
    }
  }

  private void sendConsoleMessage(final Component component) {
    final BukkitAudiences audiences = this.audience.retrieve();
    final Audience console = audiences.console();
    console.sendMessage(component);
  }

  private void shutdownAudience() {
    this.audience.shutdown();
  }

  private void registerNMS() {
    NMSHandler.init();
  }

  private void readPluginData() {
    this.configuration = new PluginConfiguration(this);
    this.murderArenaDataManager = new MurderArenaDataManager(this);
    this.murderLobbyDataManager = new MurderLobbyDataManager(this);
    this.configuration.deserialize();
    this.arenaManager = this.murderArenaDataManager.deserialize();
    this.lobbyManager = this.murderLobbyDataManager.deserialize();
  }

  private void startHostingDaemon() {
    final String hostName = this.configuration.getHostName();
    final int port = this.configuration.getPort();
    this.daemon = new PackHostingDaemon(hostName, port);
    this.daemon.buildServer();
    this.daemon.start();
  }

  private void registerCommands() {
    this.commandHandler = new AnnotationParserHandler(this);
    this.commandHandler.registerCommands();
  }

  private void registerAudienceHandler() {
    this.audience = new AudienceHandler(this);
  }

  private void enableBStats() {
    this.metrics = new Metrics(this, BSTATS_SERVER_ID);
  }

  public void updatePluginData() {
    this.murderArenaDataManager.serialize(this.arenaManager);
    this.murderLobbyDataManager.serialize(this.lobbyManager);
    this.configuration.serialize();
  }

  private void stopHostingDaemon() {
    this.daemon.stop();
  }

  private void shutdownMetrics() {
    this.metrics.shutdown();
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

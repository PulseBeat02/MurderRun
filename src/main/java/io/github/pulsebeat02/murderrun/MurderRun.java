package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.commmand.AnnotationParserHandler;
import io.github.pulsebeat02.murderrun.data.ArenaDataConfigurationConfigurationMapper;
import io.github.pulsebeat02.murderrun.data.LobbyDataConfigurationConfigurationMapper;
import io.github.pulsebeat02.murderrun.data.PluginDataConfigurationMapper;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.locale.TranslationManager;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import io.github.pulsebeat02.murderrun.resourcepack.server.ResourcePackDaemon;
import io.github.pulsebeat02.murderrun.utils.Keys;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
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

  - Create all killer gadgets

   */

  private PluginDataConfigurationMapper configuration;
  private AudienceProvider audience;
  private ResourcePackDaemon daemon;
  private ArenaDataConfigurationConfigurationMapper arenaDataConfigurationMapper;
  private LobbyDataConfigurationConfigurationMapper lobbyDataConfigurationMapper;
  private ArenaManager arenaManager;
  private LobbyManager lobbyManager;
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
    this.registerLookUpMaps();
    this.dependencyCheck();
    this.registerNMS();
    this.readPluginData();
    this.startHostingDaemon();
    this.registerCommands();
    this.enableBStats();
    this.sendConsoleMessage(Locale.PLUGIN_ENABLE.build());
  }

  private void registerLookUpMaps() {
    TranslationManager.init(this);
    MapUtils.init(this);
    Keys.init(this);
    GadgetLoadingMechanism.init(this);
  }

  private void dependencyCheck() {
    final PluginManager manager = Bukkit.getPluginManager();
    final boolean loaded =
        manager.isPluginEnabled("WorldEdit") && manager.isPluginEnabled("citizens");
    if (!loaded) {
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
    PacketToolsProvider.init();
  }

  private void readPluginData() {
    this.configuration = new PluginDataConfigurationMapper(this);
    this.arenaDataConfigurationMapper = new ArenaDataConfigurationConfigurationMapper(this);
    this.lobbyDataConfigurationMapper = new LobbyDataConfigurationConfigurationMapper(this);
    this.configuration.deserialize();
    this.arenaManager = this.arenaDataConfigurationMapper.deserialize();
    this.lobbyManager = this.lobbyDataConfigurationMapper.deserialize();
  }

  private void startHostingDaemon() {
    final String hostName = this.configuration.getHostName();
    final int port = this.configuration.getPort();
    this.daemon = new ResourcePackDaemon(hostName, port);
    this.daemon.buildPack();
    this.daemon.start();
  }

  private void registerCommands() {
    this.commandHandler = new AnnotationParserHandler(this);
    this.commandHandler.registerCommands(this);
  }

  private void registerAudienceHandler() {
    this.audience = new AudienceProvider(this);
  }

  private void enableBStats() {
    this.metrics = new Metrics(this, BSTATS_SERVER_ID);
  }

  public void updatePluginData() {
    this.arenaDataConfigurationMapper.serialize(this.arenaManager);
    this.lobbyDataConfigurationMapper.serialize(this.lobbyManager);
    this.configuration.serialize();
  }

  private void stopHostingDaemon() {
    this.daemon.stop();
  }

  private void shutdownMetrics() {
    this.metrics.shutdown();
  }

  public PluginDataConfigurationMapper getConfiguration() {
    return this.configuration;
  }

  public AudienceProvider getAudience() {
    return this.audience;
  }

  public ResourcePackDaemon getDaemon() {
    return this.daemon;
  }

  public ArenaManager getArenaManager() {
    return this.arenaManager;
  }

  public ArenaDataConfigurationConfigurationMapper getArenaDataManager() {
    return this.arenaDataConfigurationMapper;
  }

  public AnnotationParserHandler getCommandHandler() {
    return this.commandHandler;
  }

  public LobbyDataConfigurationConfigurationMapper getLobbyDataManager() {
    return this.lobbyDataConfigurationMapper;
  }

  public LobbyManager getLobbyManager() {
    return this.lobbyManager;
  }

  public Metrics getMetrics() {
    return this.metrics;
  }
}

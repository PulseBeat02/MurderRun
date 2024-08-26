package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.commmand.AnnotationParserHandler;
import io.github.pulsebeat02.murderrun.commmand.GameShutdownManager;
import io.github.pulsebeat02.murderrun.data.ArenaDataJSONMapper;
import io.github.pulsebeat02.murderrun.data.LobbyDataJSONMapper;
import io.github.pulsebeat02.murderrun.data.PluginDataConfigurationMapper;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import io.github.pulsebeat02.murderrun.resourcepack.provider.MCPackHosting;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ProviderMethod;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ResourcePackProvider;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ServerPackHosting;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderRun extends JavaPlugin {

  /*

  DEBUG

  - Finish debugging all command gadgets
  - Test all survivor gadgets
  - Give killer gadgets

  - Debug all killer traps

   */

  private static final int BSTATS_SERVER_ID = 22728;

  private PluginDataConfigurationMapper configuration;
  private AudienceProvider audience;
  private ArenaDataJSONMapper arenaDataConfigurationMapper;
  private LobbyDataJSONMapper lobbyDataConfigurationMapper;
  private ArenaManager arenaManager;
  private LobbyManager lobbyManager;
  private Metrics metrics;
  private GameShutdownManager gameShutdownManager;
  private ResourcePackProvider provider;

  @Override
  public void onDisable() {
    this.shutdownGames();
    this.updatePluginData();
    this.shutdownPluginData();
    this.stopHostingDaemon();
    this.shutdownMetrics();
    this.shutdownAudience();
  }

  @Override
  public void onEnable() {
    this.registerAudienceHandler();
    this.registerLookUpMaps();
    this.readPluginData();
    this.handlePackHosting();
    this.registerCommands();
    this.enableBStats();
  }

  private void shutdownGames() {
    this.gameShutdownManager.shutdown();
  }

  private void registerLookUpMaps() {
    GadgetLoadingMechanism.init();
    PacketToolsProvider.init();
  }

  private void shutdownAudience() {
    this.audience.shutdown();
  }

  private void readPluginData() {
    this.configuration = new PluginDataConfigurationMapper(this);
    this.arenaDataConfigurationMapper = new ArenaDataJSONMapper(this);
    this.lobbyDataConfigurationMapper = new LobbyDataJSONMapper(this);
    this.configuration.deserialize();
    this.arenaManager = this.arenaDataConfigurationMapper.deserialize();
    this.lobbyManager = this.lobbyDataConfigurationMapper.deserialize();
  }

  private void handlePackHosting() {
    final ProviderMethod method = this.configuration.getProviderMethod();
    switch (method) {
      case MC_PACK_HOSTING -> this.provider = new MCPackHosting();
      case LOCALLY_HOSTED_DAEMON -> {
        final String hostName = this.configuration.getHostName();
        final int port = this.configuration.getPort();
        this.provider = new ServerPackHosting(hostName, port);
      }
      default -> {} // Do nothing
    }
    this.provider.start();
  }

  private void registerCommands() {
    final AnnotationParserHandler commandHandler = new AnnotationParserHandler(this);
    commandHandler.registerCommands();
    this.gameShutdownManager = new GameShutdownManager();
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

  public void shutdownPluginData() {
    this.arenaDataConfigurationMapper.shutdown();
    this.lobbyDataConfigurationMapper.shutdown();
    this.configuration.shutdown();
  }

  private void stopHostingDaemon() {
    this.provider.shutdown();
  }

  private void shutdownMetrics() {
    this.metrics.shutdown();
  }

  public AudienceProvider getAudience() {
    return this.audience;
  }

  public ResourcePackProvider getProvider() {
    return this.provider;
  }

  public ArenaManager getArenaManager() {
    return this.arenaManager;
  }

  public LobbyManager getLobbyManager() {
    return this.lobbyManager;
  }

  public GameShutdownManager getGameShutdownManager() {
    return this.gameShutdownManager;
  }
}

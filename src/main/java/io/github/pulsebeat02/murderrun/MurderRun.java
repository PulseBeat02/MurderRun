package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.commmand.AnnotationParserHandler;
import io.github.pulsebeat02.murderrun.commmand.GameShutdownManager;
import io.github.pulsebeat02.murderrun.data.RelationalDataImplAssignation;
import io.github.pulsebeat02.murderrun.data.yaml.ConfigurationManager;
import io.github.pulsebeat02.murderrun.data.yaml.PluginDataConfigurationMapper;
import io.github.pulsebeat02.murderrun.dependency.DependencyManager;
import io.github.pulsebeat02.murderrun.game.Capabilities;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.PlayerResourcePackChecker;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.gadget.GlobalGadgetRegistry;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.papi.MurderRunExpansion;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import io.github.pulsebeat02.murderrun.resourcepack.provider.PackProviderMethod;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ResourcePackProvider;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderRun extends JavaPlugin {

  /*

  - Rewrite all messy code
  - Trial Database Support (test)
  - Fix nodejs support

   */

  private static final int BSTATS_SERVER_ID = 22728;

  private PluginDataConfigurationMapper configuration;
  private AudienceProvider audience;

  private ConfigurationManager<ArenaManager> arenaDataConfigurationMapper;
  private ConfigurationManager<LobbyManager> lobbyDataConfigurationMapper;
  private ConfigurationManager<StatisticsManager> statisticsConfigurationMapper;

  private ArenaManager arenaManager;
  private LobbyManager lobbyManager;
  private StatisticsManager statisticsManager;

  private Metrics metrics;
  private GameShutdownManager gameShutdownManager;
  private PlayerResourcePackChecker playerResourcePackChecker;
  private ResourcePackProvider provider;
  private MurderRunExpansion expansion;

  @Override
  public void onLoad() {
    this.installDependencies();
  }

  @Override
  public void onDisable() {
    this.shutdownGames();
    this.unregisterExpansion();
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
    this.registerGameUtilities();
    this.registerExpansion();
    this.enableBStats();
  }

  private void installDependencies() {
    final DependencyManager manager = new DependencyManager();
    manager.installDependencies();
  }

  private void unregisterExpansion() {
    if (Capabilities.PLACEHOLDER_API.isEnabled() && this.expansion != null) {
      this.expansion.unregister();
    }
  }

  private void registerExpansion() {
    if (Capabilities.PLACEHOLDER_API.isEnabled()) {
      this.expansion = new MurderRunExpansion(this);
      this.expansion.register();
    }
  }

  private void shutdownGames() {
    if (this.gameShutdownManager != null) {
      this.gameShutdownManager.shutdown();
    }
  }

  private void registerLookUpMaps() {
    GlobalGadgetRegistry.init();
    PacketToolsProvider.init();
    GameProperties.init();
  }

  private void shutdownAudience() {
    this.audience.shutdown();
  }

  private void readPluginData() {
    this.configuration = new PluginDataConfigurationMapper(this);
    this.configuration.deserialize();
    this.handleRelationalDataManagement();
  }

  private void handleRelationalDataManagement() {
    final RelationalDataImplAssignation relationalDataImplAssignation = new RelationalDataImplAssignation(this);
    this.arenaDataConfigurationMapper = relationalDataImplAssignation.getArenas();
    this.lobbyDataConfigurationMapper = relationalDataImplAssignation.getLobbies();
    this.statisticsConfigurationMapper = relationalDataImplAssignation.getStatistics();
    this.arenaManager = this.arenaDataConfigurationMapper.deserialize();
    this.lobbyManager = this.lobbyDataConfigurationMapper.deserialize();
    this.statisticsManager = this.statisticsConfigurationMapper.deserialize();
  }

  private void handlePackHosting() {
    final PackProviderMethod packProviderMethod = new PackProviderMethod(this);
    this.provider = packProviderMethod.getProvider();
    this.provider.start();
  }

  private void registerCommands() {
    final AnnotationParserHandler commandHandler = new AnnotationParserHandler(this);
    commandHandler.registerCommands();
  }

  private void registerGameUtilities() {
    this.gameShutdownManager = new GameShutdownManager();
    this.playerResourcePackChecker = new PlayerResourcePackChecker();
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
    this.statisticsConfigurationMapper.serialize(this.statisticsManager);
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

  public PlayerResourcePackChecker getPlayerResourcePackChecker() {
    return this.playerResourcePackChecker;
  }

  public PluginDataConfigurationMapper getConfiguration() {
    return this.configuration;
  }

  public StatisticsManager getStatisticsManager() {
    return this.statisticsManager;
  }

  public MurderRunExpansion getExpansion() {
    return this.expansion;
  }
}

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
package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.commmand.AnnotationParserHandler;
import io.github.pulsebeat02.murderrun.commmand.GameShutdownManager;
import io.github.pulsebeat02.murderrun.data.RelationalDataProvider;
import io.github.pulsebeat02.murderrun.data.yaml.ConfigurationManager;
import io.github.pulsebeat02.murderrun.data.yaml.PluginDataConfigurationMapper;
import io.github.pulsebeat02.murderrun.dependency.DependencyManager;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.PlayerResourcePackChecker;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.arena.drops.TerrainDropAnalyzer;
import io.github.pulsebeat02.murderrun.game.capability.Capabilities;
import io.github.pulsebeat02.murderrun.game.extension.papi.MurderRunExpansion;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetRegistry;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import io.github.pulsebeat02.murderrun.gui.gadget.GadgetTestingGui;
import io.github.pulsebeat02.murderrun.gui.shop.GadgetShopGui;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import io.github.pulsebeat02.murderrun.resourcepack.provider.PackProviderMethod;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ResourcePackProvider;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderRun extends JavaPlugin {

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
    this.readPluginData();
    this.registerLookUpMaps();
    this.handlePackHosting();
    this.registerCommands();
    this.registerGameUtilities();
    this.registerExpansion();
    this.enableMetrics();
  }

  private void installDependencies() {
    final DependencyManager manager = new DependencyManager();
    manager.installDependencies();
  }

  private void unregisterExpansion() {
    if (Capabilities.PLACEHOLDERAPI.isEnabled() && this.expansion != null) {
      this.expansion.unregister();
    }
  }

  private void registerExpansion() {
    if (Capabilities.PLACEHOLDERAPI.isEnabled()) {
      this.expansion = new MurderRunExpansion(this);
      this.expansion.register();
    }
  }

  private void shutdownGames() {
    if (this.gameShutdownManager != null) {
      this.gameShutdownManager.forceShutdown();
    }
  }

  private void registerLookUpMaps() {
    GadgetRegistry.init();
    PacketToolsProvider.init();
    GameProperties.init();
    GadgetShopGui.init();
    GadgetTestingGui.init();
    TerrainDropAnalyzer.init();
  }

  private void shutdownAudience() {
    if (this.audience != null) {
      this.audience.shutdown();
    }
  }

  private void readPluginData() {
    this.configuration = new PluginDataConfigurationMapper(this);
    this.configuration.deserialize();
    this.handleRelationalDataManagement();
  }

  private void handleRelationalDataManagement() {
    final RelationalDataProvider relationalDataProvider = new RelationalDataProvider(this);
    this.arenaDataConfigurationMapper = relationalDataProvider.getArenas();
    this.lobbyDataConfigurationMapper = relationalDataProvider.getLobbies();
    this.statisticsConfigurationMapper = relationalDataProvider.getStatistics();
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
    this.playerResourcePackChecker = new PlayerResourcePackChecker(this);
    this.playerResourcePackChecker.registerEvents();
  }

  private void registerAudienceHandler() {
    this.audience = new AudienceProvider(this);
  }

  private void enableMetrics() {
    System.setProperty("bstats.relocatecheck", "false");
    this.metrics = new Metrics(this, BSTATS_SERVER_ID);
  }

  public void updatePluginData() {
    if (this.arenaDataConfigurationMapper != null) {
      this.arenaDataConfigurationMapper.serialize(this.arenaManager);
      this.lobbyDataConfigurationMapper.serialize(this.lobbyManager);
      this.statisticsConfigurationMapper.serialize(this.statisticsManager);
      this.configuration.serialize();
    }
  }

  public void shutdownPluginData() {
    if (this.arenaDataConfigurationMapper != null) {
      this.arenaDataConfigurationMapper.shutdown();
      this.lobbyDataConfigurationMapper.shutdown();
      this.configuration.shutdown();
    }
  }

  private void stopHostingDaemon() {
    if (this.provider != null) {
      this.provider.shutdown();
    }
  }

  private void shutdownMetrics() {
    if (this.metrics != null) {
      this.metrics.shutdown();
    }
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

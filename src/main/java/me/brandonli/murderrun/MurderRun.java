/*

MIT License

Copyright (c) 2025 Brandon Li

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
package me.brandonli.murderrun;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import java.util.concurrent.atomic.AtomicBoolean;
import me.brandonli.murderrun.api.event.ApiEventBus;
import me.brandonli.murderrun.api.event.EventBusProvider;
import me.brandonli.murderrun.api.event.EventBusTests;
import me.brandonli.murderrun.commmand.AnnotationParserHandler;
import me.brandonli.murderrun.commmand.GameShutdownManager;
import me.brandonli.murderrun.data.RelationalDataProvider;
import me.brandonli.murderrun.data.yaml.ConfigurationManager;
import me.brandonli.murderrun.data.yaml.PluginDataConfigurationMapper;
import me.brandonli.murderrun.data.yaml.QuickJoinConfigurationMapper;
import me.brandonli.murderrun.dependency.DependencyManager;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.PlayerResourcePackChecker;
import me.brandonli.murderrun.game.ability.AbilityRegistry;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.game.arena.drops.TerrainDropAnalyzer;
import me.brandonli.murderrun.game.capability.Capabilities;
import me.brandonli.murderrun.game.extension.nexo.NexoManager;
import me.brandonli.murderrun.game.extension.papi.MurderRunExpansion;
import me.brandonli.murderrun.game.extension.parties.PartiesManager;
import me.brandonli.murderrun.game.gadget.GadgetRegistry;
import me.brandonli.murderrun.game.lobby.GameManager;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.game.map.SchematicLoader;
import me.brandonli.murderrun.game.statistics.StatisticsManager;
import me.brandonli.murderrun.gui.ability.AbilityTestingGui;
import me.brandonli.murderrun.gui.arena.ArenaCreationManager;
import me.brandonli.murderrun.gui.gadget.GadgetTestingGui;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.provider.PackProviderMethod;
import me.brandonli.murderrun.resourcepack.provider.ResourcePackProvider;
import me.brandonli.murderrun.utils.ClassGraphUtils;
import me.brandonli.murderrun.utils.screen.ScreenUtils;
import me.brandonli.murderrun.utils.versioning.VersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderRun extends JavaPlugin {

  private static final int BSTATS_SERVER_ID = 22728;

  private PluginDataConfigurationMapper configuration;
  private QuickJoinConfigurationMapper quickJoinConfiguration;

  private AudienceProvider audience;

  private ConfigurationManager<ArenaManager> arenaDataConfigurationMapper;
  private ConfigurationManager<LobbyManager> lobbyDataConfigurationMapper;
  private ConfigurationManager<StatisticsManager> statisticsConfigurationMapper;
  private ConfigurationManager<ArenaCreationManager> arenaCreationManagerConfigurationManager;

  private ArenaManager arenaManager;
  private LobbyManager lobbyManager;
  private StatisticsManager statisticsManager;
  private ArenaCreationManager arenaCreationManager;
  private GameManager gameManager;

  private Metrics metrics;
  private GameShutdownManager gameShutdownManager;
  private PlayerResourcePackChecker playerResourcePackChecker;
  private ResourcePackProvider provider;

  private MurderRunExpansion expansion;
  private PartiesManager partiesManager;
  private NexoManager nexoManager;

  private AtomicBoolean disabling;
  private VersionChecker versionChecker;

  @Override
  public void onLoad() {
    this.installDependencies();
  }

  @Override
  public void onDisable() {
    this.disabling = new AtomicBoolean(true);
    this.shutdownGames();
    this.unregisterExtensions();
    this.updatePluginData();
    this.shutdownPluginData();
    this.stopHostingDaemon();
    this.unloadLookupMaps();
    this.unloadEventBusApi();
    this.shutdownVersionChecker();
    this.shutdownMetrics();
    this.shutdownAudience();
  }

  @Override
  public void onEnable() {
    this.disabling = new AtomicBoolean(false);
    this.registerAudienceHandler();
    this.initializeEventBusApi();
    this.readPluginData();
    this.registerLookUpMaps();
    this.handlePackHosting();
    this.registerCommands();
    this.registerGameUtilities();
    this.registerExtensions();
    this.loadSchematics();
    this.enableMetrics();
    this.startVersionChecker();
    this.testEventBusApi();
  }

  private void shutdownVersionChecker() {
    this.versionChecker.shutdown();
  }

  private void startVersionChecker() {
    this.versionChecker = new VersionChecker(this);
    this.versionChecker.start();
  }

  private void testEventBusApi() {
    if (isDevelopmentToolsEnabled()) {
      final EventBusTests eventBusTests = new EventBusTests(this);
      eventBusTests.runTestUnits();
    }
  }

  private void unloadEventBusApi() {
    final ApiEventBus bus = EventBusProvider.getBus();
    bus.unsubscribeAll();
  }

  private void unloadLookupMaps() {
    ClassGraphUtils.close();
    ScreenUtils.close();
  }

  private void loadSchematics() {
    this.audience.console(Message.LOAD_SCHEMATICS.build());
    final SchematicLoader loader = new SchematicLoader(this);
    loader.loadSchematics();
  }

  private void installDependencies() {
    final DependencyManager manager = new DependencyManager();
    manager.installDependencies();
    this.setupPacketEvents();
  }

  private void setupPacketEvents() {
    final PacketEventsAPI<Plugin> builder = SpigotPacketEventsBuilder.build(this);
    PacketEvents.setAPI(builder);
    builder.load();
  }

  private void unregisterExtensions() {
    this.audience.console(Message.UNLOAD_EXTENSIONS.build());
    if (Capabilities.PLACEHOLDERAPI.isEnabled() && this.expansion != null) {
      this.expansion.unregister();
    }
  }

  private void registerExtensions() {
    this.audience.console(Message.LOAD_EXTENSIONS.build());
    if (Capabilities.PLACEHOLDERAPI.isEnabled()) {
      this.expansion = new MurderRunExpansion(this);
      this.expansion.register();
    }
    if (Capabilities.PARTIES.isEnabled()) {
      this.partiesManager = new PartiesManager(this);
    }
    if (Capabilities.NEXO.isEnabled()) {
      this.nexoManager = new NexoManager();
    }
  }

  private void shutdownGames() {
    this.audience.console(Message.UNLOAD_GAMES.build());
    if (this.gameShutdownManager != null) {
      this.gameShutdownManager.forceShutdown();
    }
  }

  private void initializeEventBusApi() {
    EventBusProvider.init();
  }

  private void registerLookUpMaps() {
    this.audience.console(Message.LOAD_LOOKUP.build());
    ClassGraphUtils.init();
    GadgetRegistry.init();
    AbilityRegistry.init();
    GameProperties.init();
    GadgetTestingGui.init();
    AbilityTestingGui.init();
    TerrainDropAnalyzer.init();
    ScreenUtils.init(this);
  }

  private void shutdownAudience() {
    if (this.audience != null) {
      this.audience.shutdown();
    }
  }

  private void readPluginData() {
    this.configuration = new PluginDataConfigurationMapper(this);
    this.configuration.deserialize();
    this.audience.console(Message.LOAD_DATA.build());
    this.handleRelationalDataManagement();
    this.quickJoinConfiguration = new QuickJoinConfigurationMapper(this);
    this.quickJoinConfiguration.deserialize();
  }

  private void handleRelationalDataManagement() {
    final RelationalDataProvider relationalDataProvider = new RelationalDataProvider(this);
    this.arenaDataConfigurationMapper = relationalDataProvider.getArenas();
    this.lobbyDataConfigurationMapper = relationalDataProvider.getLobbies();
    this.statisticsConfigurationMapper = relationalDataProvider.getStatistics();
    this.arenaCreationManagerConfigurationManager = relationalDataProvider.getArenaCreation();
    this.arenaManager = this.arenaDataConfigurationMapper.deserialize();
    this.lobbyManager = this.lobbyDataConfigurationMapper.deserialize();
    this.statisticsManager = this.statisticsConfigurationMapper.deserialize();
    this.arenaCreationManager = this.arenaCreationManagerConfigurationManager.deserialize();
    this.gameManager = new GameManager(this);
  }

  private void handlePackHosting() {
    this.audience.console(Message.LOAD_RESOURCEPACK.build());
    final PackProviderMethod packProviderMethod = new PackProviderMethod(this);
    this.provider = packProviderMethod.getProvider();
    this.provider.start();
  }

  private void registerCommands() {
    this.audience.console(Message.LOAD_COMMANDS.build());
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
    this.audience.console(Message.LOAD_METRICS.build());
    System.setProperty("bstats.relocatecheck", "false");
    this.metrics = new Metrics(this, BSTATS_SERVER_ID);
  }

  public void updatePluginData() {
    if (this.arenaDataConfigurationMapper != null) {
      this.arenaDataConfigurationMapper.serialize(this.arenaManager);
      this.lobbyDataConfigurationMapper.serialize(this.lobbyManager);
      this.statisticsConfigurationMapper.serialize(this.statisticsManager);
      this.arenaCreationManagerConfigurationManager.serialize(this.arenaCreationManager);
      this.configuration.serialize();
    }
  }

  public void shutdownPluginData() {
    this.audience.console(Message.UNLOAD_DATA.build());
    if (this.arenaDataConfigurationMapper != null) {
      this.arenaDataConfigurationMapper.shutdown();
      this.lobbyDataConfigurationMapper.shutdown();
      this.statisticsConfigurationMapper.shutdown();
      this.arenaCreationManagerConfigurationManager.shutdown();
      this.configuration.shutdown();
      this.quickJoinConfiguration.shutdown();
    }
  }

  private void stopHostingDaemon() {
    this.audience.console(Message.UNLOAD_RESOURCEPACK.build());
    if (this.provider != null) {
      this.provider.shutdown();
    }
  }

  private void shutdownMetrics() {
    this.audience.console(Message.UNLOAD_METRICS.build());
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

  public QuickJoinConfigurationMapper getQuickJoinConfiguration() {
    return this.quickJoinConfiguration;
  }

  public StatisticsManager getStatisticsManager() {
    return this.statisticsManager;
  }

  public ArenaCreationManager getArenaCreationManager() {
    return this.arenaCreationManager;
  }

  public MurderRunExpansion getExpansion() {
    return this.expansion;
  }

  public PartiesManager getPartiesManager() {
    return this.partiesManager;
  }

  public AtomicBoolean isDisabling() {
    return this.disabling;
  }

  public NexoManager getNexoManager() {
    return this.nexoManager;
  }

  public static boolean isDevelopmentToolsEnabled() {
    return Boolean.getBoolean("murderrun.development.tools");
  }

  public GameManager getGameManager() {
    return this.gameManager;
  }

  public VersionChecker getVersionChecker() {
    return this.versionChecker;
  }
}

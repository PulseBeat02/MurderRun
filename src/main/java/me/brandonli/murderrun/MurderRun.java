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
package me.brandonli.murderrun;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import dev.triumphteam.gui.TriumphGui;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import java.util.Optional;
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
import me.brandonli.murderrun.game.extension.vault.VaultManager;
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
import me.brandonli.murderrun.utils.map.MapTeleportSkipListener;
import me.brandonli.murderrun.utils.screen.ScreenUtils;
import me.brandonli.murderrun.utils.versioning.VersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderRun extends JavaPlugin {

  private static final int BUKKIT_STATS_SERVER_ID = 22728;

  private ConfigurationManager<ArenaManager> arenaDataConfigurationMapper;
  private ConfigurationManager<LobbyManager> lobbyDataConfigurationMapper;
  private ConfigurationManager<StatisticsManager> statisticsConfigurationMapper;
  private ConfigurationManager<ArenaCreationManager> arenaCreationManagerConfigurationManager;
  private PluginDataConfigurationMapper configuration;
  private QuickJoinConfigurationMapper quickJoinConfiguration;

  private ResourcePackProvider provider;
  private ArenaManager arenaManager;
  private LobbyManager lobbyManager;
  private StatisticsManager statisticsManager;
  private ArenaCreationManager arenaCreationManager;
  private GameManager gameManager;
  private GameShutdownManager gameShutdownManager;
  private PlayerResourcePackChecker playerResourcePackChecker;

  private AudienceProvider audience;
  private MurderRunExpansion expansion;
  private PartiesManager partiesManager;
  private NexoManager nexoManager;
  private VaultManager vaultManager;

  private Metrics metrics;
  private AtomicBoolean disabling;
  private VersionChecker versionChecker;
  private MapTeleportSkipListener mapTeleportSkipListener;

  @Override
  public void onLoad() {
    this.loadDependencies();
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
    this.shutdownMiscListeners();
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
    this.startMiscListeners();
    this.testEventBusApi();
  }

  private void shutdownMiscListeners() {
    if (this.mapTeleportSkipListener != null) {
      this.mapTeleportSkipListener.shutdown();
    }
    if (this.versionChecker != null) {
      this.versionChecker.shutdown();
    }
  }

  private void startMiscListeners() {
    if (false) {
      this.versionChecker = new VersionChecker(this);
    }
    this.versionChecker.start();
    this.mapTeleportSkipListener = new MapTeleportSkipListener(this);
    this.mapTeleportSkipListener.start();
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

  private void loadDependencies() {
    final Optional<UnsupportedOperationException> exception = this.installDependenciesExceptionally();
    if (exception.isPresent()) {
      final Server server = Bukkit.getServer();
      final PluginManager manager = server.getPluginManager();
      final UnsupportedOperationException e = exception.get();
      manager.disablePlugin(this);
      throw new AssertionError(e);
    }
  }

  private Optional<UnsupportedOperationException> installDependenciesExceptionally() {
    try {
      final DependencyManager manager = new DependencyManager();
      manager.installDependencies();
      this.setupPacketEvents();
    } catch (final UnsupportedOperationException e) {
      return Optional.of(e);
    }
    return Optional.empty();
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
    if (Capabilities.VAULT.isEnabled()) {
      this.vaultManager = new VaultManager();
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
    TriumphGui.init(this);
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
    this.metrics = new Metrics(this, BUKKIT_STATS_SERVER_ID);
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
    }
    if (this.lobbyDataConfigurationMapper != null) {
      this.lobbyDataConfigurationMapper.shutdown();
    }
    if (this.statisticsConfigurationMapper != null) {
      this.statisticsConfigurationMapper.shutdown();
    }
    if (this.arenaCreationManagerConfigurationManager != null) {
      this.arenaCreationManagerConfigurationManager.shutdown();
    }
    if (this.quickJoinConfiguration != null) {
      this.quickJoinConfiguration.shutdown();
    }
    if (this.configuration != null) {
      this.configuration.shutdown();
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

  public VaultManager getVaultManager() {
    return this.vaultManager;
  }
}

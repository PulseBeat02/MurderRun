package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.arena.MurderArenaManager;
import io.github.pulsebeat02.murderrun.config.PluginConfiguration;
import io.github.pulsebeat02.murderrun.data.ArenaDataManager;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.resourcepack.server.PackHostingDaemon;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class MurderRun extends JavaPlugin {

  /*

  TODO:

  - Add Innocent Traps for Survival
  - Add Murderer Traps for Killing
  - Add Commands (Villagers, Setting Game Configuration)
  - Add Villager Trading System and Currency

   */

  private static NamespacedKey KEY;

  private PluginConfiguration configuration;
  private AudienceHandler audience;
  private PackHostingDaemon daemon;
  private ArenaDataManager arenaDataManager;
  private MurderArenaManager arenaManager;

  @Override
  public void onEnable() {
    KEY = new NamespacedKey(this, "data");
    this.readPluginData();
    this.startHostingDaemon();
    this.audience = new AudienceHandler(this);
  }

  @Override
  public void onDisable() {
    this.writePluginData();
    this.stopHostingDaemon();
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

  public static NamespacedKey getKey() {
    return KEY;
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
}

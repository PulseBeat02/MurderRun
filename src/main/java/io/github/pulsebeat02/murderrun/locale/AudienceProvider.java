package io.github.pulsebeat02.murderrun.locale;

import io.github.pulsebeat02.murderrun.MurderRun;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;

public final class AudienceProvider {

  private final BukkitAudiences audience;

  public AudienceProvider(final MurderRun neon) {
    this.audience = BukkitAudiences.create(neon);
  }

  public void shutdown() {
    if (this.audience != null) {
      this.audience.close();
    }
  }

  public BukkitAudiences retrieve() {
    this.checkStatus();
    return this.audience;
  }

  private void checkStatus() {
    if (this.audience == null) {
      throw new AssertionError("Tried to access Adventure when the plugin was disabled!");
    }
  }

  public void console(final Component component) {
    this.checkStatus();
    final Audience console = this.audience.console();
    console.sendMessage(component);
  }
}

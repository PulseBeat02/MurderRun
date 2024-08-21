package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import java.util.concurrent.atomic.AtomicInteger;

public final class GadgetManager {

  private final MurderRun plugin;
  private final Game game;
  private final AtomicInteger activationRange;

  private GadgetLoadingMechanism mechanism;
  private GadgetActionHandler actionHandler;

  public GadgetManager(final Game game) {
    final MurderRun plugin = game.getPlugin();
    this.game = game;
    this.plugin = plugin;
    this.activationRange = new AtomicInteger(2);
  }

  public void start() {
    this.mechanism = new GadgetLoadingMechanism(this);
    this.actionHandler = new GadgetActionHandler(this);
    this.actionHandler.start();
  }

  public void shutdown() {
    this.mechanism.shutdown();
    this.actionHandler.shutdown();
  }

  public GadgetLoadingMechanism getMechanism() {
    return this.mechanism;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Game getGame() {
    return this.game;
  }

  public int getActivationRange() {
    return this.activationRange.get();
  }

  public void setActivationRange(final int range) {
    this.activationRange.getAndSet(range);
  }
}

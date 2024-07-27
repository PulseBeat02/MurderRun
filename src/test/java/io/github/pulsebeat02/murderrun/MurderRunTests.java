package io.github.pulsebeat02.murderrun;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public final class MurderRunTests {

  private final ServerMock server;
  private final MurderRun plugin;

  public static void main(final String[] args) {
    final MurderRunTests tests = new MurderRunTests();
    tests.test();
    tests.tearDown();
  }

  public MurderRunTests() {
    this.server = MockBukkit.mock();
    this.plugin = MockBukkit.load(MurderRun.class);
  }

  public void tearDown() {
    MockBukkit.unmock();
  }

  public void test() {
    final PlayerMock player = this.server.addPlayer();
    player.performCommand("murder help");
  }
}

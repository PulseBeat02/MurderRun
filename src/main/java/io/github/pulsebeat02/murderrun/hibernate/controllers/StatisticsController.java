package io.github.pulsebeat02.murderrun.hibernate.controllers;

import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import org.hibernate.SessionFactory;

public final class StatisticsController extends AbstractController<LobbyManager> {

  public StatisticsController(final SessionFactory factory) {
    super(factory, "statistics_manager");
  }
}

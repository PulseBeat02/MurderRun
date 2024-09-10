package io.github.pulsebeat02.murderrun.hibernate.controllers;

import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import org.hibernate.SessionFactory;

public final class ArenaController extends AbstractController<LobbyManager> {

  public ArenaController(final SessionFactory factory) {
    super(factory, "arena_manager");
  }
}

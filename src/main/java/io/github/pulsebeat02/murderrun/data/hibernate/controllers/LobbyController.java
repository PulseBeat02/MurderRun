package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import io.github.pulsebeat02.murderrun.data.hibernate.HibernateIdentifiers;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import org.hibernate.SessionFactory;

public final class LobbyController extends AbstractController<LobbyManager> {

  public LobbyController(final SessionFactory factory) {
    super(factory, HibernateIdentifiers.LOBBY_MANAGER_ID);
  }

  @Override
  public LobbyManager createDefaultEntity() {
    return new LobbyManager();
  }
}

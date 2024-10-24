package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateIdentifierManager;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import org.hibernate.SessionFactory;

public final class LobbyController extends AbstractController<LobbyManager> {

  public LobbyController(final HibernateIdentifierManager manager, final SessionFactory factory) {
    super(manager, factory, HibernateIdentifierManager.LOBBY_MANAGER_INDEX);
  }

  @Override
  public LobbyManager createDefaultEntity() {
    return new LobbyManager();
  }
}

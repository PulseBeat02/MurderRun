package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateIdentifierManager;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import org.hibernate.SessionFactory;

public final class ArenaController extends AbstractController<ArenaManager> {

  public ArenaController(final HibernateIdentifierManager manager, final SessionFactory factory) {
    super(manager, factory, HibernateIdentifierManager.ARENA_MANAGER_INDEX);
  }

  @Override
  public ArenaManager createDefaultEntity() {
    return new ArenaManager();
  }
}

package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import io.github.pulsebeat02.murderrun.data.hibernate.HibernateIdentifiers;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import org.hibernate.SessionFactory;

public final class ArenaController extends AbstractController<ArenaManager> {

  public ArenaController(final SessionFactory factory) {
    super(factory, HibernateIdentifiers.ARENA_MANAGER_ID);
  }

  @Override
  public ArenaManager createDefaultEntity() {
    return new ArenaManager();
  }
}

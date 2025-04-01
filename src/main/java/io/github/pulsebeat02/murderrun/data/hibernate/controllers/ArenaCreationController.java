package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateIdentifierManager;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.gui.arena.ArenaCreationManager;
import org.hibernate.SessionFactory;

public final class ArenaCreationController extends AbstractController<ArenaCreationManager> {

    public ArenaCreationController(final HibernateIdentifierManager manager, final SessionFactory factory) {
        super(manager, factory, HibernateIdentifierManager.ARENA_CREATION_MANAGER_INDEX);
    }

    @Override
    public ArenaCreationManager createDefaultEntity() {
        return new ArenaCreationManager();
    }
}


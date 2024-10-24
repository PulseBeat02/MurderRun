package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateIdentifierManager;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import org.hibernate.SessionFactory;

public final class StatisticsController extends AbstractController<StatisticsManager> {

  public StatisticsController(final HibernateIdentifierManager manager, final SessionFactory factory) {
    super(manager, factory, HibernateIdentifierManager.STATISTICS_MANAGER_INDEX);
  }

  @Override
  public StatisticsManager createDefaultEntity() {
    return new StatisticsManager();
  }
}

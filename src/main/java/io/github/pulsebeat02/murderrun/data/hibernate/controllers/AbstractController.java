package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class AbstractController<T> implements Controller<T> {

  private final SessionFactory factory;
  private final String id;

  public AbstractController(final SessionFactory factory, final String id) {
    this.factory = factory;
    this.id = id;
  }

  @Override
  public T deserialize() {
    try (final Session session = this.factory.openSession()) {
      final Transaction transaction = session.beginTransaction();
      final Class<T> clazz = this.getGenericClass();
      final T manager = session.get(clazz, this.id);
      transaction.commit();
      return manager;
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public void shutdown() {
    if (this.factory.isOpen()) {
      this.factory.close();
    }
  }

  @Override
  public void serialize(final T data) {

    if (data == null) {
      return;
    }

    try (final Session session = this.factory.getCurrentSession()) {
      final Transaction transaction = session.beginTransaction();
      session.beginTransaction();
      session.refresh(data);
      transaction.commit();
    }
  }
}

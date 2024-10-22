package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import static java.util.Objects.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class AbstractController<T> implements Controller<T> {

  private final SessionFactory factory;
  private final Object id;

  public AbstractController(final SessionFactory factory, final Object id) {
    this.factory = factory;
    this.id = id;
  }

  @Override
  public T deserialize() {
    try (final Session session = this.factory.openSession()) {
      final Transaction transaction = session.beginTransaction();
      final Class<T> clazz = this.getGenericClass();
      final T manager = session.get(clazz, this.id);
      final T defaultEntity = requireNonNull(this.createDefaultEntity());
      final T returnEntity = requireNonNullElse(manager, defaultEntity);
      transaction.commit();
      return returnEntity;
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
  }

  public abstract T createDefaultEntity();

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

    try (final Session session = this.factory.openSession()) {
      final Transaction transaction = session.beginTransaction();
      session.merge(data);
      transaction.commit();
    }
  }
}

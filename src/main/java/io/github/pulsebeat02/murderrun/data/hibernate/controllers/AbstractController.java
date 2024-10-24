package io.github.pulsebeat02.murderrun.data.hibernate.controllers;

import static java.util.Objects.*;

import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateIdentifierManager;
import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateSerializable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class AbstractController<T extends HibernateSerializable> implements Controller<T> {

  private final SessionFactory factory;
  private final HibernateIdentifierManager manager;
  private final int index;

  public AbstractController(final HibernateIdentifierManager manager, final SessionFactory factory, final int index) {
    this.factory = factory;
    this.manager = manager;
    this.index = index;
  }

  @Override
  public T deserialize() {
    final long id = this.manager.getIdentifier(this.index);
    final Class<T> clazz = this.getGenericClass();
    try (final Session session = this.factory.openSession()) {
      final Transaction transaction = session.beginTransaction();
      final T entity = session.get(clazz, id);
      if (id == -1 || entity == null) {
        final T defaultEntity = requireNonNull(this.createDefaultEntity());
        session.persist(defaultEntity);
        final long updated = defaultEntity.getId();
        this.manager.storeIdentifier(this.index, updated);
        transaction.commit();
        return defaultEntity;
      }
      transaction.commit();
      return entity;
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
      if (session.contains(data)) {
        session.refresh(data);
      } else {
        session.merge(data);
      }
      transaction.commit();
    }
  }
}

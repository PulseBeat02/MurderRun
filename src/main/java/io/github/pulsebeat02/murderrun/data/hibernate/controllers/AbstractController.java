/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
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

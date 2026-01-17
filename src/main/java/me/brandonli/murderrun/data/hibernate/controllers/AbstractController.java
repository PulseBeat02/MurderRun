/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.data.hibernate.controllers;

import static java.util.Objects.*;

import me.brandonli.murderrun.data.hibernate.identifier.HibernateIdentifierManager;
import me.brandonli.murderrun.data.hibernate.identifier.HibernateSerializable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class AbstractController<T extends HibernateSerializable> implements Controller<T> {

  private final SessionFactory factory;
  private final HibernateIdentifierManager manager;
  private final int index;

  public AbstractController(
      final HibernateIdentifierManager manager, final SessionFactory factory, final int index) {
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

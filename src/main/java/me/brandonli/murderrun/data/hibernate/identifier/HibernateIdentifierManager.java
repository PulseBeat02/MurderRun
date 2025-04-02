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
package me.brandonli.murderrun.data.hibernate.identifier;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import me.brandonli.murderrun.data.hibernate.WhitelistedHibernateObjectInputStream;
import me.brandonli.murderrun.utils.ExecutorUtils;
import me.brandonli.murderrun.utils.IOUtils;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class HibernateIdentifierManager {

  private static final String HIBERNATE_IDENTIFIERS_FILE = "hibernate_identifiers.dat";

  public static final int LOBBY_MANAGER_INDEX = 0;
  public static final int ARENA_MANAGER_INDEX = 1;
  public static final int STATISTICS_MANAGER_INDEX = 2;
  public static final int ARENA_CREATION_MANAGER_INDEX = 3;

  private final Long[] identifiers;
  private final ExecutorService service;
  private final Path path;
  private final Lock readLock;
  private final Lock writeLock;

  public HibernateIdentifierManager() {
    final Path parent = IOUtils.getPluginDataFolderPath();
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    this.path = parent.resolve(HIBERNATE_IDENTIFIERS_FILE);
    this.service = Executors.newVirtualThreadPerTaskExecutor();
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();
    this.identifiers = this.deserialize(this.path, this.readLock);
  }

  public synchronized void serialize() {
    CompletableFuture.runAsync(this::serialize0, this.service);
  }

  public synchronized void shutdown() {
    this.serialize();
    ExecutorUtils.shutdownExecutorGracefully(this.service);
  }

  private synchronized void serialize0() {
    this.writeLock.lock();
    try (final ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(this.path))) {
      oos.writeObject(this.identifiers);
    } catch (final IOException e) {
      throw new AssertionError(e);
    } finally {
      this.writeLock.unlock();
    }
  }

  public synchronized Long[] deserialize(@UnderInitialization HibernateIdentifierManager this, final Path path, final Lock read) {
    if (IOUtils.createFile(path)) {
      return new Long[] { -1L, -1L, -1L };
    }
    read.lock();
    try (final InputStream fis = Files.newInputStream(path); final ObjectInputStream ois = new WhitelistedHibernateObjectInputStream(fis)) {
      return (Long[]) ois.readObject();
    } catch (final IOException | ClassNotFoundException e) {
      throw new AssertionError(e);
    } finally {
      read.unlock();
    }
  }

  public synchronized void storeIdentifier(final int index, final Long identifier) {
    this.identifiers[index] = identifier;
    this.serialize();
  }

  public synchronized Long getIdentifier(final int index) {
    return this.identifiers[index];
  }
}

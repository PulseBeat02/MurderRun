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

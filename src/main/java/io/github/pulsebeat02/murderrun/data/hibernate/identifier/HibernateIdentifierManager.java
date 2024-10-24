package io.github.pulsebeat02.murderrun.data.hibernate.identifier;

import io.github.pulsebeat02.murderrun.utils.ExecutorUtils;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class HibernateIdentifierManager {

  private static final String HIBERNATE_IDENTIFIERS_FILE = "hibernate_identifiers.dat";

  public static final int LOBBY_MANAGER_INDEX = 0;
  public static final int ARENA_MANAGER_INDEX = 1;
  public static final int STATISTICS_MANAGER_INDEX = 2;

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
    try (final ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
      final Long[] identifiers;
      identifiers = (Long[]) ois.readObject();
      return identifiers;
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

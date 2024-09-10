package io.github.pulsebeat02.murderrun.data.sql;

import static java.util.Objects.requireNonNull;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.pulsebeat02.murderrun.data.yaml.ConfigurationManager;
import io.github.pulsebeat02.murderrun.json.GsonProvider;
import io.github.pulsebeat02.murderrun.utils.ExecutorUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AbstractDatabaseManager<T> implements ConfigurationManager<T> {

  private final ExecutorService service;
  private final Class<T> clazz;
  private final Lock readLock;
  private final Lock writeLock;
  private final HikariDataSource dataSource;
  private final String tableName;

  public AbstractDatabaseManager(final Class<T> clazz, final String url, final String tableName) {
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    this.service = Executors.newVirtualThreadPerTaskExecutor();
    this.clazz = clazz;
    this.tableName = tableName;
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();
    this.dataSource = this.createDataSource(url);
    this.createTableIfNotExists();
  }

  private HikariDataSource createDataSource(final String url) {
    final HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setDriverClassName("org.sqlite.JDBC");
    config.setMaximumPoolSize(10);
    return new HikariDataSource(config);
  }

  private void createTableIfNotExists() {
    try (final Connection conn = this.dataSource.getConnection();
        final PreparedStatement pstmt = conn.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " + this.tableName + " (data TEXT)")) {
      pstmt.executeUpdate();
    } catch (final SQLException e) {
      throw new AssertionError("Failed to create table", e);
    }
  }

  @Override
  public synchronized void serialize(final T manager) {
    requireNonNull(manager);
    CompletableFuture.runAsync(() -> this.writeToDatabase(manager), this.service);
  }

  @Override
  public synchronized void shutdown() {
    ExecutorUtils.shutdownExecutorGracefully(this.service);
    if (this.dataSource != null && !this.dataSource.isClosed()) {
      this.dataSource.close();
    }
  }

  private void writeToDatabase(final T manager) {
    requireNonNull(manager);
    this.writeLock.lock();
    try (final Connection conn = this.dataSource.getConnection();
        final PreparedStatement pstmt = conn.prepareStatement(
            "INSERT OR REPLACE INTO " + this.tableName + " (data) VALUES (?)")) {
      final String json = GsonProvider.getGson().toJson(manager);
      pstmt.setString(1, json);
      pstmt.executeUpdate();
    } catch (final SQLException e) {
      throw new AssertionError("Failed to write to database", e);
    } finally {
      this.writeLock.unlock();
    }
  }

  @Override
  public synchronized T deserialize() {
    this.readLock.lock();
    try (final Connection conn = this.dataSource.getConnection();
        final PreparedStatement pstmt =
            conn.prepareStatement("SELECT data FROM " + this.tableName + " LIMIT 1");
        final ResultSet rs = pstmt.executeQuery()) {
      if (rs.next()) {
        final String json = rs.getString("data");
        return GsonProvider.getGson().fromJson(json, this.clazz);
      } else {
        throw new AssertionError("No data found in database");
      }
    } catch (final SQLException e) {
      throw new AssertionError("Failed to read from database", e);
    } finally {
      this.readLock.unlock();
    }
  }
}

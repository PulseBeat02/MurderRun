package io.github.pulsebeat02.murderrun.data.sql;

public final class DatabaseInfo {

  private final String url;
  private final String username;
  private final String password;

  public DatabaseInfo(final String url, final String username, final String password) {
    this.url = url;
    this.username = username;
    this.password = password;
  }

  public String getUrl() {
    return this.url;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }
}

package io.github.pulsebeat02.murderrun.data.yaml;

public interface ConfigurationManager<T> {
  void serialize(final T manager);

  void shutdown();

  T deserialize();
}

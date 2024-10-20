package io.github.pulsebeat02.murderrun.resourcepack.provider.netty.injector;

import java.util.Collection;
import java.util.HashSet;

public final class InjectorHandler {

  private final Collection<Injector> injectors;

  public InjectorHandler() {
    this.injectors = new HashSet<>();
  }

  public void addInjector(final Injector injector) {
    this.injectors.add(injector);
  }

  public void removeInjector(final Injector injector) {
    this.injectors.remove(injector);
  }

  public void clearInjectors() {
    this.injectors.clear();
  }

  public Collection<Injector> getInjectors() {
    return this.injectors;
  }
}

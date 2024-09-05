package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.game.Capabilities;

public final class DependencyGadgetHandler {

  public void disableGadgets(final GlobalGadgetRegistry instance) {
    instance.unfreeze();
    if (Capabilities.LIB_DISGUISES.isDisabled()) {
      this.removeIfExists(instance, "mimic");
    }
    instance.freeze();
  }

  public void removeIfExists(final GlobalGadgetRegistry registry, final String gadget) {
    if (registry.getGadget(gadget) != null) {
      registry.removeGadget(gadget);
    }
  }
}

package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.game.Capabilities;

public final class GadgetDisabler {

  public void disableGadgets(final GadgetRegistry instance) {
    instance.unfreeze();
    if (Capabilities.LIB_DISGUISES.isDisabled()) {
      this.removeIfExists(instance, "mimic");
    }
    instance.freeze();
  }

  public void removeIfExists(final GadgetRegistry registry, final String gadget) {
    if (registry.getGadget(gadget) != null) {
      registry.removeGadget(gadget);
    }
  }
}

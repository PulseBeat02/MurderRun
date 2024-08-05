package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadgetManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;

public final class SupplyDrop extends MurderGadget {

  private static final String[] AIR_DROP_MASKS = {
    """
      AXAXAXAXA
      XAXAXAXAX
      AXAXAXAXA
      """,
    """
      XXXXAXXXX
      XAXAAAXAX
      XXXXAXXXX
      """,
    """
      AAXXAXXAA
      AXAXAXAXA
      AAXXAXXAA
      """
  };

  public SupplyDrop() {
    super(
        "supply_drop",
        Material.CHEST,
        Locale.SUPPLY_DROP_TRAP_NAME.build(),
        Locale.SUPPLY_DROP_TRAP_LORE.build());
  }

  public MurderGadget getRandomGadget(final MurderGame game) {
    final MurderGadgetManager manager = game.getGadgetManager();
    final Map<String, MurderGadget> map = manager.getGameGadgets();
    final Collection<MurderGadget> gadgets = map.values();
    if (gadgets.isEmpty()) {
      throw new AssertionError("No gadgets found!");
    }
    final List<MurderGadget> list = new ArrayList<>(gadgets);
    Collections.shuffle(list);
    return list.getFirst();
  }
}

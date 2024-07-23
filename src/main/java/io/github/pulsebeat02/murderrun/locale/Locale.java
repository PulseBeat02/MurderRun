package io.github.pulsebeat02.murderrun.locale;

import static io.github.pulsebeat02.murderrun.locale.LocaleParent.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface Locale extends LocaleParent {
  NullComponent<Sender> CAR_PART_NAME = itemName("murder_run.item.car_part.name");
  NullComponent<Sender> CAR_PART_LORE = itemLore("murder_run.item.car_part.lore");
  NullComponent<Sender> INNOCENT_PREPERATION = title("murder_run.game.preperation", GOLD);
  NullComponent<Sender> MURDERER_RELEASED = title("murder_run.game.released", RED);
  NullComponent<Sender> INNOCENT_VICTORY = title("murder_run.game.innocent_victory", GREEN);
  NullComponent<Sender> MURDERER_VICTORY = title("murder_run.game.murderer_victory", RED);
  UniComponent<Sender, Long> MURDERER_TIME = info("murder_run.game.murder_time", null);
  UniComponent<Sender, Integer> CAR_PART_RETRIEVAL =
      title("murder_run.game.car_part_retrieval", null, AQUA);
  UniComponent<Sender, String> PLAYER_DEATH = title("murder_run.game.death", null, AQUA);
}

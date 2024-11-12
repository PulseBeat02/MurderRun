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
package io.github.pulsebeat02.murderrun.game.gadget.misc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.citizens.CitizensManager;
import io.github.pulsebeat02.murderrun.game.extension.GameExtensionManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.util.Collection;
import java.util.function.Consumer;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPC.Metadata;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CameraGadget {

  private static final String TEXTURE_SIGNATURE =
    "NnBRwHr2aCWajkQvLsyjbDNa3EBUzn+r/uf4TOsHG+owrq9B0yyr1mModVd/T+K6c5TjbL4ozVs0oOUOaKnohj8BSCIeANe5I2FlFj/xV2mX0wnK1vxhwZ05u8uP7jqHJcly8Vhhc4pm907ol1Ozi+9cln8nPuJQ7AjKElwJjijQJ6CGcJiZ2p+kgByc4jxj1WCAwIgzEFhMi3VmXPc33sSMWiiOfHDTtO78fP1liOQNyxudiHDc6WWBDrNNHmS8eggxE/8ODniENFiitXC3YzZE7Y3PFvj6zia+m24mjO5ZG8bTWiDhmNnX7Bqvs/r3rv6ctxopYbmnQkvAN1DZ7cpOPe9b7zsBNtajVTLIzPqemfmKTJRtboGR/wXPOD/TrkhouN5tH9MY5MLN0xUXCSDSnL6IoQm509kSDdVh6Msci9QU88sn++TRTAtNfngySyUlgUMCAMinjikNi52fH0ccrJkn/UcnRDhWpfBRhmOdg1rs1rYO5f963nWJKDGckoBEDGwDmDP0PowI0iDWKERDvunw4hM+oEsOvk1dzHp3o2qBV1Xyts1uYQZ5jjw8iQpC/Y6htB4mV5RLbInDlcxQ2WoS9v9CFQMdXAnCW80i5ffMzq5Km+CnPt74m5tbqZOFBEFnu1ux+3M6C/ko3jLcyWNHCRf8H+7uMr0BqHk=";
  private static final String TEXTURE_DATA =
    "ewogICJ0aW1lc3RhbXAiIDogMTYyNjU5NDkyMjYxMCwKICAicHJvZmlsZUlkIiA6ICI0ZjU2ZTg2ODk2OGU0ZWEwYmNjM2M2NzRlNzQ3ODdjOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhmMzQ0Y2VhNGFhYjVjYTRlYjZmMjcwZGZhNjZlYWU5ODgzZGI2Y2NjYzVmYjIxMDYzZDc0ZjQ1ZWE2OWI2ZTMiCiAgICB9CiAgfQp9";

  private final Multimap<GamePlayer, GamePlayer> glowingPlayers;

  public CameraGadget() {
    this.glowingPlayers = HashMultimap.create();
  }

  public boolean handleCamera(final Game game, final GamePlayer player, final Item item) {
    final GamePlayerManager manager = game.getPlayerManager();
    final Location location = player.getLocation();
    final GameExtensionManager extensionManager = game.getExtensionManager();
    final CitizensManager npcManager = extensionManager.getNPCManager();
    final NPC npc = this.customizeNPC(npcManager);
    npc.spawn(location);
    item.remove();

    final LivingEntity entity = (LivingEntity) npc.getEntity();
    final GameScheduler scheduler = game.getScheduler();
    final Consumer<GamePlayer> handleGlow = opponent -> this.handleGlow(player, opponent, entity);

    final Runnable task;
    if (player instanceof Survivor) {
      task = () -> manager.applyToKillers(handleGlow);
    } else {
      task = () -> manager.applyToLivingSurvivors(handleGlow);
    }
    scheduler.scheduleRepeatedTask(task, 0, 20L);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.CAMERA_SOUND);

    return false;
  }

  private void handleGlow(final GamePlayer owner, final GamePlayer opponent, final LivingEntity npc) {
    final Player internal = opponent.getInternalPlayer();
    final boolean detected = npc.hasLineOfSight(internal);
    final MetadataManager metadata = owner.getMetadataManager();
    final Collection<GamePlayer> glow = this.glowingPlayers.get(owner);
    if (detected) {
      glow.add(opponent);
      this.setLookDirection(opponent, npc);
      metadata.setEntityGlowing(opponent, ChatColor.RED, true);
    } else if (glow.contains(opponent)) {
      glow.remove(opponent);
      metadata.setEntityGlowing(opponent, ChatColor.RED, false);
    }
  }

  private void setLookDirection(final GamePlayer target, final Entity entity) {
    final Location origin = entity.getLocation();
    final Location look = target.getLocation();
    final Vector direction = look.toVector().subtract(origin.toVector());
    origin.setDirection(direction);

    entity.teleport(origin);
  }

  private NPC customizeNPC(final CitizensManager manager) {
    final NPCRegistry registry = manager.getRegistry();
    final NPC npc = registry.createNPC(EntityType.PLAYER, "Camera");
    npc.setAlwaysUseNameHologram(false);

    final SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
    trait.setSkinPersistent("Camera", TEXTURE_SIGNATURE, TEXTURE_DATA);

    final MetadataStore metadata = npc.data();
    metadata.set(Metadata.NAMEPLATE_VISIBLE, false);

    return npc;
  }
}

/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.gadget.misc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.function.Consumer;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.extension.GameExtensionManager;
import me.brandonli.murderrun.game.extension.citizens.CitizensManager;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPC.Metadata;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
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
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(task, 0, 20L, reference);

    final PlayerAudience audience = player.getAudience();
    final GameProperties properties = game.getProperties();
    audience.playSound(properties.getCameraSound());

    return false;
  }

  private void handleGlow(
      final GamePlayer owner, final GamePlayer opponent, final LivingEntity npc) {
    opponent.apply(internal -> {
      final boolean detected = npc.hasLineOfSight(internal);
      final MetadataManager metadata = owner.getMetadataManager();
      final Collection<GamePlayer> glow = this.glowingPlayers.get(owner);
      if (detected) {
        glow.add(opponent);
        this.setLookDirection(opponent, npc);
        metadata.setEntityGlowing(opponent, NamedTextColor.RED, true);
      } else if (glow.contains(opponent)) {
        glow.remove(opponent);
        metadata.setEntityGlowing(opponent, NamedTextColor.RED, false);
      }
    });
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

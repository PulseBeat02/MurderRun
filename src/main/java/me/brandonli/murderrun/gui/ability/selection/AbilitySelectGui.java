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
package me.brandonli.murderrun.gui.ability.selection;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.text.Component.empty;

import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.components.util.GuiFiller;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import java.util.*;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.ability.Ability;
import me.brandonli.murderrun.game.ability.AbilityRegistry;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ContainerUtils;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class AbilitySelectGui extends PaginatedGui {

  private final Player viewer;
  private final MurderRun plugin;
  private final GameProperties properties;

  public AbilitySelectGui(
      final MurderRun plugin,
      final GameProperties properties,
      final Player viewer,
      final List<String> abilities) {
    super(
        ContainerUtils.createChestContainer(Message.SELECT_GUI_TITLE.build(), 6),
        45,
        InteractionModifier.VALUES);
    this.plugin = plugin;
    this.viewer = viewer;
    this.properties = properties;
    this.addAbilityItems(abilities);
    this.createNavigationPane();
  }

  @SuppressWarnings("all") // checker
  private void addAbilityItems(final List<String> abilities) {
    final AbilityRegistry registry = AbilityRegistry.getRegistry();
    abilities.stream()
        .map(registry::getAbility)
        .filter(Objects::nonNull)
        .map(Ability::getStackBuilder)
        .map(Item.Builder::build)
        .map(stack -> new GuiItem(stack, this::handleClick))
        .forEach(this::addItem);
  }

  private void createNavigationPane() {
    final GuiFiller filler = this.getFiller();
    final GuiItem back = this.createBackStack();
    final GuiItem next = this.createForwardStack();
    final GuiItem close = this.createCloseStack();
    final GuiItem border = this.createBorderStack();
    filler.fillBottom(border);
    this.setItem(6, 1, back);
    this.setItem(6, 9, next);
    this.setItem(6, 5, close);
  }

  private GuiItem createBorderStack() {
    return new GuiItem(
        Item.builder(Material.GRAY_STAINED_GLASS_PANE).name(empty()).build());
  }

  private void handleClick(final InventoryClickEvent event) {
    final ItemStack stack = event.getCurrentItem();
    final AbilityRegistry registry = AbilityRegistry.getRegistry();
    if (stack == null) {
      return;
    }

    final String data = PDCUtils.getPersistentDataAttribute(
        stack, Keys.ABILITY_KEY_NAME, PersistentDataType.STRING);
    final Ability ability = data != null ? registry.getAbility(data) : null;
    if (ability == null) {
      return;
    }

    final HumanEntity entity = event.getWhoClicked();
    final PlayerInventory inventory = entity.getInventory();
    final @Nullable ItemStack[] contents = inventory.getContents();
    for (final ItemStack content : contents) {
      if (content == null) {
        continue;
      }
      if (!PDCUtils.isAbility(content)) {
        continue;
      }
      inventory.removeItem(content);
    }
    this.playSound(entity);

    final Item.Builder actual = ability.getStackBuilder();
    final ItemStack actualStack = actual.build();
    final ItemStack clone = actualStack.clone();
    inventory.setItem(8, clone);
  }

  private GuiItem createCloseStack() {
    return new GuiItem(
        Item.builder(Material.BARRIER).name(Message.SELECT_GUI_CANCEL.build()).build(),
        event -> this.close(this.viewer));
  }

  private GuiItem createForwardStack() {
    return new GuiItem(
        Item.builder(Material.GREEN_WOOL)
            .name(Message.SELECT_GUI_FORWARD.build())
            .build(),
        event -> this.next());
  }

  private GuiItem createBackStack() {
    return new GuiItem(
        Item.builder(Material.RED_WOOL).name(Message.SELECT_GUI_BACK.build()).build(),
        event -> this.previous());
  }

  private void playSound(final HumanEntity entity) {
    final String raw = this.properties.getAbilityGuiSound();
    final Key key = key(raw);
    final Sound.Source source = Sound.Source.MASTER;
    final Sound sound = sound(key, source, 1.0f, 1.0f);
    final UUID uuid = entity.getUniqueId();
    final AudienceProvider provider = this.plugin.getAudience();
    final BukkitAudiences bukkitAudiences = provider.retrieve();
    final Audience audience = bukkitAudiences.player(uuid);
    audience.playSound(sound);
  }
}

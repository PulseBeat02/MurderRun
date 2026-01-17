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
package me.brandonli.murderrun.utils.item;

import java.util.Optional;
import java.util.UUID;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.capability.Capabilities;
import me.brandonli.murderrun.game.extension.craftengine.CraftEngineManager;
import me.brandonli.murderrun.game.extension.nexo.NexoManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.RandomUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;

/**
 * Maintainer note: The reason why I didn't use an interface for CraftEngineManager and NexoManager because
 * I wanted to allow servers to have both plugins and support both. And also they are subject to lots of change
 * in the future, so having them as separate classes makes it easier to manage.
 */
public final class ItemFactory {

  private static final String ITEM_SKULL_URL =
      "http://textures.minecraft.net/texture/6e39fa3aeff671667571d6541f685ccd6c9c4185f5d3a5af5872ec9879a2044";

  private ItemFactory() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static ItemStack createItemLocationWand() {
    return Item.builder(Material.BLAZE_ROD)
        .name(Message.ITEM_ARENA_NAME.build())
        .lore(Message.ITEM_ARENA_LORE.build())
        .pdc(Keys.ITEM_WAND, PersistentDataType.BOOLEAN, true)
        .build();
  }

  public static ItemStack[] createKillerGear(final GameProperties properties) {
    final ItemStack defaultKillerHelmet = createDefaultKillerHelmet();
    final ItemStack defaultKillerChestplate = createDefaultKillerChestplate();
    final ItemStack defaultKillerLeggings = createDefaultKillerLeggings();
    final ItemStack defaultKillerBoots = createDefaultKillerBoots();
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);

    if (Capabilities.CRAFTENGINE.isEnabled()) {
      final CraftEngineManager craftEngineManager = plugin.getCraftEngineManager();
      final Optional<Item.Builder> helmetBuilder = craftEngineManager.getKillerHelmet(properties);
      final Optional<Item.Builder> chestplateBuilder =
          craftEngineManager.getKillerChestplate(properties);
      final Optional<Item.Builder> leggingsBuilder =
          craftEngineManager.getKillerLeggings(properties);
      final Optional<Item.Builder> bootsBuilder = craftEngineManager.getKillerBoots(properties);
      final ItemStack helmet = helmetBuilder.map(Item.Builder::build).orElse(defaultKillerHelmet);
      final ItemStack chestplate =
          chestplateBuilder.map(Item.Builder::build).orElse(defaultKillerChestplate);
      final ItemStack leggings =
          leggingsBuilder.map(Item.Builder::build).orElse(defaultKillerLeggings);
      final ItemStack boots = bootsBuilder.map(Item.Builder::build).orElse(defaultKillerBoots);
      return new ItemStack[] {boots, leggings, chestplate, helmet};
    }

    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager nexoManager = plugin.getNexoManager();
      final Optional<Item.Builder> helmetBuilder = nexoManager.getKillerHelmet(properties);
      final Optional<Item.Builder> chestplateBuilder = nexoManager.getKillerChestplate(properties);
      final Optional<Item.Builder> leggingsBuilder = nexoManager.getKillerLeggings(properties);
      final Optional<Item.Builder> bootsBuilder = nexoManager.getKillerBoots(properties);
      final ItemStack helmet = helmetBuilder.map(Item.Builder::build).orElse(defaultKillerHelmet);
      final ItemStack chestplate =
          chestplateBuilder.map(Item.Builder::build).orElse(defaultKillerChestplate);
      final ItemStack leggings =
          leggingsBuilder.map(Item.Builder::build).orElse(defaultKillerLeggings);
      final ItemStack boots = bootsBuilder.map(Item.Builder::build).orElse(defaultKillerBoots);
      return new ItemStack[] {boots, leggings, chestplate, helmet};
    }

    return new ItemStack[] {
      defaultKillerBoots, defaultKillerLeggings, defaultKillerChestplate, defaultKillerHelmet
    };
  }

  private static ItemStack createDefaultKillerBoots() {
    return Item.builder(Material.LEATHER_BOOTS)
        .name(Message.KILLER_BOOTS.build())
        .dye(Color.RED)
        .enchantment(Enchantment.PROTECTION, 3)
        .build();
  }

  private static ItemStack createDefaultKillerLeggings() {
    return Item.builder(Material.LEATHER_LEGGINGS)
        .name(Message.KILLER_LEGGINGS.build())
        .dye(Color.RED)
        .enchantment(Enchantment.PROTECTION, 3)
        .build();
  }

  private static ItemStack createDefaultKillerHelmet() {
    return Item.builder(Material.PLAYER_HEAD)
        .name(Message.KILLER_HELMET.build())
        .head(ITEM_SKULL_URL)
        .build();
  }

  private static ItemStack createDefaultKillerChestplate() {
    return Item.builder(Material.LEATHER_CHESTPLATE)
        .name(Message.KILLER_CHESTPLATE.build())
        .lore(Message.SURVIVOR_GEAR_LORE.build())
        .dye(Color.RED)
        .enchantment(Enchantment.PROTECTION, 3)
        .build();
  }

  public static Item.Builder createPlayerTracker(final Item.Builder builder) {
    return builder.pdc(Keys.PLAYER_TRACKER, PersistentDataType.INTEGER, 0);
  }

  public static Item.Builder createTranslocator(final Item.Builder builder) {
    return builder.pdc(Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY, new byte[0]);
  }

  public static Item.Builder createKillerTracker(final Item.Builder builder) {
    return builder.pdc(Keys.KILLER_TRACKER, PersistentDataType.INTEGER, 0);
  }

  public static Item.Builder createSmokeGrenade(final Item.Builder builder) {
    return builder.pdc(Keys.SMOKE_GRENADE, PersistentDataType.BOOLEAN, true);
  }

  public static Item.Builder createFlashlight(
      final GameProperties properties, final Item.Builder builder) {
    return builder
        .cooldown((float) properties.getFlashlightCooldown(), Keys.FLASHLIGHT_COOLDOWN)
        .pdc(Keys.FLASHLIGHT, PersistentDataType.BOOLEAN, true);
  }

  public static Item.Builder createFlashBang(final Item.Builder builder) {
    return builder.pdc(Keys.FLASH_BANG, PersistentDataType.BOOLEAN, true);
  }

  public static Item.Builder createSurvivorHelmet(final GameProperties properties) {
    final Item.Builder helmet = createDefaultSurvivorHelmet(properties);
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    if (Capabilities.CRAFTENGINE.isEnabled()) {
      final CraftEngineManager manager = plugin.getCraftEngineManager();
      return manager.getSurvivorHelmet(properties).orElse(helmet);
    }
    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager manager = plugin.getNexoManager();
      return manager.getSurvivorHelmet(properties).orElse(helmet);
    }
    return helmet;
  }

  public static Item.Builder createSurvivorChestplate(final GameProperties properties) {
    final Item.Builder chestplate = createDefaultSurvivorChestplate(properties);
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    if (Capabilities.CRAFTENGINE.isEnabled()) {
      final CraftEngineManager manager = plugin.getCraftEngineManager();
      return manager.getSurvivorChestplate(properties).orElse(chestplate);
    }
    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager manager = plugin.getNexoManager();
      return manager.getSurvivorChestplate(properties).orElse(chestplate);
    }
    return chestplate;
  }

  public static Item.Builder createSurvivorLeggings(final GameProperties properties) {
    final Item.Builder leggings = createDefaultSurvivorLeggings(properties);
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    if (Capabilities.CRAFTENGINE.isEnabled()) {
      final CraftEngineManager manager = plugin.getCraftEngineManager();
      return manager.getSurvivorLeggings(properties).orElse(leggings);
    }
    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager manager = plugin.getNexoManager();
      return manager.getSurvivorLeggings(properties).orElse(leggings);
    }
    return leggings;
  }

  public static Item.Builder createSurvivorBoots(final GameProperties properties) {
    final Item.Builder boots = createDefaultSurvivorBoots(properties);
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    if (Capabilities.CRAFTENGINE.isEnabled()) {
      final CraftEngineManager manager = plugin.getCraftEngineManager();
      return manager.getSurvivorBoots(properties).orElse(boots);
    }
    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager manager = plugin.getNexoManager();
      return manager.getSurvivorBoots(properties).orElse(boots);
    }
    return boots;
  }

  public static Item.Builder createDefaultSurvivorHelmet(final GameProperties properties) {
    return Item.builder(properties.getSurvivorHelmetMaterial())
        .name(Message.SURVIVOR_HELMET.build())
        .lore(Message.SURVIVOR_GEAR_LORE.build())
        .enchantment(Enchantment.PROTECTION, 3)
        .pdc(Keys.GADGET_KEY_NAME, PersistentDataType.STRING, "survivor_helmet")
        .hideAttributes();
  }

  public static Item.Builder createDefaultSurvivorChestplate(final GameProperties properties) {
    return Item.builder(properties.getSurvivorChestplateMaterial())
        .name(Message.SURVIVOR_CHESTPLATE.build())
        .lore(Message.SURVIVOR_GEAR_LORE.build())
        .enchantment(Enchantment.PROTECTION, 3)
        .pdc(Keys.GADGET_KEY_NAME, PersistentDataType.STRING, "survivor_chestplate")
        .hideAttributes();
  }

  public static Item.Builder createDefaultSurvivorLeggings(final GameProperties properties) {
    return Item.builder(properties.getSurvivorLeggingsMaterial())
        .name(Message.SURVIVOR_LEGGINGS.build())
        .lore(Message.SURVIVOR_GEAR_LORE.build())
        .enchantment(Enchantment.PROTECTION, 3)
        .pdc(Keys.GADGET_KEY_NAME, PersistentDataType.STRING, "survivor_leggings")
        .hideAttributes();
  }

  public static Item.Builder createDefaultSurvivorBoots(final GameProperties properties) {
    return Item.builder(properties.getSurvivorBootsMaterial())
        .name(Message.SURVIVOR_BOOTS.build())
        .lore(Message.SURVIVOR_GEAR_LORE.build())
        .enchantment(Enchantment.PROTECTION, 3)
        .pdc(Keys.GADGET_KEY_NAME, PersistentDataType.STRING, "survivor_boots")
        .hideAttributes();
  }

  public static Item.Builder createPortalGun(final Item.Builder builder) {
    final UUID uuid = UUID.randomUUID();
    final String data = uuid.toString();
    return builder
        .pdc(Keys.PORTAL_GUN, PersistentDataType.BOOLEAN, true)
        .pdc(Keys.UUID, PersistentDataType.STRING, data)
        .enchantment(Enchantment.INFINITY, 1)
        .unbreakable()
        .hideAttributes();
  }

  public static Item.Builder createHook(final Item.Builder builder) {
    return builder
        .pdc(Keys.HOOK, PersistentDataType.BOOLEAN, true)
        .unbreakable()
        .hideAttributes();
  }

  public static Item.Builder createSpeedPendant(final Item.Builder builder) {
    return builder.modifier(Attribute.MOVEMENT_SPEED, 0.03);
  }

  public static Item.Builder createMedKit(final Item.Builder builder) {
    return builder.potionColor(Color.RED).potion(PotionType.STRONG_HEALING);
  }

  public static Item.Builder createLeaveItem() {
    return Item.builder(Material.RED_DYE)
        .name(Message.LOBBY_LEAVE_NAME.build())
        .lore(Message.LOBBY_LEAVE_LORE.build())
        .pdc(Keys.LEAVE, PersistentDataType.BOOLEAN, true)
        .hideAttributes();
  }

  public static Item.Builder createEmptyAbility() {
    return Item.builder(Material.DIAMOND)
        .name(Message.EMPTY_ABILITY_NAME.build())
        .lore(Message.EMPTY_ABILITY_LORE.build())
        .model("emptyability")
        .pdc(Keys.ABILITY_KEY_NAME, PersistentDataType.STRING, "empty_ability")
        .hideAttributes();
  }

  public static Item.Builder createAbility(
      final String pdcName,
      final Component itemName,
      final Component itemLore,
      final int cooldown) {
    final String texture = pdcName.replace("_", "");
    final NamespacedKey key = new NamespacedKey("murderrun", pdcName);
    return Item.builder(Material.FISHING_ROD)
        .name(itemName)
        .lore(itemLore)
        .model(texture)
        .pdc(Keys.ABILITY_KEY_NAME, PersistentDataType.STRING, pdcName)
        .cooldown(cooldown, key)
        .hideAttributes();
  }

  public static Item.Builder createGadget(
      final String pdcName,
      final Material material,
      final Component itemName,
      final Component itemLore) {
    final String texture = pdcName.replace("_", "");
    return Item.builder(material)
        .name(itemName)
        .lore(itemLore)
        .model(texture)
        .pdc(Keys.GADGET_KEY_NAME, PersistentDataType.STRING, pdcName)
        .hideAttributes();
  }

  public static ItemStack createSaddle() {
    return Item.builder(Material.SADDLE).build();
  }

  public static Item.Builder createShield(final GameProperties properties) {
    return Item.builder(properties.getShieldMaterial())
        .name(Message.SHIELD_NAME.build())
        .lore(Message.SHIELD_LORE.build())
        .pdc(Keys.GADGET_KEY_NAME, PersistentDataType.STRING, "shield")
        .hideAttributes()
        .durability(5);
  }

  public static Item.Builder createExcavator(final Item.Builder builder) {
    return builder.pdc(Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN, true).durability(10);
  }

  public static ItemStack createKnockBackBone(final GameProperties properties) {
    final ItemStack ghostBone = createDefaultKnockBackBone();
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    if (Capabilities.CRAFTENGINE.isEnabled()) {
      final CraftEngineManager manager = plugin.getCraftEngineManager();
      return manager.getGhostBone(properties).map(Item.Builder::build).orElse(ghostBone);
    }
    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager manager = plugin.getNexoManager();
      return manager.getGhostBone(properties).map(Item.Builder::build).orElse(ghostBone);
    }
    return ghostBone;
  }

  public static ItemStack createDefaultKnockBackBone() {
    return Item.builder(Material.BONE).enchantment(Enchantment.KNOCKBACK, 1).build();
  }

  public static ItemStack createFakePart() {
    final int random = RandomUtils.generateInt(1, 6);
    final String name = "car_part_%s".formatted(random);
    return Item.builder(Material.DIAMOND).model(name).build();
  }

  public static ItemStack createCursedNote() {
    return Item.builder(Material.PAPER).name(Message.CURSED_NOTE_NAME.build()).build();
  }

  public static ItemStack createCarPart(final String uuid) {
    final int random = RandomUtils.generateInt(1, 6);
    final String name = "car_part_%s".formatted(random);
    return Item.builder(Material.DIAMOND)
        .name(Message.CAR_PART_ITEM_NAME.build())
        .lore(Message.CAR_PART_ITEM_LORE.build())
        .model(name)
        .pdc(Keys.CAR_PART_UUID, PersistentDataType.STRING, uuid)
        .build();
  }

  public static ItemStack createCurrency(final GameProperties properties, final int amount) {
    final ItemStack currency = createDefaultCurrency(amount);
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    if (Capabilities.CRAFTENGINE.isEnabled()) {
      final CraftEngineManager manager = plugin.getCraftEngineManager();
      return manager.getCurrency(properties).map(Item.Builder::build).orElse(currency);
    }
    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager manager = plugin.getNexoManager();
      return manager.getCurrency(properties).map(Item.Builder::build).orElse(currency);
    }
    return currency;
  }

  public static ItemStack createDefaultCurrency(final int amount) {
    return Item.builder(Material.NETHER_STAR)
        .name(Message.MINEBUCKS.build())
        .amount(amount)
        .model("minebucks")
        .build();
  }

  public static ItemStack createKillerArrow(final GameProperties properties) {
    final ItemStack killerArrow = createDefaultKillerArrow();
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    if (Capabilities.CRAFTENGINE.isEnabled()) {
      final CraftEngineManager manager = plugin.getCraftEngineManager();
      return manager.getKillerArrow(properties).map(Item.Builder::build).orElse(killerArrow);
    }
    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager manager = plugin.getNexoManager();
      return manager.getKillerArrow(properties).map(Item.Builder::build).orElse(killerArrow);
    }
    return killerArrow;
  }

  public static ItemStack createDefaultKillerArrow() {
    return Item.builder(Material.ARROW)
        .name(Message.ARROW_NAME.build())
        .lore(Message.ARROW_LORE.build())
        .enchantment(Enchantment.VANISHING_CURSE, 1)
        .model("laser")
        .hideAttributes()
        .build();
  }

  public static ItemStack createKillerSword(final GameProperties properties) {
    final ItemStack killerSword = createDefaultKillerSword();
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    if (Capabilities.CRAFTENGINE.isEnabled()) {
      final CraftEngineManager manager = plugin.getCraftEngineManager();
      return manager.getKillerSword(properties).map(Item.Builder::build).orElse(killerSword);
    }
    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager manager = plugin.getNexoManager();
      return manager.getKillerSword(properties).map(Item.Builder::build).orElse(killerSword);
    }
    return killerSword;
  }

  private static ItemStack createDefaultKillerSword() {
    return Item.builder(Material.DIAMOND_SWORD)
        .name(Message.KILLER_SWORD.build())
        .model("sword")
        .modifier(Attribute.ATTACK_DAMAGE, 8)
        .pdc(Keys.SPECIAL_SWORD, PersistentDataType.BOOLEAN, true)
        .pdc(Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN, true)
        .enchantment(Enchantment.VANISHING_CURSE, 1)
        .unbreakable()
        .hideAttributes()
        .build();
  }
}

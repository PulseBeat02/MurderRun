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
package io.github.pulsebeat02.murderrun.utils.item;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.capability.Capabilities;
import io.github.pulsebeat02.murderrun.game.extension.nexo.NexoManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;

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
      .model(1)
      .build();
  }

  public static ItemStack[] createKillerGear() {
    final ItemStack defaultKillerHelmet = createDefaultKillerHelmet();
    final ItemStack defaultKillerChestplate = createDefaultKillerChestplate();
    final ItemStack defaultKillerLeggings = createDefaultKillerLeggings();
    final ItemStack defaultKillerBoots = createDefaultKillerBoots();
    if (Capabilities.NEXO.isEnabled()) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final NexoManager manager = plugin.getNexoManager();
      final ItemStack helmet = manager.getKillerHelmet().map(Item.Builder::build).orElse(defaultKillerHelmet);
      final ItemStack chestplate = manager.getKillerChestplate().map(Item.Builder::build).orElse(defaultKillerChestplate);
      final ItemStack leggings = manager.getKillerLeggings().map(Item.Builder::build).orElse(defaultKillerLeggings);
      final ItemStack boots = manager.getKillerBoots().map(Item.Builder::build).orElse(defaultKillerBoots);
      return new ItemStack[] { boots, leggings, chestplate, helmet };
    }
    return new ItemStack[] { defaultKillerBoots, defaultKillerLeggings, defaultKillerChestplate, defaultKillerHelmet };
  }

  private static ItemStack createDefaultKillerBoots() {
    return Item.builder(Material.LEATHER_BOOTS)
      .name(Message.KILLER_BOOTS.build())
      .dye(Color.RED)
      .enchantment(Enchantment.PROTECTION, 3)
      .model(1)
      .build();
  }

  private static ItemStack createDefaultKillerLeggings() {
    return Item.builder(Material.LEATHER_LEGGINGS)
      .name(Message.KILLER_LEGGINGS.build())
      .dye(Color.RED)
      .enchantment(Enchantment.PROTECTION, 3)
      .model(1)
      .build();
  }

  private static ItemStack createDefaultKillerHelmet() {
    return Item.builder(Material.PLAYER_HEAD).name(Message.KILLER_HELMET.build()).head(ITEM_SKULL_URL).build();
  }

  private static ItemStack createDefaultKillerChestplate() {
    return Item.builder(Material.LEATHER_CHESTPLATE)
      .name(Message.KILLER_CHESTPLATE.build())
      .dye(Color.RED)
      .enchantment(Enchantment.PROTECTION, 3)
      .model(1)
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
    return builder.pdc(Keys.SMOKE_GRENADE, PersistentDataType.BOOLEAN, true).model(2);
  }

  public static Item.Builder createFlashlight(final Item.Builder builder) {
    return builder.pdc(Keys.FLASHLIGHT_USE, PersistentDataType.LONG, 0L);
  }

  public static Item.Builder createFlashBang(final Item.Builder builder) {
    return builder.pdc(Keys.FLASH_BANG, PersistentDataType.BOOLEAN, true).model(1);
  }

  public static Item.Builder createSurvivorHelmet() {
    final Item.Builder helmet = createDefaultSurvivorHelmet();
    if (Capabilities.NEXO.isEnabled()) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final NexoManager manager = plugin.getNexoManager();
      return manager.getSurvivorHelmet().orElse(helmet);
    }
    return helmet;
  }

  public static Item.Builder createSurvivorChestplate() {
    final Item.Builder chestplate = createDefaultSurvivorChestplate();
    if (Capabilities.NEXO.isEnabled()) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final NexoManager manager = plugin.getNexoManager();
      return manager.getSurvivorChestplate().orElse(chestplate);
    }
    return chestplate;
  }

  public static Item.Builder createSurvivorLeggings() {
    final Item.Builder leggings = createDefaultSurvivorLeggings();
    if (Capabilities.NEXO.isEnabled()) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final NexoManager manager = plugin.getNexoManager();
      return manager.getSurvivorLeggings().orElse(leggings);
    }
    return leggings;
  }

  public static Item.Builder createSurvivorBoots() {
    final Item.Builder boots = createDefaultSurvivorBoots();
    if (Capabilities.NEXO.isEnabled()) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final NexoManager manager = plugin.getNexoManager();
      return manager.getSurvivorBoots().orElse(boots);
    }
    return boots;
  }

  public static Item.Builder createDefaultSurvivorHelmet() {
    return Item.builder(GameProperties.SURVIVOR_HELMET_MATERIAL)
      .name(Message.SURVIVOR_HELMET.build())
      .enchantment(Enchantment.PROTECTION, 3);
  }

  public static Item.Builder createDefaultSurvivorChestplate() {
    return Item.builder(GameProperties.SURVIVOR_CHESTPLATE_MATERIAL)
      .name(Message.SURVIVOR_CHESTPLATE.build())
      .enchantment(Enchantment.PROTECTION, 3);
  }

  public static Item.Builder createDefaultSurvivorLeggings() {
    return Item.builder(GameProperties.SURVIVOR_LEGGINGS_MATERIAL)
      .name(Message.SURVIVOR_LEGGINGS.build())
      .enchantment(Enchantment.PROTECTION, 3);
  }

  public static Item.Builder createDefaultSurvivorBoots() {
    return Item.builder(GameProperties.SURVIVOR_BOOTS_MATERIAL).name(Message.SURVIVOR_BOOTS.build()).enchantment(Enchantment.PROTECTION, 3);
  }

  public static Item.Builder createPortalGun(final Item.Builder builder) {
    final UUID uuid = UUID.randomUUID();
    final String data = uuid.toString();
    return builder
      .pdc(Keys.PORTAL_GUN, PersistentDataType.BOOLEAN, true)
      .pdc(Keys.UUID, PersistentDataType.STRING, data)
      .enchantment(Enchantment.INFINITY, 1)
      .unbreakable();
  }

  public static Item.Builder createHook(final Item.Builder builder) {
    return builder.pdc(Keys.HOOK, PersistentDataType.BOOLEAN, true).unbreakable();
  }

  public static Item.Builder createSpeedPendant(final Item.Builder builder) {
    return builder.modifier(Attribute.MOVEMENT_SPEED, 0.03);
  }

  public static Item.Builder createMedKit(final Item.Builder builder) {
    return builder.potionColor(Color.RED).potion(PotionType.STRONG_HEALING);
  }

  public static Item.Builder createGadget(
    final String pdcName,
    final Material material,
    final Component itemName,
    final Component itemLore
  ) {
    return Item.builder(requireNonNull(material))
      .name(requireNonNull(itemName))
      .lore(requireNonNull(itemLore))
      .pdc(Keys.GADGET_KEY_NAME, PersistentDataType.STRING, requireNonNull(pdcName))
      .hideAttributes();
  }

  public static ItemStack createSaddle() {
    return Item.builder(Material.SADDLE).build();
  }

  public static Item.Builder createShield(final Item.Builder builder) {
    return builder.durability(5);
  }

  public static Item.Builder createExcavator(final Item.Builder builder) {
    return builder.pdc(Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN, true).durability(10);
  }

  public static ItemStack createKnockBackBone() {
    final ItemStack ghostBone = createDefaultKnockBackBone();
    if (Capabilities.NEXO.isEnabled()) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final NexoManager manager = plugin.getNexoManager();
      return manager.getGhostBone().map(Item.Builder::build).orElse(ghostBone);
    }
    return ghostBone;
  }

  public static ItemStack createDefaultKnockBackBone() {
    return Item.builder(Material.BONE).enchantment(Enchantment.KNOCKBACK, 0).build();
  }

  public static ItemStack createFakePart() {
    return Item.builder(Material.DIAMOND).model(RandomUtils.generateInt(1, 6)).build();
  }

  public static ItemStack createCursedNote() {
    return Item.builder(Material.PAPER).name(Message.CURSED_NOTE_NAME.build()).build();
  }

  public static ItemStack createCarPart(final String uuid) {
    return Item.builder(Material.DIAMOND)
      .name(Message.CAR_PART_ITEM_NAME.build())
      .lore(Message.CAR_PART_ITEM_LORE.build())
      .model(RandomUtils.generateInt(1, 6))
      .pdc(Keys.CAR_PART_UUID, PersistentDataType.STRING, uuid)
      .build();
  }

  public static ItemStack createCurrency(final int amount) {
    final ItemStack currency = createDefaultCurrency(amount);
    if (Capabilities.NEXO.isEnabled()) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final NexoManager manager = plugin.getNexoManager();
      return manager.getCurrency().map(Item.Builder::build).orElse(currency);
    }
    return currency;
  }

  public static ItemStack createDefaultCurrency(final int amount) {
    return Item.builder(Material.NETHER_STAR).name(Message.MINEBUCKS.build()).amount(amount).model(1).build();
  }

  public static ItemStack createKillerArrow() {
    final ItemStack killerArrow = createDefaultKillerArrow();
    if (Capabilities.NEXO.isEnabled()) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final NexoManager manager = plugin.getNexoManager();
      return manager.getKillerArrow().map(Item.Builder::build).orElse(killerArrow);
    }
    return killerArrow;
  }

  public static ItemStack createDefaultKillerArrow() {
    return Item.builder(Material.ARROW)
      .name(Message.ARROW_NAME.build())
      .lore(Message.ARROW_LORE.build())
      .enchantment(Enchantment.VANISHING_CURSE, 1)
      .hideAttributes()
      .build();
  }

  public static ItemStack createKillerSword() {
    final ItemStack killerSword = createDefaultKillerSword();
    if (Capabilities.NEXO.isEnabled()) {
      final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
      final NexoManager manager = plugin.getNexoManager();
      return manager.getKillerSword().map(Item.Builder::build).orElse(killerSword);
    }
    return killerSword;
  }

  private static ItemStack createDefaultKillerSword() {
    return Item.builder(Material.DIAMOND_SWORD)
      .name(Message.KILLER_SWORD.build())
      .model(1)
      .hideAttributes()
      .modifier(Attribute.ATTACK_DAMAGE, 8)
      .pdc(Keys.SPECIAL_SWORD, PersistentDataType.BOOLEAN, true)
      .pdc(Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN, true)
      .enchantment(Enchantment.VANISHING_CURSE, 1)
      .unbreakable()
      .build();
  }
}

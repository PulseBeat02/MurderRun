package io.github.pulsebeat02.murderrun.utils;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ItemFactory {

  private ItemFactory() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static ItemStack createGadget(
      final String pdcName,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final @Nullable Consumer<ItemStack> consumer) {
    return ItemBuilder.builder(requireNonNull(material))
        .name(requireNonNull(itemName))
        .lore(requireNonNull(itemLore))
        .pdc(Keys.GADGET_KEY_NAME, PersistentDataType.STRING, requireNonNull(pdcName))
        .consume(consumer)
        .hideAttributes()
        .build();
  }

  public static ItemStack createSaddle() {
    return ItemBuilder.builder(Material.SADDLE).build();
  }

  public static ItemStack createShield(final ItemStack stack) {
    return ItemBuilder.builder(stack).durability(5).build();
  }

  public static ItemStack createExcavator(final ItemStack stack) {
    return ItemBuilder.builder(stack).durability(10).build();
  }

  public static ItemStack createDeathGear(final Material armor) {
    return ItemBuilder.builder(armor).dye(Color.RED).build();
  }

  public static ItemStack createPlayerHead(final Player player) {
    return ItemBuilder.builder(Material.PLAYER_HEAD).head(player).build();
  }

  public static ItemStack createGhostGear(final Material armor) {
    return ItemBuilder.builder(armor).dye(Color.WHITE).build();
  }

  public static ItemStack createKnockBackBone() {
    return ItemBuilder.builder(Material.BONE)
        .enchantment(Enchantment.KNOCKBACK, 2)
        .build();
  }

  public static ItemStack createFakePart() {
    return ItemBuilder.builder(Material.DIAMOND)
        .model(RandomUtils.generateInt(1, 6))
        .build();
  }

  public static ItemStack createCursedNote() {
    return ItemBuilder.builder(Material.PAPER)
        .name(Message.CURSED_NOTE_NAME.build())
        .build();
  }

  public static ItemStack createCarPart(final String uuid) {
    return ItemBuilder.builder(Material.DIAMOND)
        .name(Message.CAR_PART_ITEM_NAME.build())
        .lore(Message.CAR_PART_ITEM_LORE.build())
        .model(RandomUtils.generateInt(1, 6))
        .pdc(Keys.CAR_PART_UUID, PersistentDataType.STRING, uuid)
        .build();
  }

  public static ItemStack createCurrency() {
    return ItemBuilder.builder(Material.NETHER_STAR)
        .name(Message.MINEBUCKS.build())
        .model(1)
        .build();
  }

  public static ItemStack createKillerArrow() {
    return ItemBuilder.builder(Material.ARROW)
        .name(Message.ARROW_NAME.build())
        .lore(Message.ARROW_LORE.build())
        .hideAttributes()
        .build();
  }

  public static ItemStack createKillerSword() {
    return ItemBuilder.builder(Material.DIAMOND_SWORD)
        .name(Message.KILLER_SWORD.build())
        .model(1)
        .hideAttributes()
        .modifier(Attribute.GENERIC_ATTACK_DAMAGE, 8)
        .pdc(Keys.SPECIAL_SWORD, PersistentDataType.BOOLEAN, true)
        .pdc(Keys.CAN_BREAK_BLOCKS, PersistentDataType.BOOLEAN, true)
        .build();
  }
}

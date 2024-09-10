package io.github.pulsebeat02.murderrun.hibernate.converters;

import jakarta.persistence.Converter;
import org.bukkit.inventory.ItemStack;

@Converter(autoApply = true)
public final class ItemStackConverter extends AbstractJSONConverter<ItemStack> {}

package io.github.pulsebeat02.murderrun.data.hibernate.converters;

import jakarta.persistence.Converter;
import org.bukkit.inventory.ItemStack;

@Converter(autoApply = true)
public final class ItemStackConverter extends AbstractJSONConverter<ItemStack> {}

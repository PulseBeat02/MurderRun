package io.github.pulsebeat02.murderrun.hibernate.converters;

import jakarta.persistence.Converter;
import org.bukkit.Location;

@Converter(autoApply = true)
public final class LocationConverter extends AbstractJSONConverter<Location> {}

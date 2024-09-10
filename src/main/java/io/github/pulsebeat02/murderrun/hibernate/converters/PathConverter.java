package io.github.pulsebeat02.murderrun.hibernate.converters;

import jakarta.persistence.Converter;
import java.nio.file.Path;

@Converter(autoApply = true)
public final class PathConverter extends AbstractJSONConverter<Path> {}

package io.github.pulsebeat02.murderrun.data.hibernate.converters;

import io.github.pulsebeat02.murderrun.immutable.SerializableVector;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public final class SerializableVectorConverter extends AbstractJSONConverter<SerializableVector> {}

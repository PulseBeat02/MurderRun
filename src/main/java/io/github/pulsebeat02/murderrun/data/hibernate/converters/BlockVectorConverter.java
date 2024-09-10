package io.github.pulsebeat02.murderrun.data.hibernate.converters;

import com.sk89q.worldedit.math.BlockVector3;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public final class BlockVectorConverter extends AbstractJSONConverter<BlockVector3> {}

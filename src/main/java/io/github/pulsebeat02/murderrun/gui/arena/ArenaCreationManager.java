package io.github.pulsebeat02.murderrun.gui.arena;

import io.github.pulsebeat02.murderrun.data.hibernate.identifier.HibernateSerializable;
import jakarta.persistence.*;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "arena_creation_manager")
public final class ArenaCreationManager implements Serializable, HibernateSerializable {

    @Serial
    private static final long serialVersionUID = 2568177708134148153L;

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @MapKeyColumn(name = "name")
    @JoinColumn(name = "arena_creation_manager_id")
    @Column(name = "arena")
    private final Map<UUID, ArenaCreation> arenas;

    public ArenaCreationManager() {
        this.arenas = new HashMap<>();
    }

    public void addArena(
            final UUID uuid,
            final String arenaName,
            final Location spawn,
            final Location truck,
            final Location first,
            final Location second,
            final Collection<Location> itemLocations
    ) {
        final ArenaCreation creation = new ArenaCreation(arenaName, spawn, truck, first, second, itemLocations);
        this.arenas.put(uuid, creation);
    }

    public @Nullable ArenaCreation getArena(final UUID name) {
        return this.arenas.get(name);
    }

    public void removeArena(final UUID name) {
        this.arenas.remove(name);
    }

    public Map<UUID, ArenaCreation> getArenas() {
        return this.arenas;
    }

    public Set<@KeyFor("this.arenas") UUID> getPlayerData() {
        return this.arenas.keySet();
    }

    @Override
    public Long getId() {
        return this.id;
    }
}

package io.github.pulsebeat02.murderrun.gui.arena;

import io.github.pulsebeat02.murderrun.data.hibernate.converters.LocationConverter;
import jakarta.persistence.*;
import org.bukkit.Location;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "arena")
public final class ArenaCreation implements Serializable {

    @Serial
    private static final long serialVersionUID = -5175701778472967665L;

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "arena_name")
    private volatile String arenaName;

    @Convert(converter = LocationConverter.class)
    @Column(name = "spawn")
    private volatile Location spawn;

    @Convert(converter = LocationConverter.class)
    @Column(name = "truck")
    private volatile Location truck;

    @Convert(converter = LocationConverter.class)
    @Column(name = "first")
    private volatile Location first;

    @Convert(converter = LocationConverter.class)
    @Column(name = "second")
    private volatile Location second;

    @Convert(converter = LocationConverter.class)
    @Column(name = "item_locations")
    private volatile Collection<Location> itemLocations;

    public ArenaCreation(final String arenaName, final Location spawn, final Location truck, final Location first, final Location second, final Collection<Location> itemLocations) {
        this.arenaName = arenaName;
        this.spawn = spawn;
        this.truck = truck;
        this.first = first;
        this.second = second;
        this.itemLocations = itemLocations;
    }

    public ArenaCreation() {
    }

    public String getArenaName() {
        return this.arenaName;
    }

    public void setArenaName(final String arenaName) {
        this.arenaName = arenaName;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public void setSpawn(final Location spawn) {
        this.spawn = spawn;
    }

    public Location getTruck() {
        return this.truck;
    }

    public void setTruck(final Location truck) {
        this.truck = truck;
    }

    public Location getFirst() {
        return this.first;
    }

    public void setFirst(final Location first) {
        this.first = first;
    }

    public Location getSecond() {
        return this.second;
    }

    public void setSecond(final Location second) {
        this.second = second;
    }

    public Collection<Location> getItemLocations() {
        return this.itemLocations;
    }

    public void setItemLocations(final Collection<Location> itemLocations) {
        this.itemLocations = itemLocations;
    }
}

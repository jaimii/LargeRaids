package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Horse;

public class AdjudicatorRider implements Raider, RiderRaider {
    private final Vindicator bukkitEntity;
    private final Horse horse;

    public AdjudicatorRider(Vindicator bukkitEntity, Horse horse) {
        this.bukkitEntity = bukkitEntity;
        this.horse = horse;
    }

    @Override
    public Vindicator getBukkitEntity() {
        return bukkitEntity;
    }

    @Override
    public Horse getVehicle() {
        return horse;
    }
}
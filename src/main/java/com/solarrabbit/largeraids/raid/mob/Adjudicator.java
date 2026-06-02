package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.entity.Vindicator;

public class Adjudicator implements Raider {
    private final Vindicator bukkitEntity;

    public Adjudicator(Vindicator bukkitEntity) {
        this.bukkitEntity = bukkitEntity;
    }

    @Override
    public Vindicator getBukkitEntity() {
        return bukkitEntity;
    }
}
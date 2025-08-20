package com.solarrabbit.largeraids.config.custommobs;

import org.bukkit.configuration.ConfigurationSection;

public class BomberConfig {
    private final float bomberExplosivePower;
    private final float tntExplosivePower;
    private final int primedTntTicks;
    private final boolean tntBreakBlocks;

    public BomberConfig(ConfigurationSection config) {
        bomberExplosivePower = (float)config.getDouble("bomber-explosive-power");
        tntExplosivePower = (float)config.getDouble("tnt-explosive-power");
        primedTntTicks = config.getInt("primed-tnt-ticks");
        tntBreakBlocks = config.getBoolean("tnt-break-blocks");
    }

    public float getBomberExplosivePower() {
        return bomberExplosivePower;
    }

    public float getTntExplosivePower() {
        return tntExplosivePower;
    }

    public int getPrimedTntTicks() {
        return primedTntTicks;
    }

    public boolean shouldTntBreakBlocks() {
        return tntBreakBlocks;
    }
}

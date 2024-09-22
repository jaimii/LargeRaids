package com.solarrabbit.largeraids.config.custommobs;

import org.bukkit.configuration.ConfigurationSection;

public class FireworkPillagerConfig {
    private final double health;

    public FireworkPillagerConfig(ConfigurationSection config) {
        health = config.getDouble("health");
    }

    public double getHealth() {
        return health;
    }
}

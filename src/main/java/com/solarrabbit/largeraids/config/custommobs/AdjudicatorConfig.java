package com.solarrabbit.largeraids.config.custommobs;

import org.bukkit.configuration.ConfigurationSection;

public class AdjudicatorConfig {
    private final double health;
    private final double horseHealth;

    public AdjudicatorConfig(ConfigurationSection config) {
        if (config == null) {
            this.health = 24.0;
            this.horseHealth = 30.0;
        } else {
            this.health = config.getDouble("health", 24.0);
            this.horseHealth = config.getDouble("horse-health", config.getDouble("ravager-health", 30.0));
        }
    }

    public double getHealth() {
        return health;
    }

    public double getHorseHealth() {
        return horseHealth;
    }
}
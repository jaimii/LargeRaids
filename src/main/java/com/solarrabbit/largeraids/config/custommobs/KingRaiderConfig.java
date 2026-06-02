package com.solarrabbit.largeraids.config.custommobs;

import org.bukkit.configuration.ConfigurationSection;

public class KingRaiderConfig {
    private final double ravagerHealth;
    private final double ravagerDamage;
    private final double fangDamage;
    private final int fireTicks;
    private final int regenLevel;
    private final double evokerHealth;
    private final int evokerRegenLevel;

    public KingRaiderConfig(ConfigurationSection config) {
        ravagerHealth = config.getDouble("ravager-health");
        ravagerDamage = config.getDouble("ravager-damage");
        fangDamage = config.getDouble("fang-damage");
        fireTicks = config.getInt("fire-ticks");
        regenLevel = config.getInt("regen-level");
        // Fallbacks if these keys are missing from older config.yml files
        evokerHealth = config.getDouble("evoker-health", 24.0);
        evokerRegenLevel = config.getInt("evoker-regen-level", 1);
    }

    public double getRavagerHealth() {
        return ravagerHealth;
    }

    public double getRavagerDamage() {
        return ravagerDamage;
    }

    public double getFangDamage() {
        return fangDamage;
    }

    public int getFireTicks() {
        return fireTicks;
    }

    public int getRegenLevel() {
        return regenLevel;
    }

    public double getEvokerHealth() {
        return evokerHealth;
    }

    public int getEvokerRegenLevel() {
        return evokerRegenLevel;
    }
}
package com.solarrabbit.largeraids.config.custommobs;

import org.bukkit.configuration.ConfigurationSection;

public class KingRaiderConfig {
    private final double ravagerHealth;
    private final double ravagerDamage;
    private final double fangDamage;
    private final int fireTicks;
    private final int regenLevel;

    public KingRaiderConfig(ConfigurationSection config) {
    	ravagerHealth = config.getDouble("ravager-health");
    	ravagerDamage = config.getDouble("ravager-damage");
    	fangDamage = config.getDouble("fang-damage");
    	fireTicks = config.getInt("fire-ticks");
    	regenLevel = config.getInt("regen-level");
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
}

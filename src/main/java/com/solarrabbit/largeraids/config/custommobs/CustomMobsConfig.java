package com.solarrabbit.largeraids.config.custommobs;

import org.bukkit.configuration.ConfigurationSection;

public class CustomMobsConfig {
    private final FireworkPillagerConfig fireworkPillagerConfig;
    private final BomberConfig bomberConfig;
    private final KingRaiderConfig kingRaiderConfig;
    private final AdjudicatorConfig adjudicatorConfig;

    public CustomMobsConfig(ConfigurationSection config) {
        this.fireworkPillagerConfig = new FireworkPillagerConfig(config.getConfigurationSection("firework-pillager"));
        this.bomberConfig = new BomberConfig(config.getConfigurationSection("bomber"));
        this.kingRaiderConfig = new KingRaiderConfig(config.getConfigurationSection("king-raider"));
        this.adjudicatorConfig = new AdjudicatorConfig(config.getConfigurationSection("adjudicator"));
    }

    public FireworkPillagerConfig getFireworkPillagerConfig() {
        return fireworkPillagerConfig;
    }

    public BomberConfig getBomberConfig() {
        return bomberConfig;
    }

    public KingRaiderConfig getKingRaiderConfig() {
        return kingRaiderConfig;
    }

    public AdjudicatorConfig getAdjudicatorConfig() {
        return adjudicatorConfig;
    }
}
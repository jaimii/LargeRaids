package com.solarrabbit.largeraids.config.custommobs;

import org.bukkit.configuration.ConfigurationSection;

public class CustomMobsConfig {
    private final FireworkPillagerConfig fireworkPillagerConfig;
    private final BomberConfig bomberConfig;
    private final KingRaiderConfig kingRaiderConfig;

    public CustomMobsConfig(ConfigurationSection config) {
        fireworkPillagerConfig = new FireworkPillagerConfig(config.getConfigurationSection("firework-pillager"));
        bomberConfig = new BomberConfig(config.getConfigurationSection("bomber"));
        kingRaiderConfig = new KingRaiderConfig(config.getConfigurationSection("king-raider"));
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
}

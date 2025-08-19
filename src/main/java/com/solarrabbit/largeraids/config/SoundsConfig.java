package com.solarrabbit.largeraids.config;

import javax.annotation.Nonnull;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

public class SoundsConfig {
    private final Sound summonSound;
    private final Sound victorySound;
    private final Sound defeatSound;

    SoundsConfig(ConfigurationSection config) {
        String summonSoundName = config.getString("summon", null);
        summonSound = summonSoundName == null ? null : getSound(summonSoundName);
        String victorySoundName = config.getString("victory", null);
        victorySound = victorySoundName == null ? null : getSound(victorySoundName);
        String defeatSoundName = config.getString("defeat", null);
        defeatSound = defeatSoundName == null ? null : getSound(defeatSoundName);
    }

    public Sound getSummonSound() {
        return summonSound;
    }

    public Sound getVictorySound() {
        return victorySound;
    }

    public Sound getDefeatSound() {
        return defeatSound;
    }

    private Sound getSound(@Nonnull String name) {
        return Registry.SOUNDS.get(new NamespacedKey(NamespacedKey.MINECRAFT, name));
    }
}

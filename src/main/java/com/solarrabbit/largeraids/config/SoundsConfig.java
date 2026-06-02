package com.solarrabbit.largeraids.config;

import javax.annotation.Nonnull;

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
        // 1. Lowercase standard namespaced string conversion
        String formattedName = name.toLowerCase(java.util.Locale.ROOT).trim();

        // 2. Try parsing namespaced key directly (e.g., "block.anvil.land")
        org.bukkit.NamespacedKey key = org.bukkit.NamespacedKey.fromString(formattedName);
        Sound sound = (key != null) ? org.bukkit.Registry.SOUNDS.get(key) : null;

        // 3. Fallback for legacy enum config strings containing underscores (e.g., "BLOCK_ANVIL_LAND")
        if (sound == null) {
            String legacyFormatted = formattedName.replace('_', '.');
            org.bukkit.NamespacedKey legacyKey = org.bukkit.NamespacedKey.fromString(legacyFormatted);
            sound = (legacyKey != null) ? org.bukkit.Registry.SOUNDS.get(legacyKey) : null;
        }

        // 4. Safe fallback for older API versions, suppressing the deprecation warning
        if (sound == null) {
            try {
                @SuppressWarnings("deprecation")
                Sound valueOfSound = Sound.valueOf(name.toUpperCase(java.util.Locale.ROOT));
                sound = valueOfSound;
            } catch (IllegalArgumentException | NullPointerException ignored) {
                // Return null if not a valid sound name
            }
        }
        return sound;
    }
}

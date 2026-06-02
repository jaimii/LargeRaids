package com.solarrabbit.largeraids.raid.mob.manager;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.config.custommobs.CustomMobsConfig;
import com.solarrabbit.largeraids.raid.mob.Adjudicator;
import com.solarrabbit.largeraids.raid.mob.AdjudicatorRider;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Vindicator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ShieldMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class AdjudicatorManager implements CustomRaiderManager, Listener {
    private double health;
    private double horseHealth;

    public AdjudicatorManager() {
    }

    @Override
    public void loadSettings(CustomMobsConfig config) {
        if (config != null && config.getAdjudicatorConfig() != null) {
            health = config.getAdjudicatorConfig().getHealth();
            horseHealth = config.getAdjudicatorConfig().getHorseHealth();
        } else {
            health = 24.0;
            horseHealth = 30.0;
        }
    }

    @Override
    public Adjudicator spawn(Location location) {
        Vindicator vindicator = (Vindicator) location.getWorld().spawnEntity(location, EntityType.VINDICATOR);
        vindicator.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        vindicator.setHealth(health);

        // Give adjudicators 8 points of armor using 1.21+ simplified attribute naming
        vindicator.getAttribute(Attribute.ARMOR).setBaseValue(8.0);

        EntityEquipment equipment = vindicator.getEquipment();
        if (equipment != null) {
            equipment.setItemInMainHand(new ItemStack(Material.IRON_SPEAR));
            equipment.setItemInOffHand(getAdjudicatorShield());
            equipment.setHelmet(getAdjudicatorBanner());
            equipment.setHelmetDropChance(1.0f);
        }

        PersistentDataContainer pdc = vindicator.getPersistentDataContainer();
        pdc.set(getAdjudicatorNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
        vindicator.setCustomName("§6Adjudicator");

        return new Adjudicator(vindicator);
    }

    public AdjudicatorRider spawnRider(Location location) {
        Horse horse = (Horse) location.getWorld().spawnEntity(location, EntityType.HORSE);
        horse.getAttribute(Attribute.MAX_HEALTH).setBaseValue(horseHealth);
        horse.setHealth(horseHealth);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setTamed(true);
        horse.setCustomName("§6Adjudicator's Steed");

        Vindicator vindicator = (Vindicator) location.getWorld().spawnEntity(location, EntityType.VINDICATOR);
        vindicator.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        vindicator.setHealth(health);

        // Give adjudicators 8 points of armor
        vindicator.getAttribute(Attribute.ARMOR).setBaseValue(8.0);

        EntityEquipment equipment = vindicator.getEquipment();
        if (equipment != null) {
            equipment.setItemInMainHand(new ItemStack(Material.IRON_SPEAR));
            equipment.setItemInOffHand(getAdjudicatorShield());
            equipment.setHelmet(getAdjudicatorBanner());
            equipment.setHelmetDropChance(1.0f);
        }

        PersistentDataContainer pdc = vindicator.getPersistentDataContainer();
        pdc.set(getAdjudicatorNamespacedKey(), PersistentDataType.BYTE, (byte) 0);

        pdc.set(getAdjudicatorHorseKey(), PersistentDataType.STRING, horse.getUniqueId().toString());
        vindicator.setCustomName("§6Adjudicator Rider");

        horse.addPassenger(vindicator);

        return new AdjudicatorRider(vindicator, horse);
    }

    @EventHandler
    private void onAdjudicatorDamage(EntityDamageEvent evt) {
        if (!(evt.getEntity() instanceof Vindicator))
            return;

        Vindicator vindicator = (Vindicator) evt.getEntity();
        PersistentDataContainer pdc = vindicator.getPersistentDataContainer();
        NamespacedKey horseKey = getAdjudicatorHorseKey();

        if (pdc.has(horseKey, PersistentDataType.STRING)) {
            String uuidStr = pdc.get(horseKey, PersistentDataType.STRING);
            if (uuidStr != null) {
                try {
                    java.util.UUID horseUuid = java.util.UUID.fromString(uuidStr);
                    org.bukkit.entity.Entity horse = org.bukkit.Bukkit.getEntity(horseUuid);
                    if (horse != null && !horse.isDead()) {
                        evt.setCancelled(true);
                    }
                } catch (IllegalArgumentException e) {
                    // Ignore malformed UUID exceptions
                }
            }
        }
    }

    private ItemStack getAdjudicatorBanner() {
        ItemStack banner = new ItemStack(Material.BLACK_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        if (meta != null) {
            meta.addPattern(new Pattern(DyeColor.RED, PatternType.RHOMBUS));
            meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM));
            meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_CENTER));
            meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.BORDER));
            meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
            meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.HALF_HORIZONTAL));
            meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.CIRCLE));
            meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.BORDER));
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.setDisplayName(ChatColor.GOLD.toString() + "Adjudicator Banner");
            banner.setItemMeta(meta);
        }
        return banner;
    }

    private ItemStack getAdjudicatorShield() {
        ItemStack shield = new ItemStack(Material.SHIELD);
        ShieldMeta meta = (ShieldMeta) shield.getItemMeta();
        if (meta != null) {
            meta.setBaseColor(DyeColor.BLACK);
            meta.addPattern(new Pattern(DyeColor.RED, PatternType.RHOMBUS));
            meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM));
            meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_CENTER));
            meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.BORDER));
            meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
            meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.HALF_HORIZONTAL));
            meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.CIRCLE));
            meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.BORDER));
            shield.setItemMeta(meta);
        }
        return shield;
    }

    private NamespacedKey getAdjudicatorNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "adjudicator");
    }

    private NamespacedKey getAdjudicatorHorseKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "adjudicator_horse_uuid");
    }
}
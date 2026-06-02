package com.solarrabbit.largeraids.raid.mob.manager;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.config.custommobs.CustomMobsConfig;
import com.solarrabbit.largeraids.raid.mob.KingRaider;
import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class KingRaiderManager implements BossRaiderManager, Listener {
    private double ravagerHealth;
    private double ravagerDamage;
    private double fangDamage;
    private int fireTicks;
    private int regenLevel;
    private static final EntityType RIDER_TYPE = EntityType.EVOKER;

    public KingRaiderManager() {
    }

    @Override
    public void loadSettings(CustomMobsConfig config) {
        ravagerHealth = config.getKingRaiderConfig().getRavagerHealth();
        ravagerDamage = config.getKingRaiderConfig().getRavagerDamage();
        fangDamage = config.getKingRaiderConfig().getFangDamage();
        fireTicks = config.getKingRaiderConfig().getFireTicks();
        regenLevel = config.getKingRaiderConfig().getRegenLevel();
    }

    @Override
    public KingRaider spawn(Location location) {
        Ravager ravager = (Ravager) location.getWorld().spawnEntity(location, EntityType.RAVAGER);
        ravager.setCustomName("§cJuggernaut §5Ravager");
        ravager.getAttribute(Attribute.MAX_HEALTH).setBaseValue(ravagerHealth);
        ravager.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(ravagerDamage);
        ravager.setHealth(ravagerHealth);
        ravager.getPersistentDataContainer().set(getJuggernautNamespacedKey(), PersistentDataType.BYTE, (byte) 0);

        // Initialize the first 100 HP threshold trigger point in PDC
        NamespacedKey thresholdKey = new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "juggernaut_next_trigger");
        ravager.getPersistentDataContainer().set(thresholdKey, PersistentDataType.DOUBLE, ravagerHealth - 100.0);

        if (regenLevel >= 0)
            ravager.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, regenLevel));

        Spellcaster rider = (Spellcaster) location.getWorld().spawnEntity(location, RIDER_TYPE);
        EntityEquipment equipment = rider.getEquipment();
        equipment.setHelmet(getDefaultBanner());
        equipment.setHelmetDropChance(1.0f);
        rider.getPersistentDataContainer().set(getKingNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
        rider.setCustomName("§dRaid Commander");

        BossBar bossBar = createBossBar(rider);
        createBossBar(ravager);

        ravager.addPassenger(rider);
        return new KingRaider(rider, ravager, bossBar);
    }

    // --- CONDITION 1: TRIGGERS ON EVERY 100 HP LOSS ---
    @EventHandler
    private void onJuggernautDamage(EntityDamageEvent evt) {
        if (!(evt.getEntity() instanceof Ravager))
            return;

        Ravager ravager = (Ravager) evt.getEntity();
        if (!isJuggernaut(ravager))
            return;

        double currentHealth = ravager.getHealth();
        double finalDamage = evt.getFinalDamage();
        double nextHealth = currentHealth - finalDamage;

        if (nextHealth <= 0)
            return; // Juggernaut is dead, do not trigger

        PersistentDataContainer pdc = ravager.getPersistentDataContainer();
        NamespacedKey thresholdKey = new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "juggernaut_next_trigger");

        double maxHealth = ravager.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        double nextTrigger = pdc.has(thresholdKey, PersistentDataType.DOUBLE)
                ? pdc.get(thresholdKey, PersistentDataType.DOUBLE)
                : maxHealth - 100.0;

        if (nextHealth <= nextTrigger) {
            // Find a valid target nearby
            LivingEntity target = ravager.getTarget();
            if (target == null) {
                double scanRadius = 15.0;
                for (Entity near : ravager.getNearbyEntities(scanRadius, scanRadius, scanRadius)) {
                    if (near instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity) near;
                        if (isValidTarget(living)) {
                            target = living;
                            break;
                        }
                    }
                }
            }

            if (target != null) {
                triggerSonicBoomAttack(ravager, target);
            }

            // Adjust threshold boundary (handles multi-100 drops from single massive hits safely)
            while (nextHealth <= nextTrigger) {
                nextTrigger -= 100.0;
            }
            pdc.set(thresholdKey, PersistentDataType.DOUBLE, nextTrigger);
        }
    }

    // --- CONDITION 2: TRIGGERS ON SHIELD STUN ---
    @EventHandler
    private void onJuggernautShieldBlock(EntityDamageByEntityEvent evt) {
        if (!(evt.getDamager() instanceof Ravager))
            return;

        Ravager ravager = (Ravager) evt.getDamager();
        if (!isJuggernaut(ravager))
            return;

        if (!(evt.getEntity() instanceof Player))
            return;

        Player player = (Player) evt.getEntity();

        // Verify if the player is actively blocking with a shield
        if (player.isBlocking()) {
            // Check the 180-degree blocking arc facing the Juggernaut via dot product
            Vector playerLook = player.getLocation().getDirection().setY(0).normalize();
            Vector toRavager = ravager.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0).normalize();
            double dot = playerLook.dot(toRavager);

            if (dot > 0.0) { // Shield block successful
                // 1. Instantly play the dazed stun animation (shaking head side-to-side)
                ravager.playEffect(EntityEffect.RAVAGER_STUNNED);

                // 2. Trigger the dazed charging animation and counter-attack once the 40-tick stun expires
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(LargeRaids.class), () -> {
                    if (!ravager.isDead() && !player.isDead()) {
                        triggerSonicBoomAttack(ravager, player);
                    }
                }, 40L); // 40 ticks = 2 seconds stun duration
            }
        }
    }

    private void triggerSonicBoomAttack(Ravager ravager, LivingEntity target) {
        if (target == null) return;
        final LivingEntity finalTarget = target;

        // Plays the dazed, head-shaking animation as a charge-up visual before shooting the beam
        ravager.playEffect(EntityEffect.RAVAGER_STUNNED);

        // Play the Warden Sonic Charge charging sound immediately
        ravager.getWorld().playSound(ravager.getLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, 1.5F, 1.1F);

        // Fire the beam after 20 ticks (1 second) to sync with the sonic charge audio
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(LargeRaids.class), () -> {
            if (ravager.isDead() || finalTarget.isDead()) return;

            Location start = ravager.getEyeLocation();
            Location end = finalTarget.getEyeLocation();

            // Guard rails to check if the target has run too far away (range threshold of 25 blocks)
            if (start.distanceSquared(end) > 25.0 * 25.0) return;

            // Play Sonic Boom sound
            ravager.getWorld().playSound(start, Sound.ENTITY_WARDEN_SONIC_BOOM, 2.0F, 1.0F);

            // Calculate trajectory and draw Sonic Boom ring path
            Vector direction = end.toVector().subtract(start.toVector()).normalize();
            double distance = start.distance(end);

            for (double d = 0.0; d < distance; d += 1.5) {
                Location point = start.clone().add(direction.clone().multiply(d));
                point.getWorld().spawnParticle(Particle.SONIC_BOOM, point, 1, 0.0, 0.0, 0.0, 0.0);
            }

            // Inflict 10.0 true damage (5 hearts)
            finalTarget.damage(10.0, ravager);

            // Push target away slightly along the vector path
            finalTarget.setVelocity(finalTarget.getVelocity().add(direction.clone().multiply(0.4)));

        }, 20L);
    }

    private void triggerSonicBoomAttack(Ravager ravager) {
        LivingEntity target = ravager.getTarget();
        if (target == null) {
            double scanRadius = 15.0;
            for (Entity near : ravager.getNearbyEntities(scanRadius, scanRadius, scanRadius)) {
                if (near instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity) near;
                    if (isValidTarget(living)) {
                        target = living;
                        break;
                    }
                }
            }
        }
        if (target != null) {
            triggerSonicBoomAttack(ravager, target);
        }
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (entity instanceof Player) {
            Player p = (Player) entity;
            return p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR;
        }
        // Protect illagers, custom raiders, and witches
        if (entity instanceof Raider) {
            return false;
        }
        return !entity.isDead();
    }

    @EventHandler
    private void onFangsSpawn(EntitySpawnEvent evt) {
        if (evt.getEntityType() != EntityType.EVOKER_FANGS)
            return;
        EvokerFangs fangs = (EvokerFangs) evt.getEntity();
        LivingEntity owner = fangs.getOwner();
        if (owner instanceof Spellcaster && isKing((Spellcaster) owner)) {
            fangs.setVisualFire(true);
            fangs.getPersistentDataContainer().set(getKingFangsNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
        }
    }

    @EventHandler
    private void onFangsAttack(EntityDamageByEntityEvent evt) {
        if (evt.getDamager().getType() != EntityType.EVOKER_FANGS)
            return;
        if (evt.getEntity() instanceof Raider)
            return;
        if (isKingFangs((EvokerFangs) evt.getDamager())) {
            evt.getEntity().setFireTicks(fireTicks);
            evt.setDamage(fangDamage);
        }
    }

    @EventHandler
    private void onSummonVex(CreatureSpawnEvent evt) {
        if (evt.getEntityType() != EntityType.VEX)
            return;
        Vex vex = (Vex) evt.getEntity();
        LivingEntity owner = VersionUtil.getCraftVexWrapper(vex).getOwner();
        if (!(owner instanceof Spellcaster))
            return;
        Spellcaster evoker = (Spellcaster) owner;
        if (isKing(evoker))
            vex.getEquipment().setItemInMainHand(getKingVexSword());
    }

    @EventHandler
    private void onKingDamage(EntityDamageEvent evt) {
        if (evt.getEntityType() != RIDER_TYPE)
            return;
        Spellcaster king = (Spellcaster) evt.getEntity();
        Entity vehicle = king.getVehicle();
        // Kings riding juggernauts are invulnerable
        if (isKing(king) && vehicle instanceof Ravager && isJuggernaut((Ravager) vehicle))
            evt.setCancelled(true);
    }

    private ItemStack getDefaultBanner() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.CYAN, PatternType.RHOMBUS));
        meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_BOTTOM));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.HALF_HORIZONTAL));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
        meta.addPattern(new Pattern(DyeColor.GRAY, PatternType.STRIPE_CENTER));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.SKULL));
        meta.addPattern(new Pattern(DyeColor.LIGHT_GRAY, PatternType.CIRCLE));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_TOP));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.setDisplayName(ChatColor.RED.toString() + ChatColor.ITALIC + "Raid Captain Banner");
        banner.setItemMeta(meta);
        return banner;
    }

    private ItemStack getKingVexSword() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        item.addEnchantment(Enchantment.FIRE_ASPECT, 2);
        item.addEnchantment(Enchantment.SHARPNESS, 3);
        return item;
    }

    private boolean isJuggernaut(Ravager entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.has(getJuggernautNamespacedKey(), PersistentDataType.BYTE);
    }

    private boolean isKing(Raider entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.has(getKingNamespacedKey(), PersistentDataType.BYTE);
    }

    private boolean isKingFangs(EvokerFangs fangs) {
        PersistentDataContainer pdc = fangs.getPersistentDataContainer();
        return pdc.has(getKingFangsNamespacedKey(), PersistentDataType.BYTE);
    }

    private NamespacedKey getJuggernautNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "juggernaut");
    }

    private NamespacedKey getKingNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "juggernaut_king");
    }

    private NamespacedKey getKingFangsNamespacedKey() {
        return new NamespacedKey(JavaPlugin.getPlugin(LargeRaids.class), "juggernaut_king_fangs");
    }
}
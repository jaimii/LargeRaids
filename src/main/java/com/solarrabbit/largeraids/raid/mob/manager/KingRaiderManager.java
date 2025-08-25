package com.solarrabbit.largeraids.raid.mob.manager;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.config.custommobs.CustomMobsConfig;
import com.solarrabbit.largeraids.raid.mob.KingRaider;
import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
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

public class KingRaiderManager implements BossRaiderManager, Listener {
    private double ravagerHealth;
    private double ravagerDamage;
    private double fangDamage;
    private int fireTicks;
    private int regenLevel;
    private static final EntityType RIDER_TYPE = EntityType.EVOKER;

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
        ravager.setCustomName("§c§lJuggernaut Ravager");
        ravager.getAttribute(Attribute.MAX_HEALTH).setBaseValue(ravagerHealth);
        ravager.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(ravagerDamage);
        ravager.setHealth(ravagerHealth);
        ravager.getPersistentDataContainer().set(getJuggernautNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
        if (regenLevel >= 0)
            ravager.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, regenLevel));

        Spellcaster rider = (Spellcaster) location.getWorld().spawnEntity(location, RIDER_TYPE);
        EntityEquipment equipment = rider.getEquipment();
        equipment.setHelmet(getDefaultBanner());
        equipment.setHelmetDropChance(1.0f);
        rider.getPersistentDataContainer().set(getKingNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
        rider.setCustomName("§d§lArch-Evoker");

        BossBar bossBar = createBossBar(rider);
        createBossBar(ravager);

        ravager.addPassenger(rider);
        return new KingRaider(rider, ravager, bossBar);
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

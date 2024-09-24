package com.solarrabbit.largeraids.util;

import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.nms.*;

import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Vex;

public class VersionUtil {
    public static AbstractBlockPositionWrapper getBlockPositionWrapper(Location location) {
        return getBlockPositionWrapper(location.getX(), location.getY(), location.getZ());
    }

    public static AbstractBlockPositionWrapper getBlockPositionWrapper(double x, double y, double z) {
        return new com.solarrabbit.largeraids.versioned.nms.BlockPositionWrapper(x, y, z);
    }

    public static AbstractCraftRaidWrapper getCraftRaidWrapper(AbstractRaidWrapper wrapper) {
        return new com.solarrabbit.largeraids.versioned.nms.CraftRaidWrapper(wrapper);
    }

    public static AbstractCraftRaidWrapper getCraftRaidWrapper(Raid raid) {
        return new com.solarrabbit.largeraids.versioned.nms.CraftRaidWrapper(raid);
    }

    public static AbstractCraftRaiderWrapper getCraftRaiderWrapper(Raider raider) {
        return new com.solarrabbit.largeraids.versioned.nms.CraftRaiderWrapper(raider);
    }

    public static AbstractCraftServerWrapper getCraftServerWrapper(Server server) {
        return new com.solarrabbit.largeraids.versioned.nms.CraftServerWrapper(server);
    }

    public static AbstractCraftWorldWrapper getCraftWorldWrapper(World world) {
        return new com.solarrabbit.largeraids.versioned.nms.CraftWorldWrapper(world);
    }

    public static AbstractPlayerEntityWrapper getPlayerEntityWrapper(AbstractMinecraftServerWrapper server,
            AbstractWorldServerWrapper world,
            GameProfile profile) {
        return new com.solarrabbit.largeraids.versioned.nms.PlayerEntityWrapper(server, world, profile, null);
    }

    public static AbstractProfessionWrapper getMasonProfessionWrapper() {
        return com.solarrabbit.largeraids.versioned.nms.ProfessionWrapper.MASON;
    }

    public static AbstractPoiTypeWrapper getMasonPoiTypeWrapper() {
        return com.solarrabbit.largeraids.versioned.nms.PoiTypeWrapper.MASON;
    }

    public static AbstractCraftVexWrapper getCraftVexWrapper(Vex vex) {
        return new com.solarrabbit.largeraids.versioned.nms.CraftVexWrapper(vex);
    }

}

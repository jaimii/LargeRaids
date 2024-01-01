package com.solarrabbit.largeraids.util;

import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.nms.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Vex;

public class VersionUtil {
	private static final String VERSION = "v1_20_R3";

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

    public static int getServerMinorVersion() {
        return getMinorVersion(getServerVersion());
    }

    public static boolean isSupported() {
        String apiVersion = getAPIVersion();
        if (VERSION.equals(apiVersion))
        	return true;
        return false;
    }

    public static int compare(String versionA, String versionB) {
        int majDiff = getMajorVersion(versionA) - getMajorVersion(versionB);
        if (majDiff != 0)
            return majDiff;
        int minorDiff = getMinorVersion(versionA) - getMinorVersion(versionB);
        if (minorDiff != 0)
            return minorDiff;
        return getPatchVersion(versionA) - getPatchVersion(versionB);
    }

    private static int getMajorVersion(String version) {
        String[] splits = version.split("\\.");
        return Integer.parseInt(splits[0]);
    }

    private static int getMinorVersion(String version) {
        String[] splits = version.split("\\.");
        return splits.length < 2 ? 0 : Integer.parseInt(splits[1]);
    }

    private static int getPatchVersion(String version) {
        String[] splits = version.split("\\.");
        return splits.length < 3 ? 0 : Integer.parseInt(splits[2]);
    }

    private static String getServerVersion() {
        return getCraftServerWrapper(Bukkit.getServer()).getServer().getServerVersion();
    }

    private static String getAPIVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}

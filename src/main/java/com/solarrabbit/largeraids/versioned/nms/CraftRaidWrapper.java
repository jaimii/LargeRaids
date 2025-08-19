package com.solarrabbit.largeraids.versioned.nms;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import com.solarrabbit.largeraids.nms.AbstractCraftRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;

import net.minecraft.server.level.ServerLevel;

import org.bukkit.Raid;
import org.bukkit.craftbukkit.CraftRaid;

public class CraftRaidWrapper extends AbstractCraftRaidWrapper {
    private static MethodHandle raidHandle;
    private static MethodHandle levelHandle;

    public CraftRaidWrapper(AbstractRaidWrapper nmsRaid) {
        super(new CraftRaid(((RaidWrapper) nmsRaid).raid, ((RaidWrapper) nmsRaid).server));
    }

    public CraftRaidWrapper(Raid raid) {
        super(raid);
    }

    @Override
    public RaidWrapper getHandle() {
        net.minecraft.world.entity.raid.Raid nmsRaid;
        net.minecraft.world.level.Level nmsLevel;
        try {
            if (raidHandle == null) {
                Field field = CraftRaid.class.getDeclaredField("handle");
                field.setAccessible(true);
                raidHandle = MethodHandles.lookup().unreflectGetter(field);
            }
            if (levelHandle == null) {
                Field field = CraftRaid.class.getDeclaredField("level");
                field.setAccessible(true);
                levelHandle = MethodHandles.lookup().unreflectGetter(field);
            }
            nmsRaid = (net.minecraft.world.entity.raid.Raid) raidHandle.invokeExact((CraftRaid) raid);
            nmsLevel = (net.minecraft.world.level.Level) levelHandle.invokeExact((CraftRaid) raid);
        } catch (Throwable e) {
            e.printStackTrace();
            nmsRaid = null;
            nmsLevel = null;
        }
        return new RaidWrapper(nmsRaid, (ServerLevel) nmsLevel);
    }

}

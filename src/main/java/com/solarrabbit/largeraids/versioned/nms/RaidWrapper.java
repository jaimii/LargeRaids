package com.solarrabbit.largeraids.versioned.nms;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import org.apache.commons.lang3.reflect.FieldUtils;

import net.minecraft.world.entity.raid.Raid;

public class RaidWrapper implements AbstractRaidWrapper {
    final Raid raid;

    RaidWrapper(Raid raid) {
        this.raid = raid;
    }

    @Override
    public boolean isEmpty() {
        return raid == null;
    }

    @Override
    public void stop() {
        raid.stop();
    }

    @Override
    public boolean isBetweenWaves() {
        return raid.isBetweenWaves();
    }

    @Override
    public boolean hasFirstWaveSpawned() {
        return raid.hasFirstWaveSpawned();
    }

    @Override
    public void setRaidOmenLevel(int level) {
        this.raid.setRaidOmenLevel(level);
    }

    @Override
    public int getTotalGroups() {
        return this.raid.numGroups;
    }

    @Override
    public int getGroupsSpawned() {
        return this.raid.getGroupsSpawned();
    }

    @Override
    public void setGroupsSpawned(int groupsSpawned) {
        try {
            try {
                FieldUtils.writeField(this.raid, "groupsSpawned", groupsSpawned, true);
            } catch (IllegalArgumentException ignore) {
                FieldUtils.writeField(this.raid, "I", groupsSpawned, true);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinRaid(int i, AbstractRaiderWrapper raider, @Nullable AbstractBlockPositionWrapper blockPosition,
            boolean flag) {
        raid.joinRaid(i, ((RaiderWrapper) raider).raider,
                blockPosition == null ? null : ((BlockPositionWrapper) blockPosition).blockPos, flag);
    }

    @Override
    public boolean addWaveMob(int wave, AbstractRaiderWrapper raider, boolean flag) {
        return this.raid.addWaveMob(wave, ((RaiderWrapper) raider).raider, flag);
    }

    @Override
    public void removeFromRaid(AbstractRaiderWrapper raider, boolean flag) {
        this.raid.removeFromRaid(((RaiderWrapper) raider).raider, flag);
    }

    @Override
    public Set<UUID> getHeroesOfTheVillage() {
        return this.raid.heroesOfTheVillage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RaidWrapper) {
            return this.raid == ((RaidWrapper) obj).raid;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.raid.hashCode();
    }

    @Override
    public boolean isActive() {
        return raid.isActive();
    }
}

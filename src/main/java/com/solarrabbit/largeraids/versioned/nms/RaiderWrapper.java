package com.solarrabbit.largeraids.versioned.nms;

import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raider;

public class RaiderWrapper implements AbstractRaiderWrapper {
    final Raider raider;

    RaiderWrapper(Raider raider) {
        this.raider = raider;
    }

    @Override
    public RaidWrapper getCurrentRaid() {
        return new RaidWrapper(raider.getCurrentRaid(), (ServerLevel) raider.level());
    }
}

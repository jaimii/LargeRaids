package com.solarrabbit.largeraids.versioned.nms;

import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
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

    @Override
    public boolean addAttackGoal(int prio, boolean mustSee, Class<?> entityClass) {
        if (!(LivingEntity.class.isAssignableFrom(entityClass)))
            return false;
        raider.targetSelector.addGoal(prio, new NearestAttackableTargetGoal<>(raider, (Class<? extends LivingEntity>)entityClass, mustSee));
        return true;
    }
}

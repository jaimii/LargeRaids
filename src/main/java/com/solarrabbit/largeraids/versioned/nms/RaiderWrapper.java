package com.solarrabbit.largeraids.versioned.nms;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.PathfindToRaidGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
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
    public void setRaiderTarget(AbstractBlockPositionWrapper pos, double radius) {
        BlockPos blockPos = pos == null ? null : ((BlockPositionWrapper) pos).blockPos;

        for (WrappedGoal goal : raider.goalSelector.getAvailableGoals()) {
            if (goal.getGoal().getClass() == PathfindToRaidGoal.class) {
                int prio = goal.getPriority();
                raider.goalSelector.removeGoal(goal.getGoal());
                PathfindToTargetGoal<Raider> newGoal = new PathfindToTargetGoal<>(raider);
                newGoal.setTargetPos(blockPos, radius);
                raider.goalSelector.addGoal(prio, newGoal);
                break;
            } else if (goal.getGoal() instanceof PathfindToTargetGoal pathfindGoal) {
                pathfindGoal.setTargetPos(blockPos, radius);
                break;
            }
        }
    }

    @Override
    public boolean addAttackGoal(int prio, boolean mustSee, Class<?> entityClass) {
        if (!(LivingEntity.class.isAssignableFrom(entityClass)))
            return false;
        raider.targetSelector.addGoal(prio, new NearestAttackableTargetGoal<>(raider, (Class<? extends LivingEntity>)entityClass, mustSee));
        return true;
    }
}

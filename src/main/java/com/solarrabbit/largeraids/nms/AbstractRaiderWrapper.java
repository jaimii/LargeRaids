package com.solarrabbit.largeraids.nms;

public interface AbstractRaiderWrapper {
    AbstractRaidWrapper getCurrentRaid();

    boolean addAttackGoal(int prio, boolean mustSee, Class<?> entityClass);
}

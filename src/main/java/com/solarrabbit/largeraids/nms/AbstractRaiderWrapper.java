package com.solarrabbit.largeraids.nms;

public interface AbstractRaiderWrapper {
    AbstractRaidWrapper getCurrentRaid();

    void setRaiderTarget(AbstractBlockPositionWrapper pos, double radius, double navSpeed);

    boolean addAttackGoal(int prio, boolean mustSee, Class<?> entityClass);
}

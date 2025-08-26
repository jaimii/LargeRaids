package com.solarrabbit.largeraids.nms;

public interface AbstractWorldServerWrapper {
    int clearMobTargets(Class<?> entityClass, AbstractBlockPositionWrapper search, double range);

    int setMobTargets(Class<?> entityClass, AbstractBlockPositionWrapper search, double range,
            AbstractBlockPositionWrapper target, double radius, double navSpeed, int prio, boolean pathfindOnce);

    AbstractRaidWrapper getRaidAt(AbstractBlockPositionWrapper blockPos);

    AbstractRaidsWrapper getRaids();

    AbstractVillageManagerWrapper getVillageRecordManager();
}

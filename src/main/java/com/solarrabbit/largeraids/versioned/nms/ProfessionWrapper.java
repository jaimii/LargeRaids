package com.solarrabbit.largeraids.versioned.nms;

import java.util.function.Predicate;

import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;
import com.solarrabbit.largeraids.nms.AbstractProfessionWrapper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;

public class ProfessionWrapper implements AbstractProfessionWrapper {
    public static final ProfessionWrapper MASON = new ProfessionWrapper(BuiltInRegistries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.MASON));
    final Holder<VillagerProfession> professionHolder;

    ProfessionWrapper(Holder<VillagerProfession> professionHolder) {
        this.professionHolder = professionHolder;
    }

    @Override
    public Predicate<? super AbstractPoiTypeWrapper> getPredicate() {
        return (poiTypeWrapper) -> professionHolder.value().acquirableJobSite().test(((PoiTypeWrapper) poiTypeWrapper).poiTypeHolder);
    }

}

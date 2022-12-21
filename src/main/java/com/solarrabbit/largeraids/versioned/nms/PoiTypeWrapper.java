package com.solarrabbit.largeraids.versioned.nms;

import java.util.function.Predicate;

import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public class PoiTypeWrapper implements AbstractPoiTypeWrapper {
    public static final PoiTypeWrapper MASON = new PoiTypeWrapper(PoiType.MASON);
    final PoiType poiType;

    PoiTypeWrapper(PoiType poiType) {
        this.poiType = poiType;
    }

    @Override
    public Predicate<? super AbstractPoiTypeWrapper> getPredicate() {
        return (poiTypeWrapper) -> poiType.getPredicate().test(((PoiTypeWrapper) poiTypeWrapper).poiType);
    }

}

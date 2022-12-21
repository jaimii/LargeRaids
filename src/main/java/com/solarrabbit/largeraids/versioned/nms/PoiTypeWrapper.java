package com.solarrabbit.largeraids.versioned.nms;

import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public class PoiTypeWrapper implements AbstractPoiTypeWrapper {
    public static final PoiTypeWrapper MASON = new PoiTypeWrapper(Registry.POINT_OF_INTEREST_TYPE.getHolderOrThrow(PoiTypes.MASON));
    final Holder<PoiType> poiTypeHolder;

    PoiTypeWrapper(Holder<PoiType> poiTypeHolder) {
        this.poiTypeHolder = poiTypeHolder;
    }

}

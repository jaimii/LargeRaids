package com.solarrabbit.largeraids.versioned.nms;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;
import com.solarrabbit.largeraids.nms.AbstractVillageManagerWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;

public class VillageManagerWrapper implements AbstractVillageManagerWrapper {
    private final PoiManager poiManager;

    VillageManagerWrapper(PoiManager poiManager) {
        this.poiManager = poiManager;
    }

    @Override
    public void add(AbstractBlockPositionWrapper blockPos, AbstractPoiTypeWrapper poiType) {
        poiManager.add(((BlockPositionWrapper) blockPos).blockPos, ((PoiTypeWrapper) poiType).poiTypeHolder);
    }

    @Override
    public Optional<BlockPositionWrapper> take(@Nonnull Predicate<? super AbstractPoiTypeWrapper> poiPred,
            @Nonnull BiPredicate<? super AbstractPoiTypeWrapper, ? super AbstractBlockPositionWrapper> blockPosPred,
            AbstractBlockPositionWrapper blockPos,
            int d) {
        Optional<BlockPos> res = poiManager.take(poiTypeHolder -> poiPred.test(new PoiTypeWrapper(poiTypeHolder)),
                (poiTypeHolder, pos) -> blockPosPred.test(new PoiTypeWrapper(poiTypeHolder), new BlockPositionWrapper(pos)),
                ((BlockPositionWrapper) blockPos).blockPos, d);
        return res.map(BlockPositionWrapper::new);
    }

    @Override
    public void remove(AbstractBlockPositionWrapper blockPos) {
        poiManager.remove(((BlockPositionWrapper) blockPos).blockPos);
    }

}

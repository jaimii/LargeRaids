package com.solarrabbit.largeraids.versioned.nms;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class BlockPositionWrapper implements AbstractBlockPositionWrapper {
    final BlockPos blockPos;

    public BlockPositionWrapper(double x, double y, double z) {
        this.blockPos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
    }

    BlockPositionWrapper(@Nonnull BlockPos blockPos) {
        requireNonNull(blockPos);
        this.blockPos = blockPos;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockPositionWrapper))
            return false;
        return this.blockPos.equals(((BlockPositionWrapper) obj).blockPos);
    }
}

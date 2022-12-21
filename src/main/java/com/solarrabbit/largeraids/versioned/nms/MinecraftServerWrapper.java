package com.solarrabbit.largeraids.versioned.nms;

import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;

import net.minecraft.server.MinecraftServer;

public class MinecraftServerWrapper implements AbstractMinecraftServerWrapper {
    final MinecraftServer server;

    MinecraftServerWrapper(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public String getServerVersion() {
        return server.getServerVersion();
    }
}

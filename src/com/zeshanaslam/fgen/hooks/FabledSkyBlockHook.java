package com.zeshanaslam.fgen.hooks;

import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandManager;
import org.bukkit.Location;

import java.util.UUID;

public class FabledSkyBlockHook {

    public UUID getOwner(Location location) {
        IslandManager islandManager = SkyBlockAPI.getIslandManager();
        Island island = islandManager.getIslandAtLocation(location);

        return island.getOwnerUUID();
    }
}

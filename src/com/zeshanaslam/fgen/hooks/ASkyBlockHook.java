package com.zeshanaslam.fgen.hooks;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.Location;

import java.util.UUID;

public class ASkyBlockHook {

    public UUID getOwner(Location location) {
        ASkyBlockAPI aSkyBlockAPI = ASkyBlockAPI.getInstance();
        Island island = aSkyBlockAPI.getIslandAt(location);

        return island.getOwner();
    }
}

package com.zeshanaslam.fgen.utils;

import com.zeshanaslam.fgen.Main;
import com.zeshanaslam.fgen.config.GroupData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GenerateGroupData {

    private final Main main;

    public GenerateGroupData(Main main) {
        this.main = main;
    }

    public Material getGeneratedMaterial(Player player, Location location) {
        UUID islandOwner = main.hookHandler.getOwner(location) != null ? main.hookHandler.getOwner(location) : player.getUniqueId();
        if (islandOwner == null) {
            return Material.COBBLESTONE;
        }

        GroupData groupData = main.configStore.getGroupData(location.getWorld().getName(), Bukkit.getOfflinePlayer(islandOwner));

        return groupData.blocks.next();
    }
}

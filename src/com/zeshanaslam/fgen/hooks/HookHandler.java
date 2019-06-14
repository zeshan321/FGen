package com.zeshanaslam.fgen.hooks;

import com.zeshanaslam.fgen.Main;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class HookHandler {

    private final Main main;
    private boolean hookedASkyBlock;
    private boolean hookedFabledSkyBlock;
    public Permission vaultPermissions;

    public HookHandler(Main main) {
        this.main = main;
        this.hookedASkyBlock = false;
        this.hookedFabledSkyBlock = false;

        setupASkyBlock();
        setupFabledSkyBlock();
        setupPermissions();
    }

    public UUID getOwner(Location location) {
        if (hookedASkyBlock) {
            return new ASkyBlockHook().getOwner(location);
        }

        if (hookedFabledSkyBlock) {
            return new FabledSkyBlockHook().getOwner(location);
        }

        return null;
    }

    private void setupASkyBlock() {
        Plugin plugin = main.getServer().getPluginManager().getPlugin("ASkyBlock");
        if (plugin == null) {
            return;
        }

        hookedASkyBlock = true;
    }

    private void setupFabledSkyBlock() {
        Plugin plugin = main.getServer().getPluginManager().getPlugin("FabledSkyBlock");
        if (plugin == null) {
            return;
        }

        hookedFabledSkyBlock = true;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = main.getServer().getServicesManager().getRegistration(Permission.class);
        vaultPermissions = rsp.getProvider();
        return vaultPermissions != null;
    }
}

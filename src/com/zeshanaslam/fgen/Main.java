package com.zeshanaslam.fgen;

import com.zeshanaslam.fgen.config.ConfigStore;
import com.zeshanaslam.fgen.events.AFKListeners;
import com.zeshanaslam.fgen.events.BurnListeners;
import com.zeshanaslam.fgen.events.FGenListeners;
import com.zeshanaslam.fgen.hooks.HookHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin {

    public ConfigStore configStore;
    public HookHandler hookHandler;
    public HashMap<UUID, Long> lastMoved;
    public BukkitTask afkTask;

    @Override
    public void onEnable() {
        super.onEnable();

        // Config
        saveDefaultConfig();
        configStore = new ConfigStore(this);

        // Setup hooks
        hookHandler = new HookHandler(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new FGenListeners(this), this);
        getServer().getPluginManager().registerEvents(new BurnListeners(this), this);
        getServer().getPluginManager().registerEvents(new AFKListeners(this), this);
        lastMoved = new HashMap<>();

        // Start afk task
        afkTask = new AFKTask(this).runTaskTimer(this, 0, 20);

        // This is to support /reload or plugman
        for (Player player: Bukkit.getOnlinePlayers()) {
            lastMoved.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        afkTask.cancel();
    }
}

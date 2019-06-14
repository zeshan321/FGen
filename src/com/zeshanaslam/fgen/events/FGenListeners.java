package com.zeshanaslam.fgen.events;

import com.zeshanaslam.fgen.Main;
import com.zeshanaslam.fgen.utils.GenerateGroupData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

public class FGenListeners implements Listener {

    private final Main main;
    private final HashMap<Location, UUID> breaker;
    private final Random random;

    public FGenListeners(Main main) {
        this.main = main;
        this.breaker = new HashMap<>();
        this.random = new Random();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        Block broken = event.getBlock();
        Block under = broken.getLocation().add(0, -1, 0).getBlock();
        Block top = broken.getLocation().add(0, 1, 0).getBlock();

        if (under.getType().name().contains("LAVA") && top.getType().name().contains("WATER")) {
            breaker.put(broken.getLocation(), event.getPlayer().getUniqueId());
        }

        Player player = event.getPlayer();
        if (main.configStore.afkDisabledWorlds.contains(player.getWorld().getName()))
            return;

        double reduction = main.configStore.getMiningReduction(player);
        if (reduction > -1) {
            double percent = random.nextDouble() * 100;

            if (reduction <= 0 || reduction <= percent) {
                event.setDropItems(false);
                event.setExpToDrop(0);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        breaker.values().removeIf(uuid -> uuid.equals(player.getUniqueId()));
    }

    @EventHandler
    public void onChange(BlockFromToEvent event) {
        if (event.isCancelled())
            return;

        Block broken = event.getToBlock();
        Block under = broken.getLocation().add(0, -1, 0).getBlock();
        Block top = broken.getLocation().add(0, 1, 0).getBlock();
        Location brokenLocation = broken.getLocation();

        if (under.getType().name().contains("LAVA") && top.getType().name().contains("WATER")) {
            event.setCancelled(true);

            Player player = Bukkit.getPlayer(breaker.get(brokenLocation));

            GenerateGroupData generateGroupData = new GenerateGroupData(main);
            broken.setType(generateGroupData.getGeneratedMaterial(player, brokenLocation));

            breaker.remove(brokenLocation);
        }
    }
}

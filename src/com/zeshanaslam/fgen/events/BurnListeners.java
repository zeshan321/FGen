package com.zeshanaslam.fgen.events;

import com.zeshanaslam.fgen.Main;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class BurnListeners implements Listener {

    private final Main main;

    public BurnListeners(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.DROPPED_ITEM)
            return;

        if (!main.configStore.damageCauses.contains(event.getCause()))
            return;

        Entity entity = event.getEntity();
        if (main.configStore.damageCauseWorlds.contains(entity.getWorld().getName()))
            return;

        Item item = (Item) entity;
        Location loc = item.getLocation();
        int halfX = main.configStore.x / 2;
        int halfY = main.configStore.y / 2;
        int halfZ = main.configStore.z / 2;

        for (int x = (loc.getBlockX() - halfX); x < (loc.getBlockX() + halfX + 1); x++) {
            for (int y = (loc.getBlockY() - halfY); y < (loc.getBlockY() + halfY + 1); y++) {
                for (int z = (loc.getBlockZ() - halfZ); z < (loc.getBlockZ() + halfZ + 1); z++) {
                    if (loc.getWorld().getBlockAt(x, y, z).getType() == main.configStore.burnItem) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}

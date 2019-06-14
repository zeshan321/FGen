package com.zeshanaslam.fgen.events;

import com.zeshanaslam.fgen.Main;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class AFKListeners implements Listener {

    private final Main main;
    private HashMap<UUID, Location> tempLocations;

    public AFKListeners(Main main) {
        this.main = main;
        this.tempLocations = new HashMap<>();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        main.lastMoved.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        main.lastMoved.remove(player.getUniqueId());
        tempLocations.remove(player.getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to.distance(from) >= 0.1) {
            main.lastMoved.put(player.getUniqueId(), System.currentTimeMillis());
        }

        if (from.getPitch() != to.getPitch() && from.getYaw() != to.getYaw()) {
            main.lastMoved.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            Player player = (Player) event.getEntered();
            tempLocations.put(player.getUniqueId(), player.getLocation());
        }
    }

    @EventHandler
    public void onVehicle(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        for (Entity entity: vehicle.getPassengers()) {
            if (!(entity instanceof Player))
                continue;

            Player player = (Player) entity;
            Location location = player.getLocation();
            Location previous = tempLocations.get(player.getUniqueId());

            if (location.getPitch() != previous.getPitch() && location.getYaw() != previous.getYaw()) {
                main.lastMoved.put(player.getUniqueId(), System.currentTimeMillis());
                tempLocations.put(player.getUniqueId(), location);
            }
        }
    }

    @EventHandler
    public void onExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            Player player = (Player) event.getExited();
            tempLocations.remove(player.getUniqueId());
        }
    }
}
